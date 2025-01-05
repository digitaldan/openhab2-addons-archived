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
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNull;
import org.openhab.binding.matter.internal.client.model.cluster.ClusterCommand;

/**
 * Thermostat
 *
 * @author Dan Cunningham - Initial contribution
 */
public class ThermostatCluster extends BaseCluster {

    public static final String CLUSTER_NAME = "Thermostat";
    public static final int CLUSTER_ID = 0x0201;

    public Integer clusterRevision; // 65533 ClusterRevision
    public FeatureMap featureMap; // 65532 FeatureMap
    /**
     * Indicates the current Calculated Local Temperature, when available.
     * • If the LTNE feature is not supported:
     * ◦ If the LocalTemperatureCalibration is invalid or currently unavailable, the attribute shall report null.
     * ◦ If the LocalTemperatureCalibration is valid, the attribute shall report that value.
     * • Otherwise, if the LTNE feature is supported, there is no feedback externally available for the
     * LocalTemperatureCalibration. In that case, the LocalTemperature attribute shall always report null.
     */
    public Integer localTemperature; // 0 temperature R V
    /**
     * Indicates the outdoor temperature, as measured locally or remotely (over the network).
     */
    public Integer outdoorTemperature; // 1 temperature R V
    /**
     * Indicates whether the heated/cooled space is occupied or not, as measured locally or remotely (over the network).
     */
    public OccupancyBitmap occupancy; // 2 OccupancyBitmap R V
    public Integer absMinHeatSetpointLimit; // 3 temperature R V
    public Integer absMaxHeatSetpointLimit; // 4 temperature R V
    /**
     * Indicates the absolute minimum level that the cooling setpoint may be set to. This is a limitation imposed by the
     * manufacturer.
     * Refer to Setpoint Limits for constraints
     */
    public Integer absMinCoolSetpointLimit; // 5 temperature R V
    /**
     * Indicates the absolute maximum level that the cooling setpoint may be set to. This is a limitation imposed by the
     * manufacturer.
     * Refer to Setpoint Limits for constraints
     */
    public Integer absMaxCoolSetpointLimit; // 6 temperature R V
    /**
     * Indicates the level of cooling demanded by the PI (proportional integral) control loop in use by the thermostat
     * (if any), in percent. This value is 0 when the thermostat is in “off” or “heating” mode.
     * This attribute is reported regularly and may be used to control a cooling device.
     */
    public Integer piCoolingDemand; // 7 uint8 R V
    /**
     * Indicates the level of heating demanded by the PI loop in percent. This value is 0 when the thermostat is in
     * “off” or “cooling” mode.
     * This attribute is reported regularly and may be used to control a heating device.
     */
    public Integer piHeatingDemand; // 8 uint8 R V
    /**
     * Indicates the offset the Thermostat server shall make to the measured temperature (locally or remotely) to adjust
     * the Calculated Local Temperature prior to using, displaying or reporting it.
     * The purpose of this attribute is to adjust the calibration of the Thermostat server per the user’s preferences
     * (e.g., to match if there are multiple servers displaying different values for the same HVAC area) or compensate
     * for variability amongst temperature sensors.
     * If a Thermostat client attempts to write LocalTemperatureCalibration attribute to an unsupported value (e.g., out
     * of the range supported by the Thermostat server), the Thermostat server shall respond with a status of SUCCESS
     * and set the value of LocalTemperatureCalibration to the upper or lower limit reached.
     */
    public Integer localTemperatureCalibration; // 16 SignedTemperature RW VM
    /**
     * Indicates the cooling mode setpoint when the room is occupied.
     * Refer to Setpoint Limits for constraints. If an attempt is made to set this attribute such that these constraints
     * are violated, a response with the status code CONSTRAINT_ERROR shall be returned.
     * If the occupancy status of the room is unknown, this attribute shall be used as the cooling mode setpoint.
     */
    public Integer occupiedCoolingSetpoint; // 17 temperature RW VO
    /**
     * Indicates the heating mode setpoint when the room is occupied.
     * Refer to Setpoint Limits for constraints. If an attempt is made to set this attribute such that these constraints
     * are violated, a response with the status code CONSTRAINT_ERROR shall be returned.
     * If the occupancy status of the room is unknown, this attribute shall be used as the heating mode setpoint.
     */
    public Integer occupiedHeatingSetpoint; // 18 temperature RW VO
    /**
     * Indicates the cooling mode setpoint when the room is unoccupied.
     * Refer to Setpoint Limits for constraints. If an attempt is made to set this attribute such that these constraints
     * are violated, a response with the status code CONSTRAINT_ERROR shall be returned.
     * If the occupancy status of the room is unknown, this attribute shall NOT be used.
     */
    public Integer unoccupiedCoolingSetpoint; // 19 temperature RW VO
    /**
     * Indicates the heating mode setpoint when the room is unoccupied.
     * Refer to Setpoint Limits for constraints. If an attempt is made to set this attribute such that these constraints
     * are violated, a response with the status code CONSTRAINT_ERROR shall be returned.
     * If the occupancy status of the room is unknown, this attribute shall NOT be used.
     */
    public Integer unoccupiedHeatingSetpoint; // 20 temperature RW VO
    /**
     * Indicates the minimum level that the heating setpoint may be set to.
     * This attribute, and the following three attributes, allow the user to define setpoint limits more constrictive
     * than the manufacturer imposed ones. Limiting users (e.g., in a commercial building) to such setpoint limits can
     * help conserve power.
     * Refer to Setpoint Limits for constraints. If an attempt is made to set this attribute to a value which conflicts
     * with setpoint values then those setpoints shall be adjusted by the minimum amount to permit this attribute to be
     * set to the desired value. If an attempt is made to set this attribute to a value which is not consistent with the
     * constraints and cannot be resolved by modifying setpoints then a response with the status code CONSTRAINT_ERROR
     * shall be returned.
     */
    public Integer minHeatSetpointLimit; // 21 temperature RW VM
    /**
     * Indicates the maximum level that the heating setpoint may be set to.
     * Refer to Setpoint Limits for constraints. If an attempt is made to set this attribute to a value which conflicts
     * with setpoint values then those setpoints shall be adjusted by the minimum amount to permit this attribute to be
     * set to the desired value. If an attempt is made to set this attribute to a value which is not consistent with the
     * constraints and cannot be resolved by modifying setpoints then a response with the status code CONSTRAINT_ERROR
     * shall be returned.
     */
    public Integer maxHeatSetpointLimit; // 22 temperature RW VM
    /**
     * Indicates the minimum level that the cooling setpoint may be set to.
     * Refer to Setpoint Limits for constraints. If an attempt is made to set this attribute to a value which conflicts
     * with setpoint values then those setpoints shall be adjusted by the minimum amount to permit this attribute to be
     * set to the desired value. If an attempt is made to set this attribute to a value which is not consistent with the
     * constraints and cannot be resolved by modifying setpoints then a response with the status code CONSTRAINT_ERROR
     * shall be returned.
     */
    public Integer minCoolSetpointLimit; // 23 temperature RW VM
    /**
     * Indicates the maximum level that the cooling setpoint may be set to.
     * Refer to Setpoint Limits for constraints. If an attempt is made to set this attribute to a value which conflicts
     * with setpoint values then those setpoints shall be adjusted by the minimum amount to permit this attribute to be
     * set to the desired value. If an attempt is made to set this attribute to a value which is not consistent with the
     * constraints and cannot be resolved by modifying setpoints then a response with the status code CONSTRAINT_ERROR
     * shall be returned.
     */
    public Integer maxCoolSetpointLimit; // 24 temperature RW VM
    /**
     * On devices which support the AUTO feature, this attribute shall indicate the minimum difference between the Heat
     * Setpoint and the Cool Setpoint.
     * Refer to Setpoint Limits for constraints. If an attempt is made to set this attribute to a value which conflicts
     * with setpoint values then those setpoints shall be adjusted by the minimum amount to permit this attribute to be
     * set to the desired value. If an attempt is made to set this attribute to a value which is not consistent with the
     * constraints and cannot be resolved by modifying setpoints then a response with the status code CONSTRAINT_ERROR
     * shall be returned.
     */
    public Integer minSetpointDeadBand; // 25 SignedTemperature R[W] VM
    /**
     * Indicates when the local temperature, outdoor temperature and occupancy are being sensed by remote networked
     * sensors, rather than internal sensors.
     * If the LTNE feature is present in the server, the LocalTemperature RemoteSensing bit value shall always report a
     * value of 0.
     * If the LocalTemperature RemoteSensing bit is written with a value of 1 when the LTNE feature is present, the
     * write shall fail and the server shall report a CONSTRAINT_ERROR.
     */
    public RemoteSensingBitmap remoteSensing; // 26 RemoteSensingBitmap RW VM
    /**
     * Indicates the overall operating environment of the thermostat, and thus the possible system modes that the
     * thermostat can operate in.
     */
    public ControlSequenceOfOperationEnum controlSequenceOfOperation; // 27 ControlSequenceOfOperationEnum RW VM
    /**
     * Indicates the current operating mode of the thermostat. Its value shall be limited by the
     * ControlSequenceOfOperation attribute.
     */
    public SystemModeEnum systemMode; // 28 SystemModeEnum RW VM
    /**
     * Indicates the running mode of the thermostat. This attribute uses the same values as SystemModeEnum but can only
     * be Off, Cool or Heat. This attribute is intended to provide additional information when the thermostat’s system
     * mode is in auto mode.
     */
    public ThermostatRunningModeEnum thermostatRunningMode; // 30 ThermostatRunningModeEnum R V
    /**
     * Indicates the day of the week that this thermostat considers to be the start of week for weekly setpoint
     * scheduling.
     * This attribute may be able to be used as the base to determine if the device supports weekly scheduling by
     * reading the attribute. Successful response means that the weekly scheduling is supported.
     */
    public StartOfWeekEnum startOfWeek; // 32 StartOfWeekEnum R V
    /**
     * Indicates how many weekly schedule transitions the thermostat is capable of handling.
     */
    public Integer numberOfWeeklyTransitions; // 33 uint8 R V
    /**
     * Indicates how many daily schedule transitions the thermostat is capable of handling.
     */
    public Integer numberOfDailyTransitions; // 34 uint8 R V
    /**
     * Indicates the temperature hold status on the thermostat. If hold status is on, the thermostat SHOULD maintain the
     * temperature setpoint for the current mode until a system mode change. If hold status is off, the thermostat
     * SHOULD follow the setpoint transitions specified by its internal scheduling program. If the thermostat supports
     * setpoint hold for a specific duration, it SHOULD also implement the TemperatureSetpointHoldDuration attribute.
     */
    public TemperatureSetpointHoldEnum temperatureSetpointHold; // 35 TemperatureSetpointHoldEnum RW VM
    /**
     * Indicates the period in minutes for which a setpoint hold is active. Thermostats that support hold for a
     * specified duration SHOULD implement this attribute. The null value indicates the field is unused. All other
     * values are reserved.
     */
    public Integer temperatureSetpointHoldDuration; // 36 uint16 RW VM
    /**
     * Indicates the operational state of the thermostat’s programming. The thermostat shall modify its programming
     * operation when this attribute is modified by a client and update this attribute when its programming operation is
     * modified locally by a user. The thermostat may support more than one active ProgrammingOperationModeBitmap. For
     * example, the thermostat may operate simultaneously in Schedule Programming Mode and Recovery Mode.
     * Thermostats which contain a schedule may use this attribute to control how that schedule is used, even if they do
     * not support the ScheduleConfiguration feature.
     * When ScheduleActive is not set, the setpoint is altered only by manual up/down changes at the thermostat or
     * remotely, not by internal schedule programming.
     * NOTE
     * Modifying the ScheduleActive bit does not clear or delete previous weekly schedule programming configurations.
     */
    public ProgrammingOperationModeBitmap thermostatProgrammingOperationMode; // 37 ProgrammingOperationModeBitmap RW VM
    /**
     * Indicates the current relay state of the heat, cool, and fan relays. Unimplemented outputs shall be treated as if
     * they were Off.
     */
    public RelayStateBitmap thermostatRunningState; // 41 RelayStateBitmap R V
    /**
     * Indicates the source of the current active OccupiedCoolingSetpoint or OccupiedHeatingSetpoint (i.e., who or what
     * determined the current setpoint).
     * This attribute enables service providers to determine whether changes to setpoints were initiated due to occupant
     * comfort, scheduled programming or some other source (e.g., electric utility or other service provider). Because
     * automation services may initiate frequent setpoint changes, this attribute clearly differentiates the source of
     * setpoint changes made at the thermostat.
     */
    public SetpointChangeSourceEnum setpointChangeSource; // 48 SetpointChangeSourceEnum R V
    /**
     * Indicates the delta between the current active OccupiedCoolingSetpoint or OccupiedHeatingSetpoint and the
     * previous active setpoint. This attribute is meant to accompany the SetpointChangeSource attribute; devices
     * implementing SetpointChangeAmount SHOULD also implement SetpointChangeSource.
     * The null value indicates that the previous setpoint was unknown.
     */
    public Integer setpointChangeAmount; // 49 TemperatureDifference R V
    /**
     * Indicates the time in UTC at which the SetpointChangeAmount attribute change was recorded.
     */
    public Integer setpointChangeSourceTimestamp; // 50 epoch-s R V
    /**
     * Indicates the amount that the Thermostat server will allow the Calculated Local Temperature to float above the
     * OccupiedCoolingSetpoint (i.e., OccupiedCoolingSetpoint + OccupiedSetback) or below the OccupiedHeatingSetpoint
     * setpoint (i.e., OccupiedHeatingSetpoint – OccupiedSetback) before initiating a state change to bring the
     * temperature back to the user’s desired setpoint. This attribute is sometimes also referred to as the “span.”
     * The purpose of this attribute is to allow remote configuration of the span between the desired setpoint and the
     * measured temperature to help prevent over-cycling and reduce energy bills, though this may result in lower
     * comfort on the part of some users.
     * The null value indicates the attribute is unused.
     * If the Thermostat client attempts to write OccupiedSetback to a value greater than OccupiedSetbackMax, the
     * Thermostat server shall set its OccupiedSetback value to OccupiedSetbackMax and shall send a Write Attribute
     * Response command with a Status Code field enumeration of SUCCESS response.
     * If the Thermostat client attempts to write OccupiedSetback to a value less than OccupiedSetbackMin, the
     * Thermostat server shall set its OccupiedSetback value to OccupiedSetbackMin and shall send a Write Attribute
     * Response command with a Status Code field enumeration of SUCCESS response.
     */
    public Integer occupiedSetback; // 52 UnsignedTemperature RW VM
    /**
     * Indicates the minimum value that the Thermostat server will allow the OccupiedSetback attribute to be configured
     * by a user.
     * The null value indicates the attribute is unused.
     */
    public Integer occupiedSetbackMin; // 53 UnsignedTemperature R V
    /**
     * Indicates the maximum value that the Thermostat server will allow the OccupiedSetback attribute to be configured
     * by a user.
     * The null value indicates the attribute is unused.
     */
    public Integer occupiedSetbackMax; // 54 UnsignedTemperature R V
    /**
     * Indicates the amount that the Thermostat server will allow the Calculated Local Temperature to float above the
     * UnoccupiedCoolingSetpoint (i.e., UnoccupiedCoolingSetpoint + UnoccupiedSetback) or below the
     * UnoccupiedHeatingSetpoint setpoint (i.e., UnoccupiedHeatingSetpoint - UnoccupiedSetback) before initiating a
     * state change to bring the temperature back to the user’s desired setpoint. This attribute is sometimes also
     * referred to as the “span.”
     * The purpose of this attribute is to allow remote configuration of the span between the desired setpoint and the
     * measured temperature to help prevent over-cycling and reduce energy bills, though this may result in lower
     * comfort on the part of some users.
     * The null value indicates the attribute is unused.
     * If the Thermostat client attempts to write UnoccupiedSetback to a value greater than UnoccupiedSetbackMax, the
     * Thermostat server shall set its UnoccupiedSetback value to UnoccupiedSetbackMax and shall send a Write Attribute
     * Response command with a Status Code field enumeration of SUCCESS response.
     * If the Thermostat client attempts to write UnoccupiedSetback to a value less than UnoccupiedSetbackMin, the
     * Thermostat server shall set its UnoccupiedSetback value to UnoccupiedSetbackMin and shall send a Write Attribute
     * Response command with a Status Code field enumeration of SUCCESS response.
     */
    public Integer unoccupiedSetback; // 55 UnsignedTemperature RW VM
    /**
     * Indicates the minimum value that the Thermostat server will allow the UnoccupiedSetback attribute to be
     * configured by a user.
     * The null value indicates the attribute is unused.
     */
    public Integer unoccupiedSetbackMin; // 56 UnsignedTemperature R V
    /**
     * Indicates the maximum value that the Thermostat server will allow the UnoccupiedSetback attribute to be
     * configured by a user.
     * The null value indicates the attribute is unused.
     */
    public Integer unoccupiedSetbackMax; // 57 UnsignedTemperature R V
    /**
     * Indicates the delta between the Calculated Local Temperature and the OccupiedHeatingSetpoint or
     * UnoccupiedHeatingSetpoint attributes at which the Thermostat server will operate in emergency heat mode.
     * If the difference between the Calculated Local Temperature and OccupiedCoolingSetpoint or
     * UnoccupiedCoolingSetpoint is greater than or equal to the EmergencyHeatDelta and the Thermostat server’s
     * SystemMode attribute is in a heating-related mode, then the Thermostat server shall immediately switch to the
     * SystemMode attribute value that provides the highest stage of heating (e.g., emergency heat) and continue
     * operating in that running state until the OccupiedHeatingSetpoint value is reached. For example:
     * • Calculated Local Temperature &#x3D; 10.0°C
     * • OccupiedHeatingSetpoint &#x3D; 16.0°C
     * • EmergencyHeatDelta &#x3D; 2.0°C
     * ⇒ OccupiedHeatingSetpoint - Calculated Local Temperature ≥? EmergencyHeatDelta
     * ⇒ 16°C - 10°C ≥? 2°C
     * ⇒ TRUE &gt;&gt;&gt; Thermostat server changes its SystemMode to operate in 2nd stage or emergency heat mode
     * The purpose of this attribute is to provide Thermostat clients the ability to configure rapid heating when a
     * setpoint is of a specified amount greater than the measured temperature. This allows the heated space to be
     * quickly heated to the desired level set by the user.
     */
    public Integer emergencyHeatDelta; // 58 UnsignedTemperature RW VM
    /**
     * Indicates the type of Mini Split ACTypeEnum of Mini Split AC is defined depending on how Cooling and Heating
     * condition is achieved by Mini Split AC.
     */
    public ACTypeEnum acType; // 64 ACTypeEnum RW VM
    /**
     * Indicates capacity of Mini Split AC in terms of the format defined by the ACCapacityFormat attribute
     */
    public Integer acCapacity; // 65 uint16 RW VM
    /**
     * Indicates type of refrigerant used within the Mini Split AC.
     */
    public ACRefrigerantTypeEnum acRefrigerantType; // 66 ACRefrigerantTypeEnum RW VM
    /**
     * Indicates the type of compressor used within the Mini Split AC.
     */
    public ACCompressorTypeEnum acCompressorType; // 67 ACCompressorTypeEnum RW VM
    /**
     * Indicates the type of errors encountered within the Mini Split AC.
     */
    public ACErrorCodeBitmap acErrorCode; // 68 ACErrorCodeBitmap RW VM
    /**
     * Indicates the position of Louver on the AC.
     */
    public ACLouverPositionEnum acLouverPosition; // 69 ACLouverPositionEnum RW VM
    /**
     * Indicates the temperature of the AC coil, as measured locally or remotely (over the network).
     */
    public Integer acCoilTemperature; // 70 temperature R V
    /**
     * Indicates the format for the ACCapacity attribute.
     */
    public ACCapacityFormatEnum acCapacityFormat; // 71 ACCapacityFormatEnum RW VM
    // Structs

