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
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNull;
import org.openhab.binding.matter.internal.client.model.cluster.ClusterCommand;

/**
 * ColorControl
 *
 * @author Dan Cunningham - Initial contribution
 */
public class ColorControlCluster extends BaseCluster {

    public static final String CLUSTER_NAME = "ColorControl";
    public static final int CLUSTER_ID = 0x0300;

    public Integer clusterRevision; // 65533 ClusterRevision
    public FeatureMap featureMap; // 65532 FeatureMap
    /**
     * The CurrentHue attribute contains the current hue value of the light. It is updated as fast as practical during
     * commands that change the hue.
     * The hue in degrees shall be related to the CurrentHue attribute by the relationship: Hue &#x3D; CurrentHue x 360
     * / 254 (CurrentHue in the range 0 to 254 inclusive)
     * If this attribute is implemented then the CurrentSaturation and ColorMode attributes shall also be implemented.
     */
    public Integer currentHue; // 0 uint8 R V
    /**
     * The CurrentSaturation attribute holds the current saturation value of the light. It is updated as fast as
     * practical during commands that change the saturation.
     * The saturation shall be related to the CurrentSaturation attribute by the relationship: Saturation &#x3D;
     * CurrentSaturation/254 (CurrentSaturation in the range 0 to 254 inclusive)
     * If this attribute is implemented then the CurrentHue and ColorMode attributes shall also be implemented.
     */
    public Integer currentSaturation; // 1 uint8 R V
    /**
     * The RemainingTime attribute holds the time remaining, in 1/10ths of a second, until the currently active command
     * will be complete.
     */
    public Integer remainingTime; // 2 uint16 R V
    /**
     * The CurrentX attribute contains the current value of the normalized chromaticity value x, as defined in the CIE
     * xyY Color Space. It is updated as fast as practical during commands that change the color.
     * The value of x shall be related to the CurrentX attribute by the relationship x &#x3D; CurrentX / 65536 (CurrentX
     * in the range 0 to 65279 inclusive)
     */
    public Integer currentX; // 3 uint16 R V
    /**
     * The CurrentY attribute contains the current value of the normalized chromaticity value y, as defined in the CIE
     * xyY Color Space. It is updated as fast as practical during commands that change the color.
     * The value of y shall be related to the CurrentY attribute by the relationship y &#x3D; CurrentY / 65536 (CurrentY
     * in the range 0 to 65279 inclusive)
     */
    public Integer currentY; // 4 uint16 R V
    /**
     * The DriftCompensation attribute indicates what mechanism, if any, is in use for compensation for color/intensity
     * drift over time. It shall be one of the non-reserved values in Values of the DriftCompensation Attribute.
     * ### Table 8. Values of the DriftCompensation Attribute
     */
    public DriftCompensation driftCompensation; // 5 enum8 R V
    /**
     * The CompensationText attribute holds a textual indication of what mechanism, if any, is in use to compensate for
     * color/intensity drift over time.
     */
    public String compensationText; // 6 string R V
    /**
     * The ColorTemperatureMireds attribute contains a scaled inverse of the current value of the color temperature. The
     * unit of ColorTemperatureMireds is the mired (micro reciprocal degree), a.k.a. mirek (micro reciprocal kelvin). It
     * is updated as fast as practical during commands that change the color.
     * The color temperature value in kelvins shall be related to the ColorTemperatureMireds attribute in mireds by the
     * relationship
     * Color temperature in kelvins &#x3D; 1,000,000 / ColorTemperatureMireds, where ColorTemperatureMireds is in the
     * range 1 to 65279 mireds inclusive, giving a color temperature range from 1,000,000 kelvins to 15.32 kelvins.
     * If this attribute is implemented then the ColorMode attribute shall also be implemented.
     */
    public Integer colorTemperatureMireds; // 7 uint16 R V
    /**
     * The ColorMode attribute indicates which attributes are currently determining the color of the device.
     * The value of the ColorMode attribute cannot be written directly - it is set upon reception of any command in
     * section Commands to the appropriate mode for that command.
     * Table 9. Values of the ColorMode Attribute
     */
    public ColorMode colorMode; // 8 enum8 R V
    /**
     * The Options attribute is meant to be changed only during commissioning. The Options attribute is a bitmap that
     * determines the default behavior of some cluster commands. Each command that is dependent on the Options attribute
     * shall first construct a temporary Options bitmap that is in effect during the command processing. The temporary
     * Options bitmap has the same format and meaning as the Options attribute, but includes any bits that may be
     * overridden by command fields.
     * Below is the format and description of the Options attribute and temporary Options bitmap and the effect on
     * dependent commands.
     * Table 10. Options Attribute
     * ExecuteIfOff Options bit: Command execution shall NOT continue beyond the Options processing if all of these
     * criteria are true:
     * • The On/Off cluster exists on the same endpoint as this cluster.
     * • The OnOff attribute of the On/Off cluster, on this endpoint, is FALSE.
     * • The value of the ExecuteIfOff bit is 0.
     */
    public Options options; // 15 map8 RW VO
    /**
     * The EnhancedCurrentHue attribute represents non-equidistant steps along the CIE 1931 color triangle, and it
     * provides 16-bits precision.
     * The upper 8 bits of this attribute shall be used as an index in the implementation specific XY lookup table to
     * provide the non-equidistance steps. The lower 8 bits shall be used to interpolate between these steps in a linear
     * way in order to provide color zoom for the user.
     * To provide compatibility with standard ZCL, the CurrentHue attribute shall contain a hue value in the range 0 to
     * 254, calculated from the EnhancedCurrentHue attribute.
     */
    public Integer enhancedCurrentHue; // 16384 uint16 R V
    /**
     * The EnhancedColorMode attribute specifies which attributes are currently determining the color of the device, as
     * detailed in Values of the EnhancedColorMode Attribute.
     * ### Table 11. Values of the EnhancedColorMode Attribute
     * To provide compatibility with standard ZCL, the original ColorMode attribute shall indicate ‘CurrentHue and
     * CurrentSaturation’ when the light uses the EnhancedCurrentHue attribute. If the ColorMode attribute is changed,
     * e.g., due to one of the standard Color Control cluster commands defined in the ZCL, its new value shall be copied
     * to the EnhancedColorMode attribute.
     */
    public EnhancedColorMode enhancedColorMode; // 16385 enum8 R V
    /**
     * The ColorLoopActive attribute specifies the current active status of the color loop. If this attribute has the
     * value 0, the color loop shall not be active. If this attribute has the value 1, the color loop shall be active.
     * All other values (2 to 254) are reserved.
     */
    public ColorLoopActive colorLoopActive; // 16386 enum16 R V
    /**
     * The ColorLoopDirection attribute specifies the current direction of the color loop. If this attribute has the
     * value 0, the EnhancedCurrentHue attribute shall be decremented. If this attribute has the value 1, the
     * EnhancedCurrentHue attribute shall be incremented. All other values (2 to 254) are reserved.
     */
    public ColorLoopDirection colorLoopDirection; // 16387 enum16 R V
    /**
     * The ColorLoopTime attribute specifies the number of seconds it shall take to perform a full color loop, i.e., to
     * cycle all values of the EnhancedCurrentHue attribute (between 0 and 0xFFFE).
     */
    public Integer colorLoopTime; // 16388 uint16 R V
    /**
     * The ColorLoopStartEnhancedHue attribute specifies the value of the EnhancedCurrentHue attribute from which the
     * color loop shall be started.
     */
    public Integer colorLoopStartEnhancedHue; // 16389 uint16 R V
    /**
     * The ColorLoopStoredEnhancedHue attribute specifies the value of the EnhancedCurrentHue attribute before the color
     * loop was started. Once the color loop is complete, the EnhancedCurrentHue attribute shall be restored to this
     * value.
     */
    public Integer colorLoopStoredEnhancedHue; // 16390 uint16 R V
    /**
     * Bits 0-4 of the ColorCapabilities attribute shall have the same values as the corresponding bits of the
     * FeatureMap attribute. All other bits in ColorCapabilities shall be 0.
     */
    public ColorCapabilities colorCapabilities; // 16394 map16 R V
    /**
     * The ColorTempPhysicalMinMireds attribute indicates the minimum mired value supported by the hardware.
     * ColorTempPhysicalMinMireds corresponds to the maximum color temperature in kelvins supported by the hardware.
     * ColorTempPhysicalMinMireds &lt;&#x3D; ColorTemperatureMireds.
     */
    public Integer colorTempPhysicalMinMireds; // 16395 uint16 R V
    /**
     * The ColorTempPhysicalMaxMireds attribute indicates the maximum mired value supported by the hardware.
     * ColorTempPhysicalMaxMireds corresponds to the minimum color temperature in kelvins supported by the hardware.
     * ColorTemperatureMireds &lt;&#x3D; ColorTempPhysicalMaxMireds.
     */
    public Integer colorTempPhysicalMaxMireds; // 16396 uint16 R V
    /**
     * The CoupleColorTempToLevelMinMireds attribute specifies a lower bound on the value of the ColorTemperatureMireds
     * attribute for the purposes of coupling the ColorTemperatureMireds attribute to the CurrentLevel attribute when
     * the CoupleColorTempToLevel bit of the Options attribute of the Level Control cluster is equal to 1. When coupling
     * the ColorTemperatureMireds attribute to the CurrentLevel attribute, this value shall correspond to a CurrentLevel
     * value of 0xFE (100%).
     * This attribute shall be set such that the following relationship exists: ColorTempPhysicalMinMireds ≤
     * CoupleColorTempToLevelMinMireds ≤ ColorTemperatureMireds
     * Note that since this attribute is stored as a micro reciprocal degree (mired) value (i.e. color temperature in
     * kelvins &#x3D; 1,000,000 / CoupleColorTempToLevelMinMireds), the CoupleColorTempToLevelMinMireds attribute
     * corresponds to an upper bound on the value of the color temperature in kelvins supported by the device.
     */
    public Integer coupleColorTempToLevelMinMireds; // 16397 uint16 R V
    /**
     * The StartUpColorTemperatureMireds attribute shall define the desired startup color temperature value a lamp shall
     * use when it is supplied with power and this value shall be reflected in the ColorTemperatureMireds attribute. In
     * addition, the ColorMode and EnhancedColorMode attributes shall be set to 0x02 (color temperature). The values of
     * the StartUpColorTemperatureMireds attribute are listed in the table below,
     * Table 12. Values of the StartUpColorTemperatureMireds attribute
     */
    public Integer startUpColorTemperatureMireds; // 16400 uint16 RW VM
    /**
     * The NumberOfPrimaries attribute contains the number of color primaries implemented on this device. A value of
     * null shall indicate that the number of primaries is unknown.
     * Where this attribute is implemented, the attributes below for indicating the “x” and “y” color values of the
     * primaries shall also be implemented for each of the primaries from 1 to NumberOfPrimaries, without leaving gaps.
     * Implementation of the Primary1Intensity attribute and subsequent intensity attributes is optional.
     */
    public Integer numberOfPrimaries; // 16 uint8 R V
    /**
     * The Primary1X attribute contains the normalized chromaticity value x for this primary, as defined in the CIE xyY
     * Color Space.
     * The value of x shall be related to the Primary1X attribute by the relationship x &#x3D; Primary1X / 65536
     * (Primary1X in the range 0 to 65279 inclusive)
     */
    public Integer primary1X; // 17 uint16 R V
    /**
     * The Primary1Y attribute contains the normalized chromaticity value y for this primary, as defined in the CIE xyY
     * Color Space.
     * The value of y shall be related to the Primary1Y attribute by the relationship y &#x3D; Primary1Y / 65536
     * (Primary1Y in the range 0 to 65279 inclusive)
     */
    public Integer primary1Y; // 18 uint16 R V
    /**
     * The Primary1intensity attribute contains a representation of the maximum intensity of this primary as defined in
     * the Dimming Light Curve in the Ballast Configuration cluster (see Ballast Configuration Cluster), normalized such
     * that the primary with the highest maximum intensity contains the value 0xFE.
     * A value of null shall indicate that this primary is not available.
     */
    public Integer primary1Intensity; // 19 uint8 R V
    public Integer primary2X; // 21 uint16 R V
    public Integer primary2Y; // 22 uint16 R V
    public Integer primary2Intensity; // 23 uint8 R V
    public Integer primary3X; // 25 uint16 R V
    public Integer primary3Y; // 26 uint16 R V
    public Integer primary3Intensity; // 27 uint8 R V
    public Integer primary4X; // 32 uint16 R V
    public Integer primary4Y; // 33 uint16 R V
    public Integer primary4Intensity; // 34 uint8 R V
    public Integer primary5X; // 36 uint16 R V
    public Integer primary5Y; // 37 uint16 R V
    public Integer primary5Intensity; // 38 uint8 R V
    public Integer primary6X; // 40 uint16 R V
    public Integer primary6Y; // 41 uint16 R V
    public Integer primary6Intensity; // 42 uint8 R V
    /**
     * The WhitePointX attribute contains the normalized chromaticity value x, as defined in the CIE xyY Color Space, of
     * the current white point of the device.
     * The value of x shall be related to the WhitePointX attribute by the relationship x &#x3D; WhitePointX / 65536
     * (WhitePointX in the range 0 to 65279 inclusive)
     */
    public Integer whitePointX; // 48 uint16 RW VM
    /**
     * The WhitePointY attribute contains the normalized chromaticity value y, as defined in the CIE xyY
     * Color Space, of the current white point of the device.
     * The value of y shall be related to the WhitePointY attribute by the relationship y &#x3D; WhitePointY / 65536
     * (WhitePointY in the range 0 to 65279 inclusive)
     */
    public Integer whitePointY; // 49 uint16 RW VM
    /**
     * The ColorPointRX attribute contains the normalized chromaticity value x, as defined in the CIE xyY Color Space,
     * of the red color point of the device.
     * The value of x shall be related to the ColorPointRX attribute by the relationship x &#x3D; ColorPointRX / 65536
     * (ColorPointRX in the range 0 to 65279 inclusive)
     */
    public Integer colorPointRx; // 50 uint16 RW VM
    /**
     * The ColorPointRY attribute contains the normalized chromaticity value y, as defined in the CIE xyY Color Space,
     * of the red color point of the device.
     * The value of y shall be related to the ColorPointRY attribute by the relationship y &#x3D; ColorPointRY / 65536
     * (ColorPointRY in the range 0 to 65279 inclusive)
     */
    public Integer colorPointRy; // 51 uint16 RW VM
    /**
     * The ColorPointRIntensity attribute contains a representation of the relative intensity of the red color point as
     * defined in the Dimming Light Curve in the Ballast Configuration cluster (see Ballast Configuration Cluster),
     * normalized such that the color point with the highest relative intensity contains the value 0xFE.
     * A value of null shall indicate an invalid value.
     */
    public Integer colorPointRIntensity; // 52 uint8 RW VM
    public Integer colorPointGx; // 54 uint16 RW VM
    public Integer colorPointGy; // 55 uint16 RW VM
    public Integer colorPointGIntensity; // 56 uint8 RW VM
    public Integer colorPointBx; // 58 uint16 RW VM
    public Integer colorPointBy; // 59 uint16 RW VM
    public Integer colorPointBIntensity; // 60 uint8 RW VM

