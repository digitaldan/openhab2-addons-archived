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

import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.matter.internal.bridge.MatterBridgeClient;
import org.openhab.core.items.GenericItem;
import org.openhab.core.items.Item;
import org.openhab.core.items.MetadataRegistry;
import org.openhab.core.types.State;

/**
 * The {@link OnOffLightDevice}
 *
 * @author Dan Cunningham - Initial contribution
 */
@NonNullByDefault
public class TemperatureSensorDevice extends GenericDevice {

    public TemperatureSensorDevice(MetadataRegistry metadataRegistry, MatterBridgeClient client, GenericItem item) {
        super(metadataRegistry, client, item);
    }

    @Override
    public String deviceType() {
        return "TemperatureSensor";
    }

    @Override
    public void handleMatterEvent(String clusterName, String attributeName, Object data) {
    }

    @Override
    public MatterDeviceOptions activate() {
        dispose();
        primaryItem.addStateChangeListener(this);
        MetaDataMapping primaryMetadata = metaDataMapping(primaryItem);
        Map<String, Object> attributeMap = primaryMetadata.getAttributeOptions();
        State state = primaryItem.getState();
        Integer value = temperatureToValue(state);
        attributeMap.put("temperatureMeasurement.measuredValue", value == null ? 0 : value);
        return new MatterDeviceOptions(attributeMap, primaryMetadata.label);
    }

    @Override
    public void dispose() {
        primaryItem.removeStateChangeListener(this);
    }

    @Override
    public void updateState(Item item, State state) {
        Integer value = temperatureToValue(state);
        if (value != null) {
            setEndpointState("temperatureMeasurement", "measuredValue", value);
        } else {
            logger.debug("Could not convert {} to matter value", state.toString());
        }
    }
}
