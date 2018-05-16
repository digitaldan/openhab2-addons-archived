/**
 * Copyright (c) 2014,2018 by the respective copyright holders.
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.hydrawise.handler;

import static org.openhab.binding.hydrawise.HydrawiseBindingConstants.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseBridgeHandler;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.hydrawise.api.HydrawiseAuthenticationException;
import org.openhab.binding.hydrawise.api.HydrawiseCloudApiClient;
import org.openhab.binding.hydrawise.api.HydrawiseCommandException;
import org.openhab.binding.hydrawise.api.HydrawiseConnectionException;
import org.openhab.binding.hydrawise.api.model.Running;
import org.openhab.binding.hydrawise.api.model.StatusScheduleResponse;
import org.openhab.binding.hydrawise.config.HydrawiseControllerConfiguration;
import org.openhab.binding.hydrawise.config.HydrawiseRelayConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link HydrawiseCloudControllerHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Dan Cunningham - Initial contribution
 */
@NonNullByDefault
public class HydrawiseCloudControllerHandler extends BaseBridgeHandler implements HydrawiseRelayControl {

    private final Logger logger = LoggerFactory.getLogger(HydrawiseCloudControllerHandler.class);

    @Nullable
    private HydrawiseControllerConfiguration config;
    @Nullable
    private ScheduledFuture<?> pollingJob;
    @Nullable
    StatusScheduleResponse statusScheduleResponse;

    private static int POLL_FREQUENCY = 30;
    private static int DELAY_POLL = 15;
    private Map<Integer, Thing> relays = Collections.synchronizedMap(new HashMap<Integer, Thing>());
    private Map<Integer, Thing> sensors = Collections.synchronizedMap(new HashMap<Integer, Thing>());

    public HydrawiseCloudControllerHandler(Bridge bridge) {
        super(bridge);
    }

