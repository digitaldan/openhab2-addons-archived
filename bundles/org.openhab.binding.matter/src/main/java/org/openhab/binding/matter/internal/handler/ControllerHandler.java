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

import static org.openhab.binding.matter.internal.MatterBindingConstants.THING_TYPE_NODE;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.matter.internal.actions.MatterControllerActions;
import org.openhab.binding.matter.internal.client.MatterClientListener;
import org.openhab.binding.matter.internal.client.MatterWebsocketService;
import org.openhab.binding.matter.internal.client.dto.Node;
import org.openhab.binding.matter.internal.client.dto.ws.AttributeChangedMessage;
import org.openhab.binding.matter.internal.client.dto.ws.BridgeEventMessage;
import org.openhab.binding.matter.internal.client.dto.ws.EventTriggeredMessage;
import org.openhab.binding.matter.internal.client.dto.ws.NodeInitializedMessage;
import org.openhab.binding.matter.internal.client.dto.ws.NodeStateMessage;
import org.openhab.binding.matter.internal.config.ControllerConfiguration;
import org.openhab.binding.matter.internal.controller.MatterControllerClient;
import org.openhab.binding.matter.internal.discovery.MatterDiscoveryHandler;
import org.openhab.binding.matter.internal.discovery.MatterDiscoveryService;
import org.openhab.core.OpenHAB;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;
import org.openhab.core.thing.ThingUID;
import org.openhab.core.thing.binding.BaseBridgeHandler;
import org.openhab.core.thing.binding.ThingHandler;
import org.openhab.core.thing.binding.ThingHandlerService;
import org.openhab.core.types.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link ControllerHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Dan Cunningham - Initial contribution
 */
@NonNullByDefault
public class ControllerHandler extends BaseBridgeHandler implements MatterClientListener, MatterDiscoveryHandler {

    private final Logger logger = LoggerFactory.getLogger(ControllerHandler.class);
    private final MatterWebsocketService websocketService;
    // Set of nodes we are waiting to connect to
    private Set<BigInteger> outstandingNodeRequests = Collections.synchronizedSet(new HashSet<>());
    // Set of nodes we need to try reconnecting to
    private Set<BigInteger> disconnectedNodes = Collections.synchronizedSet(new HashSet<>());
    // Nodes that we have linked to a handler
    private Map<BigInteger, NodeHandler> linkedNodes = Collections.synchronizedMap(new HashMap<>());
    private @Nullable MatterDiscoveryService discoveryService;
    private @Nullable ScheduledFuture<?> reconnectFuture;
    private MatterControllerClient client;
    private boolean ready = false;

    public ControllerHandler(Bridge bridge, MatterWebsocketService websocketService) {
        super(bridge);
        client = new MatterControllerClient();
        client.addListener(this);
        this.websocketService = websocketService;
    }

    @Override
    public Collection<Class<? extends ThingHandlerService>> getServices() {
        return Set.of(MatterDiscoveryService.class, MatterControllerActions.class);
    }

    @Override
    public void initialize() {
        logger.debug("initialize");
        connect();
    }

    @Override
    public void dispose() {
        logger.debug("dispose");
        ready = false;
        client.removeListener(this);
        cancelReconnect();
        outstandingNodeRequests.clear();
        disconnectedNodes.clear();
        linkedNodes.clear();
        client.disconnect();
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
    }

    @Override
    public void childHandlerInitialized(ThingHandler childHandler, Thing childThing) {
        super.childHandlerInitialized(childHandler, childThing);
        logger.debug("childHandlerInitialized ready {} {}", ready, childHandler);
        if (childHandler instanceof NodeHandler handler) {
            BigInteger nodeId = handler.getNodeId();
            linkedNodes.put(nodeId, handler);
            updateNode(nodeId);
        }
    }

