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

import org.openhab.binding.matter.internal.client.model.cluster.BaseCluster;
import org.openhab.binding.matter.internal.client.model.cluster.ClusterCommand;
import org.openhab.binding.matter.internal.client.model.cluster.gen.DataTypes.*;

/**
 * DeviceEnergyManagement
 *
 * @author Dan Cunningham - Initial contribution
 */
public class DeviceEnergyManagementCluster extends BaseCluster {

    public static final String CLUSTER_NAME = "DeviceEnergyManagement";
    public static final int CLUSTER_ID = 0x0098;

    public Integer clusterRevision; // 65533 ClusterRevision
    public FeatureMap featureMap; // 65532 FeatureMap
    /**
     * Indicates the type of ESA.
     * This attribute enables an EMS to understand some of the basic properties about how the energy may be consumed,
     * generated, and stored by the ESA.
     * For example, the heat energy converted by a heat pump will naturally be lost through the building to the outdoor
     * environment relatively quickly, compared to storing heat in a well-insulated hot water tank. Similarly, battery
     * storage and EVs can store electrical energy for much longer durations.
     * This attribute can also help the EMS display information to a user and to make basic assumptions about typical
     * best use of energy. For example, an EVSE may not always have an EV plugged in, so knowing the type of ESA that is
     * being controlled can allow advanced energy management strategies.
     */
    public ESATypeEnum esaType; // 0 ESATypeEnum R V
    /**
     * Indicates whether the ESA is classed as a generator or load. This allows an EMS to understand whether the power
     * values reported by the ESA need to have their sign inverted when dealing with forecasts and adjustments.
     * For example, a solar PV inverter (being a generator) may produce positive values to indicate generation, however
     * an EMS when predicting the total home load would need to subtract these positive values from the loads to compute
     * a net import at the grid meter.
     * For example, a home battery storage system (BESS) which needs to charge the battery and then discharge to the
     * home loads, would be classed as a generator. These types of devices shall have this field set to true. When
     * generating its forecast or advertising its PowerAdjustmentCapability, the power values shall be positive to
     * indicate discharging to the loads in the home, and negative to indicate when it is charging its battery.
     * GRID meter &#x3D; Σ LoadPowers - Σ GeneratorPowers
     * Example:
     */
    public Boolean esaCanGenerate; // 1 bool R V
    /**
     * Indicates the current state of the ESA.
     * If the ESA is in the Offline or Fault state it cannot be controlled by an EMS, and may not be able to report its
     * Forecast information. An EMS may subscribe to the ESAState to get notified about changes in operational state.
     * The ESA may have a local user interface to allow a service technician to put the ESA into Offline mode, for
     * example to avoid the EMS accidentally starting or stopping the appliance when it is being serviced or tested.
     */
    public ESAStateEnum esaState; // 2 ESAStateEnum R V
    /**
     * Indicates the minimum electrical power that the ESA can consume when switched on. This does not include when in
     * power save or standby modes.
     * Note that for Generator ESAs that can charge an internal battery (such as a battery storage inverter), the
     * AbsMinPower will be a negative number representing the maximum power that the ESA can charge its internal
     * battery.
     */
    public BigInteger absMinPower; // 3 power-mW R V
    /**
     * Indicates the maximum electrical power that the ESA can consume when switched on.
     * The value of the AbsMaxPower attribute shall be limited
     * AbsMaxPower &gt;&#x3D; AbsMinPower
     * Note that for Generator ESAs that can discharge a battery to loads in the home (such as a battery storage
     * inverter), the AbsMaxPower will be a positive number representing the maximum power at which the ESA can
     * discharge its internal battery.
     * For example, a battery storage inverter that can charge its battery at a maximum power of 2000W and can discharge
     * the battery at a maximum power of 3000W, would have a AbsMinPower: -2000, AbsMaxPower: 3000W.
     */
    public BigInteger absMaxPower; // 4 power-mW R V
    /**
     * Indicates how the ESA can be adjusted at the current time. This attribute SHOULD be updated regularly by ESAs.
     * For example, a battery storage inverter may need to regulate its internal temperature, or the charging rate of
     * the battery may be limited due to cold temperatures, or a change in the state of charge of the battery may mean
     * that the maximum charging or discharging rate is limited.
     * An empty list shall indicate that no power adjustment is currently possible.
     * Multiple entries in the list allow to indicate that permutations of scenarios may be possible.
     * For example, a 10kWh battery could be at 80% state of charge. If charging at 2kW, then it would be full in 1
     * hour. However, it could be discharged at 2kW for 4 hours.
     * In this example the list of PowerAdjustStructs allows multiple scenarios to be offered as follows:
     */
    public List<PowerAdjustStruct> powerAdjustmentCapability; // 5 list R V
    /**
     * This attribute allows an ESA to share its intended forecast with a client (such as an Energy Management System).
     * A null value indicates that there is no forecast currently available
     * yet been selected by the user).
     * A server may reset this value attribute to null on a reboot, and it does not need to persist any previous
     * forecasts.
     */
    public ForecastStruct forecast; // 6 ForecastStruct R V
    /**
     * Indicates the current Opt-Out state of the ESA. The ESA may have a local user interface to allow the user to
     * control this OptOutState. An EMS may subscribe to the OptOutState to get notified about changes in operational
     * state.
     * If the ESA is in the LocalOptOut or OptOut states, so it cannot be controlled by an EMS for local optimization
     * reasons, it shall reject any commands which have the AdjustmentCauseEnum value LocalOptimization. If the ESA is
     * in the GridOptOut or OptOut states, so it cannot be controlled by an EMS for grid optimization reasons, it shall
     * reject any commands which have the AdjustmentCauseEnum value GridOptimization. If the ESA is in the LocalOptOut,
     * GridOptOut, or NoOptOut states, the device is still permitted to optimize its own energy usage, for example,
     * using tariff information it may obtain.
     */
    public OptOutStateEnum optOutState; // 7 OptOutStateEnum R V
    // Structs

