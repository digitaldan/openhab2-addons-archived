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
 * OccupancySensing
 *
 * @author Dan Cunningham - Initial contribution
 */
public class OccupancySensingCluster extends BaseCluster {

    public static final String CLUSTER_NAME = "OccupancySensing";
    public static final int CLUSTER_ID = 0x0406;

    public Integer clusterRevision; // 65533 ClusterRevision
    /**
     * This attribute indicates the sensed (processed) status of occupancy.
     */
    public OccupancyBitmap occupancy; // 0 OccupancyBitmap R V
    /**
     * This attribute specifies the type of the occupancy sensor.
     */
    public OccupancySensorTypeEnum occupancySensorType; // 1 OccupancySensorTypeEnum R V
    /**
     * This attribute specifies the types of the occupancy sensor. Each bit position, if set, indicates the
     * corresponding sensing capability is implemented.
     * The value of the OccupancySensorType shall be aligned to the value of the OccupancySensorTypeBitmap attribute as
     * defined below.
     */
    public OccupancySensorTypeBitmap occupancySensorTypeBitmap; // 2 OccupancySensorTypeBitmap R V
    /**
     * This attribute specifies the time delay, in seconds, before the PIR sensor changes to its unoccupied state after
     * the last detection of movement in the sensed area.
     */
    public Integer pirOccupiedToUnoccupiedDelay; // 16 uint16 RW VM
    /**
     * This attribute specifies the time delay, in seconds, before the PIR sensor changes to its occupied state after
     * the detection of movement in the sensed area.
     */
    public Integer pirUnoccupiedToOccupiedDelay; // 17 uint16 RW VM
    /**
     * This attribute specifies the number of movement detection events that must occur in the period
     * PIRUnoccupiedToOccupiedDelay, before the PIR sensor changes to its occupied state.
     */
    public Integer pirUnoccupiedToOccupiedThreshold; // 18 uint8 RW VM
    /**
     * This attribute specifies the time delay, in seconds, before the Ultrasonic sensor changes to its unoccupied state
     * after the last detection of movement in the sensed area.
     */
    public Integer ultrasonicOccupiedToUnoccupiedDelay; // 32 uint16 RW VM
    /**
     * This attribute specifies the time delay, in seconds, before the Ultrasonic sensor changes to its occupied state
     * after the detection of movement in the sensed area.
     */
    public Integer ultrasonicUnoccupiedToOccupiedDelay; // 33 uint16 RW VM
    /**
     * This attribute specifies the number of movement detection events that must occur in the period
     * UltrasonicUnoccupiedToOccupiedDelay, before the Ultrasonic sensor changes to its occupied state.
     */
    public Integer ultrasonicUnoccupiedToOccupiedThreshold; // 34 uint8 RW VM
    /**
     * This attribute specifies the time delay, in seconds, before the physical contact occupancy sensor changes to its
     * unoccupied state after detecting the unoccupied event.
     */
    public Integer physicalContactOccupiedToUnoccupiedDelay; // 48 uint16 RW VM
    /**
     * This attribute specifies the time delay, in seconds, before the physical contact sensor changes to its occupied
     * state after the detection of the occupied event.
     */
    public Integer physicalContactUnoccupiedToOccupiedDelay; // 49 uint16 RW VM
    /**
     * This attribute specifies the number of movement detection events that must occur in the period
     * PhysicalContactUnoccupiedToOccupiedDelay, before the PhysicalContact sensor changes to its occupied state.
     */
    public Integer physicalContactUnoccupiedToOccupiedThreshold; // 50 uint8 RW VM

    // Enums
    public enum OccupancySensorTypeEnum implements MatterEnum {
        PIR(0, "Pir"),
        ULTRASONIC(1, "Ultrasonic"),
        PIR_AND_ULTRASONIC(2, "PirAndUltrasonic"),
        PHYSICAL_CONTACT(3, "PhysicalContact");

        public final Integer value;
        public final String label;

        private OccupancySensorTypeEnum(Integer value, String label) {
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

    // Bitmaps
    public static class OccupancyBitmap {
        /**
         * Indicates the sensed occupancy state
         * If this bit is set, it shall indicate the occupied state else if the bit if not set, it shall indicate the
         * unoccupied state.
         */
        public boolean occupied;

        public OccupancyBitmap(boolean occupied) {
            this.occupied = occupied;
        }
    }

    public static class OccupancySensorTypeBitmap {
        public boolean pir;
        public boolean ultrasonic;
        public boolean physicalContact;

        public OccupancySensorTypeBitmap(boolean pir, boolean ultrasonic, boolean physicalContact) {
            this.pir = pir;
            this.ultrasonic = ultrasonic;
            this.physicalContact = physicalContact;
        }
    }

    public OccupancySensingCluster(BigInteger nodeId, int endpointId) {
        super(nodeId, endpointId, 1030, "OccupancySensing");
    }

    @Override
    public @NonNull String toString() {
        String str = "";
        str += "clusterRevision : " + clusterRevision + "\n";
        str += "occupancy : " + occupancy + "\n";
        str += "occupancySensorType : " + occupancySensorType + "\n";
        str += "occupancySensorTypeBitmap : " + occupancySensorTypeBitmap + "\n";
        str += "pirOccupiedToUnoccupiedDelay : " + pirOccupiedToUnoccupiedDelay + "\n";
        str += "pirUnoccupiedToOccupiedDelay : " + pirUnoccupiedToOccupiedDelay + "\n";
        str += "pirUnoccupiedToOccupiedThreshold : " + pirUnoccupiedToOccupiedThreshold + "\n";
        str += "ultrasonicOccupiedToUnoccupiedDelay : " + ultrasonicOccupiedToUnoccupiedDelay + "\n";
        str += "ultrasonicUnoccupiedToOccupiedDelay : " + ultrasonicUnoccupiedToOccupiedDelay + "\n";
        str += "ultrasonicUnoccupiedToOccupiedThreshold : " + ultrasonicUnoccupiedToOccupiedThreshold + "\n";
        str += "physicalContactOccupiedToUnoccupiedDelay : " + physicalContactOccupiedToUnoccupiedDelay + "\n";
        str += "physicalContactUnoccupiedToOccupiedDelay : " + physicalContactUnoccupiedToOccupiedDelay + "\n";
        str += "physicalContactUnoccupiedToOccupiedThreshold : " + physicalContactUnoccupiedToOccupiedThreshold + "\n";
        return str;
    }
}
