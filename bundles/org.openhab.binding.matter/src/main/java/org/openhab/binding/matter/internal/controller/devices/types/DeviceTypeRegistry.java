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
package org.openhab.binding.matter.internal.controller.devices.types;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.matter.internal.client.dto.cluster.gen.DeviceTypes;
import org.openhab.binding.matter.internal.handler.MatterBaseThingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link DeviceTypeRegistry}
 *
 * @author Dan Cunningham - Initial contribution
 */
@NonNullByDefault
public class DeviceTypeRegistry {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceTypeRegistry.class);
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static final Map<Integer, Class<? extends DeviceType>> DEVICE_TYPES = new HashMap();

    static {
        List.of(DeviceTypes.OnOffLight, DeviceTypes.OnOffLightSwitch, DeviceTypes.OnOffPlugInUnit,
                DeviceTypes.DimmableLight, DeviceTypes.DimmablePlugInUnit, DeviceTypes.DimmerSwitch,
                DeviceTypes.ColorDimmerSwitch, DeviceTypes.ExtendedColorLight, DeviceTypes.ColorTemperatureLight)
                .forEach(type -> DeviceTypeRegistry.registerDeviceType(type, LightingType.class));
    }

    /**
     * Register a device type with the device type id.
     *
     * @param deviceTypeId
     * @param deviceType
     */
    public static void registerDeviceType(Integer deviceTypeId, Class<? extends DeviceType> deviceType) {
        DEVICE_TYPES.put(deviceTypeId, deviceType);
    }

    /**
     * Create a device type based on the device type id. If the device type is not found, a generic type is returned.
     *
     * @param deviceTypeId
     * @param handler
     * @return
     */
    @SuppressWarnings("null")
    public static DeviceType createDeviceType(Integer deviceTypeId, MatterBaseThingHandler handler,
            Integer endpointNumber) {
        Class<? extends DeviceType> clazz = DEVICE_TYPES.get(deviceTypeId);
        if (clazz != null) {
            try {
                Class<?>[] constructorParameterTypes = new Class<?>[] { Integer.class, MatterBaseThingHandler.class,
                        Integer.class };
                Constructor<? extends DeviceType> constructor = clazz.getConstructor(constructorParameterTypes);
                return constructor.newInstance(deviceTypeId, handler, endpointNumber);
            } catch (Exception e) {
                LOGGER.debug("Could not create device type", e);
            }
        }
        return new GenericType(0, handler, endpointNumber);
    }
}