    /**
     * This indicates a generic mechanism for expressing cost to run an appliance, in terms of financial, GHG emissions,
     * comfort value etc.
     */
    public class CostStruct {
        /**
         * This field shall indicate the type of cost being represented (see CostTypeEnum).
         */
        public CostTypeEnum costType; // CostTypeEnum
        /**
         * This field shall indicate the value of the cost. This may be negative (indicating that it is not a cost, but
         * a free benefit).
         * For example, if the Value was -302 and DecimalPoints was 2, then this would represent a benefit of 3.02.
         */
        public Integer value; // int32
        /**
         * This field shall indicate the number of digits to the right of the decimal point in the Value field. For
         * example, if the Value was 102 and DecimalPoints was 2, then this would represent a cost of 1.02.
         */
        public Integer decimalPoints; // uint8
        /**
         * Indicates the currency for the value in the Value field. The value of the currency field shall match the
         * values defined by [ISO 4217].
         * This is an optional field. It shall be included if CostType is Financial.
         */
        public Integer currency; // uint16

        public CostStruct(CostTypeEnum costType, Integer value, Integer decimalPoints, Integer currency) {
            this.costType = costType;
            this.value = value;
            this.decimalPoints = decimalPoints;
            this.currency = currency;
        }
    }

    public class PowerAdjustStruct {
        /**
         * This field shall indicate the minimum power that the ESA can have its power adjusted to.
         * Note that this is a signed value. Negative values indicate power flows away from loads (e.g. charging a
         * battery inverter).
         * MinPower shall be less than MaxPower.
         */
        public BigInteger minPower; // power-mW
        /**
         * This field shall indicate the maximum power that the ESA can have its power adjusted to.
         * Note that this is a signed value. Negative values indicate power flows away from loads (e.g. charging a
         * battery inverter).
         * MinPower shall be less than MaxPower.
         * For example, if the charging current of an EVSE can be adjusted within the range of 6A to 32A on a 230V
         * supply, then the power adjustment range is between 1380W and 7360W. Here the MinPower would be 1380W, and
         * MaxPower would be 7360W.
         * For example, if a battery storage inverter can discharge between 0 to 3000W towards a load, then its MinPower
         * would be 0W and its MaxPower would be 3000W.
         * In another example, if a battery storage inverter can charge its internal battery, between 0W and 2000W. Here
         * power is flowing away from the home loads, so the power values need to be negative. As such the MinPower
         * becomes -2000W and MaxPower becomes 0W.
         */
        public BigInteger maxPower; // power-mW
        /**
         * This field shall indicate the minimum duration, in seconds, that a controller may invoke an ESA power
         * adjustment. Manufacturers may use this to as an anti-cycling capability to avoid controllers from rapidly
         * making power adjustments.
         * Note that MinDuration shall be less than MaxDuration.
         */
        public Integer minDuration; // elapsed-s
        /**
         * This field shall indicate the maximum duration, in seconds, that a controller may invoke an ESA power
         * adjustment. Manufacturers may use this to protect the user experience, to avoid over heating of the ESA,
         * ensuring that there is sufficient headroom to use or store energy in the ESA or for any other reason.
         * Note that MinDuration shall be less than MaxDuration.
         */
        public Integer maxDuration; // elapsed-s

        public PowerAdjustStruct(BigInteger minPower, BigInteger maxPower, Integer minDuration, Integer maxDuration) {
            this.minPower = minPower;
            this.maxPower = maxPower;
            this.minDuration = minDuration;
            this.maxDuration = maxDuration;
        }
    }

