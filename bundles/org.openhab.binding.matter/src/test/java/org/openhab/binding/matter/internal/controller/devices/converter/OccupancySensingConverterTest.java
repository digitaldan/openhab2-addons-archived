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
import org.openhab.binding.matter.internal.client.dto.cluster.gen.OccupancySensingCluster;
import org.openhab.binding.matter.internal.client.dto.ws.AttributeChangedMessage;
import org.openhab.binding.matter.internal.client.dto.ws.Path;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.thing.Channel;
import org.openhab.core.thing.ChannelGroupUID;
import org.openhab.core.types.StateDescription;

/**
 * Test class for OccupancySensingConverter
 * 
 * @author Dan Cunningham - Initial contribution
 */
class OccupancySensingConverterTest {

    @Mock
    private OccupancySensingCluster mockCluster;
    @Mock
    private MatterBridgeClient mockBridgeClient;
    @Mock
    private MatterStateDescriptionOptionProvider mockStateDescriptionProvider;
    @Mock
    private MatterChannelTypeProvider mockChannelTypeProvider;

    private TestMatterBaseThingHandler mockHandler;
    private OccupancySensingConverter converter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockHandler = Mockito.spy(new TestMatterBaseThingHandler(mockBridgeClient, mockStateDescriptionProvider,
                mockChannelTypeProvider));
        OccupancySensingCluster.OccupancyBitmap bitmap = Mockito.mock(OccupancySensingCluster.OccupancyBitmap.class);
        bitmap.occupied = true;
        mockCluster.occupancy = bitmap;
        converter = new OccupancySensingConverter(mockCluster, mockHandler, 1, "TestLabel");
    }

    @Test
    void testCreateChannels() {
        ChannelGroupUID thingUID = new ChannelGroupUID("matter:node:test:12345:1");
        Map<Channel, StateDescription> channels = converter.createChannels(thingUID);
        assertEquals(1, channels.size());
        Channel channel = channels.keySet().iterator().next();
        assertEquals("matter:node:test:12345:1#occupancysensing-occupied", channel.getUID().toString());
        assertEquals("Switch", channel.getAcceptedItemType());
    }

    @Test
    void testOnEventWithOccupancy() {
        AttributeChangedMessage message = new AttributeChangedMessage();
        message.path = new Path();
        message.path.attributeName = "occupancy";
        OccupancySensingCluster.OccupancyBitmap bitmap = Mockito.mock(OccupancySensingCluster.OccupancyBitmap.class);
        bitmap.occupied = true;
        message.value = bitmap;
        converter.onEvent(message);
        verify(mockHandler, times(1)).updateState(eq(1), eq("occupancysensing-occupied"), eq(OnOffType.ON));
    }

    @Test
    void testInitState() {
        OccupancySensingCluster.OccupancyBitmap bitmap = Mockito.mock(OccupancySensingCluster.OccupancyBitmap.class);
        bitmap.occupied = true;
        mockCluster.occupancy = bitmap;
        converter.initState();
        verify(mockHandler, times(1)).updateState(eq(1), eq("occupancysensing-occupied"), eq(OnOffType.ON));
    }
}
