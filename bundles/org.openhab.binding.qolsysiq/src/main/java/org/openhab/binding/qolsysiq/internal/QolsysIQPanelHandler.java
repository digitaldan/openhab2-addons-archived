/**
 * Copyright (c) 2010-2022 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.qolsysiq.internal;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.qolsysiq.internal.client.QolsysIQClientListener;
import org.openhab.binding.qolsysiq.internal.client.QolsysiqClient;
import org.openhab.binding.qolsysiq.internal.client.dto.actions.Action;
import org.openhab.binding.qolsysiq.internal.client.dto.actions.InfoAction;
import org.openhab.binding.qolsysiq.internal.client.dto.actions.InfoActionType;
import org.openhab.binding.qolsysiq.internal.client.dto.events.AlarmEvent;
import org.openhab.binding.qolsysiq.internal.client.dto.events.ArmingEvent;
import org.openhab.binding.qolsysiq.internal.client.dto.events.SecureArmInfoEvent;
import org.openhab.binding.qolsysiq.internal.client.dto.events.SummaryInfoEvent;
import org.openhab.binding.qolsysiq.internal.client.dto.events.ZoneActiveEvent;
import org.openhab.binding.qolsysiq.internal.client.dto.events.ZoneUpdateEvent;
import org.openhab.binding.qolsysiq.internal.client.dto.models.Partition;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;
import org.openhab.core.thing.binding.BaseBridgeHandler;
import org.openhab.core.thing.binding.ThingHandler;
import org.openhab.core.types.Command;
import org.openhab.core.types.RefreshType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link QolsysIQPanelHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Dan Cunningham - Initial contribution
 */
@NonNullByDefault
public class QolsysIQPanelHandler extends BaseBridgeHandler implements QolsysIQClientListener {

    private final Logger logger = LoggerFactory.getLogger(QolsysIQPanelHandler.class);
    private static final int RETRY_SECONDS = 30;

    private @Nullable QolsysiqClient apiClient;
    private @Nullable ScheduledFuture<?> retryFuture;
    private String key = "";
    private Map<Integer, Partition> partitions = Collections.synchronizedMap(new HashMap<Integer, Partition>());

    public QolsysIQPanelHandler(Bridge bridge) {
        super(bridge);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (command instanceof RefreshType) {
            sendAction(new InfoAction(InfoActionType.SUMMARY, key));
        }
    }

    @Override
    public void initialize() {
        updateStatus(ThingStatus.UNKNOWN);
        QolsysIQPanelConfiguration config = getConfigAs(QolsysIQPanelConfiguration.class);
        key = config.key;
        disconnect();
        apiClient = new QolsysiqClient(config.hostname, config.port, config.heartbeatInterval, scheduler);
        scheduler.execute(() -> {
            connect();
        });
    }

    @Override
    public void childHandlerInitialized(ThingHandler childHandler, Thing childThing) {
        // update child things
    }

