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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.matter.internal.bridge.MatterBridgeClient;
import org.openhab.binding.matter.internal.client.dto.cluster.gen.BaseCluster;
import org.openhab.binding.matter.internal.client.dto.cluster.gen.ClusterRegistry;
import org.openhab.core.items.GenericItem;
import org.openhab.core.items.Item;
import org.openhab.core.items.Metadata;
import org.openhab.core.items.MetadataKey;
import org.openhab.core.items.MetadataRegistry;
import org.openhab.core.items.StateChangeListener;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.PercentType;
import org.openhab.core.library.types.QuantityType;
import org.openhab.core.library.unit.ImperialUnits;
import org.openhab.core.library.unit.SIUnits;
import org.openhab.core.types.Command;
import org.openhab.core.types.State;
import org.openhab.core.types.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link GenericDevice}
 *
 * @author Dan Cunningham - Initial contribution
 */
@NonNullByDefault
public abstract class GenericDevice implements StateChangeListener {
    private static final BigDecimal TEMPERATURE_MULTIPLIER = new BigDecimal(100);

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected final GenericItem primaryItem;
    protected @Nullable Metadata primaryItemMetadata;
    protected final MatterBridgeClient client;
    protected final MetadataRegistry metadataRegistry;

    public GenericDevice(MetadataRegistry metadataRegistry, MatterBridgeClient client, GenericItem primaryItem) {
        this.metadataRegistry = metadataRegistry;
        this.client = client;
        this.primaryItem = primaryItem;
        this.primaryItemMetadata = metadataRegistry.get(new MetadataKey("matter", primaryItem.getUID()));
    }

    public abstract String deviceType();

    public abstract MatterDeviceOptions activate();

    public abstract void dispose();

    public abstract void updateState(Item item, State state);

    public abstract void handleMatterEvent(String clusterName, String attributeName, Object data);

    public void handleMatterEvent(Integer clusterId, String attributeName, Object data) {
        Class<? extends BaseCluster> cluster = ClusterRegistry.CLUSTER_IDS.get(clusterId);
        if (cluster == null) {
            logger.debug("Unknown cluster {}", clusterId);
            return;
        }
        handleMatterEvent(cluster.getName(), attributeName, data);
    }

    @Override
    public void stateChanged(Item item, State oldState, State newState) {
        logger.debug("{} state changed from {} to {}", item.getName(), oldState, newState);
        updateState(item, newState);
    }

    @Override
    public void stateUpdated(Item item, State state) {
        // updateState(item, state);
    }

    public CompletableFuture<String> registerDevice() {
        MatterDeviceOptions options = activate();
        return client.addEndpoint(deviceType(), primaryItem.getName(), options.label, primaryItem.getName(),
                "Type " + primaryItem.getType(), String.valueOf(primaryItem.getName().hashCode()), options.clusters);
    }

    public String getName() {
        return primaryItem.getName();
    }

    public CompletableFuture<Void> setEndpointState(String clusterName, String attributeName, Object state) {
        return client.setEndpointState(primaryItem.getName(), clusterName, attributeName, state);
    }

    // TODO Move all of the following into a shared UTIL class, copied from cluster converters

    /**
     * Converts a ZigBee 8 bit level as used in Level Control cluster and others to a percentage
     *
     * @param level an integer between 0 and 254
     * @return the scaled {@link PercentType}
     */
    public static PercentType levelToPercent(int level) {
        return new PercentType((int) (level * 100.0 / 254.0 + 0.5));
    }

    /**
     * Converts a {@link DecimalType} to an 8 bit level scaled between 0 and 254
     *
     * @param percent the {@link DecimalType} to convert
     * @return a scaled value between 0 and 254
     */

    public static int percentToLevel(PercentType percent) {
        return (int) (percent.floatValue() * 254.0f / 100.0f + 0.5f);
    }

