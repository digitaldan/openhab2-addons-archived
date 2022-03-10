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
package org.openhab.binding.lutronvive.internal.api.response;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 *
 * @author Dan Cunningham - Initial contribution
 */
public class Device extends Reference {

    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("serialNumber")
    @Expose
    public Integer serialNumber;
    @SerializedName("deviceType")
    @Expose
    public String deviceType;
    @SerializedName("modelNumber")
    @Expose
    public String modelNumber;
    @SerializedName("associatedArea")
    @Expose
    public Reference associatedArea;
    @SerializedName("occupancySensors")
    @Expose
    public List<Reference> occupancySensors = null;
    @SerializedName("linkNodes")
    @Expose
    public List<Reference> linkNodes = null;
    @SerializedName("deviceRules")
    @Expose
    public List<Reference> deviceRules = null;
    @SerializedName("localZones")
    @Expose
    public List<Reference> localZones = null;
    @SerializedName("daylightCalibrationType")
    @Expose
    public String daylightCalibrationType;
    @SerializedName("daylightingSensors")
    @Expose
    public List<Reference> daylightingSensors = null;
    @SerializedName("buttonGroups")
    @Expose
    public List<Reference> buttonGroups = null;
}
