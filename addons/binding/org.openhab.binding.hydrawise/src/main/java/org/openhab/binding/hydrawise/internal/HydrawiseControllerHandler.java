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

import static org.openhab.binding.hydrawise.HydrawiseBindingConstants.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.binding.BaseBridgeHandler;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.hydrawise.internal.api.HydrawiseApiClient;
import org.openhab.binding.hydrawise.internal.api.HydrawiseAuthenticationException;
import org.openhab.binding.hydrawise.internal.api.HydrawiseCommandException;
import org.openhab.binding.hydrawise.internal.api.HydrawiseConnectionException;
import org.openhab.binding.hydrawise.internal.model.StatusScheduleResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link HydrawiseControllerHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Dan Cunningham - Initial contribution
 */
@NonNullByDefault
public class HydrawiseControllerHandler extends BaseBridgeHandler {

    private final Logger logger = LoggerFactory.getLogger(HydrawiseControllerHandler.class);

    @Nullable
    private HydrawiseControllerConfiguration config;

    private Map<Integer, Thing> relays = Collections.synchronizedMap(new HashMap<Integer, Thing>());
    private Map<Integer, Thing> sensors = Collections.synchronizedMap(new HashMap<Integer, Thing>());

    public HydrawiseControllerHandler(Bridge bridge) {
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
        } catch (HydrawiseConnectionException | HydrawiseAuthenticationException e) {
            logger.error("Could not connect to Hydrawise API server", e);
            Bridge bridge = getBridge();
            if (bridge != null) {
                ((HydrawiseAccountHandler) bridge).setErrorAndReconnect(e.getMessage());
            }
        } catch (HydrawiseCommandException e) {
            logger.warn("Trouble executing command {} for channel {} : {}", command.toFullString(),
                    channelUID.getAsString(), e.getMessage());
        }
    }

    @Override
    public void initialize() {
        config = getConfigAs(HydrawiseControllerConfiguration.class);
        updateStatus(ThingStatus.ONLINE);
    }

    @Nullable
    public HydrawiseApiClient getApiClient() {
        Bridge bridge = getBridge();
        if (bridge != null) {
            return ((HydrawiseAccountHandler) bridge).getApiClient();
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

    public void updateData(StatusScheduleResponse response) {

    }
}