    // Enums
    /**
     * The DriftCompensation attribute indicates what mechanism, if any, is in use for compensation for color/intensity
     * drift over time. It shall be one of the non-reserved values in Values of the DriftCompensation Attribute.
     * ### Table 8. Values of the DriftCompensation Attribute
     */
    public enum DriftCompensation implements MatterEnum {
        NONE(0, "None"),
        OTHER_UNKNOWN(1, "OtherUnknown"),
        TEMPERATURE_MONITORING(2, "TemperatureMonitoring"),
        OPTICAL_LUMINANCE_MONITORING_AND_FEEDBACK(3, "OpticalLuminanceMonitoringAndFeedback"),
        OPTICAL_COLOR_MONITORING_AND_FEEDBACK(4, "OpticalColorMonitoringAndFeedback");

        public final Integer value;
        public final String label;

        private DriftCompensation(Integer value, String label) {
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

    /**
     * The ColorMode attribute indicates which attributes are currently determining the color of the device.
     * The value of the ColorMode attribute cannot be written directly - it is set upon reception of any command in
     * section Commands to the appropriate mode for that command.
     * Table 9. Values of the ColorMode Attribute
     */
    public enum ColorMode implements MatterEnum {
        CURRENT_HUE_AND_CURRENT_SATURATION(0, "CurrentHueAndCurrentSaturation"),
        CURRENT_XAND_CURRENT_Y(1, "CurrentXAndCurrentY"),
        COLOR_TEMPERATURE_MIREDS(2, "ColorTemperatureMireds");

        public final Integer value;
        public final String label;

        private ColorMode(Integer value, String label) {
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

    /**
     * The EnhancedColorMode attribute specifies which attributes are currently determining the color of the device, as
     * detailed in Values of the EnhancedColorMode Attribute.
     * ### Table 11. Values of the EnhancedColorMode Attribute
     * To provide compatibility with standard ZCL, the original ColorMode attribute shall indicate ‘CurrentHue and
     * CurrentSaturation’ when the light uses the EnhancedCurrentHue attribute. If the ColorMode attribute is changed,
     * e.g., due to one of the standard Color Control cluster commands defined in the ZCL, its new value shall be copied
     * to the EnhancedColorMode attribute.
     */
    public enum EnhancedColorMode implements MatterEnum {
        CURRENT_HUE_AND_CURRENT_SATURATION(0, "CurrentHueAndCurrentSaturation"),
        CURRENT_XAND_CURRENT_Y(1, "CurrentXAndCurrentY"),
        COLOR_TEMPERATURE_MIREDS(2, "ColorTemperatureMireds"),
        ENHANCED_CURRENT_HUE_AND_CURRENT_SATURATION(3, "EnhancedCurrentHueAndCurrentSaturation");

        public final Integer value;
        public final String label;

        private EnhancedColorMode(Integer value, String label) {
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

    /**
     * The ColorLoopActive attribute specifies the current active status of the color loop. If this attribute has the
     * value 0, the color loop shall not be active. If this attribute has the value 1, the color loop shall be active.
     * All other values (2 to 254) are reserved.
     */
    public enum ColorLoopActive implements MatterEnum {
        INACTIVE(0, "Inactive"),
        ACTIVE(1, "Active");

        public final Integer value;
        public final String label;

        private ColorLoopActive(Integer value, String label) {
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

    /**
     * The ColorLoopDirection attribute specifies the current direction of the color loop. If this attribute has the
     * value 0, the EnhancedCurrentHue attribute shall be decremented. If this attribute has the value 1, the
     * EnhancedCurrentHue attribute shall be incremented. All other values (2 to 254) are reserved.
     */
    public enum ColorLoopDirection implements MatterEnum {
        DECREMENT(0, "Decrement"),
        INCREMENT(1, "Increment");

        public final Integer value;
        public final String label;

        private ColorLoopDirection(Integer value, String label) {
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

    public enum MoveMode implements MatterEnum {
        STOP(0, "Stop"),
        UP(1, "Up"),
        DOWN(3, "Down");

        public final Integer value;
        public final String label;

        private MoveMode(Integer value, String label) {
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

    public enum StepMode implements MatterEnum {
        UP(1, "Up"),
        DOWN(3, "Down");

        public final Integer value;
        public final String label;

        private StepMode(Integer value, String label) {
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

    public enum Direction implements MatterEnum {
        SHORTEST_DISTANCE(0, "ShortestDistance"),
        LONGEST_DISTANCE(1, "LongestDistance"),
        UP(2, "Up"),
        DOWN(3, "Down");

        public final Integer value;
        public final String label;

        private Direction(Integer value, String label) {
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
    /**
     * The Options attribute is meant to be changed only during commissioning. The Options attribute is a bitmap that
     * determines the default behavior of some cluster commands. Each command that is dependent on the Options attribute
     * shall first construct a temporary Options bitmap that is in effect during the command processing. The temporary
     * Options bitmap has the same format and meaning as the Options attribute, but includes any bits that may be
     * overridden by command fields.
     * Below is the format and description of the Options attribute and temporary Options bitmap and the effect on
     * dependent commands.
     * Table 10. Options Attribute
     * ExecuteIfOff Options bit: Command execution shall NOT continue beyond the Options processing if all of these
     * criteria are true:
     * • The On/Off cluster exists on the same endpoint as this cluster.
     * • The OnOff attribute of the On/Off cluster, on this endpoint, is FALSE.
     * • The value of the ExecuteIfOff bit is 0.
     */
    public static class Options {
        public boolean executeIfOff;

        public Options(boolean executeIfOff) {
            this.executeIfOff = executeIfOff;
        }
    }

    /**
     * Bits 0-4 of the ColorCapabilities attribute shall have the same values as the corresponding bits of the
     * FeatureMap attribute. All other bits in ColorCapabilities shall be 0.
     */
    public static class ColorCapabilities {
        public boolean hueSaturation;
        public boolean enhancedHue;
        public boolean colorLoop;
        public boolean xY;
        public boolean colorTemperature;

        public ColorCapabilities(boolean hueSaturation, boolean enhancedHue, boolean colorLoop, boolean xY,
                boolean colorTemperature) {
            this.hueSaturation = hueSaturation;
            this.enhancedHue = enhancedHue;
            this.colorLoop = colorLoop;
            this.xY = xY;
            this.colorTemperature = colorTemperature;
        }
    }

    public static class FeatureMap {
        /**
         * HueSaturation
         * Supports color specification via hue/saturation.
         */
        public boolean hueSaturation;
        /**
         * EnhancedHue
         * Enhanced hue is supported.
         */
        public boolean enhancedHue;
        /**
         * ColorLoop
         * Color loop is supported.
         */
        public boolean colorLoop;
        /**
         * Xy
         * Supports color specification via XY.
         */
        public boolean xy;
        /**
         * ColorTemperature
         * Supports specification of color temperature.
         */
        public boolean colorTemperature;

        public FeatureMap(boolean hueSaturation, boolean enhancedHue, boolean colorLoop, boolean xy,
                boolean colorTemperature) {
            this.hueSaturation = hueSaturation;
            this.enhancedHue = enhancedHue;
            this.colorLoop = colorLoop;
            this.xy = xy;
            this.colorTemperature = colorTemperature;
        }
    }

    /**
     * The UpdateFlags field specifies which color loop attributes to update before the color loop is started. This
     * field shall be formatted as illustrated in Format of the UpdateFlags Field of the ColorLoopSet Command.
     * ### Table 28. Format of the UpdateFlags Field of the ColorLoopSet Command
     * The UpdateAction sub-field is 1 bit in length and specifies whether the device shall adhere to the action field
     * in order to process the command. If this sub-field is set to 1, the device shall adhere to the action field. If
     * this sub-field is set to 0, the device shall ignore the Action field.
     * The UpdateDirection sub-field is 1 bit in length and specifies whether the device shall update the
     * ColorLoopDirection attribute with the Direction field. If this sub-field is set to 1, the device shall update the
     * value of the ColorLoopDirection attribute with the value of the Direction field. If this sub-field is set to 0,
     * the device shall ignore the Direction field.
     * The UpdateTime sub-field is 1 bit in length and specifies whether the device shall update the ColorLoopTime
     * attribute with the Time field. If this sub-field is set to 1, the device shall update the value of the
     * ColorLoopTime attribute with the value of the Time field. If this sub-field is set to 0, the device shall ignore
     * the Time field.
     * The UpdateStartHue sub-field is 1 bit in length and specifies whether the device shall update the
     * ColorLoopStartEnhancedHue attribute with the StartHue field. If this sub-field is set to 1, the device shall
     * update the value of the ColorLoopStartEnhancedHue attribute with the value of the StartHue field. If this
     * sub-field is set to 0, the device shall ignore the StartHue field.
     */
    public static class UpdateFlags {
        public boolean updateAction;
        public boolean updateDirection;
        public boolean updateTime;
        public boolean updateStartHue;
        public short reserved;

        public UpdateFlags(boolean updateAction, boolean updateDirection, boolean updateTime, boolean updateStartHue,
                short reserved) {
            this.updateAction = updateAction;
            this.updateDirection = updateDirection;
            this.updateTime = updateTime;
            this.updateStartHue = updateStartHue;
            this.reserved = reserved;
        }
    }

    public ColorControlCluster(BigInteger nodeId, int endpointId) {
        super(nodeId, endpointId, 768, "ColorControl");
    }

    // commands
    public static ClusterCommand moveToHue(Integer hue, Direction direction, Integer transitionTime,
            Options optionsMask, Options optionsOverride) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("hue", hue);
        map.put("direction", direction);
        map.put("transitionTime", transitionTime);
        map.put("optionsMask", optionsMask);
        map.put("optionsOverride", optionsOverride);
        return new ClusterCommand("moveToHue", map);
    }

    public static ClusterCommand moveHue(MoveMode moveMode, Integer rate, Options optionsMask,
            Options optionsOverride) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("moveMode", moveMode);
        map.put("rate", rate);
        map.put("optionsMask", optionsMask);
        map.put("optionsOverride", optionsOverride);
        return new ClusterCommand("moveHue", map);
    }

    public static ClusterCommand stepHue(StepMode stepMode, Integer stepSize, Integer transitionTime,
            Options optionsMask, Options optionsOverride) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("stepMode", stepMode);
        map.put("stepSize", stepSize);
        map.put("transitionTime", transitionTime);
        map.put("optionsMask", optionsMask);
        map.put("optionsOverride", optionsOverride);
        return new ClusterCommand("stepHue", map);
    }

    public static ClusterCommand moveToSaturation(Integer saturation, Integer transitionTime, Options optionsMask,
            Options optionsOverride) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("saturation", saturation);
        map.put("transitionTime", transitionTime);
        map.put("optionsMask", optionsMask);
        map.put("optionsOverride", optionsOverride);
        return new ClusterCommand("moveToSaturation", map);
    }

    public static ClusterCommand moveSaturation(MoveMode moveMode, Integer rate, Options optionsMask,
            Options optionsOverride) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("moveMode", moveMode);
        map.put("rate", rate);
        map.put("optionsMask", optionsMask);
        map.put("optionsOverride", optionsOverride);
        return new ClusterCommand("moveSaturation", map);
    }

    public static ClusterCommand stepSaturation(StepMode stepMode, Integer stepSize, Integer transitionTime,
            Options optionsMask, Options optionsOverride) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("stepMode", stepMode);
        map.put("stepSize", stepSize);
        map.put("transitionTime", transitionTime);
        map.put("optionsMask", optionsMask);
        map.put("optionsOverride", optionsOverride);
        return new ClusterCommand("stepSaturation", map);
    }

    public static ClusterCommand moveToHueAndSaturation(Integer hue, Integer saturation, Integer transitionTime,
            Options optionsMask, Options optionsOverride) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("hue", hue);
        map.put("saturation", saturation);
        map.put("transitionTime", transitionTime);
        map.put("optionsMask", optionsMask);
        map.put("optionsOverride", optionsOverride);
        return new ClusterCommand("moveToHueAndSaturation", map);
    }

    public static ClusterCommand moveToColor(Integer colorX, Integer colorY, Integer transitionTime,
            Options optionsMask, Options optionsOverride) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("colorX", colorX);
        map.put("colorY", colorY);
        map.put("transitionTime", transitionTime);
        map.put("optionsMask", optionsMask);
        map.put("optionsOverride", optionsOverride);
        return new ClusterCommand("moveToColor", map);
    }

    public static ClusterCommand moveColor(Integer rateX, Integer rateY, Options optionsMask, Options optionsOverride) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("rateX", rateX);
        map.put("rateY", rateY);
        map.put("optionsMask", optionsMask);
        map.put("optionsOverride", optionsOverride);
        return new ClusterCommand("moveColor", map);
    }

    public static ClusterCommand stepColor(Integer stepX, Integer stepY, Integer transitionTime, Options optionsMask,
            Options optionsOverride) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("stepX", stepX);
        map.put("stepY", stepY);
        map.put("transitionTime", transitionTime);
        map.put("optionsMask", optionsMask);
        map.put("optionsOverride", optionsOverride);
        return new ClusterCommand("stepColor", map);
    }

    public static ClusterCommand moveToColorTemperature(Integer colorTemperatureMireds, Integer transitionTime,
            Options optionsMask, Options optionsOverride) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("colorTemperatureMireds", colorTemperatureMireds);
        map.put("transitionTime", transitionTime);
        map.put("optionsMask", optionsMask);
        map.put("optionsOverride", optionsOverride);
        return new ClusterCommand("moveToColorTemperature", map);
    }

    /**
     * The EnhancedMoveToHue command allows lamps to be moved in a smooth continuous transition from their current hue
     * to a target hue.
     */
    public static ClusterCommand enhancedMoveToHue(Integer enhancedHue, Direction direction, Integer transitionTime,
            Options optionsMask, Options optionsOverride) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("enhancedHue", enhancedHue);
        map.put("direction", direction);
        map.put("transitionTime", transitionTime);
        map.put("optionsMask", optionsMask);
        map.put("optionsOverride", optionsOverride);
        return new ClusterCommand("enhancedMoveToHue", map);
    }

    /**
     * The EnhancedMoveHue command allows lamps to be moved in a continuous stepped transition from their current hue to
     * a target hue.
     */
    public static ClusterCommand enhancedMoveHue(MoveMode moveMode, Integer rate, Options optionsMask,
            Options optionsOverride) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("moveMode", moveMode);
        map.put("rate", rate);
        map.put("optionsMask", optionsMask);
        map.put("optionsOverride", optionsOverride);
        return new ClusterCommand("enhancedMoveHue", map);
    }

    /**
     * The EnhancedStepHue command allows lamps to be moved in a stepped transition from their current hue to a target
     * hue, resulting in a linear transition through XY space.
     */
    public static ClusterCommand enhancedStepHue(StepMode stepMode, Integer stepSize, Integer transitionTime,
            Options optionsMask, Options optionsOverride) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("stepMode", stepMode);
        map.put("stepSize", stepSize);
        map.put("transitionTime", transitionTime);
        map.put("optionsMask", optionsMask);
        map.put("optionsOverride", optionsOverride);
        return new ClusterCommand("enhancedStepHue", map);
    }

    /**
     * The EnhancedMoveToHueAndSaturation command allows lamps to be moved in a smooth continuous transition from their
     * current hue to a target hue and from their current saturation to a target saturation.
     */
    public static ClusterCommand enhancedMoveToHueAndSaturation(Integer enhancedHue, Integer saturation,
            Integer transitionTime, Options optionsMask, Options optionsOverride) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("enhancedHue", enhancedHue);
        map.put("saturation", saturation);
        map.put("transitionTime", transitionTime);
        map.put("optionsMask", optionsMask);
        map.put("optionsOverride", optionsOverride);
        return new ClusterCommand("enhancedMoveToHueAndSaturation", map);
    }

