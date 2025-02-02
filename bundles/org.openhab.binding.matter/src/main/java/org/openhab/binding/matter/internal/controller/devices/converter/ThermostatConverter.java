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
package org.openhab.binding.matter.internal.controller.devices.converter;

import static org.openhab.binding.matter.internal.MatterBindingConstants.CHANNEL_ID_THERMOSTAT_LOCALTEMPERATURE;
import static org.openhab.binding.matter.internal.MatterBindingConstants.CHANNEL_ID_THERMOSTAT_OCCUPIEDCOOLING;
import static org.openhab.binding.matter.internal.MatterBindingConstants.CHANNEL_ID_THERMOSTAT_OCCUPIEDHEATING;
import static org.openhab.binding.matter.internal.MatterBindingConstants.CHANNEL_ID_THERMOSTAT_OUTDOORTEMPERATURE;
import static org.openhab.binding.matter.internal.MatterBindingConstants.CHANNEL_ID_THERMOSTAT_RUNNINGMODE;
import static org.openhab.binding.matter.internal.MatterBindingConstants.CHANNEL_ID_THERMOSTAT_SYSTEMMODE;
import static org.openhab.binding.matter.internal.MatterBindingConstants.CHANNEL_ID_THERMOSTAT_UNOCCUPIEDCOOLING;
import static org.openhab.binding.matter.internal.MatterBindingConstants.CHANNEL_ID_THERMOSTAT_UNOCCUPIEDHEATING;
import static org.openhab.binding.matter.internal.MatterBindingConstants.CHANNEL_LABEL_THERMOSTAT_LOCALTEMPERATURE;
import static org.openhab.binding.matter.internal.MatterBindingConstants.CHANNEL_LABEL_THERMOSTAT_OCCUPIEDCOOLING;
import static org.openhab.binding.matter.internal.MatterBindingConstants.CHANNEL_LABEL_THERMOSTAT_OCCUPIEDHEATING;
import static org.openhab.binding.matter.internal.MatterBindingConstants.CHANNEL_LABEL_THERMOSTAT_OUTDOORTEMPERATURE;
import static org.openhab.binding.matter.internal.MatterBindingConstants.CHANNEL_LABEL_THERMOSTAT_SYSTEMMODE;
import static org.openhab.binding.matter.internal.MatterBindingConstants.CHANNEL_LABEL_THERMOSTAT_UNOCCUPIEDCOOLING;
import static org.openhab.binding.matter.internal.MatterBindingConstants.CHANNEL_LABEL_THERMOSTAT_UNOCCUPIEDHEATING;
import static org.openhab.binding.matter.internal.MatterBindingConstants.CHANNEL_THERMOSTAT_LOCALTEMPERATURE;
import static org.openhab.binding.matter.internal.MatterBindingConstants.CHANNEL_THERMOSTAT_OCCUPIEDCOOLING;
import static org.openhab.binding.matter.internal.MatterBindingConstants.CHANNEL_THERMOSTAT_OCCUPIEDHEATING;
import static org.openhab.binding.matter.internal.MatterBindingConstants.CHANNEL_THERMOSTAT_OUTDOORTEMPERATURE;
import static org.openhab.binding.matter.internal.MatterBindingConstants.CHANNEL_THERMOSTAT_RUNNINGMODE;
import static org.openhab.binding.matter.internal.MatterBindingConstants.CHANNEL_THERMOSTAT_SYSTEMMODE;
import static org.openhab.binding.matter.internal.MatterBindingConstants.CHANNEL_THERMOSTAT_UNOCCUPIEDCOOLING;
import static org.openhab.binding.matter.internal.MatterBindingConstants.CHANNEL_THERMOSTAT_UNOCCUPIEDHEATING;
import static org.openhab.binding.matter.internal.MatterBindingConstants.ITEM_TYPE_NUMBER;
import static org.openhab.binding.matter.internal.MatterBindingConstants.ITEM_TYPE_NUMBER_TEMPERATURE;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.matter.internal.client.dto.cluster.gen.ThermostatCluster;
import org.openhab.binding.matter.internal.client.dto.ws.AttributeChangedMessage;
import org.openhab.binding.matter.internal.handler.MatterBaseThingHandler;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.thing.Channel;
import org.openhab.core.thing.ChannelGroupUID;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.binding.builder.ChannelBuilder;
import org.openhab.core.types.Command;
import org.openhab.core.types.RefreshType;
import org.openhab.core.types.StateDescription;
import org.openhab.core.types.StateDescriptionFragmentBuilder;
import org.openhab.core.types.StateOption;

