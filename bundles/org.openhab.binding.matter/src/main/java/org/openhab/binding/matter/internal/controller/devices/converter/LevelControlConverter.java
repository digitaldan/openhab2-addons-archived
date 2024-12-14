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

import static org.openhab.binding.matter.internal.MatterBindingConstants.CHANNEL_LABEL_LEVEL_LEVEL;
import static org.openhab.binding.matter.internal.MatterBindingConstants.CHANNEL_LEVEL_LEVEL;
import static org.openhab.binding.matter.internal.MatterBindingConstants.ITEM_TYPE_DIMMER;

import java.util.Collections;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.matter.internal.client.model.cluster.ClusterCommand;
import org.openhab.binding.matter.internal.client.model.cluster.gen.LevelControlCluster;
import org.openhab.binding.matter.internal.client.model.cluster.gen.LevelControlCluster.OptionsBitmap;
import org.openhab.binding.matter.internal.client.model.ws.AttributeChangedMessage;
import org.openhab.binding.matter.internal.handler.MatterBaseThingHandler;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.library.types.PercentType;
import org.openhab.core.thing.Channel;
import org.openhab.core.thing.ChannelGroupUID;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.binding.builder.ChannelBuilder;
import org.openhab.core.types.Command;
import org.openhab.core.types.StateDescription;

/**
 * The {@link LevelControlConverter}
 *
 * @author Dan Cunningham - Initial contribution
 */
@NonNullByDefault
public class LevelControlConverter extends GenericConverter<LevelControlCluster> {

    public LevelControlConverter(LevelControlCluster cluster, MatterBaseThingHandler handler, int endpointNumber,
            String labelPrefix) {
        super(cluster, handler, endpointNumber, labelPrefix);
    }

    @Override
    public Map<Channel, @Nullable StateDescription> createChannels(ChannelGroupUID thingUID) {
        Channel channel = ChannelBuilder.create(new ChannelUID(thingUID, CHANNEL_LEVEL_LEVEL.getId()), ITEM_TYPE_DIMMER)
                .withType(CHANNEL_LEVEL_LEVEL).withLabel(formatLabel(CHANNEL_LABEL_LEVEL_LEVEL)).build();
        return Collections.singletonMap(channel, null);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (command instanceof OnOffType onOffType) {
            ClusterCommand levelCommand = LevelControlCluster.moveToLevelWithOnOff(onOffType == OnOffType.OFF ? 0 : 100,
                    0, new OptionsBitmap(true, true), new OptionsBitmap(true, true));
            handler.sendClusterCommand(endpointNumber, LevelControlCluster.CLUSTER_NAME, levelCommand);
        } else if (command instanceof PercentType percentType) {
            ClusterCommand levelCommand = LevelControlCluster.moveToLevelWithOnOff(percentToLevel(percentType), 0,
                    new OptionsBitmap(true, true), new OptionsBitmap(true, true));
            handler.sendClusterCommand(endpointNumber, LevelControlCluster.CLUSTER_NAME, levelCommand);
        }
        super.handleCommand(channelUID, command);
    }

    @Override
    public void onEvent(AttributeChangedMessage message) {
        switch (message.path.attributeName) {
            case "currentLevel":
                Integer numberValue = message.value instanceof Number number ? number.intValue() : 0;
                logger.debug("currentLevel {}", message.value);
                updateState(CHANNEL_LEVEL_LEVEL, levelToPercent(numberValue));
                break;
        }
    }

    @Override
    public void updateCluster(LevelControlCluster cluster) {
        super.updateCluster(cluster);
        updateState(CHANNEL_LEVEL_LEVEL, levelToPercent(cluster.currentLevel));
    }
}