    /**
     * This indicates a list of &#x27;slots&#x27; describing the overall timing of the ESA’s planned energy and power
     * use, with different power and energy demands per slot. For example, slots might be used to describe the distinct
     * stages of a washing machine cycle.
     * Where an ESA does not know the actual power and energy use of the system, it may support the SFR
     * feature and instead report its internal state.
     */
    public class ForecastStruct {
        /**
         * This field shall indicate the sequence number for the current forecast. If the ESA updates a forecast, it
         * shall monotonically increase this value.
         * The ESA does not need to persist this value across reboots, since the EMS SHOULD be able to detect that any
         * previous subscriptions are lost if a device reboots. The loss of a subscription and subsequent
         * re-subscription allows the EMS to learn about any new forecasts.
         * The value of ForecastId is allowed to wrap.
         */
        public Integer forecastId; // uint16
        /**
         * This field shall indicate which element of the Slots list is currently active in the Forecast sequence. A
         * null value indicates that the sequence has not yet started.
         */
        public Integer activeSlotNumber; // uint16
        /**
         * This field shall indicate the planned start time, in UTC, for the entire Forecast.
         */
        public Integer startTime; // epoch-s
        /**
         * This field shall indicate the planned end time, in UTC, for the entire Forecast.
         */
        public Integer endTime; // epoch-s
        /**
         * This field shall indicate the earliest start time, in UTC, that the entire Forecast can be shifted to.
         * A null value indicates that it can be started immediately.
         */
        public Integer earliestStartTime; // epoch-s
        /**
         * This field shall indicate the latest end time, in UTC, for the entire Forecast.
         * e.g. for an EVSE charging session, this may indicate the departure time for the vehicle, by which time the
         * charging session must end.
         */
        public Integer latestEndTime; // epoch-s
        /**
         * This field shall indicate that some part of the Forecast can be paused. It aims to allow a client to read
         * this flag and if it is false, then none of the slots contain SlotIsPausable set to true. This can save a
         * client from having to check each slot in the list.
         */
        public Boolean isPauseable; // bool
        /**
         * This field shall contain a list of SlotStructs.
         * It shall contain at least 1 entry, and a maximum of 10.
         */
        public List<SlotStruct> slots; // list
        /**
         * This field shall contain the reason the current Forecast was generated.
         */
        public ForecastUpdateReasonEnum forecastUpdateReason; // ForecastUpdateReasonEnum

        public ForecastStruct(Integer forecastId, Integer activeSlotNumber, Integer startTime, Integer endTime,
                Integer earliestStartTime, Integer latestEndTime, Boolean isPauseable, List<SlotStruct> slots,
                ForecastUpdateReasonEnum forecastUpdateReason) {
            this.forecastId = forecastId;
            this.activeSlotNumber = activeSlotNumber;
            this.startTime = startTime;
            this.endTime = endTime;
            this.earliestStartTime = earliestStartTime;
            this.latestEndTime = latestEndTime;
            this.isPauseable = isPauseable;
            this.slots = slots;
            this.forecastUpdateReason = forecastUpdateReason;
        }
    }

