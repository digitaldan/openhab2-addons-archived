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
package org.openhab.binding.matter.internal.client.dto.cluster;

import java.util.Collections;
import java.util.Map;

/**
 * The {@link ClusterCommand}
 *
 * @author Dan Cunningham - Initial contribution
 */
public class ClusterCommand {
    public String commandName;
    public Map<String, Object> args;

    /**
     * @param commandName
     * @param options
     */
    public ClusterCommand(String commandName, Map<String, Object> args) {
        super();
        this.commandName = commandName;
        this.args = args;
    }

    public ClusterCommand(String commandName) {
        super();
        this.commandName = commandName;
        this.args = Collections.emptyMap();
    }
}
