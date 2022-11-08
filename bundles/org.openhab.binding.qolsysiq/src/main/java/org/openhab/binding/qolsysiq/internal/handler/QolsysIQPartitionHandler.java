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
package org.openhab.binding.qolsysiq.internal.handler;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.qolsysiq.internal.QolsysIQBindingConstants;
import org.openhab.binding.qolsysiq.internal.client.dto.action.AlarmAction;
import org.openhab.binding.qolsysiq.internal.client.dto.action.AlarmActionType;
import org.openhab.binding.qolsysiq.internal.client.dto.action.ArmingAction;
import org.openhab.binding.qolsysiq.internal.client.dto.action.ArmingActionType;
import org.openhab.binding.qolsysiq.internal.client.dto.event.AlarmEvent;
import org.openhab.binding.qolsysiq.internal.client.dto.event.ArmingEvent;
import org.openhab.binding.qolsysiq.internal.client.dto.event.ErrorEvent;
import org.openhab.binding.qolsysiq.internal.client.dto.event.SecureArmInfoEvent;
import org.openhab.binding.qolsysiq.internal.client.dto.model.AlarmType;
import org.openhab.binding.qolsysiq.internal.client.dto.model.Partition;
import org.openhab.binding.qolsysiq.internal.client.dto.model.PartitionStatus;
import org.openhab.binding.qolsysiq.internal.client.dto.model.Zone;
import org.openhab.binding.qolsysiq.internal.config.QolsysIQPartitionConfiguration;
import org.openhab.binding.qolsysiq.internal.discovery.QolsysIQChildDiscoveryService;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.StringType;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusInfo;
import org.openhab.core.thing.ThingUID;
import org.openhab.core.thing.binding.BaseBridgeHandler;
import org.openhab.core.thing.binding.BridgeHandler;
import org.openhab.core.thing.binding.ThingHandler;
import org.openhab.core.thing.binding.ThingHandlerService;
import org.openhab.core.types.Command;
import org.openhab.core.types.RefreshType;
import org.openhab.core.types.UnDefType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Dan Cunningham - Initial contribution
 */
@NonNullByDefault
public class QolsysIQPartitionHandler extends BaseBridgeHandler implements QolsysIQChildDiscoveryHandler {
    private final Logger logger = LoggerFactory.getLogger(QolsysIQPartitionHandler.class);
    private List<Zone> zones = Collections.synchronizedList(new LinkedList<Zone>());
    private @Nullable QolsysIQChildDiscoveryService discoveryService;
    private @Nullable ScheduledFuture<?> delayFuture;
    private @Nullable Partition partitionCache;
    private int partitionId;

    public QolsysIQPartitionHandler(Bridge bridge) {
        super(bridge);
    }

    @Override
    public void initialize() {
        logger.debug("initialize");
        partitionId = getConfigAs(QolsysIQPartitionConfiguration.class).id;
        logger.debug("initialize partition {}", partitionId);
        refresh();
    }

    @Override
    public void dispose() {
        cancelExitDelayJob();
    }

    @Override
    public void bridgeStatusChanged(ThingStatusInfo bridgeStatusInfo) {
        super.bridgeStatusChanged(bridgeStatusInfo);
        if (bridgeStatusInfo.getStatus() == ThingStatus.OFFLINE) {
            cancelExitDelayJob();
        }
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (command instanceof RefreshType || "REFRESH".equals(command.toString())) {
            refresh();
            return;
        }

        QolsysIQPanelHandler panel = panelHandler();
        if (panel != null) {
            if (channelUID.getId().equals(QolsysIQBindingConstants.CHANNEL_PARTITION_ALARM_STATE)) {
                panel.sendAction(new AlarmAction(AlarmActionType.valueOf(command.toString()), ""));
                return;
            }

            ArmingActionType armingType = null;
            String code = null;

            if (channelUID.getId().equals(QolsysIQBindingConstants.CHANNEL_PARTITION_COMMAND_DISARM)) {
                armingType = ArmingActionType.DISARM;
                code = command.toString();
                // clear the channel as it holds no state
                updateState(QolsysIQBindingConstants.CHANNEL_PARTITION_COMMAND_DISARM, UnDefType.UNDEF);
            } else if (channelUID.getId().equals(QolsysIQBindingConstants.CHANNEL_PARTITION_STATUS)) {
                String armingTypeName = command.toString();
                if (armingTypeName.contains(":")) {
                    String[] split = armingTypeName.split(":");
                    armingTypeName = split[0];
                    if (split.length > 1) {
                        code = split[1];
                    }
                }
                armingType = ArmingActionType.valueOf(armingTypeName);

                // reset channel to last known state until we get an update from the panel
                Partition partitionCache = this.partitionCache;
                if (partitionCache != null) {
                    updateState(channelUID, new StringType(partitionCache.status.toString()));
                }
            }

            if (armingType != null) {
                panel.sendAction(new ArmingAction(armingType, "", partitionId(), code));
            } else {
                logger.debug("Unknown arm command {} to channel {}", command, channelUID);
            }
        }
    }