    /**
     * This indicates a specific stage of an ESA’s operation.
     */
    public class SlotStruct {
        /**
         * This field shall indicate the minimum time (in seconds) that the appliance expects to be in this slot for.
         */
        public Integer minDuration; // elapsed-s
        /**
         * This field shall indicate the maximum time (in seconds) that the appliance expects to be in this slot for.
         */
        public Integer maxDuration; // elapsed-s
        /**
         * This field shall indicate the expected time (in seconds) that the appliance expects to be in this slot for.
         */
        public Integer defaultDuration; // elapsed-s
        /**
         * This field shall indicate the time (in seconds) that has already elapsed whilst in this slot. If the slot has
         * not yet been started, then it shall be 0. Once the slot has been completed, then this reflects how much time
         * was spent in this slot.
         * When subscribed to, a change in this field value shall NOT cause the Forecast attribute to be updated since
         * this value may change every 1 second.
         * When the Forecast attribute is read, then this value shall be the most recent value.
         */
        public Integer elapsedSlotTime; // elapsed-s
        /**
         * This field shall indicate the time (in seconds) that is estimated to be remaining.
         * Note that it may not align to the DefaultDuration - ElapsedSlotTime since an appliance may have revised its
         * planned operation based on conditions.
         * When subscribed to, a change in this field value shall NOT cause the Forecast attribute to be updated, since
         * this value may change every 1 second.
         * Note that if the ESA is currently paused, then this value shall not change.
         * When the Forecast attribute is read, then this value shall be the most recent value.
         */
        public Integer remainingSlotTime; // elapsed-s
        public Boolean slotIsPauseable; // bool
        /**
         * This field shall indicate the shortest period that the slot can be paused for. This can be set to avoid
         * controllers trying to pause ESAs for short periods and then resuming operation in a cyclic fashion which may
         * damage or cause excess energy to be consumed with restarting of an operation.
         */
        public Integer minPauseDuration; // elapsed-s
        /**
         * This field shall indicate the longest period that the slot can be paused for.
         */
        public Integer maxPauseDuration; // elapsed-s
        /**
         * This field shall indicate a manufacturer defined value indicating the state of the ESA.
         * This may be used by an observing EMS which also has access to the metering data to ascertain the typical
         * power drawn when the ESA is in a manufacturer defined state.
         * Some appliances, such as smart thermostats, may not know how much power is being drawn by the HVAC system,
         * but do know what they have asked the HVAC system to do.
         * Manufacturers can use this value to indicate a variety of states in an unspecified way. For example, they may
         * choose to use values between 0-100 as a percentage of compressor modulation, or could use these values as
         * Enum states meaning heating with fan, heating without fan etc.
         * NOTE An ESA shall always use the same value to represent the same operating state.
         * By providing this information a smart EMS may be able to learn the observed power draw when the ESA is put
         * into a specific state. It can potentially then use the information in the PowerForecastReporting data to
         * predict the power draw from the appliance and potentially ask it to modify its timing via one of the
         * adjustment request commands, or adjust other ESAs power to compensate.
         */
        public Integer manufacturerEsaState; // uint16
        /**
         * This field shall indicate the expected power that the appliance will use during this slot. It may be
         * considered the average value over the slot, and some variation from this would be expected (for example, as
         * it is ramping up).
         */
        public BigInteger nominalPower; // power-mW
        /**
         * This field shall indicate the lowest power that the appliance expects to use during this slot. (e.g. during a
         * ramp up it may be 0W).
         * Some appliances (e.g. battery inverters which can charge and discharge) may have a negative power.
         */
        public BigInteger minPower; // power-mW
        /**
         * This field shall indicate the maximum power that the appliance expects to use during this slot. (e.g. during
         * a ramp up it may be 0W). This field ignores the effects of short-lived inrush currents.
         * Some appliances (e.g. battery inverters which can charge and discharge) may have a negative power.
         */
        public BigInteger maxPower; // power-mW
        /**
         * This field shall indicate the expected energy that the appliance expects to use or produce during this slot.
         * Some appliances (e.g. battery inverters which can charge and discharge) may have a negative energy.
         */
        public BigInteger nominalEnergy; // energy-mWh
        /**
         * This field shall indicate the current estimated cost for operating.
         * For example, if the device has access to an Energy pricing server it may be able to use the tariff to
         * estimate the cost of energy for this slot in the power forecast.
         * When an Energy Management System requests a change in the schedule, then the device may suggest a change in
         * the cost as a result of shifting its energy. This can allow a demand side response service to be informed of
         * the relative cost to use energy at a different time.
         * The Costs field is a list of CostStruct structures which allows multiple CostTypeEnum and Values to be shared
         * by the energy appliance. These could be based on GHG emissions, comfort value for the consumer etc.
         * For example, comfort could be expressed in abstract units or in currency. A water heater that is heated
         * earlier in the day is likely to lose some of its heat before it is needed, which could require a top-up
         * heating event to occur later in the day (which may incur additional cost).
         * If the ESA cannot calculate its cost for any reason (such as losing its connection to a Price server) it may
         * omit this field. This is treated as extra meta data that an EMS may use to optimize a system.
         */
        public List<CostStruct> costs; // list
        /**
         * This field shall indicate the minimum power that the appliance can be requested to use.
         * For example, some EVSEs cannot be switched on to charge below 6A which may equate to ~1.3kW in EU markets. If
         * the slot indicates a NominalPower of 0W (indicating it is expecting to be off), this allows an ESA to
         * indicate it could be switched on to charge, but this would be the minimum power limit it can be set to.
         */
        public BigInteger minPowerAdjustment; // power-mW
        /**
         * This field shall indicate the maximum power that the appliance can be requested to use.
         * For example, an EVSE may be limited by its electrical supply to 32A which would be ~7.6kW in EU markets. If
         * the slot indicates a NominalPower of 0W (indicating it is expecting to be off), this allows an ESA to
         * indicate it could be switched on to charge, but this would be the maximum power limit it can be set to.
         */
        public BigInteger maxPowerAdjustment; // power-mW
        /**
         * This field shall indicate the minimum time, in seconds, that the slot can be requested to shortened to.
         * For example, if the slot indicates a NominalPower of 0W (indicating it is expecting to be off), this would
         * allow an ESA to specify the minimum time it could be switched on for. This is to help protect the appliance
         * from being damaged by short cycling times.
         * For example, a heat pump compressor may have a minimum cycle time of order a few minutes.
         */
        public Integer minDurationAdjustment; // elapsed-s
        /**
         * This field shall indicate the maximum time, in seconds, that the slot can be requested to extended to.
         * For example, if the slot indicates a NominalPower of 0W (indicating it is expecting to be off), this allows
         * an ESA to specify the maximum time it could be switched on for. This may allow a battery or water heater to
         * indicate the maximum duration that it can charge for before becoming full. In the case of a battery inverter
         * which can be discharged, it may equally indicate the maximum time the battery could be discharged for (at the
         * MaxPowerAdjustment power level).
         */
        public Integer maxDurationAdjustment; // elapsed-s

