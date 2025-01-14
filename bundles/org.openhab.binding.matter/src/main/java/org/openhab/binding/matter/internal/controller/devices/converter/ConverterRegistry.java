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

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.matter.internal.client.model.cluster.gen.BaseCluster;
import org.openhab.binding.matter.internal.client.model.cluster.gen.BooleanStateCluster;
import org.openhab.binding.matter.internal.client.model.cluster.gen.ColorControlCluster;
import org.openhab.binding.matter.internal.client.model.cluster.gen.DoorLockCluster;
import org.openhab.binding.matter.internal.client.model.cluster.gen.ElectricalPowerMeasurementCluster;
import org.openhab.binding.matter.internal.client.model.cluster.gen.FanControlCluster;
import org.openhab.binding.matter.internal.client.model.cluster.gen.IlluminanceMeasurementCluster;
import org.openhab.binding.matter.internal.client.model.cluster.gen.LevelControlCluster;
import org.openhab.binding.matter.internal.client.model.cluster.gen.ModeSelectCluster;
import org.openhab.binding.matter.internal.client.model.cluster.gen.OccupancySensingCluster;
import org.openhab.binding.matter.internal.client.model.cluster.gen.OnOffCluster;
import org.openhab.binding.matter.internal.client.model.cluster.gen.PowerSourceCluster;
import org.openhab.binding.matter.internal.client.model.cluster.gen.RelativeHumidityMeasurementCluster;
import org.openhab.binding.matter.internal.client.model.cluster.gen.SwitchCluster;
import org.openhab.binding.matter.internal.client.model.cluster.gen.TemperatureMeasurementCluster;
import org.openhab.binding.matter.internal.client.model.cluster.gen.ThermostatCluster;
import org.openhab.binding.matter.internal.client.model.cluster.gen.WiFiNetworkDiagnosticsCluster;
import org.openhab.binding.matter.internal.client.model.cluster.gen.WindowCoveringCluster;
import org.openhab.binding.matter.internal.handler.MatterBaseThingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link ConverterRegistry}
 *
 * @author Dan Cunningham - Initial contribution
 */
@NonNullByDefault
public class ConverterRegistry {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConverterRegistry.class);
    private static final Map<Integer, Class<? extends GenericConverter<? extends BaseCluster>>> CONVERTERS = new HashMap<>();

    static {
        ConverterRegistry.registerConverter(ColorControlCluster.CLUSTER_ID, ColorControlConverter.class);
        ConverterRegistry.registerConverter(LevelControlCluster.CLUSTER_ID, LevelControlConverter.class);
        ConverterRegistry.registerConverter(ModeSelectCluster.CLUSTER_ID, ModeSelectConverter.class);
        ConverterRegistry.registerConverter(OnOffCluster.CLUSTER_ID, OnOffConverter.class);
        ConverterRegistry.registerConverter(SwitchCluster.CLUSTER_ID, SwitchConverter.class);
        ConverterRegistry.registerConverter(ThermostatCluster.CLUSTER_ID, ThermostatConverter.class);
        ConverterRegistry.registerConverter(WindowCoveringCluster.CLUSTER_ID, WindowCoveringConverter.class);
        ConverterRegistry.registerConverter(PowerSourceCluster.CLUSTER_ID, PowerSourceConverter.class);
        ConverterRegistry.registerConverter(FanControlCluster.CLUSTER_ID, FanControlConverter.class);
        ConverterRegistry.registerConverter(RelativeHumidityMeasurementCluster.CLUSTER_ID,
                RelativeHumidityMeasurementConverter.class);
        ConverterRegistry.registerConverter(TemperatureMeasurementCluster.CLUSTER_ID,
                TemperatureMeasurementConverter.class);
        ConverterRegistry.registerConverter(OccupancySensingCluster.CLUSTER_ID, OccupancySensingConverter.class);
        ConverterRegistry.registerConverter(IlluminanceMeasurementCluster.CLUSTER_ID,
                IlluminanceMeasurementConverter.class);
        ConverterRegistry.registerConverter(BooleanStateCluster.CLUSTER_ID, BooleanStateConverter.class);
        ConverterRegistry.registerConverter(WiFiNetworkDiagnosticsCluster.CLUSTER_ID,
                WiFiNetworkDiagnosticsConverter.class);
        ConverterRegistry.registerConverter(DoorLockCluster.CLUSTER_ID, DoorLockConverter.class);
        ConverterRegistry.registerConverter(ElectricalPowerMeasurementCluster.CLUSTER_ID,
                ElectricalPowerMeasurementConverter.class);
    }

    public static void registerConverter(Integer clusterId,
            Class<? extends GenericConverter<? extends BaseCluster>> converter) {
        CONVERTERS.put(clusterId, converter);
    }

    public static @Nullable GenericConverter<? extends BaseCluster> createConverter(BaseCluster cluster,
            MatterBaseThingHandler handler, int endpointNumber, String labelPrefix) {
        Class<? extends GenericConverter<? extends BaseCluster>> clazz = CONVERTERS.get(cluster.id);
        if (clazz != null) {
            try {
                Class<?>[] constructorParameterTypes = new Class<?>[] { cluster.getClass(),
                        MatterBaseThingHandler.class, int.class, String.class };
                Constructor<? extends GenericConverter<? extends BaseCluster>> constructor = clazz
                        .getConstructor(constructorParameterTypes);
                return constructor.newInstance(cluster, handler, endpointNumber, labelPrefix);
            } catch (Exception e) {
                LOGGER.debug("Could not create converter", e);
            }
        } else {
            LOGGER.debug("No converter found for cluster {}", cluster.id);
        }
        return null;
    }
}
