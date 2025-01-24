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
package org.openhab.binding.matter.internal.bridge.devices;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.openhab.binding.matter.internal.bridge.MatterBridgeClient;
import org.openhab.binding.matter.internal.bridge.devices.GenericDevice.MatterDeviceOptions;
import org.openhab.core.items.Metadata;
import org.openhab.core.items.MetadataKey;
import org.openhab.core.items.MetadataRegistry;
import org.openhab.core.library.items.ColorItem;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.HSBType;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.library.types.PercentType;

/**
 * Test class for ColorDevice
 * 
 * @author Dan Cunningham - Initial contribution
 */
class ColorDeviceTest {

    @Mock
    private MetadataRegistry metadataRegistry;
    @Mock
    private MatterBridgeClient client;

    private ColorItem colorItem;
    private Metadata metadata;
    private ColorDevice device;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        MetadataKey key = new MetadataKey("matter", "test");
        metadata = new Metadata(key, "test", Map.of());
        when(metadataRegistry.get(any(MetadataKey.class))).thenReturn(metadata);

        colorItem = Mockito.spy(new ColorItem("test"));
        device = new ColorDevice(metadataRegistry, client, colorItem);
    }

    @Test
    void testDeviceType() {
        assertEquals("ColorLight", device.deviceType());
    }

    @Test
    void testHandleMatterEventOnOff() throws InterruptedException {
        device.handleMatterEvent("onOff", "onOff", true);
        Thread.sleep(600);
        verify(colorItem).send(OnOffType.ON);

        device.handleMatterEvent("onOff", "onOff", false);
        Thread.sleep(600);
        verify(colorItem).send(OnOffType.OFF);
    }

    @Test
    void testHandleMatterEventColor() throws InterruptedException {
        device.handleMatterEvent("colorControl", "currentHue", Double.valueOf(127));
        device.handleMatterEvent("colorControl", "currentSaturation", Double.valueOf(254));
        device.handleMatterEvent("levelControl", "currentLevel", Double.valueOf(254));

        Thread.sleep(600);
        verify(colorItem).send(any(HSBType.class));
    }

    @Test
    void testHandleMatterEventColorTemperature() throws InterruptedException {
        device.handleMatterEvent("colorControl", "colorTemperatureMireds", 200.0);

        Thread.sleep(600);
        verify(colorItem).send(any(HSBType.class));
    }

    @Test
    void testHandleMatterEventLevel() throws InterruptedException {
        device.handleMatterEvent("levelControl", "currentLevel", Double.valueOf(127));

        Thread.sleep(600); // Wait longer than the 500ms timer
        verify(colorItem).send(eq(new HSBType(new DecimalType(0), new PercentType(0), new PercentType(50))));
    }

    @Test
    void testHandleMatterEventLevelPreservesHueAndSaturation() throws InterruptedException {
        // Set initial color state (180 degrees hue, 50% saturation)
        device.handleMatterEvent("colorControl", "currentHue", Double.valueOf(127));
        device.handleMatterEvent("colorControl", "currentSaturation", Double.valueOf(127));
        Thread.sleep(600);
        verify(colorItem).send(eq(new HSBType(new DecimalType(180), new PercentType(50), new PercentType(0))));

        // Set initial brightness to 10%
        device.handleMatterEvent("levelControl", "currentLevel", Double.valueOf(25));
        Thread.sleep(600);
        verify(colorItem).send(eq(new HSBType(new DecimalType(180), new PercentType(50), new PercentType(10))));

        // Update brightness to 50% - should preserve hue and saturation
        device.handleMatterEvent("levelControl", "currentLevel", Double.valueOf(127));
        Thread.sleep(600);
        verify(colorItem).send(eq(new HSBType(new DecimalType(180), new PercentType(50), new PercentType(50))));
    }

    @Test
    void testHandleMatterEventWithDeviceOff() throws InterruptedException {
        device.handleMatterEvent("onOff", "onOff", false);
        Thread.sleep(600);
        verify(colorItem).send(OnOffType.OFF);

        device.handleMatterEvent("colorControl", "currentHue", Double.valueOf(127));
        device.handleMatterEvent("colorControl", "currentSaturation", Double.valueOf(254));
        device.handleMatterEvent("levelControl", "currentLevel", Double.valueOf(254));
        Thread.sleep(600);

        verify(colorItem, times(2)).send(OnOffType.OFF);
    }

    @Test
    void testUpdateStateWithHSB() {
        HSBType hsb = new HSBType(new DecimalType(0), new PercentType(100), new PercentType(100));
        device.updateState(colorItem, hsb);

        verify(client).setEndpointState(any(), eq("onOff"), eq("onOff"), eq(true));
        verify(client).setEndpointState(any(), eq("levelControl"), eq("currentLevel"), eq(254));
        verify(client).setEndpointState(any(), eq("colorControl"), eq("currentHue"), eq(0.0f));
        verify(client).setEndpointState(any(), eq("colorControl"), eq("currentSaturation"), eq(254.0f));

        hsb = new HSBType(new DecimalType(0), new PercentType(100), new PercentType(0));
        device.updateState(colorItem, hsb);

        verify(client).setEndpointState(any(), eq("onOff"), eq("onOff"), eq(false));
    }

    @Test
    void testUpdateStateWithPercent() {
        device.updateState(colorItem, new PercentType(100));
        verify(client).setEndpointState(any(), eq("onOff"), eq("onOff"), eq(true));
        verify(client).setEndpointState(any(), eq("levelControl"), eq("currentLevel"), eq(254));

        device.updateState(colorItem, PercentType.ZERO);
        verify(client).setEndpointState(any(), eq("onOff"), eq("onOff"), eq(false));
        verify(client, times(1)).setEndpointState(any(), eq("levelControl"), eq("currentLevel"), any());
    }

    @Test
    void testActivate() {
        HSBType hsb = new HSBType(new DecimalType(0), new PercentType(100), new PercentType(100));
        colorItem.setState(hsb);
        MatterDeviceOptions options = device.activate();

        Map<String, Object> levelMap = options.clusters.get("levelControl");
        Map<String, Object> colorMap = options.clusters.get("colorControl");
        Map<String, Object> onOffMap = options.clusters.get("onOff");

        assertEquals(254, levelMap.get("currentLevel"));
        assertEquals(0.0f, colorMap.get("currentHue"));
        assertEquals(254.0f, colorMap.get("currentSaturation"));
        assertEquals(true, onOffMap.get("onOff"));
    }

    @Test
    void testActivateWithOffState() {
        HSBType hsb = new HSBType(new DecimalType(0), new PercentType(100), new PercentType(0));
        colorItem.setState(hsb);
        MatterDeviceOptions options = device.activate();

        Map<String, Object> levelMap = options.clusters.get("levelControl");
        Map<String, Object> onOffMap = options.clusters.get("onOff");

        assertEquals(0, levelMap.get("currentLevel"));
        assertEquals(false, onOffMap.get("onOff"));
    }

    @AfterEach
    void tearDown() {
        device.dispose(); // Clean up the timer
    }
}
