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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

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
import org.openhab.core.library.items.ContactItem;
import org.openhab.core.library.items.SwitchItem;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.library.types.OpenClosedType;

/**
 * Test class for ContactSensorDevice
 * 
 * @author Dan Cunningham - Initial contribution
 */
class ContactSensorDeviceTest {

    @Mock
    private MetadataRegistry metadataRegistry;
    @Mock
    private MatterBridgeClient client;

    private ContactItem contactItem;
    private SwitchItem switchItem;
    private Metadata metadata;
    private ContactSensorDevice contactDevice;
    private ContactSensorDevice switchDevice;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        MetadataKey key = new MetadataKey("matter", "test");
        metadata = new Metadata(key, "test", Map.of());
        when(metadataRegistry.get(any(MetadataKey.class))).thenReturn(metadata);

        contactItem = Mockito.spy(new ContactItem("testContact"));
        switchItem = Mockito.spy(new SwitchItem("testSwitch"));
        contactDevice = new ContactSensorDevice(metadataRegistry, client, contactItem);
        switchDevice = new ContactSensorDevice(metadataRegistry, client, switchItem);
    }

    @Test
    void testDeviceType() {
        assertEquals("ContactSensor", contactDevice.deviceType());
    }

    @Test
    void testUpdateStateWithContact() {
        contactItem.setState(OpenClosedType.OPEN);
        contactDevice.updateState(contactItem, OpenClosedType.OPEN);
        verify(client).setEndpointState(any(), eq("booleanState"), eq("stateValue"), eq(false));

        contactItem.setState(OpenClosedType.CLOSED);
        contactDevice.updateState(contactItem, OpenClosedType.CLOSED);
        verify(client).setEndpointState(any(), eq("booleanState"), eq("stateValue"), eq(true));
    }

    @Test
    void testUpdateStateWithSwitch() {
        switchItem.setState(OnOffType.ON);
        switchDevice.updateState(switchItem, OnOffType.ON);
        verify(client).setEndpointState(any(), eq("booleanState"), eq("stateValue"), eq(false));

        switchItem.setState(OnOffType.OFF);
        switchDevice.updateState(switchItem, OnOffType.OFF);
        verify(client).setEndpointState(any(), eq("booleanState"), eq("stateValue"), eq(true));
    }

    @Test
    void testActivateWithContact() {
        contactItem.setState(OpenClosedType.OPEN);
        MatterDeviceOptions options = contactDevice.activate();

        Map<String, Object> booleanStateMap = options.clusters.get("booleanState");
        assertEquals(false, booleanStateMap.get("stateValue"));

        contactItem.setState(OpenClosedType.CLOSED);
        options = contactDevice.activate();
        booleanStateMap = options.clusters.get("booleanState");
        assertEquals(true, booleanStateMap.get("stateValue"));
    }

    @Test
    void testActivateWithSwitch() {
        switchItem.setState(OnOffType.ON);
        MatterDeviceOptions options = switchDevice.activate();

        Map<String, Object> booleanStateMap = options.clusters.get("booleanState");
        assertEquals(false, booleanStateMap.get("stateValue"));

        switchItem.setState(OnOffType.OFF);
        options = switchDevice.activate();
        booleanStateMap = options.clusters.get("booleanState");
        assertEquals(true, booleanStateMap.get("stateValue"));
    }
}