    @Override
    public void disconnected(Exception reason) {
        logger.debug("disconnected", reason);
        updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, reason.getMessage());
        startRetryFuture();
    }

    @Override
    public void alarmEvent(AlarmEvent event) {
        logger.debug("AlarmEvent {}", event.partitionId);
        QolsysIQPartitionHandler handler = partitionHandler(event.partitionId);
        if (handler != null) {
            handler.alarmEvent(event);
        }
    }

    @Override
    public void armingEvent(ArmingEvent event) {
        logger.debug("ArmingEvent {}", event.partitionId);
        QolsysIQPartitionHandler handler = partitionHandler(event.partitionId);
        if (handler != null) {
            handler.armingEvent(event);
        }
    }

    @Override
    public void summaryInfoEvent(SummaryInfoEvent event) {
        logger.debug("SummaryInfoEvent");
        partitions.clear();
        event.partitionList.forEach(p -> {
            partitions.put(p.partitionId, p);
            QolsysIQPartitionHandler handler = partitionHandler(p.partitionId);
            if (handler != null) {
                handler.updatePartition(p);
            }
            p.zoneList.forEach(z -> {
                QolsysIQZoneHandler zoneHandler = zoneHandler(z.zoneId);
                if (zoneHandler != null) {
                    zoneHandler.updateZone(z);
                }
            });
        });
    }

    @Override
    public void secureArmInfoEvent(SecureArmInfoEvent event) {
        logger.debug("ArmingEvent {}", event.value);
        QolsysIQPartitionHandler handler = partitionHandler(event.partitionId);
        if (handler != null) {
            handler.secureArmInfoEvent(event);
        }
    }

    @Override
    public void zoneActiveEvent(ZoneActiveEvent event) {
        logger.debug("ZoneActiveEvent {} {}", event.zone.zoneId, event.zone.status);
        QolsysIQZoneHandler handler = zoneHandler(event.zone.zoneId);
        if (handler != null) {
            handler.zoneActiveEvent(event);
        }
    }

    @Override
    public void zoneUpdateEvent(ZoneUpdateEvent event) {
        logger.debug("ZoneUpdateEvent {}", event.zone.name);
        QolsysIQZoneHandler handler = zoneHandler(event.zone.zoneId);
        if (handler != null) {
            handler.zoneUpdateEvent(event);
        }
    }

    /**
     * Sends the action to the panel. This will replace the token of the action passed in with the one configured here
     *
     * @param action
     */
    protected void sendAction(Action action) {
        action.token = key;
        QolsysiqClient client = this.apiClient;
        if (client != null) {
            try {
                client.sendAction(action);
            } catch (IOException e) {
                logger.debug("Could not send action", e);
                setOfflineAndReconnect(e);
            }
        }
    }

    private void connect() {
        stopRetryFuture();
        disconnect();
        QolsysIQPanelConfiguration config = getConfigAs(QolsysIQPanelConfiguration.class);
        key = config.key;
        QolsysiqClient apiClient = new QolsysiqClient(config.hostname, config.port, config.heartbeatInterval,
                scheduler);
        try {
            apiClient.connect();
            apiClient.addListener(this);
            this.apiClient = apiClient;
            updateStatus(ThingStatus.ONLINE);
        } catch (IOException e) {
            logger.debug("Could not connect");
            setOfflineAndReconnect(e);
        }
    }

    private void disconnect() {
        logger.debug("disconnect");
        QolsysiqClient apiClient = this.apiClient;
        if (apiClient != null) {
            apiClient.removeListener(this);
            apiClient.disconnect();
        }
    }

    private void startRetryFuture() {
        logger.debug("startRetryFuture");
        stopRetryFuture();
        this.retryFuture = scheduler.schedule(this::connect, RETRY_SECONDS, TimeUnit.SECONDS);
    }

    private void stopRetryFuture() {
        logger.debug("stopRetryFuture");
        ScheduledFuture<?> retryFuture = this.retryFuture;
        if (retryFuture != null && !retryFuture.isDone()) {
            retryFuture.cancel(false);
        }
    }

    private void setOfflineAndReconnect(Exception reason) {
        logger.debug("setOfflineAndReconnect");
        ScheduledFuture<?> retryFuture = this.retryFuture;
        if (retryFuture != null && !retryFuture.isDone()) {
            disconnect();
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, reason.getMessage());
            startRetryFuture();
        }
    }

    private @Nullable QolsysIQPartitionHandler partitionHandler(int partitionId) {
        for (Thing thing : getThing().getThings()) {
            ThingHandler handler = thing.getHandler();
            if (handler != null && handler instanceof QolsysIQPartitionHandler) {
                if (((QolsysIQPartitionHandler) handler).partitionId() == partitionId) {
                    return (QolsysIQPartitionHandler) handler;
                }
            }
        }
        return null;
    }

    private @Nullable QolsysIQZoneHandler zoneHandler(int zoneId) {
        for (Thing thing : getThing().getThings()) {
            ThingHandler handler = thing.getHandler();
            if (handler != null && handler instanceof QolsysIQZoneHandler) {
                if (((QolsysIQZoneHandler) handler).zoneId() == zoneId) {
                    return (QolsysIQZoneHandler) handler;
                }
            }
        }
        return null;
    }
}
