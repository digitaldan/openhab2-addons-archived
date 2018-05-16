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

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.openhab.binding.hydrawise.api.model.Relay;
import org.openhab.binding.hydrawise.api.model.Running;
import org.openhab.binding.hydrawise.config.HydrawiseRelayConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link HydrawiseRelayHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Dan Cunningham - Initial contribution
 */
@NonNullByDefault
public class HydrawiseRelayHandler extends BaseThingHandler {

    private final Logger logger = LoggerFactory.getLogger(HydrawiseRelayHandler.class);

    private HydrawiseRelayConfiguration config;

    public HydrawiseRelayHandler(Thing zone) {
        super(zone);
        config = getConfigAs(HydrawiseRelayConfiguration.class);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        logger.debug("relay {} handleCommand {} for channel {}", config.relayId, channelUID.getAsString(), command);
        HydrawiseRelayControl controller = getController();
        if (controller == null) {
            logger.warn("Aborting command {}, no controller", command);
            return;
        }

        /*
         * Refreshes will happen by the bridge
         */
        if (command == RefreshType.REFRESH) {
            return;
        }

        switch (channelUID.getId()) {
            case CHANNEL_RELAY_RUN_CUSTOM:
                if (!(command instanceof DecimalType)) {
                    logger.warn("Invalid command type for run custom{}", command.getClass().getName());
                    return;
                }
                controller.runRelay(((DecimalType) command).intValue(), config);
                break;
            case CHANNEL_RELAY_RUN:
                if (!(command instanceof OnOffType)) {
                    logger.warn("Invalid command type for run {}", command.getClass().getName());
                    return;
                }
                if (command == OnOffType.ON) {
                    controller.runRelay(config);
                } else {
                    controller.stopRelay(config);
                }
                break;
        }
    }

    @Override
    public void initialize() {
        config = getConfigAs(HydrawiseRelayConfiguration.class);
        logger.debug("initialized with  id {}", config.relayId);
        updateStatus(ThingStatus.ONLINE);
    }

    @Nullable
    public HydrawiseRelayConfiguration getConfiguration() {
        return config;
    }

    public void updateRelay(Relay relay) {
        if (relay.getRelayId().intValue() != config.relayId.intValue()) {
            logger.warn("Will not update me, relay {} , with new config for relay {}", config.relayId,
                    relay.getRelayId());
        }
        updateState(CHANNEL_RELAY_ICON, new StringType(relay.getIcon()));
        updateState(CHANNEL_RELAY_TIME, new DecimalType(relay.getRunTimeSeconds()));

    }

    public void setRelayRunning(@Nullable Running running) {
        if (running == null) {
            updateState(CHANNEL_RELAY_RUN, OnOffType.OFF);
            updateState(CHANNEL_RELAY_TIME_LEFT, new DecimalType(0));
        } else {
            updateState(CHANNEL_RELAY_RUN, OnOffType.ON);
            updateState(CHANNEL_RELAY_TIME_LEFT, new DecimalType(running.getTimeLeft()));
        }
    }

    @Nullable
    private HydrawiseRelayControl getController() {
        Bridge bridge = getBridge();
        if (bridge != null && (bridge.getHandler() instanceof HydrawiseRelayControl)) {
            return (HydrawiseRelayControl) bridge.getHandler();
        } else {
            return null;
        }
    }
}
