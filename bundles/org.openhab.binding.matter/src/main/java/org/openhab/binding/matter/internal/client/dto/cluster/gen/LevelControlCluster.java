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
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNull;
import org.openhab.binding.matter.internal.client.dto.cluster.ClusterCommand;

/**
 * LevelControl
 *
 * @author Dan Cunningham - Initial contribution
 */
public class LevelControlCluster extends BaseCluster {

    public static final String CLUSTER_NAME = "LevelControl";
    public static final int CLUSTER_ID = 0x0008;

    public Integer clusterRevision; // 65533 ClusterRevision
    public FeatureMap featureMap; // 65532 FeatureMap
    /**
     * Indicates the current level of this device. The meaning of &#x27;level&#x27; is device dependent.
     */
    public Integer currentLevel; // 0 uint8 R V
    /**
     * Indicates the time remaining until the current command is complete - it is specified in 1/10ths of a second.
     */
    public Integer remainingTime; // 1 uint16 R V
    /**
     * Indicates the minimum value of CurrentLevel that is capable of being assigned.
     */
    public Integer minLevel; // 2 uint8 R V
    /**
     * Indicates the maximum value of CurrentLevel that is capable of being assigned.
     */
    public Integer maxLevel; // 3 uint8 R V
    /**
     * Indicates the frequency at which the device is at CurrentLevel. A CurrentFrequency of 0 is unknown.
     */
    public Integer currentFrequency; // 4 uint16 R V
    /**
     * Indicates the minimum value of CurrentFrequency that is capable of being assigned. MinFrequency shall be less
     * than or equal to MaxFrequency. A value of 0 indicates undefined.
     */
    public Integer minFrequency; // 5 uint16 R V
    /**
     * Indicates the maximum value of CurrentFrequency that is capable of being assigned. MaxFrequency shall be greater
     * than or equal to MinFrequency. A value of 0 indicates undefined.
     */
    public Integer maxFrequency; // 6 uint16 R V
    /**
     * Indicates the time taken to move to or from the target level when On or Off commands are received by an On/Off
     * cluster on the same endpoint. It is specified in 1/10ths of a second.
     * The actual time taken SHOULD be as close to OnOffTransitionTime as the device is able. Please note that if the
     * device is not able to move at a variable rate, the OnOffTransitionTime attribute SHOULD NOT be implemented.
     */
    public Integer onOffTransitionTime; // 16 uint16 RW VO
    /**
     * Indicates the value that the CurrentLevel attribute is set to when the OnOff attribute of an On/Off cluster on
     * the same endpoint is set to TRUE, as a result of processing an On/Off cluster command. If the OnLevel attribute
     * is not implemented, or is set to the null value, it has no effect. For more details see Effect of On/Off Commands
     * on the CurrentLevel Attribute.
     * OnLevel represents a mandatory field that was previously not present or optional. Implementers should be aware
     * that older devices may not implement it.
     */
    public Integer onLevel; // 17 uint8 RW VO
    /**
     * Indicates the time taken to move the current level from the minimum level to the maximum level when an On command
     * is received by an On/Off cluster on the same endpoint. It is specified in 1/10ths of a second. If this attribute
     * is not implemented, or contains a null value, the OnOffTransitionTime shall be used instead.
     */
    public Integer onTransitionTime; // 18 uint16 RW VO
    /**
     * Indicates the time taken to move the current level from the maximum level to the minimum level when an Off
     * command is received by an On/Off cluster on the same endpoint. It is specified in 1/10ths of a second. If this
     * attribute is not implemented, or contains a null value, the OnOffTransitionTime shall be used instead.
     */
    public Integer offTransitionTime; // 19 uint16 RW VO
    /**
     * Indicates the movement rate, in units per second, when a Move command is received with a null value Rate
     * parameter.
     */
    public Integer defaultMoveRate; // 20 uint8 RW VO
    /**
     * Indicates the selected options of the device.
     * The Options attribute is a bitmap that determines the default behavior of some cluster commands. Each command
     * that is dependent on the Options attribute shall first construct a temporary Options bitmap that is in effect
     * during the command processing. The temporary Options bitmap has the same format and meaning as the Options
     * attribute, but includes any bits that may be overridden by command fields.
     * This attribute is meant to be changed only during commissioning.
     * Command execution shall NOT continue beyond the Options processing if all of these criteria are true:
     * • The command is one of the ‘without On/Off’ commands: Move, Move to Level, Step, or Stop.
     * • The On/Off cluster exists on the same endpoint as this cluster.
     * • The OnOff attribute of the On/Off cluster, on this endpoint, is FALSE.
     * • The value of the ExecuteIfOff bit is 0.
     */
    public OptionsBitmap options; // 15 OptionsBitmap RW VO
    /**
     * Indicates the desired startup level for a device when it is supplied with power and this level shall be reflected
     * in the CurrentLevel attribute. The values of the StartUpCurrentLevel attribute are listed below:
     * This behavior does not apply to reboots associated with OTA. After an OTA restart, the CurrentLevel attribute
     * shall return to its value prior to the restart.
     */
    public Integer startUpCurrentLevel; // 16384 uint8 RW VM

    // Enums
    public enum MoveModeEnum implements MatterEnum {
        UP(0, "Up"),
        DOWN(1, "Down");

        public final Integer value;
        public final String label;