    /**
     * The Color Loop Set command allows a color loop to be activated such that the color lamp cycles through its range
     * of hues.
     */
    public static ClusterCommand colorLoopSet(UpdateFlags updateFlags, Integer action, Integer direction, Integer time,
            Integer startHue, Options optionsMask, Options optionsOverride) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("updateFlags", updateFlags);
        map.put("action", action);
        map.put("direction", direction);
        map.put("time", time);
        map.put("startHue", startHue);
        map.put("optionsMask", optionsMask);
        map.put("optionsOverride", optionsOverride);
        return new ClusterCommand("colorLoopSet", map);
    }

    /**
     * The StopMoveStep command is provided to allow MoveTo and Step commands to be stopped. (Note this automatically
     * provides symmetry to the Level Control cluster.)
     * NOTE the StopMoveStep command has no effect on an active color loop.
     */
    public static ClusterCommand stopMoveStep(Options optionsMask, Options optionsOverride) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("optionsMask", optionsMask);
        map.put("optionsOverride", optionsOverride);
        return new ClusterCommand("stopMoveStep", map);
    }

    /**
     * The MoveColorTemperature command allows the color temperature of a lamp to be moved at a specified rate.
     */
    public static ClusterCommand moveColorTemperature(MoveMode moveMode, Integer rate,
            Integer colorTemperatureMinimumMireds, Integer colorTemperatureMaximumMireds, Options optionsMask,
            Options optionsOverride) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("moveMode", moveMode);
        map.put("rate", rate);
        map.put("colorTemperatureMinimumMireds", colorTemperatureMinimumMireds);
        map.put("colorTemperatureMaximumMireds", colorTemperatureMaximumMireds);
        map.put("optionsMask", optionsMask);
        map.put("optionsOverride", optionsOverride);
        return new ClusterCommand("moveColorTemperature", map);
    }

    /**
     * The StepColorTemperature command allows the color temperature of a lamp to be stepped with a specified step size.
     */
    public static ClusterCommand stepColorTemperature(StepMode stepMode, Integer stepSize, Integer transitionTime,
            Integer colorTemperatureMinimumMireds, Integer colorTemperatureMaximumMireds, Options optionsMask,
            Options optionsOverride) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("stepMode", stepMode);
        map.put("stepSize", stepSize);
        map.put("transitionTime", transitionTime);
        map.put("colorTemperatureMinimumMireds", colorTemperatureMinimumMireds);
        map.put("colorTemperatureMaximumMireds", colorTemperatureMaximumMireds);
        map.put("optionsMask", optionsMask);
        map.put("optionsOverride", optionsOverride);
        return new ClusterCommand("stepColorTemperature", map);
    }

    @Override
    public @NonNull String toString() {
        String str = "";
        str += "clusterRevision : " + clusterRevision + "\n";
        str += "featureMap : " + featureMap + "\n";
        str += "currentHue : " + currentHue + "\n";
        str += "currentSaturation : " + currentSaturation + "\n";
        str += "remainingTime : " + remainingTime + "\n";
        str += "currentX : " + currentX + "\n";
        str += "currentY : " + currentY + "\n";
        str += "driftCompensation : " + driftCompensation + "\n";
        str += "compensationText : " + compensationText + "\n";
        str += "colorTemperatureMireds : " + colorTemperatureMireds + "\n";
        str += "colorMode : " + colorMode + "\n";
        str += "options : " + options + "\n";
        str += "enhancedCurrentHue : " + enhancedCurrentHue + "\n";
        str += "enhancedColorMode : " + enhancedColorMode + "\n";
        str += "colorLoopActive : " + colorLoopActive + "\n";
        str += "colorLoopDirection : " + colorLoopDirection + "\n";
        str += "colorLoopTime : " + colorLoopTime + "\n";
        str += "colorLoopStartEnhancedHue : " + colorLoopStartEnhancedHue + "\n";
        str += "colorLoopStoredEnhancedHue : " + colorLoopStoredEnhancedHue + "\n";
        str += "colorCapabilities : " + colorCapabilities + "\n";
        str += "colorTempPhysicalMinMireds : " + colorTempPhysicalMinMireds + "\n";
        str += "colorTempPhysicalMaxMireds : " + colorTempPhysicalMaxMireds + "\n";
        str += "coupleColorTempToLevelMinMireds : " + coupleColorTempToLevelMinMireds + "\n";
        str += "startUpColorTemperatureMireds : " + startUpColorTemperatureMireds + "\n";
        str += "numberOfPrimaries : " + numberOfPrimaries + "\n";
        str += "primary1X : " + primary1X + "\n";
        str += "primary1Y : " + primary1Y + "\n";
        str += "primary1Intensity : " + primary1Intensity + "\n";
        str += "primary2X : " + primary2X + "\n";
        str += "primary2Y : " + primary2Y + "\n";
        str += "primary2Intensity : " + primary2Intensity + "\n";
        str += "primary3X : " + primary3X + "\n";
        str += "primary3Y : " + primary3Y + "\n";
        str += "primary3Intensity : " + primary3Intensity + "\n";
        str += "primary4X : " + primary4X + "\n";
        str += "primary4Y : " + primary4Y + "\n";
        str += "primary4Intensity : " + primary4Intensity + "\n";
        str += "primary5X : " + primary5X + "\n";
        str += "primary5Y : " + primary5Y + "\n";
        str += "primary5Intensity : " + primary5Intensity + "\n";
        str += "primary6X : " + primary6X + "\n";
        str += "primary6Y : " + primary6Y + "\n";
        str += "primary6Intensity : " + primary6Intensity + "\n";
        str += "whitePointX : " + whitePointX + "\n";
        str += "whitePointY : " + whitePointY + "\n";
        str += "colorPointRx : " + colorPointRx + "\n";
        str += "colorPointRy : " + colorPointRy + "\n";
        str += "colorPointRIntensity : " + colorPointRIntensity + "\n";
        str += "colorPointGx : " + colorPointGx + "\n";
        str += "colorPointGy : " + colorPointGy + "\n";
        str += "colorPointGIntensity : " + colorPointGIntensity + "\n";
        str += "colorPointBx : " + colorPointBx + "\n";
        str += "colorPointBy : " + colorPointBy + "\n";
        str += "colorPointBIntensity : " + colorPointBIntensity + "\n";
        return str;
    }
}
