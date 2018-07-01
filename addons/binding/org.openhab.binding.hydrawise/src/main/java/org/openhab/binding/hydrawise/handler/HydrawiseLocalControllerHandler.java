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
import org.openhab.binding.hydrawise.api.HydrawiseCommandException;
import org.openhab.binding.hydrawise.api.HydrawiseConnectionException;
import org.openhab.binding.hydrawise.api.HydrawiseLocalApiClient;
import org.openhab.binding.hydrawise.api.model.LocalScheduleResponse;
import org.openhab.binding.hydrawise.api.model.Running;
import org.openhab.binding.hydrawise.config.HydrawiseLocalControllerConfiguration;
import org.openhab.binding.hydrawise.config.HydrawiseRelayConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link HydrawiseLocalControllerHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Dan Cunningham - Initial contribution
 */
@NonNullByDefault
public class HydrawiseLocalControllerHandler extends BaseBridgeHandler implements HydrawiseRelayControl {

    private final Logger logger = LoggerFactory.getLogger(HydrawiseLocalControllerHandler.class);

    @Nullable
    private HydrawiseLocalControllerConfiguration config;
    @Nullable
    private ScheduledFuture<?> pollingJob;
    @Nullable
    LocalScheduleResponse localScheduleResponse;

    private static int POLL_FREQUENCY = 15;
    private static int DELAY_POLL = 5;
    private Map<Integer, Thing> relays = Collections.synchronizedMap(new HashMap<Integer, Thing>());
    @Nullable
    private HydrawiseLocalApiClient apiClient;

    public HydrawiseLocalControllerHandler(Bridge bridge) {
        super(bridge);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {

    }

    @SuppressWarnings("null")
    @Override
    public void initialize() {
        config = getConfigAs(HydrawiseLocalControllerConfiguration.class);
        apiClient = new HydrawiseLocalApiClient(config.host, config.username, config.password);
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
        }

    }

    @Nullable
    public Thing getRelayThing(int id) {
        return relays.get(new Integer(id));
    }

    private void updateStatusSchedule() {
        try {
            localScheduleResponse = apiClient.getLocalSchedule();
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
        if (localScheduleResponse == null) {
            return;
        }
        logger.trace("updateData for {}", localScheduleResponse.getName());
        relays.values().forEach(child -> {
            HydrawiseRelayHandler relayHandler = (HydrawiseRelayHandler) child.getHandler();
            if (relayHandler == null) {
                return;
            }
            HydrawiseRelayConfiguration relayConfig = relayHandler.getConfiguration();
            localScheduleResponse.getRelays().forEach(relay -> {
                if (relayConfig != null && relay.getRelayId().equals(relayConfig.relayId)) {
                    logger.trace("updateRelay for {}", relay.getName());
                    relayHandler.updateRelay(relay);
                }
            });
            Running runningRelay = null;
            List<Running> runningRelays = localScheduleResponse.getRunning();
            if (runningRelays != null) {
                for (Running running : localScheduleResponse.getRunning()) {
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
            apiClient.runZone(config.number);
            startPolling(DELAY_POLL);
        } catch (HydrawiseConnectionException | HydrawiseAuthenticationException | HydrawiseCommandException e) {
            logger.warn("Could not run relay", e);
        }
    }

    @Override
    public void runRelay(int duration, HydrawiseRelayConfiguration config) {
        try {
            apiClient.runZone(duration, config.number);
            startPolling(DELAY_POLL);
        } catch (HydrawiseConnectionException | HydrawiseAuthenticationException | HydrawiseCommandException e) {
            logger.warn("Could not run relay", e);
        }
    }

    @Override
    public void stopRelay(HydrawiseRelayConfiguration config) {
        try {
            apiClient.stopZone(config.number);
            startPolling(DELAY_POLL);
        } catch (HydrawiseConnectionException | HydrawiseAuthenticationException | HydrawiseCommandException e) {
            logger.warn("Could not stop relay", e);
        }
    }

    @Nullable
    public HydrawiseLocalApiClient getApiClient() {
        return apiClient;
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