        public SlotStruct(Integer minDuration, Integer maxDuration, Integer defaultDuration, Integer elapsedSlotTime,
                Integer remainingSlotTime, Boolean slotIsPauseable, Integer minPauseDuration, Integer maxPauseDuration,
                Integer manufacturerEsaState, BigInteger nominalPower, BigInteger minPower, BigInteger maxPower,
                BigInteger nominalEnergy, List<CostStruct> costs, BigInteger minPowerAdjustment,
                BigInteger maxPowerAdjustment, Integer minDurationAdjustment, Integer maxDurationAdjustment) {
            this.minDuration = minDuration;
            this.maxDuration = maxDuration;
            this.defaultDuration = defaultDuration;
            this.elapsedSlotTime = elapsedSlotTime;
            this.remainingSlotTime = remainingSlotTime;
            this.slotIsPauseable = slotIsPauseable;
            this.minPauseDuration = minPauseDuration;
            this.maxPauseDuration = maxPauseDuration;
            this.manufacturerEsaState = manufacturerEsaState;
            this.nominalPower = nominalPower;
            this.minPower = minPower;
            this.maxPower = maxPower;
            this.nominalEnergy = nominalEnergy;
            this.costs = costs;
            this.minPowerAdjustment = minPowerAdjustment;
            this.maxPowerAdjustment = maxPowerAdjustment;
            this.minDurationAdjustment = minDurationAdjustment;
            this.maxDurationAdjustment = maxDurationAdjustment;
        }
    }

    public class SlotAdjustmentStruct {
        /**
         * This field shall indicate the index into the Slots list within the Forecast that is to be modified. It shall
         * be less than the actual length of the Slots list (implicitly it must be in the range 0 to 9 based on the
         * maximum length of the Slots list constraint).
         */
        public Integer slotIndex; // uint8
        /**
         * This field shall indicate the new requested power that the ESA shall operate at. It MUST be between the
         * MinPowerAdjustment and MaxPowerAdjustment for the slot as advertised by the ESA.
         */
        public BigInteger nominalPower; // power-mW
        /**
         * This field shall indicate the new requested duration, in seconds, that the ESA shall extend or shorten the
         * slot duration to. It MUST be between the MinDurationAdjustment and MaxDurationAdjustment for the slot as
         * advertised by the ESA.
         */
        public Integer duration; // elapsed-s

        public SlotAdjustmentStruct(Integer slotIndex, BigInteger nominalPower, Integer duration) {
            this.slotIndex = slotIndex;
            this.nominalPower = nominalPower;
            this.duration = duration;
        }
    }

    /**
     * The ConstraintsStruct allows a client to inform an ESA about a constraint period (such as a grid event, or
     * perhaps excess solar PV). The format allows the client to suggest that the ESA can either turn up its energy
     * consumption, or turn down its energy consumption during this period.
     */
    public class ConstraintsStruct {
        /**
         * This field shall indicate the start time of the constraint period that the client wishes the ESA to compute a
         * new Forecast.
         * This value is in UTC and MUST be in the future.
         */
        public Integer startTime; // epoch-s
        /**
         * This field shall indicate the duration of the constraint in seconds.
         */
        public Integer duration; // elapsed-s
        /**
         * This field shall indicate the nominal power that client wishes the ESA to operate at during the constrained
         * period. It MUST be between the AbsMinPower and AbsMaxPower attributes as advertised by the ESA if it supports
         * PFR.
         * This is a signed value and can be used to indicate charging or discharging. If the ESA does NOT support PFR
         * this value shall be ignored by the ESA.
         */
        public BigInteger nominalPower; // power-mW
        /**
         * This field shall indicate the maximum energy that can be transferred to or from the ESA during the constraint
         * period.
         * This is a signed value and can be used to indicate charging or discharging. If the ESA does NOT support PFR
         * this value may be ignored by the ESA.
         */
        public BigInteger maximumEnergy; // energy-mWh
        /**
         * This field shall indicate the turn up or turn down nature that the grid wants as the outcome by the ESA
         * during the constraint period.
         * This is expressed as a signed value between -100 to +100. A value of 0 would indicate no bias to using more
         * or less energy. A negative value indicates a request to use less energy. A positive value indicates a request
         * to use more energy.
         * Note that the mapping between values and operation is manufacturer specific.
         */
        public Integer loadControl; // int8

        public ConstraintsStruct(Integer startTime, Integer duration, BigInteger nominalPower, BigInteger maximumEnergy,
                Integer loadControl) {
            this.startTime = startTime;
            this.duration = duration;
            this.nominalPower = nominalPower;
            this.maximumEnergy = maximumEnergy;
            this.loadControl = loadControl;
        }
    }

    // Enums
    public enum CostTypeEnum {
        FINANCIAL(0, "Financial"),
        GHG_EMISSIONS(1, "GhgEmissions"),
        COMFORT(2, "Comfort"),
        TEMPERATURE(3, "Temperature");

        public final Integer value;
        public final String label;

        private CostTypeEnum(Integer value, String label) {
            this.value = value;
            this.label = label;
        }
    }

