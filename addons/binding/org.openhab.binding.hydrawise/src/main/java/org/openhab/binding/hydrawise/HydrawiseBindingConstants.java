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
package org.openhab.binding.hydrawise;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.smarthome.core.thing.ThingTypeUID;

/**
 * The {@link HydrawiseBindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Dan Cunningham - Initial contribution
 */
@NonNullByDefault
public class HydrawiseBindingConstants {

    private static final String BINDING_ID = "hydrawise";
    public static final ThingTypeUID HYDRAWISE_ACCOUNT_THING_TYPE = new ThingTypeUID(BINDING_ID, "account");
    public static final ThingTypeUID HYDRAWISE_CONTROLLER_THING_TYPE = new ThingTypeUID(BINDING_ID, "controller");
    public static final ThingTypeUID HYDRAWISE_LOCAL_CONTROLLER_THING_TYPE = new ThingTypeUID(BINDING_ID,
            "localController");
    public static final ThingTypeUID HYDRAWISE_RELAY_THING_TYPE = new ThingTypeUID(BINDING_ID, "relay");
    public static final ThingTypeUID HYDRAWISE_SENSOR_THING_TYPE = new ThingTypeUID(BINDING_ID, "sensor");
    public static final Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = Stream
            .of(HYDRAWISE_ACCOUNT_THING_TYPE, HYDRAWISE_CONTROLLER_THING_TYPE, HYDRAWISE_LOCAL_CONTROLLER_THING_TYPE,
                    HYDRAWISE_RELAY_THING_TYPE, HYDRAWISE_SENSOR_THING_TYPE)
            .collect(Collectors.toSet());
    public static final Set<ThingTypeUID> SUPPORTED_RELAY_AND_SENSOR_THING_TYPES_UIDS = Stream
            .of(HYDRAWISE_RELAY_THING_TYPE, HYDRAWISE_SENSOR_THING_TYPE).collect(Collectors.toSet());
    public static final String CHANNEL_RELAY_RUN_CUSTOM = "runcustom";
    public static final String CHANNEL_RELAY_RUN = "run";
    public static final String CHANNEL_RELAY_STOP = "stop";
    public static final String CHANNEL_RELAY_SUSPEND = "suspend";
    public static final String CHANNEL_RELAY_NAME = "name";
    public static final String CHANNEL_RELAY_ICON = "icon";
    public static final String CHANNEL_RELAY_LAST_WATER = "lastwater";
    public static final String CHANNEL_RELAY_TIME = "time";
    public static final String CHANNEL_RELAY_TYPE = "type";
    public static final String CHANNEL_RELAY_NICE_TIME = "nicetime";
    public static final String CHANNEL_RELAY_TIME_LEFT = "timeLeft";
    public static final String CHANNEL_RUN_ALL_RELAYS = "runall";
    public static final String CHANNEL_STOP_ALL_RELAYS = "stopall";
    public static final String CHANNEL_SUSPEND_ALL_RELAYS = "suspendall";
    public static final String CHANNEL_SENSOR_NAME = "name";
    public static final String CHANNEL_SENSOR_INPUT = "input";
    public static final String CHANNEL_SENSOR_MODE = "mode";
    public static final String CHANNEL_SENSOR_TIMER = "timer";
    public static final String CHANNEL_SENSOR_OFFTIMER = "offtimer";
    public static final String CHANNEL_SENSOR_OFFLEVEL = "offlevel";
    public static final String CHANNEL_SENSOR_ACTIVE = "active";
}
