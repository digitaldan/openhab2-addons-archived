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
package org.openhab.binding.matter.internal.handler;

import static org.openhab.binding.matter.internal.MatterBindingConstants.THING_TYPE_BRIDGE_ENDPOINT;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.matter.internal.MatterChannelTypeProvider;
import org.openhab.binding.matter.internal.MatterStateDescriptionOptionProvider;
import org.openhab.binding.matter.internal.actions.MatterNodeActions;
import org.openhab.binding.matter.internal.client.dto.Endpoint;
import org.openhab.binding.matter.internal.client.dto.Node;
import org.openhab.binding.matter.internal.client.dto.cluster.gen.BridgedDeviceBasicInformationCluster;
import org.openhab.binding.matter.internal.client.dto.ws.AttributeChangedMessage;
import org.openhab.binding.matter.internal.client.dto.ws.EventTriggeredMessage;
import org.openhab.binding.matter.internal.config.NodeConfiguration;
import org.openhab.binding.matter.internal.discovery.MatterDiscoveryService;
import org.openhab.binding.matter.internal.util.MatterUIDUtils;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingTypeUID;
import org.openhab.core.thing.ThingUID;
import org.openhab.core.thing.binding.BridgeHandler;
import org.openhab.core.thing.binding.ThingHandler;
import org.openhab.core.thing.binding.ThingHandlerService;
import org.openhab.core.thing.binding.builder.BridgeBuilder;
import org.openhab.core.thing.binding.builder.ThingBuilder;

/**
 * The {@link BridgeEndpointHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Dan Cunningham - Initial contribution
 */
@NonNullByDefault
public class NodeHandler extends MatterBaseThingHandler implements BridgeHandler {
    protected BigInteger nodeId = BigInteger.valueOf(0);
    private ConcurrentHashMap<Integer, BridgeEndpointHandler> bridgedEndpoints = new ConcurrentHashMap<>();

    public NodeHandler(Bridge bridge, MatterStateDescriptionOptionProvider stateDescriptionProvider,
            MatterChannelTypeProvider channelGroupTypeProvider) {
        super(bridge, stateDescriptionProvider, channelGroupTypeProvider);
    }

    @Override
    public void initialize() {
        NodeConfiguration config = getConfigAs(NodeConfiguration.class);
        nodeId = new BigInteger(config.nodeId);
        logger.debug("initialize endpoint {}", nodeId);
        super.initialize();
    }

    @Override
    public BigInteger getNodeId() {
        return nodeId;
    }

    @Override
    public ThingTypeUID getDynamicThingTypeUID() {
        return MatterUIDUtils.nodeThingTypeUID(getNodeId());
    }

    @Override
    public boolean isBridgeType() {
        return true;
    }

    @Override
    public Bridge getThing() {
        return (Bridge) super.getThing();
    }

    @Override
    protected ThingBuilder editThing() {
        return BridgeBuilder.create(getDynamicThingTypeUID(), getThing().getUID()).withBridge(getThing().getBridgeUID())
                .withChannels(getThing().getChannels()).withConfiguration(getThing().getConfiguration())
                .withLabel(getThing().getLabel()).withLocation(getThing().getLocation())
                .withProperties(getThing().getProperties());
    }

    @Override
    public Collection<Class<? extends ThingHandlerService>> getServices() {
        return Set.of(MatterNodeActions.class);
    }

    @Override
    protected boolean shouldAddEndpoint(Endpoint endpoint) {
        if (endpoint.clusters.containsKey(BridgedDeviceBasicInformationCluster.CLUSTER_NAME)) {
            updateBridgeEndpoint(endpoint);
            return false;
        }
        return true;
    }

    @Override
    public void handleRemoval() {
        ControllerHandler bridge = controllerHandler();
        if (bridge != null) {
            bridge.removeNode(nodeId);
        }
        super.handleRemoval();
    }

    @Override
    public void childHandlerInitialized(ThingHandler childHandler, Thing childThing) {
        logger.debug("childHandlerInitialized ready {}", childHandler);
        if (childHandler instanceof BridgeEndpointHandler handler) {
            bridgedEndpoints.put(handler.getEndpointId(), handler);
            ControllerHandler ch = controllerHandler();
            if (ch != null) {
                // todo we should change this to only request the endpoint
                ch.updateNode(getNodeId());
            }
        }
    }

    @Override
    public void childHandlerDisposed(ThingHandler childHandler, Thing childThing) {
        if (childHandler instanceof BridgeEndpointHandler handler) {
            bridgedEndpoints.entrySet().removeIf(entry -> entry.getValue().equals(handler));
        }
    }

    @Override
    public void onEvent(AttributeChangedMessage message) {
        if (bridgedEndpoints.get(message.path.endpointId) instanceof BridgeEndpointHandler endpointHandler) {
            endpointHandler.onEvent(message);
        } else {
            super.onEvent(message);
        }
    }

    @Override
    public void onEvent(EventTriggeredMessage message) {
        if (bridgedEndpoints.get(message.path.endpointId) instanceof BridgeEndpointHandler endpointHandler) {
            endpointHandler.onEvent(message);
        } else {
            super.onEvent(message);
        }
    }

    @Override
    protected synchronized void updateBaseEndpoint(Endpoint endpoint) {
        if (getThing().getStatus() != ThingStatus.ONLINE) {
            logger.debug("Setting Online {}", getNodeId());
            updateStatus(ThingStatus.ONLINE);
        }
        super.updateBaseEndpoint(endpoint);
    }

    public void updateNode(Node node) {
        updateRootProperties(node.rootEndpoint);
        updateBaseEndpoint(node.rootEndpoint);
    }

    private void updateBridgeEndpoint(Endpoint endpoint) {
        BridgeEndpointHandler handler = bridgedEndpoints.get(endpoint.number);
        if (handler != null) {
            updateBridgeEndpointMap(endpoint, handler);
            handler.updateBaseEndpoint(endpoint);
        } else {
            discoverChildBridge(endpoint);
        }
    }

    private void updateBridgeEndpointMap(Endpoint endpoint, final BridgeEndpointHandler handler) {
        bridgedEndpoints.put(endpoint.number, handler);
        endpoint.children.forEach(e -> updateBridgeEndpointMap(e, handler));
    }

    private void discoverChildBridge(Endpoint endpoint) {
        logger.debug("discoverChildBridge {}", endpoint.number);
        ControllerHandler controller = controllerHandler();
        if (controller != null) {
            MatterDiscoveryService discoveryService = controller.getDiscoveryService();
            if (discoveryService != null) {
                ThingUID bridgeUID = getThing().getUID();
                ThingUID thingUID = new ThingUID(THING_TYPE_BRIDGE_ENDPOINT, bridgeUID, endpoint.number.toString());
                discoveryService.discoverBridgeEndpoint(thingUID, bridgeUID, endpoint);
            }
        }
    }
}
