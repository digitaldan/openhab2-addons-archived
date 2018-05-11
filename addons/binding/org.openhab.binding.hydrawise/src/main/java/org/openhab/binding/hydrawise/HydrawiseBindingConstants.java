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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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
    public static final ThingTypeUID HYDRAWISE_RELAY_THING_TYPE = new ThingTypeUID(BINDING_ID, "relay");
    public static final ThingTypeUID HYDRAWISE_SENSOR_THING_TYPE = new ThingTypeUID(BINDING_ID, "sensor");

    public static final Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = new HashSet<ThingTypeUID>(
            Arrays.asList(HYDRAWISE_ACCOUNT_THING_TYPE, HYDRAWISE_CONTROLLER_THING_TYPE, HYDRAWISE_RELAY_THING_TYPE,
                    HYDRAWISE_SENSOR_THING_TYPE));
    public static final Set<ThingTypeUID> SUPPORTED_RELAY_AND_SENSOR_THING_TYPES_UIDS = new HashSet<ThingTypeUID>(
            Arrays.asList(HYDRAWISE_RELAY_THING_TYPE, HYDRAWISE_SENSOR_THING_TYPE));

    public static final String CHANNEL_RUN_RELAY = "run";
    public static final String CHANNEL_STOP_RELAY = "stop";
    public static final String CHANNEL_SUSPEND_RELAY = "suspend";

    public static final String CHANNEL_RUN_ALL_RELAYS = "runall";
    public static final String CHANNEL_STOP_ALL_RELAYS = "stopall";
    public static final String CHANNEL_SUSPEND_ALL_RELAYS = "suspendall";

}
