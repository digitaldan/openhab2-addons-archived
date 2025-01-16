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

// AUTO-GENERATED, DO NOT EDIT!

package org.openhab.binding.matter.internal.client.dto.cluster.gen;

import java.math.BigInteger;

import org.eclipse.jdt.annotation.NonNull;

/**
 * TemperatureMeasurement
 *
 * @author Dan Cunningham - Initial contribution
 */
public class TemperatureMeasurementCluster extends BaseCluster {

    public static final String CLUSTER_NAME = "TemperatureMeasurement";
    public static final int CLUSTER_ID = 0x0402;

    public Integer clusterRevision; // 65533 ClusterRevision
    /**
     * Indicates the measured temperature. The null value indicates that the temperature is unknown.
     */
    public Integer measuredValue; // 0 temperature R V
    /**
     * Indicates the minimum value of MeasuredValue that is capable of being measured. See Measured Value for more
     * details.
     * The null value indicates that the value is not available.
     */
    public Integer minMeasuredValue; // 1 temperature R V
    /**
     * This attribute indicates the maximum value of MeasuredValue that is capable of being measured. See Measured Value
     * for more details.
     * The null value indicates that the value is not available.
     */
    public Integer maxMeasuredValue; // 2 temperature R V
    /**
     * See Measured Value.
     */
    public Integer tolerance; // 3 uint16 R V

    public TemperatureMeasurementCluster(BigInteger nodeId, int endpointId) {
        super(nodeId, endpointId, 1026, "TemperatureMeasurement");
    }

    @Override
    public @NonNull String toString() {
        String str = "";
        str += "clusterRevision : " + clusterRevision + "\n";
        str += "measuredValue : " + measuredValue + "\n";
        str += "minMeasuredValue : " + minMeasuredValue + "\n";
        str += "maxMeasuredValue : " + maxMeasuredValue + "\n";
        str += "tolerance : " + tolerance + "\n";
        return str;
    }
}