/**
 * The {@link ThermostatConverter}
 *
 * @author Dan Cunningham - Initial contribution
 */
@NonNullByDefault
public class ThermostatConverter extends GenericConverter<ThermostatCluster> {

    public ThermostatConverter(ThermostatCluster cluster, MatterBaseThingHandler handler, int endpointNumber,
            String labelPrefix) {
        super(cluster, handler, endpointNumber, labelPrefix);
    }

    @Override
    public Map<Channel, @Nullable StateDescription> createChannels(ChannelGroupUID thingUID) {
        Map<Channel, @Nullable StateDescription> channels = new HashMap<>();

        Channel channel = ChannelBuilder
                .create(new ChannelUID(thingUID, CHANNEL_ID_THERMOSTAT_SYSTEMMODE), ITEM_TYPE_NUMBER)
                .withType(CHANNEL_THERMOSTAT_SYSTEMMODE).withLabel(formatLabel(CHANNEL_LABEL_THERMOSTAT_SYSTEMMODE))
                .build();

        List<StateOption> modeOptions = new ArrayList<>();

        modeOptions.add(new StateOption(ThermostatCluster.SystemModeEnum.OFF.value.toString(),
                ThermostatCluster.SystemModeEnum.OFF.label));
        if (initializingCluster.featureMap.autoMode) {
            modeOptions.add(new StateOption(ThermostatCluster.SystemModeEnum.AUTO.value.toString(),
                    ThermostatCluster.SystemModeEnum.AUTO.label));
        }
        if (initializingCluster.featureMap.cooling) {
            modeOptions.add(new StateOption(ThermostatCluster.SystemModeEnum.COOL.value.toString(),
                    ThermostatCluster.SystemModeEnum.COOL.label));
            modeOptions.add(new StateOption(ThermostatCluster.SystemModeEnum.PRECOOLING.value.toString(),
                    ThermostatCluster.SystemModeEnum.PRECOOLING.label));
        }
        if (initializingCluster.featureMap.heating) {
            modeOptions.add(new StateOption(ThermostatCluster.SystemModeEnum.HEAT.value.toString(),
                    ThermostatCluster.SystemModeEnum.HEAT.label));
            modeOptions.add(new StateOption(ThermostatCluster.SystemModeEnum.EMERGENCY_HEAT.value.toString(),
                    ThermostatCluster.SystemModeEnum.EMERGENCY_HEAT.label));
        }
        modeOptions.add(new StateOption(ThermostatCluster.SystemModeEnum.FAN_ONLY.value.toString(),
                ThermostatCluster.SystemModeEnum.FAN_ONLY.label));
        modeOptions.add(new StateOption(ThermostatCluster.SystemModeEnum.DRY.value.toString(),
                ThermostatCluster.SystemModeEnum.DRY.label));
        modeOptions.add(new StateOption(ThermostatCluster.SystemModeEnum.SLEEP.value.toString(),
                ThermostatCluster.SystemModeEnum.SLEEP.label));

        StateDescription stateDescriptionMode = StateDescriptionFragmentBuilder.create().withPattern("%d")
                .withOptions(modeOptions).build().toStateDescription();
        channels.put(channel, stateDescriptionMode);

        if (!initializingCluster.featureMap.localTemperatureNotExposed) {
            Channel tempChannel = ChannelBuilder
                    .create(new ChannelUID(thingUID, CHANNEL_ID_THERMOSTAT_LOCALTEMPERATURE),
                            ITEM_TYPE_NUMBER_TEMPERATURE)
                    .withType(CHANNEL_THERMOSTAT_LOCALTEMPERATURE)
                    .withLabel(formatLabel(CHANNEL_LABEL_THERMOSTAT_LOCALTEMPERATURE)).build();

            StateDescription stateDescription = StateDescriptionFragmentBuilder.create().withPattern("%.1f %unit%")
                    .build().toStateDescription();
            channels.put(tempChannel, stateDescription);
        }
        if (initializingCluster.outdoorTemperature != null) {
            Channel tempChannel = ChannelBuilder
                    .create(new ChannelUID(thingUID, CHANNEL_ID_THERMOSTAT_OUTDOORTEMPERATURE),
                            ITEM_TYPE_NUMBER_TEMPERATURE)
                    .withType(CHANNEL_THERMOSTAT_OUTDOORTEMPERATURE)
                    .withLabel(formatLabel(CHANNEL_LABEL_THERMOSTAT_OUTDOORTEMPERATURE)).build();
            StateDescription stateDescription = StateDescriptionFragmentBuilder.create().withPattern("%.1f %unit%")
                    .build().toStateDescription();
            channels.put(tempChannel, stateDescription);
        }
        if (initializingCluster.featureMap.heating) {
            Channel tempChannel = ChannelBuilder
                    .create(new ChannelUID(thingUID, CHANNEL_ID_THERMOSTAT_OCCUPIEDHEATING),
                            ITEM_TYPE_NUMBER_TEMPERATURE)
                    .withType(CHANNEL_THERMOSTAT_OCCUPIEDHEATING)
                    .withLabel(formatLabel(CHANNEL_LABEL_THERMOSTAT_OCCUPIEDHEATING)).build();
            StateDescription stateDescription = StateDescriptionFragmentBuilder.create()
                    .withMinimum(valueToTemperature(initializingCluster.absMinHeatSetpointLimit).toBigDecimal())
                    .withMaximum(valueToTemperature(initializingCluster.absMaxHeatSetpointLimit).toBigDecimal())
                    .withStep(BigDecimal.valueOf(1)).withPattern("%.1f %unit%").withReadOnly(false).build()
                    .toStateDescription();
            channels.put(tempChannel, stateDescription);
        }
        if (initializingCluster.featureMap.cooling) {
            Channel tempChannel = ChannelBuilder
                    .create(new ChannelUID(thingUID, CHANNEL_ID_THERMOSTAT_OCCUPIEDCOOLING),
                            ITEM_TYPE_NUMBER_TEMPERATURE)
                    .withType(CHANNEL_THERMOSTAT_OCCUPIEDCOOLING)
                    .withLabel(formatLabel(CHANNEL_LABEL_THERMOSTAT_OCCUPIEDCOOLING)).build();
            StateDescription stateDescription = StateDescriptionFragmentBuilder.create()
                    .withMinimum(valueToTemperature(initializingCluster.absMinCoolSetpointLimit).toBigDecimal())
                    .withMaximum(valueToTemperature(initializingCluster.absMaxCoolSetpointLimit).toBigDecimal())
                    .withStep(BigDecimal.valueOf(1)).withPattern("%.1f %unit%").withReadOnly(false).build()
                    .toStateDescription();
            channels.put(tempChannel, stateDescription);
        }
        if (initializingCluster.featureMap.occupancy) {
            if (initializingCluster.featureMap.heating) {
                Channel tempChannel = ChannelBuilder
                        .create(new ChannelUID(thingUID, CHANNEL_ID_THERMOSTAT_UNOCCUPIEDHEATING),
                                ITEM_TYPE_NUMBER_TEMPERATURE)
                        .withType(CHANNEL_THERMOSTAT_UNOCCUPIEDHEATING)
                        .withLabel(formatLabel(CHANNEL_LABEL_THERMOSTAT_UNOCCUPIEDHEATING)).build();
                StateDescription stateDescription = StateDescriptionFragmentBuilder.create()
                        .withMinimum(valueToTemperature(initializingCluster.absMinHeatSetpointLimit).toBigDecimal())
                        .withMaximum(valueToTemperature(initializingCluster.absMaxHeatSetpointLimit).toBigDecimal())
                        .withStep(BigDecimal.valueOf(1)).withPattern("%.1f %unit%").withReadOnly(false).build()
                        .toStateDescription();
                channels.put(tempChannel, stateDescription);
            }
            if (initializingCluster.featureMap.cooling) {
                Channel tempChannel = ChannelBuilder
                        .create(new ChannelUID(thingUID, CHANNEL_ID_THERMOSTAT_UNOCCUPIEDCOOLING),
                                ITEM_TYPE_NUMBER_TEMPERATURE)
                        .withType(CHANNEL_THERMOSTAT_UNOCCUPIEDCOOLING)
                        .withLabel(formatLabel(CHANNEL_LABEL_THERMOSTAT_UNOCCUPIEDCOOLING)).build();
                StateDescription stateDescription = StateDescriptionFragmentBuilder.create()
                        .withMinimum(valueToTemperature(initializingCluster.absMinCoolSetpointLimit).toBigDecimal())
                        .withMaximum(valueToTemperature(initializingCluster.absMaxCoolSetpointLimit).toBigDecimal())
                        .withStep(BigDecimal.valueOf(1)).withPattern("%.1f %unit%").withReadOnly(false).build()
                        .toStateDescription();
                channels.put(tempChannel, stateDescription);
            }
        }
        if (initializingCluster.thermostatRunningMode != null) {
            Channel tempChannel = ChannelBuilder
                    .create(new ChannelUID(thingUID, CHANNEL_ID_THERMOSTAT_RUNNINGMODE), ITEM_TYPE_NUMBER)
                    .withType(CHANNEL_THERMOSTAT_RUNNINGMODE)
                    .withLabel(formatLabel(CHANNEL_LABEL_THERMOSTAT_UNOCCUPIEDCOOLING)).build();
            List<StateOption> options = new ArrayList<>();
            options.add(new StateOption(ThermostatCluster.ThermostatRunningModeEnum.OFF.value.toString(),
                    ThermostatCluster.ThermostatRunningModeEnum.OFF.label));
            options.add(new StateOption(ThermostatCluster.ThermostatRunningModeEnum.HEAT.value.toString(),
                    ThermostatCluster.ThermostatRunningModeEnum.HEAT.label));
            options.add(new StateOption(ThermostatCluster.ThermostatRunningModeEnum.COOL.value.toString(),
                    ThermostatCluster.ThermostatRunningModeEnum.COOL.label));
            StateDescription stateDescription = StateDescriptionFragmentBuilder.create().withOptions(options).build()
                    .toStateDescription();
            channels.put(tempChannel, stateDescription);
        }

        return channels;
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (!(command instanceof RefreshType)) {
            String id = channelUID.getIdWithoutGroup();
            if (id.equals(CHANNEL_ID_THERMOSTAT_SYSTEMMODE)) {
                handler.writeAttribute(endpointNumber, ThermostatCluster.CLUSTER_NAME, "systemMode",
                        command.toString());
                return;
            }
            if (id.equals(CHANNEL_ID_THERMOSTAT_OCCUPIEDHEATING)) {
                handler.writeAttribute(endpointNumber, ThermostatCluster.CLUSTER_NAME, "occupiedHeatingSetpoint",
                        String.valueOf(temperatureToValue(command)));
                return;
            }
            if (id.equals(CHANNEL_ID_THERMOSTAT_OCCUPIEDCOOLING)) {
                handler.writeAttribute(endpointNumber, ThermostatCluster.CLUSTER_NAME, "occupiedCoolingSetpoint",
                        String.valueOf(temperatureToValue(command)));
                return;
            }
        }
        super.handleCommand(channelUID, command);
    }