    public enum ESATypeEnum {
        EVSE(0, "Evse"),
        SPACE_HEATING(1, "SpaceHeating"),
        WATER_HEATING(2, "WaterHeating"),
        SPACE_COOLING(3, "SpaceCooling"),
        SPACE_HEATING_COOLING(4, "SpaceHeatingCooling"),
        BATTERY_STORAGE(5, "BatteryStorage"),
        SOLAR_PV(6, "SolarPv"),
        FRIDGE_FREEZER(7, "FridgeFreezer"),
        WASHING_MACHINE(8, "WashingMachine"),
        DISHWASHER(9, "Dishwasher"),
        COOKING(10, "Cooking"),
        HOME_WATER_PUMP(11, "HomeWaterPump"),
        IRRIGATION_WATER_PUMP(12, "IrrigationWaterPump"),
        POOL_PUMP(13, "PoolPump"),
        OTHER(255, "Other");

        public final Integer value;
        public final String label;

        private ESATypeEnum(Integer value, String label) {
            this.value = value;
            this.label = label;
        }
    }

    public enum ESAStateEnum {
        OFFLINE(0, "Offline"),
        ONLINE(1, "Online"),
        FAULT(2, "Fault"),
        POWER_ADJUST_ACTIVE(3, "PowerAdjustActive"),
        PAUSED(4, "Paused");

        public final Integer value;
        public final String label;

        private ESAStateEnum(Integer value, String label) {
            this.value = value;
            this.label = label;
        }
    }

    public enum OptOutStateEnum {
        NO_OPT_OUT(0, "NoOptOut"),
        LOCAL_OPT_OUT(1, "LocalOptOut"),
        GRID_OPT_OUT(2, "GridOptOut"),
        OPT_OUT(3, "OptOut");

        public final Integer value;
        public final String label;

        private OptOutStateEnum(Integer value, String label) {
            this.value = value;
            this.label = label;
        }
    }

    public enum CauseEnum {
        NORMAL_COMPLETION(0, "NormalCompletion"),
        OFFLINE(1, "Offline"),
        FAULT(2, "Fault"),
        USER_OPT_OUT(3, "UserOptOut"),
        CANCELLED(4, "Cancelled");

        public final Integer value;
        public final String label;

        private CauseEnum(Integer value, String label) {
            this.value = value;
            this.label = label;
        }
    }

    public enum AdjustmentCauseEnum {
        LOCAL_OPTIMIZATION(0, "LocalOptimization"),
        GRID_OPTIMIZATION(1, "GridOptimization");

        public final Integer value;
        public final String label;

        private AdjustmentCauseEnum(Integer value, String label) {
            this.value = value;
            this.label = label;
        }
    }

    public enum ForecastUpdateReasonEnum {
        INTERNAL_OPTIMIZATION(0, "InternalOptimization"),
        LOCAL_OPTIMIZATION(1, "LocalOptimization"),
        GRID_OPTIMIZATION(2, "GridOptimization");

        public final Integer value;
        public final String label;

        private ForecastUpdateReasonEnum(Integer value, String label) {
            this.value = value;
            this.label = label;
        }
    }

