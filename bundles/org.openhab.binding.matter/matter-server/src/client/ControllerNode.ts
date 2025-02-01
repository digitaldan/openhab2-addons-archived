// Include this first to auto-register Crypto, Network and Time Node.js implementations
import { CommissioningController, MatterServer } from "@project-chip/matter.js";
import { NodeId } from "@matter/types";
import { PairedNode, CommissioningControllerNodeOptions, NodeStates } from "@project-chip/matter.js/device";
import { EndpointInterface } from "@project-chip/matter.js/endpoint";
import { Environment, Logger, StorageContext } from "@matter/general";
import { ControllerStore } from "@matter/node";
import { WebSocketSession } from "../app";
import { EventType, NodeState } from '../MessageTypes';

const logger = Logger.get("ControllerNode");

/**
 * This class contains represents the Matter Controller / Admin client
 */
export class ControllerNode {

    private environment: Environment = Environment.default;
    private storageContext?: StorageContext;
    private matterServer?: MatterServer;
    private nodes: Map<NodeId, PairedNode> = new Map();
    commissioningController?: CommissioningController;

    constructor(
        private readonly storageLocation: string,
        private readonly controllerName: string,
        private readonly nodeNum: number,
        private readonly ws: WebSocketSession,
        private readonly netInterface?: string
    ) { }

    get Store() {
        if (!this.storageContext) {
            throw new Error("Storage uninitialized");
        }
        return this.storageContext;
    }

    /**
     * Closes the controller node
     */
    async close() {
        await this.matterServer?.close();
        this.nodes.clear();
    }

    /**
     * Initializes the controller node
     */
    async initialize() {
        const outputDir = this.storageLocation;
        const id = `${this.controllerName}-${this.nodeNum.toString()}`
        const fabricLabel = `openHAB: ${this.controllerName}`;
        
        logger.info(`Storage location: ${outputDir} (Directory)`);
        this.environment.vars.set('storage.path', outputDir)
    
        //TODO we may need to choose which network interface to use
        if (this.netInterface !== undefined) {
            this.environment.vars.set("mdns.networkinterface", this.netInterface);
        }
        this.commissioningController = new CommissioningController({
            environment: {
                environment: this.environment,
                id,
            },
            autoConnect: false,
            adminFabricLabel: fabricLabel,
        });
        await this.commissioningController.initializeControllerStore();

        const controllerStore = this.environment.get(ControllerStore);        
        // TODO: Implement resetStorage
        // if (resetStorage) {
        //     await controllerStore.erase();
        // }
        this.storageContext = controllerStore.storage.createContext("Node");

        if (await this.Store.has("ControllerFabricLabel")) {
            await this.commissioningController.updateFabricLabel(
                await this.Store.get<string>("ControllerFabricLabel",fabricLabel),
            );
        }

        if (this.matterServer !== undefined) {
            await this.matterServer.start();
        } else if (this.commissioningController !== undefined) {
            await this.commissioningController.start();
        } else {
            throw new Error("No controller initialized");
        }
    }

    /**
     * Connects to a node, setting up event listeners.  If called multiple times for the same node, it will trigger a node reconnect.
     * @param nodeId 
     * @returns 
     */
    async initializeNode(nodeId: string | number) {
        if (this.commissioningController === undefined) {
            throw new Error("CommissioningController not initialized");
        }

        let node = this.nodes.get(NodeId(BigInt(nodeId)));
        if (node !== undefined) {
            node.triggerReconnect();
            return;
        }

        node = await this.commissioningController.connectNode(NodeId(BigInt(nodeId)));
        this.nodes.set(node.nodeId, node);
        node.events.attributeChanged.on((data) => {
            data.path.nodeId = node.nodeId;
            this.ws.sendEvent(EventType.AttributeChanged, data);
        });

        node.events.eventTriggered.on((data) => {
            data.path.nodeId = node.nodeId;
            this.ws.sendEvent(EventType.EventTriggered, data);
        });
        
        node.events.stateChanged.on(info => {
            const data: any = {
                nodeId: node.nodeId,
                state: NodeStates[info]
            };
            this.ws.sendEvent(EventType.NodeStateInformation, data)
        });

        node.events.structureChanged.on(() => {
            const data: any = {
                nodeId: node.nodeId,
                state: NodeState.STRUCTURE_CHANGED
            };
            this.ws.sendEvent(EventType.NodeStateInformation, data)
        });

        node.events.decommissioned.on(() => {
            this.nodes.delete(node.nodeId);
            const data: any = {
                nodeId: node.nodeId,
                state: NodeState.DECOMMISSIONED
            };
            this.ws.sendEvent(EventType.NodeStateInformation, data)
        });

        node.events.initializedFromRemote.on(() => {
            this.sendSerializedNode(node);
        });
    }

