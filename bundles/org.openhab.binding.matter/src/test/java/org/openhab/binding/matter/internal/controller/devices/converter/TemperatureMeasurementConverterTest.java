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
import org.openhab.binding.matter.internal.client.dto.cluster.gen.TemperatureMeasurementCluster;
import org.openhab.binding.matter.internal.client.dto.ws.AttributeChangedMessage;
import org.openhab.binding.matter.internal.client.dto.ws.Path;
import org.openhab.core.library.types.QuantityType;
import org.openhab.core.library.unit.SIUnits;
import org.openhab.core.thing.Channel;
import org.openhab.core.thing.ChannelGroupUID;
import org.openhab.core.types.StateDescription;

/**
 * Test class for TemperatureMeasurementConverter
 * 
 * @author Dan Cunningham - Initial contribution
 */
class TemperatureMeasurementConverterTest {

    @Mock
    private TemperatureMeasurementCluster mockCluster;
    @Mock
    private MatterBridgeClient mockBridgeClient;
    @Mock
    private MatterStateDescriptionOptionProvider mockStateDescriptionProvider;
    @Mock
    private MatterChannelTypeProvider mockChannelTypeProvider;

    private TestMatterBaseThingHandler mockHandler;
    private TemperatureMeasurementConverter converter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockHandler = Mockito.spy(new TestMatterBaseThingHandler(mockBridgeClient, mockStateDescriptionProvider,
                mockChannelTypeProvider));
        converter = new TemperatureMeasurementConverter(mockCluster, mockHandler, 1, "TestLabel");
    }

    @Test
    void testCreateChannels() {
        ChannelGroupUID thingUID = new ChannelGroupUID("matter:node:test:12345:1");
        Map<Channel, StateDescription> channels = converter.createChannels(thingUID);
        assertEquals(1, channels.size());
        Channel channel = channels.keySet().iterator().next();
        assertEquals("matter:node:test:12345:1#temperaturemeasurement-measuredvalue", channel.getUID().toString());
        assertEquals("Number:Temperature", channel.getAcceptedItemType());
    }

    @Test
    void testOnEventWithMeasuredValue() {
        AttributeChangedMessage message = new AttributeChangedMessage();
        message.path = new Path();
        message.path.attributeName = "measuredValue";
        message.value = 2000; // 20°C
        converter.onEvent(message);
        verify(mockHandler, times(1)).updateState(eq(1), eq("temperaturemeasurement-measuredvalue"),
                eq(new QuantityType<>(20.0, SIUnits.CELSIUS)));
    }

    @Test
    void testInitState() {
        mockCluster.measuredValue = 2000; // 20°C
        converter.initState();
        verify(mockHandler, times(1)).updateState(eq(1), eq("temperaturemeasurement-measuredvalue"),
                eq(new QuantityType<>(20.0, SIUnits.CELSIUS)));
    }
}
