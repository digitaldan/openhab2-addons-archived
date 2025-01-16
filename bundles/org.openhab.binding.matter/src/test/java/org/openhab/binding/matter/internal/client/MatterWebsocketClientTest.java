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
package org.openhab.binding.matter.internal.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openhab.binding.matter.internal.client.dto.cluster.gen.DescriptorCluster;
import org.openhab.binding.matter.internal.client.dto.ws.AttributeChangedMessage;
import org.openhab.binding.matter.internal.client.dto.ws.EventTriggeredMessage;
import org.openhab.binding.matter.internal.client.dto.ws.Message;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Test class for the MatterWebsocketClient class.
 * 
 * @author Dan Cunningham - Initial contribution
 */
class MatterWebsocketClientTest {

    private MatterWebsocketClient client;

    @BeforeEach
    void setUp() {
        client = new MatterWebsocketClient();
    }

    @Test
    void testDeserializeAttributeChangedMessage() {
        String json = "{ \"path\": { \"clusterId\": 1, \"attributeName\": \"testAttribute\" }, \"version\": 1, \"value\": \"testValue\" }";
        JsonElement jsonElement = JsonParser.parseString(json);
        AttributeChangedMessage message = client.getGson().fromJson(jsonElement, AttributeChangedMessage.class);

        assertNotNull(message);
        assertEquals("testAttribute", message.path.attributeName);
        assertEquals(1, message.version);
        assertEquals("testValue", message.value);
    }

    @Test
    void testDeserializeEventTriggeredMessage() {
        String json = "{ \"path\": { \"clusterId\": 1, \"eventName\": \"testEvent\" }, \"events\": [] }";
        JsonElement jsonElement = JsonParser.parseString(json);
        EventTriggeredMessage message = client.getGson().fromJson(jsonElement, EventTriggeredMessage.class);

        assertNotNull(message);
        assertEquals("testEvent", message.path.eventName);
        assertEquals(0, message.events.length);
    }

    @Test
    void testDeserializeGenericMessage() {
        String json = "{\"type\":\"response\",\"message\":{\"type\":\"resultSuccess\",\"id\":\"1\",\"result\":{}}}";
        JsonElement jsonElement = JsonParser.parseString(json);
        Message message = client.getGson().fromJson(jsonElement, Message.class);

        assertNotNull(message);
        assertEquals("response", message.type);
    }

    @Test
    void testDeserializeComplexCluster() {
        String json = "{ \"type\": \"response\", \"message\": { \"type\": \"resultSuccess\", \"id\": \"example-id\", \"result\": { \"id\": \"8507467286360628650\", \"rootEndpoint\": { \"number\": 0, \"clusters\": { \"Descriptor\": { \"id\": 29, \"name\": \"Descriptor\", \"deviceTypeList\": [{ \"deviceType\": 22, \"revision\": 1 }] } } } } } }";
        JsonElement jsonElement = JsonParser.parseString(json);
        JsonObject descriptorJson = jsonElement.getAsJsonObject().getAsJsonObject("message").getAsJsonObject("result")
                .getAsJsonObject("rootEndpoint").getAsJsonObject("clusters").getAsJsonObject("Descriptor");

        DescriptorCluster descriptorCluster = client.getGson().fromJson(descriptorJson, DescriptorCluster.class);

        assertNotNull(descriptorCluster);
        assertEquals(29, descriptorCluster.CLUSTER_ID);
        assertEquals("Descriptor", descriptorCluster.CLUSTER_NAME);
        assertNotNull(descriptorCluster.deviceTypeList);
        assertEquals(1, descriptorCluster.deviceTypeList.size());
        assertEquals(22, descriptorCluster.deviceTypeList.get(0).deviceType);
        assertEquals(1, descriptorCluster.deviceTypeList.get(0).revision);
    }
}