    @Override
    public void onEvent(AttributeChangedMessage message) {
        logger.debug("OnEvent: {}", message.path.attributeName);
        Integer numberValue = message.value instanceof Number number ? number.intValue() : 0;
        switch (message.path.attributeName) {
            case "systemMode":
                if (message.value instanceof ThermostatCluster.SystemModeEnum systemModeEnum) {
                    updateState(CHANNEL_ID_THERMOSTAT_SYSTEMMODE, new DecimalType(systemModeEnum.value));
                }
                break;
            case "occupiedHeatingSetpoint":
                updateState(CHANNEL_ID_THERMOSTAT_OCCUPIEDHEATING, valueToTemperature(numberValue));
                break;
            case "occupiedCoolingSetpoint":
                updateState(CHANNEL_ID_THERMOSTAT_OCCUPIEDCOOLING, valueToTemperature(numberValue));
                break;
            case "unoccupiedHeatingSetpoint":
                updateState(CHANNEL_ID_THERMOSTAT_UNOCCUPIEDHEATING, valueToTemperature(numberValue));
                break;
            case "unoccupiedCoolingSetpoint":
                updateState(CHANNEL_ID_THERMOSTAT_UNOCCUPIEDCOOLING, valueToTemperature(numberValue));
                break;
            case "localTemperature":
                updateState(CHANNEL_ID_THERMOSTAT_LOCALTEMPERATURE, valueToTemperature(numberValue));
                break;
            case "outdoorTemperature":
                updateState(CHANNEL_ID_THERMOSTAT_OUTDOORTEMPERATURE, valueToTemperature(numberValue));
                break;
            case "thermostatRunningMode":
                updateState(CHANNEL_ID_THERMOSTAT_RUNNINGMODE, new DecimalType(numberValue));
                break;
            default:
                logger.debug("Unknown attribute {}", message.path.attributeName);
        }
        super.onEvent(message);
    }

