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
package org.openhab.binding.matter.internal.devices.converter;

import static org.openhab.binding.matter.internal.MatterBindingConstants.CHANNEL_BOOLEANSTATE_STATEVALUE;
import static org.openhab.binding.matter.internal.MatterBindingConstants.CHANNEL_LABEL_BOOLEANSTATE_STATEVALUE;
import static org.openhab.binding.matter.internal.MatterBindingConstants.ITEM_TYPE_SWITCH;

import java.util.Collections;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.matter.internal.client.model.cluster.gen.BooleanStateCluster;
import org.openhab.binding.matter.internal.client.model.ws.AttributeChangedMessage;
import org.openhab.binding.matter.internal.handler.EndpointHandler;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.thing.Channel;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.ThingUID;
import org.openhab.core.thing.binding.builder.ChannelBuilder;
import org.openhab.core.types.Command;
import org.openhab.core.types.StateDescription;

/**
 * @author Dan Cunningham
 */
@NonNullByDefault
public class BooleanStateConverter extends GenericConverter<BooleanStateCluster> {

    public BooleanStateConverter(BooleanStateCluster cluster, EndpointHandler handler) {
        super(cluster, handler);
    }

    public Map<Channel, @Nullable StateDescription> createChannels(ThingUID thingUID) {
        Channel channel = ChannelBuilder
                .create(new ChannelUID(thingUID, CHANNEL_BOOLEANSTATE_STATEVALUE.getId()), ITEM_TYPE_SWITCH)
                .withType(CHANNEL_BOOLEANSTATE_STATEVALUE).withLabel(CHANNEL_LABEL_BOOLEANSTATE_STATEVALUE).build();
        return Collections.singletonMap(channel, null);
    }

    public void handleCommand(ChannelUID channelUID, Command command) {
    }

    public void onEvent(AttributeChangedMessage message) {
        switch (message.path.attributeName) {
            case "stateValue":
                if (message.value instanceof Boolean booleanValue) {
                    updateState(CHANNEL_BOOLEANSTATE_STATEVALUE, OnOffType.from(booleanValue));

                }
        }
    }

    public void updateCluster(BooleanStateCluster cluster) {
        super.updateCluster(cluster);
        updateState(CHANNEL_BOOLEANSTATE_STATEVALUE, OnOffType.from(cluster.stateValue));
    }
}
