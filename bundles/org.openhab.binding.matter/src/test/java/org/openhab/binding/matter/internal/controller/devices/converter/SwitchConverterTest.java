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
import static org.junit.jupiter.api.Assertions.assertTrue;
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
import org.openhab.binding.matter.internal.client.dto.cluster.gen.SwitchCluster;
import org.openhab.binding.matter.internal.client.dto.ws.AttributeChangedMessage;
import org.openhab.binding.matter.internal.client.dto.ws.EventTriggeredMessage;
import org.openhab.binding.matter.internal.client.dto.ws.Path;
import org.openhab.binding.matter.internal.client.dto.ws.TriggerEvent;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.thing.Channel;
import org.openhab.core.thing.ChannelGroupUID;
import org.openhab.core.thing.type.ChannelKind;
import org.openhab.core.types.StateDescription;

/**
 * Test class for SwitchConverter
 * 
 * @author Dan Cunningham - Initial contribution
 */
class SwitchConverterTest {

    @Mock
    private SwitchCluster mockCluster;
    @Mock
    private MatterBridgeClient mockBridgeClient;
    @Mock
    private MatterStateDescriptionOptionProvider mockStateDescriptionProvider;
    @Mock
    private MatterChannelTypeProvider mockChannelTypeProvider;

    private TestMatterBaseThingHandler mockHandler;
    private SwitchConverter converter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockHandler = Mockito.spy(new TestMatterBaseThingHandler(mockBridgeClient, mockStateDescriptionProvider,
                mockChannelTypeProvider));
        mockCluster.featureMap = new SwitchCluster.FeatureMap(true, true, true, true, true);
        mockCluster.numberOfPositions = 2;
        converter = new SwitchConverter(mockCluster, mockHandler, 1, "TestLabel");
    }

    @Test
    void testCreateChannels() {
        ChannelGroupUID thingUID = new ChannelGroupUID("matter:node:test:12345:1");
        Map<Channel, StateDescription> channels = converter.createChannels(thingUID);

        // Should create channels for switch position and all trigger events
        assertEquals(8, channels.size());

        boolean hasStateChannel = false;
        int triggerChannels = 0;

        for (Channel channel : channels.keySet()) {
            if (channel.getKind() == ChannelKind.STATE) {
                hasStateChannel = true;
                assertEquals("Number", channel.getAcceptedItemType());
                assertEquals("switch-switch", channel.getUID().getIdWithoutGroup());
            } else if (channel.getKind() == ChannelKind.TRIGGER) {
                triggerChannels++;
            }
        }

        assertTrue(hasStateChannel);
        assertEquals(7, triggerChannels); // All trigger channels enabled
    }

    @Test
    void testOnEventWithCurrentPosition() {
        AttributeChangedMessage message = new AttributeChangedMessage();
        message.path = new Path();
        message.path.attributeName = "currentPosition";
        message.value = 1;
        converter.onEvent(message);
        verify(mockHandler, times(1)).updateState(eq(1), eq("switch-switch"), eq(new DecimalType(1)));
    }

    @Test
    void testOnEventWithTrigger() {
        EventTriggeredMessage message = new EventTriggeredMessage();
        message.path = new Path();
        message.path.eventName = "SwitchLatched";
        TriggerEvent event = new TriggerEvent();
        event.data = "{\"newPosition\": 1}";
        message.events = new TriggerEvent[] { event };
        converter.onEvent(message);
        verify(mockHandler, times(1)).triggerChannel(eq(1), eq("switch-switchlatched"),
                eq("\"{\\\"newPosition\\\": 1}\""));
    }

    @Test
    void testInitState() {
        mockCluster.currentPosition = 1;
        converter.initState();
        verify(mockHandler, times(1)).updateState(eq(1), eq("switch-switch"), eq(new DecimalType(1)));
    }
}
