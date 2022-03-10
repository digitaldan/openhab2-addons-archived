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
package org.openhab.binding.lutronvive.internal.api.request;

import com.google.gson.annotations.SerializedName;

//{"commandUrl":"/zonetypegroup/14/commandprocessor","commandType":"GoToDimmedLevel","level":100}
/**
 *
 * @author Dan Cunningham - Initial contribution
 */
public class ExecuteCommand {

    String commandUrl;
    CommandType commandType;
    Integer level;

    public ExecuteCommand(String commandUrl, CommandType commandType, Integer level) {
        super();
        this.commandUrl = commandUrl;
        this.commandType = commandType;
        this.level = level;
    }

    public ExecuteCommand(String commandUrl, CommandType commandType) {
        super();
        this.commandUrl = commandUrl;
        this.commandType = commandType;
    }

    public enum CommandType {
        @SerializedName("Stop")
        STOP,
        @SerializedName("Lower")
        LOWER,
        @SerializedName("Raise")
        RAISE,
        @SerializedName("GoToDimmedLevel")
        GOTODIMMEDLEVEL;

    }
}
