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

import static org.openhab.binding.matter.internal.MatterBindingConstants.CHANNEL_LEVEL_LEVEL;
import static org.openhab.binding.matter.internal.MatterBindingConstants.CHANNEL_ONOFF_ONOFF;

import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.matter.internal.client.model.cluster.BaseCluster;
import org.openhab.binding.matter.internal.client.model.cluster.ClusterCommand;
import org.openhab.binding.matter.internal.client.model.cluster.gen.ColorControlCluster;
import org.openhab.binding.matter.internal.client.model.cluster.gen.DeviceTypes;
import org.openhab.binding.matter.internal.client.model.cluster.gen.LevelControlCluster;
import org.openhab.binding.matter.internal.client.model.cluster.gen.OnOffCluster;
import org.openhab.binding.matter.internal.client.model.ws.AttributeChangedMessage;
import org.openhab.binding.matter.internal.controller.devices.converter.ColorControlConverter;
import org.openhab.binding.matter.internal.controller.devices.converter.GenericConverter;
import org.openhab.binding.matter.internal.handler.MatterBaseThingHandler;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.library.types.PercentType;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.type.ChannelTypeUID;
import org.openhab.core.types.Command;
import org.openhab.core.types.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Dan Cunningham - Initial contribution
 *
 *         Lighting requires special handling for the OnOff, ColorControl and LevelControl clusters.
 *         For example, the Matter specification mandates Switches also must have a LevelControl cluster, even though
 *         they do not support dimming. We will filter those clusters out as well as coordinate commands among required
 *         clusters.
 *
 */
@NonNullByDefault
public class LightingType extends DeviceType {
    private final Logger logger = LoggerFactory.getLogger(LightingType.class);
    private OnOffType lastOnOff = OnOffType.OFF;

    public LightingType(Integer deviceType, MatterBaseThingHandler handler, Integer endpointNumber) {
        super(deviceType, handler, endpointNumber);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        logger.debug("Handling command for channel: {}", channelUID);
        // we want openHAB OnOff commands to always use OnOffCluster if its available (and not LevelControlCluster or
        // ColorControlCluster)
        if (command instanceof OnOffType onOffType) {
            ClusterCommand onOffCommand = onOffType == OnOffType.ON ? OnOffCluster.on() : OnOffCluster.off();
            handler.sendClusterCommand(endpointNumber, OnOffCluster.CLUSTER_NAME, onOffCommand);
        } else {
            super.handleCommand(channelUID, command);
        }
    }

    @Override
    public void onEvent(AttributeChangedMessage message) {
        logger.debug("OnEvent: {} with value {}", message.path.attributeName, message.value);
        switch (message.path.attributeName) {
            case "currentLevel":
                logger.debug("currentLevel lastOnOff {}", lastOnOff);
                PercentType level = GenericConverter.levelToPercent(((Double) message.value).intValue());
                // if the device is off, we don't care about level
                if (lastOnOff != OnOffType.OFF) {
                    updateChannel(LevelControlCluster.CLUSTER_ID, CHANNEL_LEVEL_LEVEL, level);
                    if (clusterToConverters.get(
                            ColorControlCluster.CLUSTER_ID) instanceof ColorControlConverter colorControlConverter) {
                        colorControlConverter.updateBrightness(level);
                    }
                }
                return;
            case "onOff":
                lastOnOff = OnOffType.from((Boolean) message.value);
                logger.debug("onOff lastOnOff {}", lastOnOff);
                updateChannel(LevelControlCluster.CLUSTER_ID, CHANNEL_LEVEL_LEVEL, lastOnOff);
                updateChannel(OnOffCluster.CLUSTER_ID, CHANNEL_ONOFF_ONOFF, lastOnOff);
                if (clusterToConverters
                        .get(ColorControlCluster.CLUSTER_ID) instanceof ColorControlConverter colorControlConverter) {
                    colorControlConverter.updateOnOff(lastOnOff);
                }
                return;
        }
        // no matching cluster, bubble up for generic cluster processing
        super.onEvent(message);
    }

    @Override
    public void refreshState() {
        super.refreshState();
        // if the device is off, levels are 0 in openHAB, but in matter levels are detached from onOff
        if (allClusters.get(OnOffCluster.CLUSTER_NAME) instanceof OnOffCluster onOffCluster) {
            lastOnOff = OnOffType.from(onOffCluster.onOff);
            if (lastOnOff == OnOffType.OFF) {
                // this could use some improvement doing this after we already refreshed channels, ie: how to initially
                // tie state between clusters
                updateChannel(LevelControlCluster.CLUSTER_ID, CHANNEL_LEVEL_LEVEL, lastOnOff);
                updateChannel(OnOffCluster.CLUSTER_ID, CHANNEL_ONOFF_ONOFF, lastOnOff);
                if (clusterToConverters
                        .get(ColorControlCluster.CLUSTER_ID) instanceof ColorControlConverter colorControlConverter) {
                    colorControlConverter.updateOnOff(lastOnOff);
                }
            }
        }
        //do we need to do the same for levelControl and Color?
    }

    @Override
    protected @Nullable GenericConverter<? extends BaseCluster> createConverter(BaseCluster cluster,
            Map<String, BaseCluster> allClusters, String labelPrefix) {
        logger.debug("checking converter for cluster: {}", cluster.getClass().getSimpleName());
        // Skip creating certain converters that this DeviceType will coordinate
        if ((cluster instanceof OnOffCluster && !isSwitch())
                || (cluster instanceof LevelControlCluster && (isSwitch() || isColor()))) {
            return null;
        }

        return super.createConverter(cluster, allClusters, labelPrefix);
    }

    private void updateChannel(Integer clusterId, ChannelTypeUID channelTypeUID, State state) {
        GenericConverter<? extends BaseCluster> converter = clusterToConverters.get(clusterId);
        if (converter != null) {
            @SuppressWarnings("unchecked")
            GenericConverter<BaseCluster> specificConverter = (GenericConverter<BaseCluster>) converter;
            specificConverter.updateState(channelTypeUID, state);
        } else {
            logger.debug("No converter found for clusterId: {}", clusterId);
        }
    }

    private boolean isSwitch() {
        return deviceType.equals(DeviceTypes.OnOffLight) || deviceType.equals(DeviceTypes.OnOffLightSwitch)
                || deviceType.equals(DeviceTypes.OnOffPlugInUnit);
    }

    private boolean isColor() {
        return deviceType.equals(DeviceTypes.ExtendedColorLight)
                || deviceType.equals(DeviceTypes.ColorTemperatureLight);
    }
}