    /**
     * Returns a node by nodeId.  If the node has not been initialized, it will throw an error.
     * @param nodeId 
     * @returns 
     */
    getNode(nodeId: number | string | NodeId) {
        if (this.commissioningController === undefined) {
            throw new Error("CommissioningController not initialized");
        }
        //const node = await this.commissioningController.connectNode(NodeId(BigInt(nodeId)))
        const node = this.nodes.get(NodeId(BigInt(nodeId)));
        if (node === undefined) {
            throw new Error(`Node ${nodeId} not connected`);
        }
        return node;
    }

    /**
     * Returns all commissioned nodes Ids
     * @returns 
     */
    async getCommissionedNodes() {
        return this.commissioningController?.getCommissionedNodes();
    }

    /**
     * Finds the given endpoint, included nested endpoints
     * @param node 
     * @param endpointId 
     * @returns 
     */
    getEndpoint(node: PairedNode, endpointId: number) {
        const endpoints = node.getDevices();
        for (const e of endpoints) {
            const endpoint = this.findEndpoint(e, endpointId);
            if (endpoint != undefined) {
                return endpoint;
            }
        }
        return undefined;
    }

    /**
     * 
     * @param root Endpoints can have child endpoints. This function recursively searches for the endpoint with the given id.
     * @param endpointId 
     * @returns 
     */
    private findEndpoint(root: EndpointInterface, endpointId: number): EndpointInterface | undefined {
        if (root.number === endpointId) {
            return root;
        }
        for (const endpoint of root.getChildEndpoints()) {
            const found = this.findEndpoint(endpoint, endpointId);
            if (found !== undefined) {
                return found;
            }
        }
        return undefined;
    }

    /**
     * Serializes a node and sends it to the web socket
     * @param node 
     */
    sendSerializedNode(node: PairedNode) {
        this.serializePairedNode(node).then(data => {
            this.ws.sendEvent(EventType.NodeInitialized, data);
        }).catch(error => {
            logger.error(`Error serializing node: ${error}`);
            this.printError(error, "serializePairedNode");
            node.triggerReconnect();
        });
    }

    /**
     * Serializes a node and returns the json string   
     * @param node 
     * @returns 
     */
    async serializePairedNode(node: PairedNode) {
        if (!this.commissioningController) {
            throw new Error("CommissioningController not initialized");
        }
    
        // Recursive function to build the hierarchy
        async function serializeEndpoint(endpoint: EndpointInterface): Promise<any> {
            const endpointData: any = {
                number: endpoint.number,
                clusters: {},
                children: []
            };
    
            // Serialize clusters
            for (const cluster of endpoint.getAllClusterClients()) {
                if (!cluster.id) continue;
                
                const clusterData: any = {
                    id: cluster.id,
                    name: cluster.name
                };
    
                // Serialize attributes
                for (const attributeName in cluster.attributes) {
                    const attribute = cluster.attributes[attributeName];
                    if (!attribute) continue;
                    const attributeValue = await attribute.get();
                    if (attributeValue !== undefined) {
                        clusterData[attributeName] = attributeValue;
                    }
                }
    
                endpointData.clusters[cluster.name] = clusterData;
            }
    
            // Serialize child endpoints recursively
            for (const child of endpoint.getChildEndpoints()) {
                endpointData.children.push(await serializeEndpoint(child));
            }
    
            return endpointData;
        }
    
        // Start serialization from the root endpoint
        const rootEndpoint = node.getRootEndpoint() as EndpointInterface;
        const data: any = {
            id: node.nodeId,
            rootEndpoint: await serializeEndpoint(rootEndpoint)
        };
    
        return data;
    }

    printError(error: Error, functionName: String) {

        logger.error(`Error executing function ${functionName}: ${error.message}`);
        logger.error(`Stack trace: ${error.stack}`);

        // Log additional error properties if available
        if ('code' in error) {
            logger.error(`Error code: ${(error as any).code}`);
        }
        if ('name' in error) {
            logger.error(`Error name: ${(error as any).name}`);
        }

        // Fallback: log the entire error object in case there are other useful details
        logger.error(`Full error object: ${JSON.stringify(error, Object.getOwnPropertyNames(error))}`)
        logger.error(error)
    }
}
