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
package org.openhab.binding.matter.internal.bridge.devices;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.matter.internal.bridge.MatterBridgeClient;
import org.openhab.binding.matter.internal.client.dto.cluster.gen.ThermostatCluster;
import org.openhab.core.items.GenericItem;
import org.openhab.core.items.GroupItem;
import org.openhab.core.items.Item;
import org.openhab.core.items.MetadataRegistry;
import org.openhab.core.library.items.NumberItem;
import org.openhab.core.library.items.StringItem;
import org.openhab.core.library.items.SwitchItem;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.library.types.QuantityType;
import org.openhab.core.library.types.StringType;
import org.openhab.core.types.State;
import org.openhab.core.types.UnDefType;

/**
 * The {@link ThermostatDevice}
 *
 * @author Dan Cunningham - Initial contribution
 */
@NonNullByDefault
public class ThermostatDevice extends GenericDevice {
    private final Map<String, GenericItem> itemMap = new HashMap<>();
    private final Map<String, String> attributeToItemNameMap = new HashMap<>();
    private final SystemModeMapper systemModeMapper = new SystemModeMapper();

    public ThermostatDevice(MetadataRegistry metadataRegistry, MatterBridgeClient client, GenericItem item) {
        super(metadataRegistry, client, item);
    }

    @Override
    public String deviceType() {
        return "Thermostat";
    }

