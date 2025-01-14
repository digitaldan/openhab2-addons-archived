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

import static org.openhab.binding.matter.internal.MatterBindingConstants.CHANNEL_ELECTRICALPOWERMEASUREMENT_ACTIVECURRENT;
import static org.openhab.binding.matter.internal.MatterBindingConstants.CHANNEL_ELECTRICALPOWERMEASUREMENT_ACTIVEPOWER;
import static org.openhab.binding.matter.internal.MatterBindingConstants.CHANNEL_LABEL_ELECTRICALPOWERMEASUREMENT_ACTIVECURRENT;
import static org.openhab.binding.matter.internal.MatterBindingConstants.CHANNEL_LABEL_ELECTRICALPOWERMEASUREMENT_ACTIVEPOWER;
import static org.openhab.binding.matter.internal.MatterBindingConstants.ITEM_TYPE_NUMBER_ELECTRICCURRENT;
import static org.openhab.binding.matter.internal.MatterBindingConstants.ITEM_TYPE_NUMBER_POWER;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.matter.internal.client.model.cluster.gen.ElectricalPowerMeasurementCluster;
import org.openhab.binding.matter.internal.client.model.ws.AttributeChangedMessage;
import org.openhab.binding.matter.internal.handler.MatterBaseThingHandler;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.thing.Channel;
import org.openhab.core.thing.ChannelGroupUID;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.binding.builder.ChannelBuilder;
import org.openhab.core.types.StateDescription;
import org.openhab.core.types.UnDefType;

/**
 * The {@link TemperatureMeasurementConverter}
 *
 * @author Dan Cunningham - Initial contribution
 */
@NonNullByDefault
public class ElectricalPowerMeasurementConverter extends GenericConverter<ElectricalPowerMeasurementCluster> {

    public ElectricalPowerMeasurementConverter(ElectricalPowerMeasurementCluster cluster,
            MatterBaseThingHandler handler, int endpointNumber, String labelPrefix) {
        super(cluster, handler, endpointNumber, labelPrefix);
    }

    @Override
    public Map<Channel, @Nullable StateDescription> createChannels(ChannelGroupUID thingUID) {
        Map<Channel, @Nullable StateDescription> map = new HashMap<>();
        // Active Power is mandatory
        Channel activePowerChannel = ChannelBuilder
                .create(new ChannelUID(thingUID, CHANNEL_ELECTRICALPOWERMEASUREMENT_ACTIVEPOWER.getId()),
                        ITEM_TYPE_NUMBER_POWER)
                .withType(CHANNEL_ELECTRICALPOWERMEASUREMENT_ACTIVEPOWER)
                .withLabel(formatLabel(CHANNEL_LABEL_ELECTRICALPOWERMEASUREMENT_ACTIVEPOWER)).build();
        map.put(activePowerChannel, null);
        
        // optional cluster if not null
        if (initializingCluster.activeCurrent != null) {
            Channel activeCurrentChannel = ChannelBuilder
                    .create(new ChannelUID(thingUID, CHANNEL_ELECTRICALPOWERMEASUREMENT_ACTIVECURRENT.getId()),
                            ITEM_TYPE_NUMBER_ELECTRICCURRENT)
                    .withType(CHANNEL_ELECTRICALPOWERMEASUREMENT_ACTIVECURRENT)
                    .withLabel(formatLabel(CHANNEL_LABEL_ELECTRICALPOWERMEASUREMENT_ACTIVECURRENT)).build();
            map.put(activeCurrentChannel, null);
        }
        return map;
    }

    @Override
    public void onEvent(AttributeChangedMessage message) {
        switch (message.path.attributeName) {
            case "activePower":
                updateState(CHANNEL_ELECTRICALPOWERMEASUREMENT_ACTIVEPOWER,
                        new DecimalType(message.value instanceof Number number ? number.intValue() : 0));
                break;
            case "activeCurrent":
                updateState(CHANNEL_ELECTRICALPOWERMEASUREMENT_ACTIVECURRENT,
                        new DecimalType(message.value instanceof Number number ? number.intValue() : 0));
                break;
        }
        super.onEvent(message);
    }

    @Override
    public void initState() {
        updateState(CHANNEL_ELECTRICALPOWERMEASUREMENT_ACTIVEPOWER,
                initializingCluster.activePower != null ? new DecimalType(initializingCluster.activePower)
                        : UnDefType.NULL);
        // optional cluster
        if (initializingCluster.activeCurrent != null) {
            updateState(CHANNEL_ELECTRICALPOWERMEASUREMENT_ACTIVECURRENT,
                    initializingCluster.activeCurrent != null ? new DecimalType(initializingCluster.activeCurrent)
                            : UnDefType.NULL);
        }
    }
}