    /**
     * This represents a single transition in a Thermostat schedule
     */
    public class WeeklyScheduleTransitionStruct {
        /**
         * This field shall represent the start time of the schedule transition during the associated day. The time will
         * be represented by a 16 bits unsigned integer to designate the minutes since midnight. For example, 6am will
         * be represented by 360 minutes since midnight and 11:30pm will be represented by 1410 minutes since midnight.
         */
        public Integer transitionTime; // uint16
        /**
         * This field shall represent the heat setpoint to be applied at this associated transition start time.
         */
        public Integer heatSetpoint; // temperature
        /**
         * This field shall represent the cool setpoint to be applied at this associated transition start time.
         */
        public Integer coolSetpoint; // temperature

        public WeeklyScheduleTransitionStruct(Integer transitionTime, Integer heatSetpoint, Integer coolSetpoint) {
            this.transitionTime = transitionTime;
            this.heatSetpoint = heatSetpoint;
            this.coolSetpoint = coolSetpoint;
        }
    }

    // Enums
    public enum ACCapacityFormatEnum implements MatterEnum {
        BT_UH(0, "BtUh");

        public final Integer value;
        public final String label;

        private ACCapacityFormatEnum(Integer value, String label) {
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

    public enum ACCompressorTypeEnum implements MatterEnum {
        UNKNOWN(0, "Unknown"),
        T1(1, "T1"),
        T2(2, "T2"),
        T3(3, "T3");

        public final Integer value;
        public final String label;

        private ACCompressorTypeEnum(Integer value, String label) {
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

    public enum ACLouverPositionEnum implements MatterEnum {
        CLOSED(1, "Closed"),
        OPEN(2, "Open"),
        QUARTER(3, "Quarter"),
        HALF(4, "Half"),
        THREE_QUARTERS(5, "ThreeQuarters");

        public final Integer value;
        public final String label;

        private ACLouverPositionEnum(Integer value, String label) {
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

    public enum ACRefrigerantTypeEnum implements MatterEnum {
        UNKNOWN(0, "Unknown"),
        R22(1, "R22"),
        R410A(2, "R410A"),
        R407C(3, "R407C");

        public final Integer value;
        public final String label;

        private ACRefrigerantTypeEnum(Integer value, String label) {
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

    public enum ACTypeEnum implements MatterEnum {
        UNKNOWN(0, "Unknown"),
        COOLING_FIXED(1, "CoolingFixed"),
        HEAT_PUMP_FIXED(2, "HeatPumpFixed"),
        COOLING_INVERTER(3, "CoolingInverter"),
        HEAT_PUMP_INVERTER(4, "HeatPumpInverter");

        public final Integer value;
        public final String label;

        private ACTypeEnum(Integer value, String label) {
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

    public enum SetpointRaiseLowerModeEnum implements MatterEnum {
        HEAT(0, "Heat"),
        COOL(1, "Cool"),
        BOTH(2, "Both");

        public final Integer value;
        public final String label;

        private SetpointRaiseLowerModeEnum(Integer value, String label) {
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
     * NOTE
     * CoolingAndHeating
     * A thermostat indicating it supports CoolingAndHeating (or CoolingAndHeatingWithReheat) SHOULD be able to request
     * heating or cooling on demand and will usually support the Auto SystemMode.
     * Systems which support cooling or heating, requiring external intervention to change modes or where the whole
     * building must be in the same mode, SHOULD report CoolingOnly or HeatingOnly based on the current capability.
     */
    public enum ControlSequenceOfOperationEnum implements MatterEnum {
        COOLING_ONLY(0, "CoolingOnly"),
        COOLING_WITH_REHEAT(1, "CoolingWithReheat"),
        HEATING_ONLY(2, "HeatingOnly"),
        HEATING_WITH_REHEAT(3, "HeatingWithReheat"),
        COOLING_AND_HEATING(4, "CoolingAndHeating"),
        COOLING_AND_HEATING_WITH_REHEAT(5, "CoolingAndHeatingWithReheat");

        public final Integer value;
        public final String label;

        private ControlSequenceOfOperationEnum(Integer value, String label) {
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

    public enum SetpointChangeSourceEnum implements MatterEnum {
        MANUAL(0, "Manual"),
        SCHEDULE(1, "Schedule"),
        EXTERNAL(2, "External");

        public final Integer value;
        public final String label;

        private SetpointChangeSourceEnum(Integer value, String label) {
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

    public enum StartOfWeekEnum implements MatterEnum {
        SUNDAY(0, "Sunday"),
        MONDAY(1, "Monday"),
        TUESDAY(2, "Tuesday"),
        WEDNESDAY(3, "Wednesday"),
        THURSDAY(4, "Thursday"),
        FRIDAY(5, "Friday"),
        SATURDAY(6, "Saturday");

        public final Integer value;
        public final String label;

        private StartOfWeekEnum(Integer value, String label) {
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
     * Table 35. Interpretation of Heat, Cool and Auto SystemModeEnum Values
     */
    public enum SystemModeEnum implements MatterEnum {
        OFF(0, "Off"),
        AUTO(1, "Auto"),
        COOL(3, "Cool"),
        HEAT(4, "Heat"),
        EMERGENCY_HEAT(5, "EmergencyHeat"),
        PRECOOLING(6, "Precooling"),
        FAN_ONLY(7, "FanOnly"),
        DRY(8, "Dry"),
        SLEEP(9, "Sleep");

        public final Integer value;
        public final String label;

        private SystemModeEnum(Integer value, String label) {
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

    public enum ThermostatRunningModeEnum implements MatterEnum {
        OFF(0, "Off"),
        COOL(3, "Cool"),
        HEAT(4, "Heat");

        public final Integer value;
        public final String label;

        private ThermostatRunningModeEnum(Integer value, String label) {
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

    public enum TemperatureSetpointHoldEnum implements MatterEnum {
        SETPOINT_HOLD_OFF(0, "SetpointHoldOff"),
        SETPOINT_HOLD_ON(1, "SetpointHoldOn");

        public final Integer value;
        public final String label;

        private TemperatureSetpointHoldEnum(Integer value, String label) {
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
    public static class ACErrorCodeBitmap {
        public boolean compressorFail;
        public boolean roomSensorFail;
        public boolean outdoorSensorFail;
        public boolean coilSensorFail;
        public boolean fanFail;

        public ACErrorCodeBitmap(boolean compressorFail, boolean roomSensorFail, boolean outdoorSensorFail,
                boolean coilSensorFail, boolean fanFail) {
            this.compressorFail = compressorFail;
            this.roomSensorFail = roomSensorFail;
            this.outdoorSensorFail = outdoorSensorFail;
            this.coilSensorFail = coilSensorFail;
            this.fanFail = fanFail;
        }
    }

    public static class AlarmCodeBitmap {
        public boolean initialization;
        public boolean hardware;
        public boolean selfCalibration;

        public AlarmCodeBitmap(boolean initialization, boolean hardware, boolean selfCalibration) {
            this.initialization = initialization;
            this.hardware = hardware;
            this.selfCalibration = selfCalibration;
        }
    }

    public static class HVACSystemTypeBitmap {
        /**
         * Stage of cooling the HVAC system is using.
         * These bits shall indicate what stage of cooling the HVAC system is using.
         * • 00 &#x3D; Cool Stage 1
         * • 01 &#x3D; Cool Stage 2
         * • 10 &#x3D; Cool Stage 3
         * • 11 &#x3D; Reserved
         */
        public short coolingStage;
        /**
         * Stage of heating the HVAC system is using.
         * These bits shall indicate what stage of heating the HVAC system is using.
         * • 00 &#x3D; Heat Stage 1
         * • 01 &#x3D; Heat Stage 2
         * • 10 &#x3D; Heat Stage 3
         * • 11 &#x3D; Reserved
         */
        public short heatingStage;
        /**
         * Is the heating type Heat Pump.
         * This bit shall indicate whether the HVAC system is conventional or a heat pump.
         * • 0 &#x3D; Conventional
         * • 1 &#x3D; Heat Pump
         */
        public boolean heatingIsHeatPump;
        /**
         * Does the HVAC system use fuel.
         * This bit shall indicate whether the HVAC system uses fuel.
         * • 0 &#x3D; Does not use fuel
         * • 1 &#x3D; Uses fuel
         */
        public boolean heatingUsesFuel;

        public HVACSystemTypeBitmap(short coolingStage, short heatingStage, boolean heatingIsHeatPump,
                boolean heatingUsesFuel) {
            this.coolingStage = coolingStage;
            this.heatingStage = heatingStage;
            this.heatingIsHeatPump = heatingIsHeatPump;
            this.heatingUsesFuel = heatingUsesFuel;
        }
    }

    public static class ProgrammingOperationModeBitmap {
        public boolean scheduleActive;
        public boolean autoRecovery;
        public boolean economy;

        public ProgrammingOperationModeBitmap(boolean scheduleActive, boolean autoRecovery, boolean economy) {
            this.scheduleActive = scheduleActive;
            this.autoRecovery = autoRecovery;
            this.economy = economy;
        }
    }

    public static class RelayStateBitmap {
        public boolean heat;
        public boolean cool;
        public boolean fan;
        public boolean heatStage2;
        public boolean coolStage2;
        public boolean fanStage2;
        public boolean fanStage3;

        public RelayStateBitmap(boolean heat, boolean cool, boolean fan, boolean heatStage2, boolean coolStage2,
                boolean fanStage2, boolean fanStage3) {
            this.heat = heat;
            this.cool = cool;
            this.fan = fan;
            this.heatStage2 = heatStage2;
            this.coolStage2 = coolStage2;
            this.fanStage2 = fanStage2;
            this.fanStage3 = fanStage3;
        }
    }

    public static class RemoteSensingBitmap {
        public boolean localTemperature;
        /**
         * OutdoorTemperature is derived from a remote node
         * This bit shall be supported if the OutdoorTemperature attribute is supported.
         */
        public boolean outdoorTemperature;
        public boolean occupancy;

        public RemoteSensingBitmap(boolean localTemperature, boolean outdoorTemperature, boolean occupancy) {
            this.localTemperature = localTemperature;
            this.outdoorTemperature = outdoorTemperature;
            this.occupancy = occupancy;
        }
    }

    public static class ScheduleDayOfWeekBitmap {
        public boolean sunday;
        public boolean monday;
        public boolean tuesday;
        public boolean wednesday;
        public boolean thursday;
        public boolean friday;
        public boolean saturday;
        public boolean away;

        public ScheduleDayOfWeekBitmap(boolean sunday, boolean monday, boolean tuesday, boolean wednesday,
                boolean thursday, boolean friday, boolean saturday, boolean away) {
            this.sunday = sunday;
            this.monday = monday;
            this.tuesday = tuesday;
            this.wednesday = wednesday;
            this.thursday = thursday;
            this.friday = friday;
            this.saturday = saturday;
            this.away = away;
        }
    }

    public static class ScheduleModeBitmap {
        public boolean heatSetpointPresent;
        public boolean coolSetpointPresent;

        public ScheduleModeBitmap(boolean heatSetpointPresent, boolean coolSetpointPresent) {
            this.heatSetpointPresent = heatSetpointPresent;
            this.coolSetpointPresent = coolSetpointPresent;
        }
    }

    public static class FeatureMap {
        /**
         * Heating
         * Thermostat is capable of managing a heating device
         */
        public boolean heating;
        /**
         * Cooling
         * Thermostat is capable of managing a cooling device
         */
        public boolean cooling;
        /**
         * Occupancy
         * Supports Occupied and Unoccupied setpoints
         */
        public boolean occupancy;
        /**
         * ScheduleConfiguration
         * Supports remote configuration of a weekly schedule of setpoint transitions
         */
        public boolean scheduleConfiguration;
        /**
         * Setback
         * Supports configurable setback (or span)
         */
        public boolean setback;
        /**
         * AutoMode
         * Supports a System Mode of Auto
         */
        public boolean autoMode;
        /**
         * LocalTemperatureNotExposed
         * This feature indicates that the Calculated Local Temperature used internally is unavailable to report
         * externally, for example due to the temperature control being done by a separate subsystem which does not
         * offer a view into the currently measured temperature, but allows setpoints to be provided.
         */
        public boolean localTemperatureNotExposed;

        public FeatureMap(boolean heating, boolean cooling, boolean occupancy, boolean scheduleConfiguration,
                boolean setback, boolean autoMode, boolean localTemperatureNotExposed) {
            this.heating = heating;
            this.cooling = cooling;
            this.occupancy = occupancy;
            this.scheduleConfiguration = scheduleConfiguration;
            this.setback = setback;
            this.autoMode = autoMode;
            this.localTemperatureNotExposed = localTemperatureNotExposed;
        }
    }

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

    public ThermostatCluster(BigInteger nodeId, int endpointId) {
        super(nodeId, endpointId, 513, "Thermostat");
    }

    // commands
    /**
     * Upon receipt, the attributes for the indicated setpoint(s) shall have the amount specified in the Amount field
     * added to them. If the resulting value is outside the limits imposed by MinCoolSetpointLimit,
     * MaxCoolSetpointLimit, MinHeatSetpointLimit and MaxHeatSetpointLimit, the value is clamped to those limits. This
     * is not considered an error condition.
     */
    public static ClusterCommand setpointRaiseLower(SetpointRaiseLowerModeEnum mode, Integer amount) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("mode", mode);
        map.put("amount", amount);
        return new ClusterCommand("setpointRaiseLower", map);
    }

    /**
     * Upon receipt, the weekly schedule for updating setpoints shall be stored in the thermostat and SHOULD begin at
     * the time of receipt. A status code shall be sent in response.
     * When a command is received that requires a total number of transitions greater than the device supports, the
     * status of the response shall be INSUFFICIENT_SPACE.
     * When any of the setpoints sent in the sequence is out of range (AbsMin/MaxSetPointLimit), or when the Mode for
     * Sequence field includes a mode not supported by the device, the status of the response shall be CONSTRAINT_ERROR
     * and no setpoints from the entire sequence SHOULD be used.
     * When an overlapping transition is detected, the status of the response shall be FAILURE.
     * When a device which does not support multiple days in a command receives a command with more than one bit set in
     * the DayOfWeekForSequence field, or when a device which does not support multiple modes in a command receives a
     * command with more than one bit set in the ModeForSequence field, or when the contents of the Transitions field
     * does not agree with NumberOfTransitionsForSequence, DayOfWeekForSequence or ModeForSequence, the status of the
     * response shall be INVALID_COMMAND.
     * When the transitions could be added successfully, the status of the response shall be SUCCESS.
     * The set weekly schedule command is used to update the thermostat weekly setpoint schedule from a management
     * system. If the thermostat already has a weekly setpoint schedule programmed, then it SHOULD replace each daily
     * setpoint set as it receives the updates from the management system. For example, if the thermostat has 4
     * setpoints for every day of the week and is sent a Set Weekly Schedule command with one setpoint for Saturday then
     * the thermostat SHOULD remove all 4 setpoints for Saturday and replace those with the updated setpoint but leave
     * all other days unchanged. If the schedule is larger than what fits in one frame or contains more than 10
     * transitions, the schedule shall then be sent using multiple Set Weekly Schedule Commands.
     */
    public static ClusterCommand setWeeklySchedule(Integer numberOfTransitionsForSequence,
            ScheduleDayOfWeekBitmap dayOfWeekForSequence, ScheduleModeBitmap modeForSequence,
            List<WeeklyScheduleTransitionStruct> transitions) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("numberOfTransitionsForSequence", numberOfTransitionsForSequence);
        map.put("dayOfWeekForSequence", dayOfWeekForSequence);
        map.put("modeForSequence", modeForSequence);
        map.put("transitions", transitions);
        return new ClusterCommand("setWeeklySchedule", map);
    }

    /**
     * Upon receipt, the unit SHOULD send in return the Get Weekly Schedule Response command. The Days to Return and
     * Mode to Return fields are defined as bitmask for the flexibility to support multiple days and multiple modes
     * within one command. If thermostat cannot handle incoming command with multiple days and/or multiple modes within
     * one command, it shall send default response of INVALID_COMMAND in return.
     */
    public static ClusterCommand getWeeklySchedule(ScheduleDayOfWeekBitmap daysToReturn,
            ScheduleModeBitmap modeToReturn) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("daysToReturn", daysToReturn);
        map.put("modeToReturn", modeToReturn);
        return new ClusterCommand("getWeeklySchedule", map);
    }

    /**
     * This command is used to clear the weekly schedule. The Clear weekly schedule has no payload.
     * Upon receipt, all transitions currently stored shall be cleared and a default response of SUCCESS shall be sent
     * in response. There are no error responses to this command.
     */
    public static ClusterCommand clearWeeklySchedule() {
        return new ClusterCommand("clearWeeklySchedule");
    }

    @Override
    public @NonNull String toString() {
        String str = "";
        str += "clusterRevision : " + clusterRevision + "\n";
        str += "featureMap : " + featureMap + "\n";
        str += "localTemperature : " + localTemperature + "\n";
        str += "outdoorTemperature : " + outdoorTemperature + "\n";
        str += "occupancy : " + occupancy + "\n";
        str += "absMinHeatSetpointLimit : " + absMinHeatSetpointLimit + "\n";
        str += "absMaxHeatSetpointLimit : " + absMaxHeatSetpointLimit + "\n";
        str += "absMinCoolSetpointLimit : " + absMinCoolSetpointLimit + "\n";
        str += "absMaxCoolSetpointLimit : " + absMaxCoolSetpointLimit + "\n";
        str += "piCoolingDemand : " + piCoolingDemand + "\n";
        str += "piHeatingDemand : " + piHeatingDemand + "\n";
        str += "localTemperatureCalibration : " + localTemperatureCalibration + "\n";
        str += "occupiedCoolingSetpoint : " + occupiedCoolingSetpoint + "\n";
        str += "occupiedHeatingSetpoint : " + occupiedHeatingSetpoint + "\n";
        str += "unoccupiedCoolingSetpoint : " + unoccupiedCoolingSetpoint + "\n";
        str += "unoccupiedHeatingSetpoint : " + unoccupiedHeatingSetpoint + "\n";
        str += "minHeatSetpointLimit : " + minHeatSetpointLimit + "\n";
        str += "maxHeatSetpointLimit : " + maxHeatSetpointLimit + "\n";
        str += "minCoolSetpointLimit : " + minCoolSetpointLimit + "\n";
        str += "maxCoolSetpointLimit : " + maxCoolSetpointLimit + "\n";
        str += "minSetpointDeadBand : " + minSetpointDeadBand + "\n";
        str += "remoteSensing : " + remoteSensing + "\n";
        str += "controlSequenceOfOperation : " + controlSequenceOfOperation + "\n";
        str += "systemMode : " + systemMode + "\n";
        str += "thermostatRunningMode : " + thermostatRunningMode + "\n";
        str += "startOfWeek : " + startOfWeek + "\n";
        str += "numberOfWeeklyTransitions : " + numberOfWeeklyTransitions + "\n";
        str += "numberOfDailyTransitions : " + numberOfDailyTransitions + "\n";
        str += "temperatureSetpointHold : " + temperatureSetpointHold + "\n";
        str += "temperatureSetpointHoldDuration : " + temperatureSetpointHoldDuration + "\n";
        str += "thermostatProgrammingOperationMode : " + thermostatProgrammingOperationMode + "\n";
        str += "thermostatRunningState : " + thermostatRunningState + "\n";
        str += "setpointChangeSource : " + setpointChangeSource + "\n";
        str += "setpointChangeAmount : " + setpointChangeAmount + "\n";
        str += "setpointChangeSourceTimestamp : " + setpointChangeSourceTimestamp + "\n";
        str += "occupiedSetback : " + occupiedSetback + "\n";
        str += "occupiedSetbackMin : " + occupiedSetbackMin + "\n";
        str += "occupiedSetbackMax : " + occupiedSetbackMax + "\n";
        str += "unoccupiedSetback : " + unoccupiedSetback + "\n";
        str += "unoccupiedSetbackMin : " + unoccupiedSetbackMin + "\n";
        str += "unoccupiedSetbackMax : " + unoccupiedSetbackMax + "\n";
        str += "emergencyHeatDelta : " + emergencyHeatDelta + "\n";
        str += "acType : " + acType + "\n";
        str += "acCapacity : " + acCapacity + "\n";
        str += "acRefrigerantType : " + acRefrigerantType + "\n";
        str += "acCompressorType : " + acCompressorType + "\n";
        str += "acErrorCode : " + acErrorCode + "\n";
        str += "acLouverPosition : " + acLouverPosition + "\n";
        str += "acCoilTemperature : " + acCoilTemperature + "\n";
        str += "acCapacityFormat : " + acCapacityFormat + "\n";
        return str;
    }
}