    @SuppressWarnings("null")
    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (getApiClient() == null || config == null) {
            logger.warn("Cannot execute command {} for channel {}, binding not configured", command.toFullString(),
                    channelUID.getAsString());
            return;
        }
        try {
            switch (channelUID.getId()) {
                case CHANNEL_RUN_ALL_RELAYS:
                    getApiClient().runAllRelays(0, config.controllerId);
                    break;
                case CHANNEL_STOP_ALL_RELAYS:
                    getApiClient().stopAllRelays(config.controllerId);
                    break;
                case CHANNEL_SUSPEND_ALL_RELAYS:
                    getApiClient().suspendAllRelays(0, config.controllerId);
                    break;
            }
            startPolling(DELAY_POLL);
        } catch (HydrawiseConnectionException | HydrawiseAuthenticationException e) {
            logger.error("Could not connect to Hydrawise API server", e);
            Bridge bridge = getBridge();
            if (bridge != null) {
                ((HydrawiseCloudAccountHandler) bridge).setErrorAndReconnect(e.getMessage());
            }
        } catch (HydrawiseCommandException e) {
            logger.warn("Trouble executing command {} for channel {} : {}", command.toFullString(),
                    channelUID.getAsString(), e.getMessage());
        }
    }

    @Override
    public void initialize() {
        config = getConfigAs(HydrawiseControllerConfiguration.class);
        startPolling(0);
    }

    @Override
    public void dispose() {
        stopPolling();
    }

    @Override
    public void childHandlerInitialized(ThingHandler childHandler, Thing childThing) {
        if (childHandler instanceof HydrawiseRelayHandler) {
            HydrawiseRelayHandler handler = (HydrawiseRelayHandler) childHandler;
            relays.put(handler.getConfiguration().relayId, childThing);
            refreshData();
        }
    }

    @Override
    public void childHandlerDisposed(ThingHandler childHandler, Thing childThing) {
        if (childHandler instanceof HydrawiseRelayHandler) {
            HydrawiseRelayHandler handler = (HydrawiseRelayHandler) childHandler;
            relays.remove(handler.getConfiguration().relayId);
            refreshData();
        }

    }

    @Nullable
    public HydrawiseCloudApiClient getApiClient() {
        Bridge bridge = getBridge();
        if (bridge != null && (bridge.getHandler() instanceof HydrawiseCloudAccountHandler)) {
            return ((HydrawiseCloudAccountHandler) bridge.getHandler()).getApiClient();
        } else {
            return null;
        }
    }

    @Nullable
    public Thing getRelayThing(int id) {
        return relays.get(new Integer(id));
    }

    @Nullable
    public Thing getSensorThing(int id) {
        return sensors.get(new Integer(id));
    }

    @Nullable
    public HydrawiseControllerConfiguration getConfiguration() {
        return config;
    }

    private void updateStatusSchedule() {
        try {
            statusScheduleResponse = getApiClient().getStatusSchedule(config.controllerId);
            if (getThing().getStatus() != ThingStatus.ONLINE) {
                updateStatus(ThingStatus.ONLINE);
            }
        } catch (HydrawiseConnectionException e) {
            logger.warn("Could not connect to service", e);
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, e.getMessage());
        } catch (HydrawiseAuthenticationException e) {
            logger.error("Invalid Credentials", e);
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, "Invalid Credentials");
        }
        refreshData();
    }

    public void refreshData() {
        if (statusScheduleResponse == null) {
            return;
        }
        logger.trace("updateData for {}", statusScheduleResponse.getName());
        relays.values().forEach(child -> {
            HydrawiseRelayHandler relayHandler = (HydrawiseRelayHandler) child.getHandler();
            statusScheduleResponse.getRelays().forEach(relay -> {
                if (relay.getRelayId().equals(relayHandler.getConfiguration().relayId)) {
                    logger.trace("updateRelay for {}", relay.getName());
                    relayHandler.updateRelay(relay);
                }
            });
            Running runningRelay = null;
            List<Running> runningRelays = statusScheduleResponse.getRunning();
            if (runningRelays != null) {
                for (Running running : statusScheduleResponse.getRunning()) {
                    if (running.getRelayId().equals(relayHandler.getConfiguration().relayId)) {
                        runningRelay = running;
                        break;
                    }
                }
            }
            relayHandler.setRelayRunning(runningRelay);
        });
    }

    @Override
    public void runRelay(HydrawiseRelayConfiguration config) {
        try {
            getApiClient().runZone(config.relayId);
            startPolling(DELAY_POLL);
        } catch (HydrawiseConnectionException | HydrawiseAuthenticationException | HydrawiseCommandException e) {
            logger.warn("Could not run relay", e);
        }
    }

    @Override
    public void runRelay(int duration, HydrawiseRelayConfiguration config) {
        try {
            getApiClient().runZone(duration, config.relayId);
            startPolling(DELAY_POLL);
        } catch (HydrawiseConnectionException | HydrawiseAuthenticationException | HydrawiseCommandException e) {
            logger.warn("Could not run relay", e);
        }
    }

    @Override
    public void stopRelay(HydrawiseRelayConfiguration config) {
        try {
            getApiClient().stopZone(config.relayId);
            startPolling(DELAY_POLL);
        } catch (HydrawiseConnectionException | HydrawiseAuthenticationException | HydrawiseCommandException e) {
            logger.warn("Could not stop relay", e);
        }
    }

    private synchronized void startPolling(int delay) {
        stopPolling();
        if (pollingJob == null || pollingJob.isCancelled()) {
            pollingJob = scheduler.scheduleWithFixedDelay(() -> {
                updateStatusSchedule();
            }, delay, POLL_FREQUENCY, TimeUnit.SECONDS);
        }
    }

    private void stopPolling() {
        if (pollingJob != null && !pollingJob.isCancelled()) {
            pollingJob.cancel(true);
            pollingJob = null;
        }
    }

}
