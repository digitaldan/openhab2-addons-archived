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
import static org.mockito.Mockito.never;
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
import org.openhab.core.items.GroupItem;
import org.openhab.core.items.Metadata;
import org.openhab.core.items.MetadataKey;
import org.openhab.core.items.MetadataRegistry;
import org.openhab.core.library.items.DimmerItem;
import org.openhab.core.library.items.SwitchItem;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.library.types.PercentType;

/**
 * Test class for DimmableLightDevice
 * 
 * @author Dan Cunningham - Initial contribution
 */
class DimmableLightDeviceTest {

    @Mock
    private MetadataRegistry metadataRegistry;
    @Mock
    private MatterBridgeClient client;

    private DimmerItem dimmerItem;
    private SwitchItem switchItem;
    private GroupItem groupItem;
    private Metadata metadata;
    private DimmableLightDevice dimmerDevice;
    private DimmableLightDevice switchDevice;
    private DimmableLightDevice groupDevice;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        MetadataKey key = new MetadataKey("matter", "test");
        metadata = new Metadata(key, "test", Map.of());
        when(metadataRegistry.get(any(MetadataKey.class))).thenReturn(metadata);

        dimmerItem = Mockito.spy(new DimmerItem("testDimmer"));
        switchItem = Mockito.spy(new SwitchItem("testSwitch"));
        groupItem = Mockito.spy(new GroupItem("testGroup", dimmerItem));

