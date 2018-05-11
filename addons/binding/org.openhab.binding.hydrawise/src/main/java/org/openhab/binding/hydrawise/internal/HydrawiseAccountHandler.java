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
package org.openhab.binding.hydrawise.internal;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.Channel;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseBridgeHandler;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.hydrawise.internal.api.HydrawiseApiClient;
import org.openhab.binding.hydrawise.internal.api.HydrawiseAuthenticationException;
import org.openhab.binding.hydrawise.internal.api.HydrawiseConnectionException;
import org.openhab.binding.hydrawise.internal.config.HydrawiseAccountConfiguration;
import org.openhab.binding.hydrawise.internal.model.CustomerDetailsResponse;
import org.openhab.binding.hydrawise.internal.model.StatusScheduleResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link HydrawiseHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Dan Cunningham - Initial contribution
 */
@NonNullByDefault
public class HydrawiseAccountHandler extends BaseBridgeHandler {

    private final Logger logger = LoggerFactory.getLogger(HydrawiseAccountHandler.class);
    private Map<Integer, HydrawiseControllerHandler> controllers = Collections
            .synchronizedMap(new HashMap<Integer, HydrawiseControllerHandler>());
    @Nullable
    private HydrawiseApiClient apiClient;
    @Nullable
    private ScheduledFuture<?> pollingJob;
    @Nullable
    private HydrawiseAccountConfiguration config;

    public HydrawiseAccountHandler(Bridge bridge) {
        super(bridge);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {

    }

    @Override
    public void initialize() {
        config = getConfigAs(HydrawiseAccountConfiguration.class);
        apiClient = new HydrawiseApiClient(config.apiKey);
        scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    CustomerDetailsResponse response = apiClient.getCustomerDetails();
                    updateAccount(response);
                    updateStatus(ThingStatus.ONLINE);
                    startPolling();
                } catch (HydrawiseConnectionException | HydrawiseAuthenticationException e) {
                    logger.error("Could not connect to service", e);
                    updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, e.getMessage());
                }

            }
        }, 0, TimeUnit.SECONDS);
    }

    @Override
    public void childHandlerInitialized(ThingHandler childHandler, Thing childThing) {
        if (childHandler instanceof HydrawiseControllerHandler) {
            HydrawiseControllerHandler handler = (HydrawiseControllerHandler) childHandler;
            controllers.put(handler.getConfiguration().controllerId, handler);
        }
    }

    @Override
    public void childHandlerDisposed(ThingHandler childHandler, Thing childThing) {
        if (childHandler instanceof HydrawiseControllerHandler) {
            HydrawiseControllerHandler handler = (HydrawiseControllerHandler) childHandler;
            controllers.remove(handler.getConfiguration().controllerId);
        }

    }

    @Override
    public void dispose() {
        stopPolling();

        if (apiClient != null) {
            apiClient.stopClient();
        }
    }

    @Nullable
    public HydrawiseApiClient getApiClient() {
        return apiClient;
    }

    @Nullable
    public HydrawiseControllerHandler getControllerThing(int id) {
        return controllers.get(new Integer(id));
    }

    public void setErrorAndReconnect(String error) {
        updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, error);
        // reconnect
    }

    private void updateAccount(CustomerDetailsResponse response) {
        for (Channel channel : getThing().getChannels()) {

        }
    }

    private void refreshData() {
        if (apiClient == null) {
            return;
        }
        for (HydrawiseControllerHandler controller : controllers.values()) {
            try {
                Integer id = controller.getConfiguration().controllerId;
                StatusScheduleResponse response = apiClient.getStatusSchedule(id.intValue());
                controller.updateData(response);
                if (getThing().getStatus() != ThingStatus.ONLINE) {
                    updateStatus(ThingStatus.ONLINE);
                }
            } catch (HydrawiseConnectionException | HydrawiseAuthenticationException e) {
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, e.getMessage());
                return;
            }
        }
    }

    private synchronized void startPolling() {
        if (pollingJob == null || pollingJob.isCancelled()) {
            pollingJob = scheduler.scheduleWithFixedDelay(() -> {
                refreshData();
            }, 0, config.refresh, TimeUnit.SECONDS);
        }
    }

    private void stopPolling() {
        if (pollingJob != null && !pollingJob.isCancelled()) {
            pollingJob.cancel(true);
            pollingJob = null;
        }
    }
}