        private MoveModeEnum(Integer value, String label) {
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

    public enum StepModeEnum implements MatterEnum {
        UP(0, "Up"),
        DOWN(1, "Down");

        public final Integer value;
        public final String label;

        private StepModeEnum(Integer value, String label) {
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
    public static class OptionsBitmap {
        /**
         * Dependency on On/Off cluster
         * This bit indicates if this cluster has a dependency with the On/Off cluster.
         */
        public boolean executeIfOff;
        /**
         * Dependency on Color Control cluster
         * This bit indicates if this cluster has a dependency with the Color Control cluster.
         */
        public boolean coupleColorTempToLevel;

        public OptionsBitmap(boolean executeIfOff, boolean coupleColorTempToLevel) {
            this.executeIfOff = executeIfOff;
            this.coupleColorTempToLevel = coupleColorTempToLevel;
        }
    }

    public static class FeatureMap {
        /**
         * OnOff
         * Dependency with the On/Off cluster
         */
        public boolean onOff;
        /**
         * Lighting
         * This feature supports an interface for controlling the level of a light source. For the CurrentLevel
         * attribute:
         * A value of 0x00 shall NOT be used.
         * A value of 0x01 shall indicate the minimum level that can be attained on a device. A value of 0xFE shall
         * indicate the maximum level that can be attained on a device. A value of null shall represent an undefined
         * value.
         * All other values are application specific gradations from the minimum to the maximum level.
         */
        public boolean lighting;
        /**
         * Frequency
         * NOTE The Frequency feature is provisional.
         */
        public boolean frequency;

        public FeatureMap(boolean onOff, boolean lighting, boolean frequency) {
            this.onOff = onOff;
            this.lighting = lighting;
            this.frequency = frequency;
        }
    }

    public LevelControlCluster(BigInteger nodeId, int endpointId) {
        super(nodeId, endpointId, 8, "LevelControl");
    }

    // commands
    public static ClusterCommand moveToLevel(Integer level, Integer transitionTime, OptionsBitmap optionsMask,
            OptionsBitmap optionsOverride) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("level", level);
        map.put("transitionTime", transitionTime);
        map.put("optionsMask", optionsMask);
        map.put("optionsOverride", optionsOverride);
        return new ClusterCommand("moveToLevel", map);
    }

    public static ClusterCommand move(MoveModeEnum moveMode, Integer rate, OptionsBitmap optionsMask,
            OptionsBitmap optionsOverride) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("moveMode", moveMode);
        map.put("rate", rate);
        map.put("optionsMask", optionsMask);
        map.put("optionsOverride", optionsOverride);
        return new ClusterCommand("move", map);
    }

    public static ClusterCommand step(StepModeEnum stepMode, Integer stepSize, Integer transitionTime,
            OptionsBitmap optionsMask, OptionsBitmap optionsOverride) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("stepMode", stepMode);
        map.put("stepSize", stepSize);
        map.put("transitionTime", transitionTime);
        map.put("optionsMask", optionsMask);
        map.put("optionsOverride", optionsOverride);
        return new ClusterCommand("step", map);
    }

    public static ClusterCommand stop(OptionsBitmap optionsMask, OptionsBitmap optionsOverride) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("optionsMask", optionsMask);
        map.put("optionsOverride", optionsOverride);
        return new ClusterCommand("stop", map);
    }

    public static ClusterCommand moveToLevelWithOnOff(Integer level, Integer transitionTime, OptionsBitmap optionsMask,
            OptionsBitmap optionsOverride) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("level", level);
        map.put("transitionTime", transitionTime);
        map.put("optionsMask", optionsMask);
        map.put("optionsOverride", optionsOverride);
        return new ClusterCommand("moveToLevelWithOnOff", map);
    }

    public static ClusterCommand moveWithOnOff(MoveModeEnum moveMode, Integer rate, OptionsBitmap optionsMask,
            OptionsBitmap optionsOverride) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("moveMode", moveMode);
        map.put("rate", rate);
        map.put("optionsMask", optionsMask);
        map.put("optionsOverride", optionsOverride);
        return new ClusterCommand("moveWithOnOff", map);
    }

    public static ClusterCommand stepWithOnOff(StepModeEnum stepMode, Integer stepSize, Integer transitionTime,
            OptionsBitmap optionsMask, OptionsBitmap optionsOverride) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("stepMode", stepMode);
        map.put("stepSize", stepSize);
        map.put("transitionTime", transitionTime);
        map.put("optionsMask", optionsMask);
        map.put("optionsOverride", optionsOverride);
        return new ClusterCommand("stepWithOnOff", map);
    }

    public static ClusterCommand stopWithOnOff(OptionsBitmap optionsMask, OptionsBitmap optionsOverride) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("optionsMask", optionsMask);
        map.put("optionsOverride", optionsOverride);
        return new ClusterCommand("stopWithOnOff", map);
    }

    public static ClusterCommand moveToClosestFrequency(Integer frequency) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("frequency", frequency);
        return new ClusterCommand("moveToClosestFrequency", map);
    }

    @Override
    public @NonNull String toString() {
        String str = "";
        str += "clusterRevision : " + clusterRevision + "\n";
        str += "featureMap : " + featureMap + "\n";
        str += "currentLevel : " + currentLevel + "\n";
        str += "remainingTime : " + remainingTime + "\n";
        str += "minLevel : " + minLevel + "\n";
        str += "maxLevel : " + maxLevel + "\n";
        str += "currentFrequency : " + currentFrequency + "\n";
        str += "minFrequency : " + minFrequency + "\n";
        str += "maxFrequency : " + maxFrequency + "\n";
        str += "onOffTransitionTime : " + onOffTransitionTime + "\n";
        str += "onLevel : " + onLevel + "\n";
        str += "onTransitionTime : " + onTransitionTime + "\n";
        str += "offTransitionTime : " + offTransitionTime + "\n";
        str += "defaultMoveRate : " + defaultMoveRate + "\n";
        str += "options : " + options + "\n";
        str += "startUpCurrentLevel : " + startUpCurrentLevel + "\n";
        return str;
    }
}
