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
package org.openhab.binding.lutronvive.internal;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.core.thing.ThingTypeUID;

/**
 * The {@link LutronViveBindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Dan Cunningham - Initial contribution
 */
@NonNullByDefault
public class LutronViveBindingConstants {

    public static final String BINDING_ID = "lutronvive";

    // List of all Thing Type UIDs
    public static final ThingTypeUID THING_TYPE_HUB = new ThingTypeUID(BINDING_ID, "hub");
    public static final ThingTypeUID THING_TYPE_AREA = new ThingTypeUID(BINDING_ID, "area");

    // List of all Channel ids
    public static final String CHANNEL_SWITCH = "switch";
    public static final String CHANNEL_DIMMER = "dimmer";
    public static final String CHANNEL_OCCUPANCY = "occupancy";
}
