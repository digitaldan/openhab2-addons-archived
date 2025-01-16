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

import static org.openhab.binding.matter.internal.MatterBindingConstants.CHANNEL_ID_ILLUMINANCEMEASURMENT_MEASUREDVALUE;
import static org.openhab.binding.matter.internal.MatterBindingConstants.CHANNEL_ILLUMINANCEMEASURMENT_MEASUREDVALUE;
import static org.openhab.binding.matter.internal.MatterBindingConstants.CHANNEL_LABEL_ILLUMINANCEMEASURMENT_MEASUREDVALUE;
import static org.openhab.binding.matter.internal.MatterBindingConstants.ITEM_TYPE_NUMBER_ILLUMINANCE;

import java.util.Collections;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.matter.internal.client.model.cluster.gen.IlluminanceMeasurementCluster;
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
 * The {@link IlluminanceMeasurementConverter}
 *
 * @author Dan Cunningham - Initial contribution
 */
@NonNullByDefault
public class IlluminanceMeasurementConverter extends GenericConverter<IlluminanceMeasurementCluster> {

    public IlluminanceMeasurementConverter(IlluminanceMeasurementCluster cluster, MatterBaseThingHandler handler,
            int endpointNumber, String labelPrefix) {
        super(cluster, handler, endpointNumber, labelPrefix);
    }

    @Override
    public Map<Channel, @Nullable StateDescription> createChannels(ChannelGroupUID thingUID) {
        Channel channel = ChannelBuilder
                .create(new ChannelUID(thingUID, CHANNEL_ID_ILLUMINANCEMEASURMENT_MEASUREDVALUE),
                        ITEM_TYPE_NUMBER_ILLUMINANCE)
                .withType(CHANNEL_ILLUMINANCEMEASURMENT_MEASUREDVALUE)
                .withLabel(formatLabel(CHANNEL_LABEL_ILLUMINANCEMEASURMENT_MEASUREDVALUE)).build();
        return Collections.singletonMap(channel, null);
    }

    @Override
    public void onEvent(AttributeChangedMessage message) {
        switch (message.path.attributeName) {
            case "measuredValue":
                updateState(CHANNEL_ID_ILLUMINANCEMEASURMENT_MEASUREDVALUE, new DecimalType((Number) message.value));
                break;
        }
        super.onEvent(message);
    }

    @Override
    public void initState() {
        updateState(CHANNEL_ID_ILLUMINANCEMEASURMENT_MEASUREDVALUE,
                initializingCluster.measuredValue != null ? new DecimalType(initializingCluster.measuredValue)
                        : UnDefType.UNDEF);
    }
}
