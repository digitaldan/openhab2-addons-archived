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
import org.openhab.binding.matter.internal.client.dto.cluster.gen.WiFiNetworkDiagnosticsCluster;
import org.openhab.binding.matter.internal.client.dto.ws.AttributeChangedMessage;
import org.openhab.binding.matter.internal.client.dto.ws.Path;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.thing.Channel;
import org.openhab.core.thing.ChannelGroupUID;
import org.openhab.core.types.StateDescription;
import org.openhab.core.types.UnDefType;

/**
 * Test class for WiFiNetworkDiagnosticsConverter
 * 
 * @author Dan Cunningham - Initial contribution
 */
class WiFiNetworkDiagnosticsConverterTest {

    @Mock
    private WiFiNetworkDiagnosticsCluster mockCluster;
    @Mock
    private MatterBridgeClient mockBridgeClient;
    @Mock
    private MatterStateDescriptionOptionProvider mockStateDescriptionProvider;
    @Mock
    private MatterChannelTypeProvider mockChannelTypeProvider;

    private TestMatterBaseThingHandler mockHandler;
    private WiFiNetworkDiagnosticsConverter converter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockHandler = Mockito.spy(new TestMatterBaseThingHandler(mockBridgeClient, mockStateDescriptionProvider,
                mockChannelTypeProvider));
        mockCluster.rssi = -70;
        converter = new WiFiNetworkDiagnosticsConverter(mockCluster, mockHandler, 1, "TestLabel");
    }

    @Test
    void testCreateChannels() {
        ChannelGroupUID thingUID = new ChannelGroupUID("matter:node:test:12345:1");
        Map<Channel, StateDescription> channels = converter.createChannels(thingUID);
        assertEquals(1, channels.size());
        Channel channel = channels.keySet().iterator().next();
        assertEquals("matter:node:test:12345:1#wifinetworkdiagnostics-rssi", channel.getUID().toString());
        assertEquals("Number:Power", channel.getAcceptedItemType());
    }

    @Test
    void testOnEventWithRssi() {
        AttributeChangedMessage message = new AttributeChangedMessage();
        message.path = new Path();
        message.path.attributeName = "rssi";
        message.value = -70;
        converter.onEvent(message);
        verify(mockHandler, times(1)).updateState(eq(1), eq("wifinetworkdiagnostics-rssi"), eq(new DecimalType(-70)));
    }

    @Test
    void testOnEventWithNonNumberValue() {
        AttributeChangedMessage message = new AttributeChangedMessage();
        message.path = new Path();
        message.path.attributeName = "rssi";
        message.value = "invalid";
        converter.onEvent(message);
        // Should not call updateState for non-number values
        verify(mockHandler, times(0)).updateState(eq(1), eq("wifinetworkdiagnostics-rssi"), eq(new DecimalType(-70)));
    }

    @Test
    void testInitState() {
        mockCluster.rssi = -70;
        converter.initState();
        verify(mockHandler, times(1)).updateState(eq(1), eq("wifinetworkdiagnostics-rssi"), eq(new DecimalType(-70)));
    }

    @Test
    void testInitStateWithNullValue() {
        mockCluster.rssi = null;
        converter.initState();
        verify(mockHandler, times(1)).updateState(eq(1), eq("wifinetworkdiagnostics-rssi"), eq(UnDefType.NULL));
    }
}
