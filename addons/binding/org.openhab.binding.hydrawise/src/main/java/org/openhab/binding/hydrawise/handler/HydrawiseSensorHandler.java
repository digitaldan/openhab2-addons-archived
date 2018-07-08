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
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.hydrawise.api.model.Sensor;
import org.openhab.binding.hydrawise.config.HydrawiseSensorConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link HydrawiseSensorHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Dan Cunningham - Initial contribution
 */
@NonNullByDefault
public class HydrawiseSensorHandler extends BaseThingHandler {

    private final Logger logger = LoggerFactory.getLogger(HydrawiseSensorHandler.class);

    public HydrawiseSensorHandler(Thing sensor) {
        super(sensor);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {

    }

    @Override
    public void initialize() {
        updateStatus(ThingStatus.ONLINE);
    }

    public void updateSensor(Sensor sensor) {
        updateState(CHANNEL_SENSOR_ACTIVE, new DecimalType(sensor.getActive()));
        updateState(CHANNEL_SENSOR_INPUT, new DecimalType(sensor.getInput()));
        updateState(CHANNEL_SENSOR_MODE, new DecimalType(sensor.getMode()));
        updateState(CHANNEL_SENSOR_OFFLEVEL, new DecimalType(sensor.getOfflevel()));
        updateState(CHANNEL_SENSOR_OFFTIMER, new DecimalType(sensor.getActive()));
        updateState(CHANNEL_SENSOR_TIMER, new DecimalType(sensor.getActive()));
    }

    public HydrawiseSensorConfiguration getConfiguration() {
        return getConfigAs(HydrawiseSensorConfiguration.class);
    }

}