    @Override
    public void childHandlerDisposed(ThingHandler childHandler, Thing childThing) {
        super.childHandlerDisposed(childHandler, childThing);
        logger.debug("childHandlerDisposed {}", childHandler);
        if (!ready) {
            return;
        }
        if (childHandler instanceof NodeHandler handler) {
            // todo support decommissioned removal
            removeNode(handler.getNodeId());
        }
    }

    @Override
    public void setDiscoveryService(@Nullable MatterDiscoveryService service) {
        logger.debug("setDiscoveryService");
        this.discoveryService = service;
    }

    public @Nullable MatterDiscoveryService getDiscoveryService() {
        return discoveryService;
    }

    @Override
    public CompletableFuture<Void> startScan(@Nullable String code) {
        if (code != null) {
            if (!client.isConnected()) {
                logger.debug("not connected");
                return CompletableFuture.completedFuture(null);
            }
            return client.pairNode(code);
        } else {
            return syncAllNodes();
        }
    }

    @Override
    public void onEvent(NodeStateMessage message) {
        logger.debug("Node onEvent: node {} is {}", message.nodeId, message.state);
        switch (message.state) {
            case CONNECTED:
                updateEndpointStatuses(message.nodeId, ThingStatus.UNKNOWN, ThingStatusDetail.NOT_YET_READY,
                        "Waiting for data");
                break;
            case STRUCTURECHANGED:
                updateNode(message.nodeId);
                break;
            case DECOMMISSIONED:
                updateEndpointStatuses(message.nodeId, ThingStatus.OFFLINE, ThingStatusDetail.GONE,
                        "Node " + message.state);
                removeNode(message.nodeId);
                break;
            case DISCONNECTED:
                if (linkedNodes.containsKey(message.nodeId)) {
                    disconnectedNodes.add(message.nodeId);
                }
                // fall through
            case RECONNECTING:
            case WAITINGFORDEVICEDISCOVERY:
                updateEndpointStatuses(message.nodeId, ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                        "Node " + message.state);
                break;
            default:
        }
    }

    @Override
    public void onEvent(AttributeChangedMessage message) {
        NodeHandler handler = linkedNodes.get(message.path.nodeId);
        if (handler == null) {
            logger.debug("No handler found for node {}", message.path.nodeId);
            return;
        }
        handler.onEvent(message);
    }

    @Override
    public void onEvent(EventTriggeredMessage message) {
        NodeHandler handler = linkedNodes.get(message.path.nodeId);
        if (handler == null) {
            logger.debug("No handler found for node {}", message.path.nodeId);
            return;
        }
        handler.onEvent(message);
    }

    @Override
    public void onEvent(NodeInitializedMessage message) {
        logger.debug("NodeInitializedMessage onEvent: node {} is {}", message.node.id, message.node);
        updateNode(message.node);
    }

    @Override
    public void onEvent(BridgeEventMessage message) {
    }

    @Override
    public void onConnect() {
        logger.debug("Websocket connected");
    }

    @Override
    public void onDisconnect(String reason) {
        logger.debug("websocket disconnected");
        setOffline(reason);
    }

    @Override
    public void onReady() {
        logger.debug("websocket ready");
        ready = true;
        updateStatus(ThingStatus.ONLINE);
        cancelReconnect();
        linkedNodes.keySet().forEach(nodeId -> updateNode(nodeId));
    }

    public MatterControllerClient getClient() {
        return client;
    }

    protected void removeNode(BigInteger nodeId) {
        try {
            logger.debug("removing node {}", nodeId);
            disconnectedNodes.remove(nodeId);
            outstandingNodeRequests.remove(nodeId);
            linkedNodes.remove(nodeId);
            client.disconnectNode(nodeId).get();
        } catch (Exception e) {
            logger.debug("Could not remove node {}", nodeId, e);
        }
    }

