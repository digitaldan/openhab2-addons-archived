/**
 * Copyright (c) 2010-2024 Contributors to the openHAB project
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
package org.openhab.binding.grandstreamgds.internal;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * The {@link GrandstreamGDSConfiguration} class contains fields mapping thing configuration parameters.
 *
 * @author Dan Cunningham - Initial contribution
 */
@NonNullByDefault
public class GrandstreamGDSConfiguration {

    public String url = "";
    public String username = "admin";
    public String password = "";
    public String doorSensor = "digital_input1";
    public Boolean modifyGdsConfig = true;
    public Integer refreshSeconds = 60;
}