    // Bitmaps
    public static class FeatureMap {
        /**
         * PowerAdjustment
         * For Energy Smart Appliances (ESA) the definition of being &#x27;smart&#x27; mandates that they can report
         * their current power adjustment capability and have an EMS request a temporary adjustment. This may typically
         * be to curtail power requirements during peak periods, but can also be used to turn on an ESA if there is
         * excess renewable or local generation (Solar PV).
         * For example, a home may have solar PV which often produces more power than the home requires,
         * resulting in the excess power flowing into the grid. This excess power naturally fluctuates when clouds pass
         * overhead and other loads in the home are switched on and off.
         * EVSE Example: An EMS may therefore be able to turn on the EVSE (if the vehicle is plugged in) and can start
         * charging the vehicle, and periodically modify the charging power depending on PV generation and other home
         * loads, so as to minimize import and export to the grid.
         */
        public boolean powerAdjustment;
        /**
         * PowerForecastReporting
         * For Energy Smart Appliances (ESA) the definition of being &#x27;smart&#x27; implies that they can report
         * their indicative forecast power demands or generation, to a greater or lesser extent. For some ESAs this is
         * highly predictable (in terms of both power and time), in other appliances this is more challenging and only a
         * basic level of forecast is possible.
         * Forecasts are defined from a current time, using a slot format, where the slot is akin to a relatively
         * constant operating mode.
         * Washing machine example: a washing machine may have stages of a washing cycle: heating, tumbling, rinse and
         * spin stages. At each stage, the approximate minimum and maximum power consumption may be known, as well as
         * the duration of that stage.
         * In some circumstances the ESA may allow the stage to be delayed or paused (subject to safety and
         * manufacturer’s discretion and user preferences).
         * Typically, appliances with a heating element cannot have their power consumption adjusted and can only be
         * paused or delayed.
         * Some ESAs may not be flexible other than a delayed cycle start (for example, once the washing cycle has been
         * started then they run continuously until the cycle completes).
         * Appliances that only support the PowerForecastReporting and not any of the adjustment features may indicate
         * that they are not flexible in the forecast slot format.
         * The PowerForecastReporting and the adjustment features aim to align to the [SAREF4ENER] ontology.
         * Inverter driven ESAs: some inverter driven ESAs can consume or generate a variable amount of power.
         * For example, a single phase EVSE can be adjusted in the range of 6-32Amps in 0.6 Amp steps in EU or on a
         * hardwired 120V supply in the range of 6-15 Amps in US.
         * For example, a home battery may be adjusted to charge or discharge in steps of 1W.
         * For example, a heat pump may be able to modulate its compressor inverter between 20-100% of its rated power.
         * The ESA indicates its power adjustment range and its nominal power consumption as part of its Forecast.
         */
        public boolean powerForecastReporting;
        /**
         * StateForecastReporting
         * Some ESAs do not know their actual power consumption, but do know the state of operation. Like the
         * PowerForecastingReporting feature, this uses the same slot structure mechanism to indicate a change in state
         * vs time.
         * An external observing EMS may have access to real-time meter readings, and could learn the typical power
         * consumption based on the advertised internal state of the ESA.
         * To enable this capability, the ESA shall report its internal operational state using an manufacturer specific
         * value.
         * Once the EMS has built a model of the state vs observed power consumption, it may request a forecast
         * adjustment for particular times of the day, encouraging the ESA to use power at alternative times.
         */
        public boolean stateForecastReporting;
        /**
         * StartTimeAdjustment
         * ESAs which support the Start Time Adjustment feature, allow an EMS to recommend a change to the start time of
         * the energy transfer that the ESA has previously suggested it would use.
         * Washing machine example: A Washing Machine may have been set to start a wash cycle at 9pm when the variable
         * tariff normally reduces.
         * However, the EMS is aware that a grid event has occurred, making it cheaper to run the cycle at a later time,
         * but the washing machine is not aware of this.
         * The EMS first requests the Forecast data from each of its registered ESAs. It determines that the washing
         * machine has a power profile suggesting it will start the wash cycle at 9pm, but the EMS now knows that the
         * grid event means it will be cheaper to delay the start until 11pm.
         * The EMS can then optimize the cost by asking the washing machine to delay starting the wash cycle until 11pm.
         * It does this by sending a StartTimeAdjustRequest to the washing machine to request delaying the start of the
         * washing cycle.
         */
        public boolean startTimeAdjustment;
        /**
         * Pausable
         * ESAs which support the Pausable feature, allow an EMS to recommend a pause in the middle of a forecast power
         * profile that the ESA is currently using.
         * Washing machine example: A Washing Machine is in operation, and starting its water heating step.
         * However, the EMS becomes aware from the smart meter that the total home load on the grid is close to
         * exceeding its allowed total grid load.
         * The EMS first requests the Forecast data from each of its registered ESAs. It determines that the washing
         * machine has a power profile suggesting its current step in the wash cycle is using power to heat the water,
         * but that this step can be paused.
         * The EMS can then reduce the grid load by asking the washing machine to pause the wash cycle for a short
         * duration.
         * It does this by sending a PauseRequest to the washing machine to request pausing the current step of the
         * forecast power usage for a period to allow other home loads to finish before resuming the washing cycle.
         */
        public boolean pausable;
        /**
         * ForecastAdjustment
         * ESAs which support the Forecast adjustment feature, allow an EMS to recommend a change to the start, duration
         * and/or power level limits of the steps of the power profile that the ESA has previously suggested it would
         * use.
         * Heat pump and Solar PV example: A heat pump may have the ability to heat hot water as well as heating the
         * home. The heat pump scheduling system may have determined that the home will be unoccupied during the day, or
         * that the indoor temperature is above the set-point and so it knows that it will not need to heat the home.
         * However, the hot water tank is likely to need to be reheated before the homeowner comes home in the evening.
         * The heat pump is not aware that the property also has a solar PV inverter which is also an ESA that is
         * communicating with the EMS.
         * The EMS first requests the Forecast data from each of its registered ESAs. It determines that the heat pump
         * has a power profile suggesting it needs to heat hot water around 6pm. The solar PV inverter has forecast that
         * it will generate 3.6kW of power during the middle of the day and into the afternoon before the sun goes down.
         * The EMS can then optimize the home considering other non-ESA loads and can ask the heat pump to heat the hot
         * water around 3pm when it has forecast that excess solar power will be available.
         * It does this by sending a ModifyForecastRequest to the heat pump and asks the heat pump to expect to run at a
         * lower power consumption (within the solar excess power) which requires the heat pump to run for a longer
         * duration to achieve its required energy demand.
         */
        public boolean forecastAdjustment;
        /**
         * ConstraintBasedAdjustment
         * ESAs which support the Constraint-Based Adjustment feature allow an EMS to inform the ESA of periods during
         * which power usage should be modified (for example when the EMS has been made aware that the grid supplier has
         * requested reduced energy usage due to overall peak grid demand) and may cause the ESA to modify the intended
         * power profile has previously suggested it would use.
         * EVSE example: An EVSE scheduling system may have determined that the vehicle would be charged starting at a
         * moderate rate at 1am, so that it has enough charge by the time it is needed later that morning.
         * However, the DSR service provider has informed the EMS that due to high forecast winds it is now forecast
         * that there will be very cheap energy available from wind generation between 2am and 3am.
         * The EMS first requests the Forecast data from each of its registered ESAs. It determines that the
         * EVSE has a power profile suggesting it plans to start charging the vehicle at 1am.
         * The EMS can then try to reduce the cost of charging the EV by informing the EVSE of the desire to increase
         * the charging between scheduled times.
         * It does this by sending a RequestConstraintBasedForecast to the EVSE and asks it to run at a higher
         * NominalPower consumption during the constraint period, which may require it to decrease its charge rate
         * outside the constraint period to achieve its required energy demand.
         */
        public boolean constraintBasedAdjustment;

