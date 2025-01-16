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
    // Structs

    /**
     * This event shall be generated, when the latching switch is moved to a new position. It may have been delayed by
     * debouncing within the switch.
     */
    public class SwitchLatched {
        /**
         * This field shall indicate the new value of the CurrentPosition attribute, i.e. after the move.
         */
        public Integer newPosition; // uint8

        public SwitchLatched(Integer newPosition) {
            this.newPosition = newPosition;
        }
    }

    /**
     * This event shall be generated, when the momentary switch starts to be pressed (after debouncing).
     */
    public class InitialPress {
        /**
         * This field shall indicate the new value of the CurrentPosition attribute, i.e. while pressed.
         */
        public Integer newPosition; // uint8

        public InitialPress(Integer newPosition) {
            this.newPosition = newPosition;
        }
    }

    /**
     * This event shall be generated, when the momentary switch has been pressed for a &quot;long&quot; time (this time
     * interval is manufacturer determined (e.g. since it depends on the switch physics)).
     */
    public class LongPress {
        /**
         * This field shall indicate the new value of the CurrentPosition attribute, i.e. while pressed.
         */
        public Integer newPosition; // uint8

        public LongPress(Integer newPosition) {
            this.newPosition = newPosition;
        }
    }

    /**
     * This event shall be generated, when the momentary switch has been released (after debouncing).
     * • If the server supports the Momentary Switch LongPress (MSL) feature, this event shall be generated when the
     * switch is released if no LongPress event had been generated since the previous InitialPress event.
     * • If the server does not support the Momentary Switch LongPress (MSL) feature, this event shall be generated when
     * the switch is released - even when the switch was pressed for a long time.
     * • Also see Section 1.13.7, “Sequence of generated events”.
     */
    public class ShortRelease {
        /**
         * This field shall indicate the previous value of the CurrentPosition attribute, i.e. just prior to release.
         */
        public Integer previousPosition; // uint8

        public ShortRelease(Integer previousPosition) {
            this.previousPosition = previousPosition;
        }
    }

    /**
     * This event shall be generated, when the momentary switch has been released (after debouncing) and after having
     * been pressed for a long time, i.e. this event shall be generated when the switch is released if a LongPress event
     * has been generated since the previous InitialPress event. Also see Section 1.13.7, “Sequence of generated
     * events”.
     */
    public class LongRelease {
        /**
         * This field shall indicate the previous value of the CurrentPosition attribute, i.e. just prior to release.
         */
        public Integer previousPosition; // uint8

        public LongRelease(Integer previousPosition) {
            this.previousPosition = previousPosition;
        }
    }

    /**
     * This event shall be generated to indicate how many times the momentary switch has been pressed in a multi-press
     * sequence, during that sequence. See Multi Press Details below.
     */
    public class MultiPressOngoing {
        /**
         * This field shall indicate the new value of the CurrentPosition attribute, i.e. while pressed.
         */
        public Integer newPosition; // uint8
        /**
         * This field shall contain:
         * • a value of 2 when the second press of a multi-press sequence has been detected,
         * • a value of 3 when the third press of a multi-press sequence has been detected,
         * • a value of N when the Nth press of a multi-press sequence has been detected.
         */
        public Integer currentNumberOfPressesCounted; // uint8

        public MultiPressOngoing(Integer newPosition, Integer currentNumberOfPressesCounted) {
            this.newPosition = newPosition;
            this.currentNumberOfPressesCounted = currentNumberOfPressesCounted;
        }
    }

    /**
     * This event shall be generated to indicate how many times the momentary switch has been pressed in a multi-press
     * sequence, after it has been detected that the sequence has ended. See Multi Press Details.
     * The PreviousPosition field shall indicate the previous value of the CurrentPosition attribute, i.e. just prior to
     * release.
     * The TotalNumberOfPressesCounted field shall contain:
     * • a value of 1 when there was one press in a multi-press sequence (and the sequence has ended),
     * i.e. there was no double press (or more),
     * • a value of 2 when there were exactly two presses in a multi-press sequence (and the sequence has ended),
     * • a value of 3 when there were exactly three presses in a multi-press sequence (and the sequence has ended),
     * • a value of N when there were exactly N presses in a multi-press sequence (and the sequence has ended).
     */
    public class MultiPressComplete {
        public Integer previousPosition; // uint8
        public Integer totalNumberOfPressesCounted; // uint8

        public MultiPressComplete(Integer previousPosition, Integer totalNumberOfPressesCounted) {
            this.previousPosition = previousPosition;
            this.totalNumberOfPressesCounted = totalNumberOfPressesCounted;
        }
    }

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