    @Override
    public Collection<Class<? extends ThingHandlerService>> getServices() {
        return Collections.singleton(QolsysIQChildDiscoveryService.class);
    }

    @Override
    public void setDiscoveryService(QolsysIQChildDiscoveryService service) {
        this.discoveryService = service;
    }

    @Override
    public void startDiscovery() {
        refresh();
    }

    public void discoverChildDevices() {
        synchronized (zones) {
            zones.forEach(z -> {
                QolsysIQChildDiscoveryService discoveryService = this.discoveryService;
                if (discoveryService != null) {
                    ThingUID bridgeUID = getThing().getUID();
                    ThingUID thingUID = new ThingUID(QolsysIQBindingConstants.THING_TYPE_ZONE, bridgeUID,
                            String.valueOf(z.zoneId));
                    discoveryService.discoverQolsysIQChildThing(thingUID, bridgeUID, z.zoneId,
                            "Qolsys IQ Zone: " + z.name);
                }
            });
        }
    }

    public int partitionId() {
        return partitionId;
    }

    protected void alarmEvent(AlarmEvent event) {
        updatePartitionStatus(PartitionStatus.ALARM);
        updateState(QolsysIQBindingConstants.CHANNEL_PARTITION_ALARM_STATE, new StringType(event.alarmType.toString()));
    }

    protected void armingEvent(ArmingEvent event) {
        updatePartitionStatus(event.armingType);
        updateDelay(event.delay == null ? 0 : event.delay);
        Partition partitionCache = this.partitionCache;
        if (partitionCache != null) {
            partitionCache.status = event.armingType;
        }
    }

    protected void errorEvent(ErrorEvent event) {
    }

    protected void secureArmInfoEvent(SecureArmInfoEvent event) {
        setSecureArm(event.value);
    }

    protected void updatePartition(Partition partition) {
        if (getThing().getStatus() != ThingStatus.ONLINE) {
            updateStatus(ThingStatus.ONLINE);
        }
        this.partitionCache = partition;
        updatePartitionStatus(partition.status);
        setSecureArm(partition.secureArm);
        synchronized (zones) {
            zones.clear();
            zones.addAll(partition.zoneList);
            zones.forEach(z -> {
                QolsysIQZoneHandler zoneHandler = zoneHandler(z.zoneId);
                if (zoneHandler != null) {
                    zoneHandler.updateZone(z);
                }
            });
        }
        discoverChildDevices();
    }

    protected @Nullable Zone getZone(Integer zoneId) {
        synchronized (zones) {
            return zones.stream().filter(z -> z.zoneId.equals(zoneId)).findAny().orElse(null);
        }
    }

    private void refresh() {
        QolsysIQPanelHandler panel = panelHandler();
        if (panel != null) {
            panel.refresh();
        }
    }

    private void updatePartitionStatus(PartitionStatus status) {
        updateState(QolsysIQBindingConstants.CHANNEL_PARTITION_STATUS, new StringType(status.toString()));
        if (status == PartitionStatus.DISARM) {
            updateState(QolsysIQBindingConstants.CHANNEL_PARTITION_ALARM_STATE,
                    new StringType(AlarmType.NONE.toString()));
            updateDelay(0);
        }
    }

    private void setSecureArm(Boolean secure) {
        Map<String, String> props = new HashMap<String, String>();
        props.put("secureArm", String.valueOf(secure));
        getThing().setProperties(props);
    }

    private void updateDelay(Integer delay) {
        logger.debug("updateDelay {}", delay);
        cancelExitDelayJob();
        if (delay <= 0) {
            updateState(QolsysIQBindingConstants.CHANNEL_PARTITION_COMMAND_DELAY, new DecimalType(0));
            return;
        }
        final long startTime = System.currentTimeMillis();
        final long endTime = startTime + (delay * 1000);
        delayFuture = scheduler.scheduleAtFixedRate(() -> {
            long remaining = endTime - System.currentTimeMillis();
            logger.debug("updateDelay remaining {}", remaining / 1000);
            if (remaining <= 0) {
                cancelExitDelayJob();
            } else {
                updateState(QolsysIQBindingConstants.CHANNEL_PARTITION_COMMAND_DELAY,
                        new DecimalType(remaining / 1000));
            }
        }, 1, 1, TimeUnit.SECONDS);
    }

    private void cancelExitDelayJob() {
        ScheduledFuture<?> delayFuture = this.delayFuture;
        if (delayFuture != null && !delayFuture.isDone()) {
            delayFuture.cancel(false);
        }
        updateState(QolsysIQBindingConstants.CHANNEL_PARTITION_COMMAND_DELAY, new DecimalType(0));
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

    private @Nullable QolsysIQPanelHandler panelHandler() {
        Bridge bridge = getBridge();
        if (bridge != null) {
            BridgeHandler handler = bridge.getHandler();
            if (handler != null && handler instanceof QolsysIQPanelHandler) {
                return (QolsysIQPanelHandler) handler;
            }
        }
        return null;
    }
}
