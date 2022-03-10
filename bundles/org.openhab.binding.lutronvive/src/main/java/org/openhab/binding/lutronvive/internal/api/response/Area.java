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
public class Area extends Reference {

    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("parent")
    @Expose
    public Reference parent;
    @SerializedName("associatedDevices")
    @Expose
    public List<Reference> associatedDevices = null;
    @SerializedName("associatedZones")
    @Expose
    public List<Reference> associatedZones = null;
    @SerializedName("zoneTypeGroups")
    @Expose
    public List<AreaZoneTypeGroup> zoneTypeGroups = null;
    @SerializedName("daylightingGainSettings")
    @Expose
    public Reference daylightingGainSettings;
    @SerializedName("occupancySettings")
    @Expose
    public Reference occupancySettings;
    @SerializedName("occupancySensorSettings")
    @Expose
    public Reference occupancySensorSettings;
}
