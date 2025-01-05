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
 * Switch
 *
 * @author Dan Cunningham - Initial contribution
 */
public class SwitchCluster extends BaseCluster {

    public static final String CLUSTER_NAME = "Switch";
    public static final int CLUSTER_ID = 0x003B;

    public Integer clusterRevision; // 65533 ClusterRevision
    public FeatureMap featureMap; // 65532 FeatureMap
    /**
     * Indicates the maximum number of positions the switch has. Any kind of switch has a minimum of 2 positions. Also
     * see Multi Position Details for the case NumberOfPositions&gt;2.
     */
    public Integer numberOfPositions; // 0 uint8 R V
    /**
     * Indicates the position of the switch. The valid range is zero to NumberOfPositions-1. CurrentPosition value 0
     * shall be assigned to the default position of the switch: for example the &quot;open&quot; state of a rocker
     * switch, or the &quot;idle&quot; state of a push button switch.
     */
    public Integer currentPosition; // 1 uint8 R V
    /**
     * Indicates how many consecutive presses can be detected and reported by a momentary switch which supports
     * multi-press (e.g. it will report the value 3 if it can detect single press, double press and triple press, but
     * not quad press and beyond).
     */
    public Integer multiPressMax; // 2 uint8 R V

    // Bitmaps
    public static class FeatureMap {
        /**
         * LatchingSwitch
         * This feature is for a switch that maintains its position after being pressed (or turned).
         */
        public boolean latchingSwitch;
        /**
         * MomentarySwitch
         * This feature is for a switch that does not maintain its position after being pressed (or turned). After
         * releasing, it goes back to its idle position.
         */
        public boolean momentarySwitch;
        /**
         * MomentarySwitchRelease
         * This feature is for a momentary switch that can distinguish and report release events. When this feature flag
         * MSR is present, MS shall be present as well.
         */
        public boolean momentarySwitchRelease;
        /**
         * MomentarySwitchLongPress
         * This feature is for a momentary switch that can distinguish and report long presses from short presses. When
         * this feature flag MSL is present, MS and MSR shall be present as well.
         */
        public boolean momentarySwitchLongPress;
        /**
         * MomentarySwitchMultiPress
         * This feature is for a momentary switch that can distinguish and report double press and potentially multiple
         * presses with more events, such as triple press, etc. When this feature flag MSM is present, MS and MSR shall
         * be present as well.
         */
        public boolean momentarySwitchMultiPress;

        public FeatureMap(boolean latchingSwitch, boolean momentarySwitch, boolean momentarySwitchRelease,
                boolean momentarySwitchLongPress, boolean momentarySwitchMultiPress) {
            this.latchingSwitch = latchingSwitch;
            this.momentarySwitch = momentarySwitch;
            this.momentarySwitchRelease = momentarySwitchRelease;
            this.momentarySwitchLongPress = momentarySwitchLongPress;
            this.momentarySwitchMultiPress = momentarySwitchMultiPress;
        }
    }

    public SwitchCluster(BigInteger nodeId, int endpointId) {
        super(nodeId, endpointId, 59, "Switch");
    }

    @Override
    public @NonNull String toString() {
        String str = "";
        str += "clusterRevision : " + clusterRevision + "\n";
        str += "featureMap : " + featureMap + "\n";
        str += "numberOfPositions : " + numberOfPositions + "\n";
        str += "currentPosition : " + currentPosition + "\n";
        str += "multiPressMax : " + multiPressMax + "\n";
        return str;
    }
}
