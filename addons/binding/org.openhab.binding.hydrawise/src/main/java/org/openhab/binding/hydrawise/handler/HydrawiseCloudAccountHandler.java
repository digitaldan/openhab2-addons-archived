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
import java.util.Map;
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
import org.openhab.binding.hydrawise.api.HydrawiseCloudApiClient;
import org.openhab.binding.hydrawise.api.HydrawiseAuthenticationException;
import org.openhab.binding.hydrawise.api.HydrawiseConnectionException;
import org.openhab.binding.hydrawise.api.model.CustomerDetailsResponse;
import org.openhab.binding.hydrawise.config.HydrawiseAccountConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link HydrawiseHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Dan Cunningham - Initial contribution
 */
@NonNullByDefault
public class HydrawiseCloudAccountHandler extends BaseBridgeHandler {

    private final Logger logger = LoggerFactory.getLogger(HydrawiseCloudAccountHandler.class);
    private Map<Integer, HydrawiseCloudControllerHandler> controllers = Collections
            .synchronizedMap(new HashMap<Integer, HydrawiseCloudControllerHandler>());
    @Nullable
    private HydrawiseCloudApiClient apiClient;
    @Nullable
    private HydrawiseAccountConfiguration config;

    public HydrawiseCloudAccountHandler(Bridge bridge) {
        super(bridge);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {

    }

    @Override
    public void initialize() {
        config = getConfigAs(HydrawiseAccountConfiguration.class);
        apiClient = new HydrawiseCloudApiClient(config.apiKey);
        scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    CustomerDetailsResponse response = apiClient.getCustomerDetails();
                    updateAccount(response);
                    updateStatus(ThingStatus.ONLINE);
                } catch (HydrawiseConnectionException | HydrawiseAuthenticationException e) {
                    logger.error("Could not connect to service", e);
                    updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, e.getMessage());
                }

            }
        }, 0, TimeUnit.SECONDS);
    }

    @Override
    public void childHandlerInitialized(ThingHandler childHandler, Thing childThing) {
        if (childHandler instanceof HydrawiseCloudControllerHandler) {
            HydrawiseCloudControllerHandler handler = (HydrawiseCloudControllerHandler) childHandler;
            controllers.put(handler.getConfiguration().controllerId, handler);
        }
    }

    @Override
    public void childHandlerDisposed(ThingHandler childHandler, Thing childThing) {
        if (childHandler instanceof HydrawiseCloudControllerHandler) {
            HydrawiseCloudControllerHandler handler = (HydrawiseCloudControllerHandler) childHandler;
            controllers.remove(handler.getConfiguration().controllerId);
        }

    }

    @Override
    public void dispose() {
        if (apiClient != null) {
            apiClient.stopClient();
        }
    }

    @Nullable
    public HydrawiseCloudApiClient getApiClient() {
        return apiClient;
    }

    @Nullable
    public HydrawiseCloudControllerHandler getControllerThing(int id) {
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
}
