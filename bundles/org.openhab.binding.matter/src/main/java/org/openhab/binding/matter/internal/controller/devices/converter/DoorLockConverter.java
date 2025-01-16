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

import static org.openhab.binding.matter.internal.MatterBindingConstants.CHANNEL_DOORLOCK_STATE;
import static org.openhab.binding.matter.internal.MatterBindingConstants.CHANNEL_ID_DOORLOCK_STATE;
import static org.openhab.binding.matter.internal.MatterBindingConstants.CHANNEL_LABEL_DOORLOCK_STATE;
import static org.openhab.binding.matter.internal.MatterBindingConstants.ITEM_TYPE_SWITCH;

import java.util.Collections;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.matter.internal.client.dto.cluster.ClusterCommand;
import org.openhab.binding.matter.internal.client.dto.cluster.gen.DoorLockCluster;
import org.openhab.binding.matter.internal.client.dto.ws.AttributeChangedMessage;
import org.openhab.binding.matter.internal.handler.MatterBaseThingHandler;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.thing.Channel;
import org.openhab.core.thing.ChannelGroupUID;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.binding.builder.ChannelBuilder;
import org.openhab.core.types.Command;
import org.openhab.core.types.StateDescription;

/**
 * The {@link SwitchConverter}
 *
 * @author Dan Cunningham - Initial contribution
 */
@NonNullByDefault
public class DoorLockConverter extends GenericConverter<DoorLockCluster> {

    public DoorLockConverter(DoorLockCluster cluster, MatterBaseThingHandler handler, int endpointNumber,
            String labelPrefix) {
        super(cluster, handler, endpointNumber, labelPrefix);
    }

    @Override
    public Map<Channel, @Nullable StateDescription> createChannels(ChannelGroupUID thingUID) {
        Channel channel = ChannelBuilder.create(new ChannelUID(thingUID, CHANNEL_ID_DOORLOCK_STATE), ITEM_TYPE_SWITCH)
                .withType(CHANNEL_DOORLOCK_STATE).withLabel(formatLabel(CHANNEL_LABEL_DOORLOCK_STATE)).build();

        return Collections.singletonMap(channel, null);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (command instanceof OnOffType onOffType) {
            ClusterCommand doorLockCommand = onOffType == OnOffType.ON ? DoorLockCluster.lockDoor("")
                    : DoorLockCluster.unlockDoor("");
            handler.sendClusterCommand(endpointNumber, DoorLockCluster.CLUSTER_NAME, doorLockCommand);
        }
        super.handleCommand(channelUID, command);
    }

    @Override
    public void onEvent(AttributeChangedMessage message) {
        Integer numberValue = message.value instanceof Number number ? number.intValue() : 0;
        switch (message.path.attributeName) {
            case "lockState":
                updateState(CHANNEL_ID_DOORLOCK_STATE,
                        numberValue.equals(DoorLockCluster.LockStateEnum.LOCKED.getValue()) ? OnOffType.ON
                                : OnOffType.OFF);
            default:
                break;
        }
        super.onEvent(message);
    }

    @Override
    public void initState() {
        updateState(CHANNEL_ID_DOORLOCK_STATE,
                initializingCluster.lockState.getValue().equals(DoorLockCluster.LockStateEnum.LOCKED.getValue())
                        ? OnOffType.ON
                        : OnOffType.OFF);
    }
}