    @Override
    public void initState() {
        if (initializingCluster.localTemperature != null) {
            updateState(CHANNEL_ID_THERMOSTAT_LOCALTEMPERATURE,
                    valueToTemperature(initializingCluster.localTemperature));
        }
        if (initializingCluster.outdoorTemperature != null) {
            updateState(CHANNEL_ID_THERMOSTAT_OUTDOORTEMPERATURE,
                    valueToTemperature(initializingCluster.outdoorTemperature));
        }
        if (initializingCluster.systemMode != null) {
            updateState(CHANNEL_ID_THERMOSTAT_SYSTEMMODE, new DecimalType(initializingCluster.systemMode.value));
        }
        if (initializingCluster.occupiedHeatingSetpoint != null) {
            updateState(CHANNEL_ID_THERMOSTAT_OCCUPIEDHEATING,
                    valueToTemperature(initializingCluster.occupiedHeatingSetpoint));
        }
        if (initializingCluster.occupiedCoolingSetpoint != null) {
            updateState(CHANNEL_ID_THERMOSTAT_OCCUPIEDCOOLING,
                    valueToTemperature(initializingCluster.occupiedCoolingSetpoint));
        }
        if (initializingCluster.unoccupiedHeatingSetpoint != null) {
            updateState(CHANNEL_ID_THERMOSTAT_UNOCCUPIEDHEATING,
                    valueToTemperature(initializingCluster.unoccupiedHeatingSetpoint));
        }
        if (initializingCluster.unoccupiedCoolingSetpoint != null) {
            updateState(CHANNEL_ID_THERMOSTAT_UNOCCUPIEDCOOLING,
                    valueToTemperature(initializingCluster.unoccupiedCoolingSetpoint));
        }
        if (initializingCluster.thermostatRunningMode != null) {
            updateState(CHANNEL_ID_THERMOSTAT_RUNNINGMODE,
                    new DecimalType(initializingCluster.thermostatRunningMode.value));
        }
    }
}
