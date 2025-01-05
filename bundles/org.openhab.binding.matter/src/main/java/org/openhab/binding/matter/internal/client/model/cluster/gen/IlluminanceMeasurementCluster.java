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

package org.openhab.binding.matter.internal.client.model.cluster.gen;

import java.math.BigInteger;

import org.eclipse.jdt.annotation.NonNull;

/**
 * IlluminanceMeasurement
 *
 * @author Dan Cunningham - Initial contribution
 */
public class IlluminanceMeasurementCluster extends BaseCluster {

    public static final String CLUSTER_NAME = "IlluminanceMeasurement";
    public static final int CLUSTER_ID = 0x0400;

    public Integer clusterRevision; // 65533 ClusterRevision
    /**
     * The MeasuredValue attribute represents the illuminance in Lux (symbol lx) as follows:
     * • MeasuredValue &#x3D; 10,000 x log10(illuminance) + 1,
     * where 1 lx &lt;&#x3D; illuminance &lt;&#x3D; 3.576 Mlx, corresponding to a MeasuredValue in the range 1 to
     * 0xFFFE. The MeasuredValue attribute can take the following values:
     * • 0 indicates a value of illuminance that is too low to be measured,
     * • MinMeasuredValue &lt;&#x3D; MeasuredValue &lt;&#x3D; MaxMeasuredValue under normal circumstances,
     * • null indicates that the illuminance measurement is invalid.
     * The MeasuredValue attribute is updated continuously as new measurements are made.
     */
    public Integer measuredValue; // 0 uint16 R V
    /**
     * The MinMeasuredValue attribute indicates the minimum value of MeasuredValue that can be measured. A value of null
     * indicates that this attribute is not defined. See Measured Value for more details.
     */
    public Integer minMeasuredValue; // 1 uint16 R V
    /**
     * The MaxMeasuredValue attribute indicates the maximum value of MeasuredValue that can be measured. A value of null
     * indicates that this attribute is not defined. See Measured Value for more details.
     */
    public Integer maxMeasuredValue; // 2 uint16 R V
    /**
     * See Measured Value.
     */
    public Integer tolerance; // 3 uint16 R V
    /**
     * The LightSensorType attribute specifies the electronic type of the light sensor. This attribute shall be set to
     * one of the non-reserved values listed in LightSensorTypeEnum or null in case the sensor type is unknown.
     */
    public Integer lightSensorType; // 4 uint8 R V

    // Enums
    public enum LightSensorTypeEnum implements MatterEnum {
        PHOTODIODE(0, "Photodiode"),
        CMOS(1, "Cmos");

        public final Integer value;
        public final String label;

        private LightSensorTypeEnum(Integer value, String label) {
            this.value = value;
            this.label = label;
        }

        @Override
        public Integer getValue() {
            return value;
        }

        @Override
        public String getLabel() {
            return label;
        }
    }

    public IlluminanceMeasurementCluster(BigInteger nodeId, int endpointId) {
        super(nodeId, endpointId, 1024, "IlluminanceMeasurement");
    }

    @Override
    public @NonNull String toString() {
        String str = "";
        str += "clusterRevision : " + clusterRevision + "\n";
        str += "measuredValue : " + measuredValue + "\n";
        str += "minMeasuredValue : " + minMeasuredValue + "\n";
        str += "maxMeasuredValue : " + maxMeasuredValue + "\n";
        str += "tolerance : " + tolerance + "\n";
        str += "lightSensorType : " + lightSensorType + "\n";
        return str;
    }
}