        dimmerDevice = new DimmableLightDevice(metadataRegistry, client, dimmerItem);
        switchDevice = new DimmableLightDevice(metadataRegistry, client, switchItem);
        groupDevice = new DimmableLightDevice(metadataRegistry, client, groupItem);
    }

    @AfterEach
    void tearDown() {
        dimmerDevice.dispose();
        switchDevice.dispose();
        groupDevice.dispose();
    }

    @Test
    void testDeviceType() {
        assertEquals("DimmableLight", dimmerDevice.deviceType());
    }

    @Test
    void testHandleMatterEventOnOff() throws InterruptedException {
        dimmerDevice.handleMatterEvent("onOff", "onOff", true);
        Thread.sleep(600);
        verify(dimmerItem).send(new PercentType(100));

        dimmerDevice.handleMatterEvent("onOff", "onOff", false);
        Thread.sleep(600);
        verify(dimmerItem).send(OnOffType.OFF);
    }

    @Test
    void testHandleMatterEventOnOffGroup() throws InterruptedException {
        groupDevice.handleMatterEvent("onOff", "onOff", true);
        Thread.sleep(600);
        verify(groupItem).send(new PercentType(100));

        groupDevice.handleMatterEvent("onOff", "onOff", false);
        Thread.sleep(600);
        verify(groupItem).send(OnOffType.OFF);
    }

    @Test
    void testHandleMatterEventLevel() throws InterruptedException {
        dimmerDevice.handleMatterEvent("onOff", "onOff", true);
        dimmerDevice.handleMatterEvent("levelControl", "currentLevel", Double.valueOf(254));
        Thread.sleep(600);
        verify(dimmerItem).send(new PercentType(100));

        dimmerDevice.handleMatterEvent("levelControl", "currentLevel", Double.valueOf(127));
        Thread.sleep(600);
        verify(dimmerItem).send(new PercentType(50));
    }

    @Test
    void testHandleMatterEventLevelWhileOff() throws InterruptedException {
        dimmerDevice.handleMatterEvent("onOff", "onOff", false);
        dimmerDevice.handleMatterEvent("levelControl", "currentLevel", Double.valueOf(254));
        Thread.sleep(600);
        verify(dimmerItem).send(OnOffType.OFF);
    }

    @Test
    void testHandleMatterEventLevelGroup() throws InterruptedException {
        groupDevice.handleMatterEvent("onOff", "onOff", true);
        groupDevice.handleMatterEvent("levelControl", "currentLevel", Double.valueOf(254));
        Thread.sleep(600);
        verify(groupItem).send(new PercentType(100));

        groupDevice.handleMatterEvent("levelControl", "currentLevel", Double.valueOf(127));
        Thread.sleep(600);
        verify(groupItem).send(new PercentType(50));
    }

    @Test
    void testUpdateStateWithPercent() {
        dimmerDevice.updateState(dimmerItem, new PercentType(100));
        verify(client).setEndpointState(any(), eq("onOff"), eq("onOff"), eq(true));
        verify(client).setEndpointState(any(), eq("levelControl"), eq("currentLevel"), eq(254));

        dimmerDevice.updateState(dimmerItem, PercentType.ZERO);
        verify(client).setEndpointState(any(), eq("onOff"), eq("onOff"), eq(false));
    }

    @Test
    void testActivate() {
        dimmerItem.setState(new PercentType(100));
        MatterDeviceOptions options = dimmerDevice.activate();

        Map<String, Object> levelMap = options.clusters.get("levelControl");
        Map<String, Object> onOffMap = options.clusters.get("onOff");

        assertEquals(100, levelMap.get("currentLevel"));
        assertEquals(true, onOffMap.get("onOff"));
    }

    @Test
    void testActivateWithOffState() {
        dimmerItem.setState(PercentType.ZERO);
        MatterDeviceOptions options = dimmerDevice.activate();

        Map<String, Object> levelMap = options.clusters.get("levelControl");
        Map<String, Object> onOffMap = options.clusters.get("onOff");

        assertEquals(0, levelMap.get("currentLevel"));
        assertEquals(false, onOffMap.get("onOff"));
    }

    @Test
    void testHandleMatterEventOnOffWithTimer() throws InterruptedException {
        dimmerDevice.handleMatterEvent("onOff", "onOff", true);
        dimmerDevice.handleMatterEvent("levelControl", "currentLevel", Double.valueOf(254));
        
        // Verify that no immediate update is sent
        verify(dimmerItem, never()).send(any(OnOffType.class));
        verify(dimmerItem, never()).send(any(PercentType.class));
        
        Thread.sleep(600);
        verify(dimmerItem).send(new PercentType(100));
    }

    @Test
    void testHandleMatterEventLevelWithTimer() throws InterruptedException {
        dimmerDevice.handleMatterEvent("onOff", "onOff", true);
        dimmerDevice.handleMatterEvent("levelControl", "currentLevel", Double.valueOf(127));
        
        // Verify that no immediate update is sent
        verify(dimmerItem, never()).send(any(PercentType.class));
        
        // Wait for the timer to complete and verify the update
        Thread.sleep(600); // Sleep slightly longer than the 500ms timer
        verify(dimmerItem).send(new PercentType(50));
    }

    @Test
    void testHandleMatterEventMultipleUpdatesWithTimer() throws InterruptedException {
        // Send multiple updates in quick succession
        dimmerDevice.handleMatterEvent("onOff", "onOff", true);
        dimmerDevice.handleMatterEvent("levelControl", "currentLevel", Double.valueOf(127));
        dimmerDevice.handleMatterEvent("levelControl", "currentLevel", Double.valueOf(254));
        
        // Verify that only the final state is sent after the timer
        Thread.sleep(600); // Sleep slightly longer than the 500ms timer
        verify(dimmerItem).send(new PercentType(100));
        verify(dimmerItem, times(1)).send(any(PercentType.class));
    }

    @Test
    void testHandleMatterEventOffStateWithTimer() throws InterruptedException {
        // Test transition to off state
        dimmerDevice.handleMatterEvent("onOff", "onOff", false);
        dimmerDevice.handleMatterEvent("levelControl", "currentLevel", Double.valueOf(0));
        
        Thread.sleep(600); // Sleep slightly longer than the 500ms timer
        verify(dimmerItem).send(OnOffType.OFF);
    }

    @Test
    void testHandleMatterEventGroupItemWithTimer() throws InterruptedException {
        groupDevice.handleMatterEvent("onOff", "onOff", true);
        groupDevice.handleMatterEvent("levelControl", "currentLevel", Double.valueOf(127));
        
        Thread.sleep(600); // Sleep slightly longer than the 500ms timer
        verify(groupItem).send(new PercentType(50));
    }
}
