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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.openhab.binding.matter.internal.MatterChannelTypeProvider;
import org.openhab.binding.matter.internal.MatterStateDescriptionOptionProvider;
import org.openhab.binding.matter.internal.bridge.MatterBridgeClient;
import org.openhab.binding.matter.internal.client.dto.cluster.gen.FanControlCluster;
import org.openhab.binding.matter.internal.client.dto.ws.AttributeChangedMessage;
import org.openhab.binding.matter.internal.client.dto.ws.Path;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.IncreaseDecreaseType;
import org.openhab.core.library.types.PercentType;
import org.openhab.core.thing.Channel;
import org.openhab.core.thing.ChannelGroupUID;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.types.StateDescription;

/**
 * Test class for FanControlConverter
 * 
 * @author Dan Cunningham - Initial contribution
 */
class FanControlConverterTest {

    @Mock
    private FanControlCluster mockCluster;
    @Mock
    private MatterBridgeClient mockBridgeClient;
    @Mock
    private MatterStateDescriptionOptionProvider mockStateDescriptionProvider;
    @Mock
    private MatterChannelTypeProvider mockChannelTypeProvider;

    private TestMatterBaseThingHandler mockHandler;
    private FanControlConverter converter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockHandler = Mockito.spy(new TestMatterBaseThingHandler(mockBridgeClient, mockStateDescriptionProvider,
                mockChannelTypeProvider));
        mockCluster.fanModeSequence = FanControlCluster.FanModeSequenceEnum.OFF_LOW_MED_HIGH_AUTO;
        converter = new FanControlConverter(mockCluster, mockHandler, 1, "TestLabel");
    }

    @Test
    void testCreateChannels() {
        ChannelGroupUID thingUID = new ChannelGroupUID("matter:node:test:12345:1");
        Map<Channel, StateDescription> channels = converter.createChannels(thingUID);
        assertEquals(2, channels.size());

        for (Channel channel : channels.keySet()) {
            String channelId = channel.getUID().getIdWithoutGroup();
            switch (channelId) {
                case "fancontrol-mode":
                    assertEquals("Number", channel.getAcceptedItemType());
                    break;
                case "fancontrol-percent":
                    assertEquals("Dimmer", channel.getAcceptedItemType());
                    break;
            }
        }
    }

    @Test
    void testHandleCommandMode() {
        ChannelUID channelUID = new ChannelUID("matter:node:test:12345:1#fancontrol-mode");
        converter.handleCommand(channelUID, new DecimalType(1)); // Low mode
        verify(mockHandler, times(1)).writeAttribute(eq(1), eq(FanControlCluster.CLUSTER_NAME), eq("fanMode"), eq("1"));
    }

    @Test
    void testHandleCommandPercent() {
        ChannelUID channelUID = new ChannelUID("matter:node:test:12345:1#fancontrol-percent");
        converter.handleCommand(channelUID, new PercentType(50));
        verify(mockHandler, times(1)).writeAttribute(eq(1), eq(FanControlCluster.CLUSTER_NAME), eq("percentSetting"),
                eq("50"));
    }

    @Test
    void testHandleCommandIncrease() {
        ChannelUID channelUID = new ChannelUID("matter:node:test:12345:1#fancontrol-percent");
        converter.handleCommand(channelUID, IncreaseDecreaseType.INCREASE);
        verify(mockHandler, times(1)).sendClusterCommand(eq(1), eq(FanControlCluster.CLUSTER_NAME),
                eq(FanControlCluster.step(FanControlCluster.StepDirectionEnum.INCREASE, false, false)));
    }

    @Test
    void testHandleCommandDecrease() {
        ChannelUID channelUID = new ChannelUID("matter:node:test:12345:1#fancontrol-percent");
        converter.handleCommand(channelUID, IncreaseDecreaseType.DECREASE);
        verify(mockHandler, times(1)).sendClusterCommand(eq(1), eq(FanControlCluster.CLUSTER_NAME),
                eq(FanControlCluster.step(FanControlCluster.StepDirectionEnum.DECREASE, false, true)));
    }

    @Test
    void testOnEventWithFanMode() {
        AttributeChangedMessage message = new AttributeChangedMessage();
        message.path = new Path();
        message.path.attributeName = "fanMode";
        message.value = FanControlCluster.FanModeEnum.LOW.getValue();
        converter.onEvent(message);
        verify(mockHandler, times(1)).updateState(eq(1), eq("fancontrol-mode"), eq(new DecimalType(1)));
    }

    @Test
    void testOnEventWithPercentSetting() {
        AttributeChangedMessage message = new AttributeChangedMessage();
        message.path = new Path();
        message.path.attributeName = "percentSetting";
        message.value = 50;
        converter.onEvent(message);
        verify(mockHandler, times(1)).updateState(eq(1), eq("fancontrol-percent"), eq(new PercentType(50)));
    }

    @Test
    void testInitState() {
        mockCluster.fanMode = FanControlCluster.FanModeEnum.LOW;
        mockCluster.percentSetting = 50;
        converter.initState();
        verify(mockHandler, times(1)).updateState(eq(1), eq("fancontrol-mode"),
                eq(new DecimalType(mockCluster.fanMode.value)));
        verify(mockHandler, times(1)).updateState(eq(1), eq("fancontrol-percent"), eq(new PercentType(50)));
    }
}