    /**
     * Converts a {@link Command} to a ZigBee / Matter temperature integer
     *
     * @param type the {@link Type} to convert
     * @return the {@link Type} or null if the conversion was not possible
     */
    public static @Nullable Integer temperatureToValue(Type type) {
        BigDecimal value = null;
        if (type instanceof QuantityType<?> quantity) {
            if (quantity.getUnit() == SIUnits.CELSIUS) {
                value = quantity.toBigDecimal();
            } else if (quantity.getUnit() == ImperialUnits.FAHRENHEIT) {
                QuantityType<?> celsius = quantity.toUnit(SIUnits.CELSIUS);
                if (celsius == null) {
                    return null;
                }
                value = celsius.toBigDecimal();
            } else {
                return null;
            }
        } else if (type instanceof Number number) {
            // No scale, so assumed to be Celsius
            value = BigDecimal.valueOf(number.doubleValue());
        }
        if (value == null) {
            return null;
        }
        // originally this used RoundingMode.CEILING, if there are accuracy problems, we may want to revisit that
        return value.setScale(2, RoundingMode.HALF_UP).multiply(TEMPERATURE_MULTIPLIER).intValue();
    }

    /**
     * Converts an integer value into a {@link QuantityType}. The temperature as an integer is assumed to be multiplied
     * by 100 as per the ZigBee / Matter standard format.
     *
     * @param value the integer value to convert
     * @return the {@link QuantityType}
     */
    public static QuantityType valueToTemperature(int value) {
        return new QuantityType<>(BigDecimal.valueOf(value, 2), SIUnits.CELSIUS);
    }

    protected MetaDataMapping metaDataMapping(GenericItem item) {
        Metadata metadata = metadataRegistry.get(new MetadataKey("matter", item.getUID()));
        String label = item.getLabel();
        List<String> attributeList = List.of();
        Map<String, Object> config = Map.of();
        if (metadata != null) {
            attributeList = Arrays.asList(metadata.getValue().split(","));
            config = new HashMap<>(metadata.getConfiguration());
            if (config.get("label") instanceof String customLabel) {
                label = customLabel;
            }

            // convert the value of fixed labels into a cluster attribute
            if (config.get("fixedLabels") instanceof String fixedLabels) {
                List<KeyValue> labelList = parseFixedLabels(fixedLabels);
                config.put("fixedLabel.labelList", labelList);
            }
        }

        if (label == null) {
            label = item.getName();
        }

        return new MetaDataMapping(attributeList, config, label);
    }

    class MetaDataMapping {
        public final List<String> attributes;
        public final Map<String, Object> config;
        public final String label;

        public MetaDataMapping(List<String> attributes, Map<String, Object> config, String label) {
            this.attributes = attributes;
            this.config = config;
            this.label = label;
        }

        // this parses any foo.bar=
        public Map<String, Object> getAttributeOptions() {
            return config.entrySet().stream().filter(entry -> entry.getKey().contains("."))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }
    }

    class MatterDeviceOptions {
        public final Map<String, Map<String, Object>> clusters;
        public final String label;

        public MatterDeviceOptions(Map<String, Object> attributes, String label) {
            this.clusters = mapClusterAttributes(attributes);
            this.label = label;
        }
    }

    Map<String, Map<String, Object>> mapClusterAttributes(Map<String, Object> clusterAttributes) {
        Map<String, Map<String, Object>> returnMap = new HashMap<>();
        clusterAttributes.forEach((key, value) -> {
            String[] parts = key.split("\\.");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Key must be in the format 'clusterName.attributeName'");
            }
            String clusterName = parts[0];
            String attributeName = parts[1];

            // Get or create the child map for the clusterName
            Map<String, Object> attributes = returnMap.computeIfAbsent(clusterName, k -> new HashMap<>());

            // Update the attributeName with the value
            if (attributes != null) {
                attributes.put(attributeName, value);
            }
        });
        return returnMap;
    }

    private List<KeyValue> parseFixedLabels(String labels) {
        Map<String, String> keyValueMap = Arrays.stream(labels.split(", ")).map(pair -> pair.split("=", 2))
                .filter(parts -> parts.length == 2)
                .collect(Collectors.toMap(parts -> parts[0].trim(), parts -> parts[1].trim()));
        return keyValueMap.entrySet().stream().map(entry -> new KeyValue(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    class KeyValue {
        public final String label;
        public final String value;

        public KeyValue(String label, String value) {
            this.label = label;
            this.value = value;
        }
    }
}