    @Override
    public void handleMatterEvent(String clusterName, String attributeName, Object data) {
        String pathName = clusterName + "." + attributeName;
        String itemUid = attributeToItemNameMap.get(pathName);
        if (itemUid != null) {
            GenericItem item = itemMap.get(itemUid);
            if (item != null) {
                // State state = TypeParser.parseState(item.getAcceptedDataTypes(), data.toString());
                switch (pathName) {
                    case "thermostat.occupiedHeatingSetpoint":
                    case "thermostat.occupiedCoolingSetpoint":
                        if (item instanceof NumberItem numberItem) {
                            QuantityType t = valueToTemperature(Float.valueOf(data.toString()).intValue());
                            numberItem.send(t);
                        }
                        break;
                    case "thermostat.systemMode":
                        try {
                            String mappedMode = systemModeMapper
                                    .toCustomValue(Double.valueOf(data.toString()).intValue());
                            if (item instanceof NumberItem numberItem) {
                                numberItem.send(new DecimalType(mappedMode));
                            } else if (item instanceof StringItem stringItem) {
                                stringItem.send(new StringType(mappedMode));
                            } else if (item instanceof SwitchItem switchItem) {
                                switchItem.send(OnOffType.from(mappedMode));
                            }
                        } catch (SystemModeMappingException e) {
                            logger.debug("Could not convert {} to custom value", data);
                        }
                        break;
                    case "onOff.onOff":
                        try {
                            if (data instanceof Boolean onOff) {
                                String mappedMode = onOff ? systemModeMapper.onToCustomValue()
                                        : systemModeMapper.toCustomValue(0);
                                if (item instanceof NumberItem) {
                                    item.setState(new DecimalType(mappedMode));
                                } else {
                                    item.setState(new StringType(mappedMode));
                                }
                            }
                        } catch (SystemModeMappingException e) {
                            logger.debug("Could not convert {} to custom value", data);
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }

    public void updateState(Item item, State state) {
        attributeToItemNameMap.forEach((attribute, itemUid) -> {
            if (itemUid.equals(item.getUID())) {
                // we need to do conversion here
                String[] pair = attribute.split("\\.");
                if (pair.length != 2) {
                    logger.debug("Unknown attribute format {}", attribute);
                    return;
                }
                String clusterName = pair[0];
                String attributeName = pair[1];
                switch (attribute) {
                    case "thermostat.localTemperature":
                    case "thermostat.outdoorTemperature":
                    case "thermostat.occupiedHeatingSetpoint":
                    case "thermostat.occupiedCoolingSetpoint":
                        Integer value = temperatureToValue(state);
                        if (value != null) {
                            logger.debug("Setting {} to {}", attributeName, value);
                            setEndpointState(clusterName, attributeName, value);
                        } else {
                            logger.debug("Could not convert {} to matter value", state.toString());
                        }
                        break;
                    case "thermostat.systemMode":
                        try {
                            int mode = systemModeMapper.fromCustomValue(state.toString()).value;
                            setEndpointState(clusterName, attributeName, mode);
                            setEndpointState("onOff", "onOff", mode > 0);
                        } catch (SystemModeMappingException e) {
                            logger.debug("Could not convert {} to matter value", state.toString());
                        }
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public MatterDeviceOptions activate() {
        dispose();
        primaryItem.addStateChangeListener(this);
        Map<String, Object> attributeMap = new HashMap<>();
        MetaDataMapping primaryMetadata = metaDataMapping(primaryItem);
        // add any settings for attributes from config, like thermostat.minHeatSetpointLimit=0
        attributeMap.putAll(primaryMetadata.getAttributeOptions());
        for (Item member : ((GroupItem) primaryItem).getAllMembers()) {
            if (member instanceof GenericItem genericMember) {
                MetaDataMapping metadata = metaDataMapping(genericMember);
                if (metadata != null) {
                    State state = genericMember.getState();
                    for (String attribute : metadata.attributes) {
                        switch (attribute) {
                            case "thermostat.localTemperature":
                            case "thermostat.outdoorTemperature":
                            case "thermostat.occupiedHeatingSetpoint":
                            case "thermostat.occupiedCoolingSetpoint":
                                if (state instanceof UnDefType) {
                                    attributeMap.put(attribute, 0);
                                } else {
                                    Integer value = temperatureToValue(state);
                                    attributeMap.put(attribute, value != null ? value : 0);
                                }
                                break;
                            case "thermostat.systemMode":
                                int mode = 0;
                                if (state instanceof DecimalType decimalType) {
                                    mode = decimalType.intValue();
                                }
                                attributeMap.put(attribute, mode);
                                attributeMap.put("onOff.onOff", mode > 0);
                                systemModeMapper.initializeMappings(metadata.config);
                                break;
                            default:
                                continue;
                        }
                        if (!itemMap.containsKey(genericMember.getUID())) {
                            itemMap.put(genericMember.getUID(), genericMember);
                            genericMember.addStateChangeListener(this);
                        }
                        // add any settings for attributes from config, like thermostat.minHeatSetpointLimit=0
                        attributeMap.putAll(metadata.getAttributeOptions());
                        attributeToItemNameMap.put(attribute, genericMember.getUID());
                    }
                }
            }
        }
        return new MatterDeviceOptions(attributeMap, primaryMetadata.label);
    }

    @Override
    public void dispose() {
        attributeToItemNameMap.clear();
        primaryItem.removeStateChangeListener(this);
        itemMap.forEach((uid, item) -> {
            ((GenericItem) item).removeStateChangeListener(this);
        });
        itemMap.clear();
    }

    class SystemModeMapper {
        private final Map<Integer, String> intToCustomMap = new HashMap<>();
        private final Map<String, ThermostatCluster.SystemModeEnum> customToEnumMap = new HashMap<>();
        private @Nullable String onMode = null;

        public SystemModeMapper() {
            Map<String, Object> mappings = new HashMap<>();
            mappings.put("OFF", 0);
            mappings.put("HEAT", 1);
            mappings.put("COOL", 2);
            mappings.put("AUTO", 3);
            mappings.put("EMERGENCY_HEAT", 4);
            mappings.put("PRECOOLING", 5);
            mappings.put("FAN_ONLY", 6);
            mappings.put("DRY", 7);
            mappings.put("SLEEP", 8);
            mappings.put("ON", 3);
            initializeMappings(mappings);
        }

        public SystemModeMapper(Map<String, Object> mappings) {
            initializeMappings(mappings);
        }

        private void initializeMappings(Map<String, Object> mappings) {
            if (mappings.isEmpty()) {
                return;
            }

            // don't bother mapping if there's no OFF
            if (!mappings.containsKey("OFF")) {
                return;
            }

            intToCustomMap.clear();
            customToEnumMap.clear();
            for (Map.Entry<String, Object> entry : mappings.entrySet()) {
                String customKey = entry.getKey().trim();
                Object valueObj = entry.getValue();
                String customValue = valueObj != null ? valueObj.toString().trim() : null;

                if ("ON".equals(customKey) && customValue != null) {
                    onMode = customValue;
                    continue;
                }

                try {
                    ThermostatCluster.SystemModeEnum mode = ThermostatCluster.SystemModeEnum.valueOf(customKey);
                    if (customValue != null) {
                        intToCustomMap.put(mode.value, customValue);
                        customToEnumMap.put(customValue, mode);
                    }
                } catch (IllegalArgumentException e) {
                    // ignore unknown values
                }
            }
        }

        public String toCustomValue(int modeValue) throws SystemModeMappingException {
            String value = intToCustomMap.get(modeValue);
            if (value == null) {
                throw new SystemModeMappingException("No mapping for mode: " + modeValue);
            }
            return value;
        }

        public String onToCustomValue() throws SystemModeMappingException {
            String value = this.onMode;
            if (value == null) {
                value = intToCustomMap.get(ThermostatCluster.SystemModeEnum.AUTO.value);
            }
            if (value == null) {
                value = ThermostatCluster.SystemModeEnum.AUTO.getValue().toString();
            }
            return value;
        }

        public ThermostatCluster.SystemModeEnum fromCustomValue(String customValue) throws SystemModeMappingException {
            if ("ON".equals(customValue)) {
                String onMode = this.onMode;
                if (onMode != null) {
                    return fromCustomValue(onMode);
                } else {
                    return ThermostatCluster.SystemModeEnum.AUTO;
                }
            }

            ThermostatCluster.SystemModeEnum value = customToEnumMap.get(customValue);
            if (value == null) {
                throw new SystemModeMappingException("No mapping for custom value: " + customValue);
            }
            return value;
        }
    }

    class SystemModeMappingException extends Exception {
        private static final long serialVersionUID = 1L;

        public SystemModeMappingException(String message) {
            super(message);
        }
    }
}
