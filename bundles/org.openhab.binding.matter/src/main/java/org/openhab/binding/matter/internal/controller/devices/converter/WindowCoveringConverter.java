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

import static org.openhab.binding.matter.internal.MatterBindingConstants.CHANNEL_ID_WINDOWCOVERING_LIFT;
import static org.openhab.binding.matter.internal.MatterBindingConstants.CHANNEL_LABEL_WINDOWCOVERING_LIFT;
import static org.openhab.binding.matter.internal.MatterBindingConstants.CHANNEL_WINDOWCOVERING_LIFT;
import static org.openhab.binding.matter.internal.MatterBindingConstants.ITEM_TYPE_ROLLERSHUTTER;

import java.util.Collections;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.matter.internal.client.model.cluster.ClusterCommand;
import org.openhab.binding.matter.internal.client.model.cluster.gen.WindowCoveringCluster;
import org.openhab.binding.matter.internal.client.model.ws.AttributeChangedMessage;
import org.openhab.binding.matter.internal.handler.MatterBaseThingHandler;
import org.openhab.core.library.types.PercentType;
import org.openhab.core.library.types.StopMoveType;
import org.openhab.core.library.types.UpDownType;
import org.openhab.core.thing.Channel;
import org.openhab.core.thing.ChannelGroupUID;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.binding.builder.ChannelBuilder;
import org.openhab.core.types.Command;
import org.openhab.core.types.StateDescription;

/**
 * The {@link WindowCoveringConverter}
 *
 * @author Dan Cunningham - Initial contribution
 */
@NonNullByDefault
public class WindowCoveringConverter extends GenericConverter<WindowCoveringCluster> {

    public WindowCoveringConverter(WindowCoveringCluster cluster, MatterBaseThingHandler handler, int endpointNumber,
            String labelPrefix) {
        super(cluster, handler, endpointNumber, labelPrefix);
    }

    @Override
    public Map<Channel, @Nullable StateDescription> createChannels(ChannelGroupUID thingUID) {
        Channel channel = ChannelBuilder
                .create(new ChannelUID(thingUID, CHANNEL_ID_WINDOWCOVERING_LIFT), ITEM_TYPE_ROLLERSHUTTER)
                .withType(CHANNEL_WINDOWCOVERING_LIFT).withLabel(formatLabel(CHANNEL_LABEL_WINDOWCOVERING_LIFT))
                .build();
        return Collections.singletonMap(channel, null);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (command instanceof UpDownType upDownType) {
            switch (upDownType) {
                case UP:
                    moveCommand(WindowCoveringCluster.upOrOpen());
                    break;
                case DOWN:
                    moveCommand(WindowCoveringCluster.downOrClose());
                    break;
                default:
                    break;
            }
        } else if (command instanceof StopMoveType stopMoveType) {
            switch (stopMoveType) {
                case STOP:
                    moveCommand(WindowCoveringCluster.stopMotion());
                    break;
                default:
                    break;
            }
        } else if (command instanceof PercentType percentType) {
            moveCommand(WindowCoveringCluster.goToLiftPercentage(percentType.intValue()));
        }
        super.handleCommand(channelUID, command);
    }

    @Override
    public void onEvent(AttributeChangedMessage message) {
        Integer numberValue = message.value instanceof Number number ? number.intValue() : 0;
        switch (message.path.attributeName) {
            case "currentPositionLiftPercentage":
                updateState(CHANNEL_ID_WINDOWCOVERING_LIFT, new PercentType(numberValue));
                break;
            case "currentPositionLiftPercent100ths":
                updateState(CHANNEL_ID_WINDOWCOVERING_LIFT, new PercentType(numberValue / 100));
                break;
            default:
                logger.debug("Unknown attribute {}", message.path.attributeName);
        }
        super.onEvent(message);
    }

    @Override
    public void initState() {
        Integer pos = 0;
        if (initializingCluster.currentPositionLift != null) {
            pos = initializingCluster.currentPositionLiftPercentage;
        } else if (initializingCluster.currentPositionLiftPercent100ths != null) {
            pos = initializingCluster.currentPositionLiftPercent100ths / 100;
        }
        updateState(CHANNEL_ID_WINDOWCOVERING_LIFT, new PercentType(pos));
    }

    private void moveCommand(ClusterCommand command) {
        handler.sendClusterCommand(endpointNumber, WindowCoveringCluster.CLUSTER_NAME, command);
    }
}