        public FeatureMap(boolean powerAdjustment, boolean powerForecastReporting, boolean stateForecastReporting,
                boolean startTimeAdjustment, boolean pausable, boolean forecastAdjustment,
                boolean constraintBasedAdjustment) {
            this.powerAdjustment = powerAdjustment;
            this.powerForecastReporting = powerForecastReporting;
            this.stateForecastReporting = stateForecastReporting;
            this.startTimeAdjustment = startTimeAdjustment;
            this.pausable = pausable;
            this.forecastAdjustment = forecastAdjustment;
            this.constraintBasedAdjustment = constraintBasedAdjustment;
        }
    }

    public DeviceEnergyManagementCluster(BigInteger nodeId, int endpointId) {
        super(nodeId, endpointId, 152, "DeviceEnergyManagement");
    }

    // commands
    /**
     * Allows a client to request an adjustment in the power consumption of an ESA for a specified duration.
     */
    public static ClusterCommand powerAdjustRequest(BigInteger power, Integer duration, AdjustmentCauseEnum cause) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("power", power);
        map.put("duration", duration);
        map.put("cause", cause);

        return new ClusterCommand("powerAdjustRequest", map);
    }

    /**
     * Allows a client to cancel an ongoing PowerAdjustmentRequest operation.
     */
    public static ClusterCommand cancelPowerAdjustRequest() {
        Map<String, Object> map = new LinkedHashMap<>();

        return new ClusterCommand("cancelPowerAdjustRequest");
    }

    /**
     * Allows a client to adjust the start time of a Forecast sequence that has not yet started operation (i.e. where
     * the current Forecast StartTime is in the future).
     */
    public static ClusterCommand startTimeAdjustRequest(Integer requestedStartTime, AdjustmentCauseEnum cause) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("requestedStartTime", requestedStartTime);
        map.put("cause", cause);

        return new ClusterCommand("startTimeAdjustRequest", map);
    }

    /**
     * Allows a client to temporarily pause an operation and reduce the ESAs energy demand.
     */
    public static ClusterCommand pauseRequest(Integer duration, AdjustmentCauseEnum cause) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("duration", duration);
        map.put("cause", cause);

        return new ClusterCommand("pauseRequest", map);
    }

    /**
     * Allows a client to cancel the PauseRequest command and enable earlier resumption of operation.
     */
    public static ClusterCommand resumeRequest() {
        Map<String, Object> map = new LinkedHashMap<>();

        return new ClusterCommand("resumeRequest");
    }

    /**
     * Allows a client to modify a Forecast within the limits allowed by the ESA.
     */
    public static ClusterCommand modifyForecastRequest(Integer forecastId, List<SlotAdjustmentStruct> slotAdjustments,
            AdjustmentCauseEnum cause) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("forecastId", forecastId);
        map.put("slotAdjustments", slotAdjustments);
        map.put("cause", cause);

        return new ClusterCommand("modifyForecastRequest", map);
    }

    /**
     * Allows a client to ask the ESA to recompute its Forecast based on power and time constraints.
     */
    public static ClusterCommand requestConstraintBasedForecast(List<ConstraintsStruct> constraints,
            AdjustmentCauseEnum cause) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("constraints", constraints);
        map.put("cause", cause);

        return new ClusterCommand("requestConstraintBasedForecast", map);
    }

    /**
     * Allows a client to request cancellation of a previous adjustment request in a StartTimeAdjustRequest,
     * ModifyForecastRequest or RequestConstraintBasedForecast command.
     */
    public static ClusterCommand cancelRequest() {
        Map<String, Object> map = new LinkedHashMap<>();

        return new ClusterCommand("cancelRequest");
    }

    public String toString() {
        String str = "";
        str += "clusterRevision : " + clusterRevision + "\n";
        str += "featureMap : " + featureMap + "\n";
        str += "esaType : " + esaType + "\n";
        str += "esaCanGenerate : " + esaCanGenerate + "\n";
        str += "esaState : " + esaState + "\n";
        str += "absMinPower : " + absMinPower + "\n";
        str += "absMaxPower : " + absMaxPower + "\n";
        str += "powerAdjustmentCapability : " + powerAdjustmentCapability + "\n";
        str += "forecast : " + forecast + "\n";
        str += "optOutState : " + optOutState + "\n";
        return str;
    }
}