    protected CompletableFuture<Void> updateNode(BigInteger id) {
        logger.debug("updateNode BEGIN {}", id);

        // If we are already waiting to get this node, return a completed future
        synchronized (this) {
            // If we are already waiting to get this node, return a completed future
            if (!ready || outstandingNodeRequests.contains(id)) {
                return CompletableFuture.completedFuture(null);
            }
            outstandingNodeRequests.add(id);
        }

        return client.initializeNode(id).thenAccept((Void) -> {
            disconnectedNodes.remove(id);
            logger.debug("updateNode END {}", id);
        }).exceptionally(e -> {
            logger.debug("Could not update node {}", id, e);
            disconnectedNodes.add(id);
            String message = e.getMessage();
            updateEndpointStatuses(id, ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                    message != null ? message : "");
            return null;
        }).whenComplete((node, e) -> outstandingNodeRequests.remove(id));
    }

    /**
     * Update the endpoints (devices) for a node
     * 
     * @param node
     */
    private synchronized void updateNode(Node node) {
        NodeHandler handler = linkedNodes.get(node.id);
        disconnectedNodes.remove(node.id);
        if (handler != null) {
            handler.updateNode(node);
        } else {
            discoverChildNode(node);
        }
    }

    private void connect() {
        logger.debug("connect");
        if (client.isConnected()) {
            logger.debug("Client already connected");
            return;
        }
        String folderName = OpenHAB.getUserDataFolder() + File.separator + "matter";
        File folder = new File(folderName);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        String storagePath = folder.getAbsolutePath();
        String controllerName = "controller-" + getThing().getUID().getId();

        logger.debug("matter config: {}", storagePath);
        final ControllerConfiguration config = getConfigAs(ControllerConfiguration.class);
        client.connect(websocketService, new BigInteger(config.nodeId), controllerName, storagePath);
    }

    /**
     * Synchronize all nodes with the controller
     * 
     * @return
     */
    private CompletableFuture<Void> syncAllNodes() {
        logger.debug("refresh");
        if (!ready) {
            return CompletableFuture.completedFuture(null);
        }

        return client.getCommissionedNodeIds().thenCompose(nodeIds -> {
            List<CompletableFuture<Void>> futures = new ArrayList<>();

            for (BigInteger id : nodeIds) {
                CompletableFuture<Void> updateFuture = updateNode(id);
                futures.add(updateFuture);
            }

            // Return a Future that completes when all updateNode futures are complete
            return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        }).exceptionally(e -> {
            logger.debug("Error communicating with controller", e);
            return null;
        }).whenComplete((nodeIds, e) -> {
            logger.debug("refresh done");
        });
    }

    private synchronized void reconnect() {
        logger.debug("reconnect!");
        cancelReconnect();
        this.reconnectFuture = scheduler.schedule(this::connect, 30, TimeUnit.SECONDS);
    }

    private synchronized void cancelReconnect() {
        ScheduledFuture<?> reconnectFuture = this.reconnectFuture;
        if (reconnectFuture != null) {
            reconnectFuture.cancel(true);
        }
        this.reconnectFuture = null;
    }

    private void updateEndpointStatuses(BigInteger nodeId, ThingStatus status, ThingStatusDetail detail,
            String details) {
        for (Thing thing : getThing().getThings()) {
            ThingHandler handler = thing.getHandler();
            if (handler instanceof NodeHandler endpointHandler) {
                if (nodeId.equals(endpointHandler.getNodeId())) {
                    endpointHandler.setEndpointStatus(status, detail, details);
                }
            }
        }
    }

    private void setOffline(@Nullable String message) {
        logger.debug("setOffline {}", message);
        client.disconnect();
        updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, message);
        reconnect();
    }

    private void discoverChildNode(Node node) {
        logger.debug("discoverChildNode {}", node.id);

        MatterDiscoveryService discoveryService = this.discoveryService;
        if (discoveryService != null) {
            ThingUID bridgeUID = getThing().getUID();
            ThingUID thingUID = new ThingUID(THING_TYPE_NODE, bridgeUID, node.id.toString());
            discoveryService.discoverNodeDevice(thingUID, bridgeUID, node);
        }
    }
}
