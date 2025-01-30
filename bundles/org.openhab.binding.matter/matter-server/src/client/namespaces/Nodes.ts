import { CommissioningControllerNodeOptions, PairedNode } from "@project-chip/matter.js/device";
import { EndpointInterface } from "@matter/protocol";
import { NodeCommissioningOptions } from "@project-chip/matter.js";
import { GeneralCommissioning, OperationalCredentialsCluster } from "@matter/main/clusters";
import { ManualPairingCodeCodec, QrPairingCodeCodec, QrCode, NodeId, FabricIndex, ClusterId } from "@matter/types";

import { Logger } from "@matter/main";
import { MatterNode } from "../MatterNode";
const logger = Logger.get("matter");

/**
 * Methods not marked as private are intended to be exposed to websocket clients
 */
export class Nodes {

    constructor(private theNode: MatterNode, private nodeListener: Partial<CommissioningControllerNodeOptions>) {
    }

    async listNodes() {
        if (this.theNode.commissioningController === undefined) {
            throw new Error("CommissioningController not initialized");
        }
        return this.theNode.getCommissionedNodes();
    }

    async getNode(nodeId: string | number) {
        const node = await this.theNode.getNode(nodeId, this.nodeListener);
        const data = await this.serializePairedNode(node);
        return data;
    }

    async serializePairedNode(node: PairedNode) {
        if (!this.theNode.commissioningController) {
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
    
    async pairNode(pairingCode: string | undefined, shortDiscriminator: number | undefined, setupPinCode: number | undefined) {
        let discriminator: number | undefined;
        let nodeIdStr: string | undefined;
        let ipPort: number | undefined;
        let ip: string | undefined;
        let instanceId: string | undefined;
        let ble = false

        if (typeof pairingCode === "string" && pairingCode.trim().length > 0) {
            if (pairingCode.indexOf('MT:') == 0) {
                const qrcode = QrPairingCodeCodec.decode(pairingCode)[0];
                setupPinCode = qrcode.passcode;
                discriminator = qrcode.discriminator;
            } else {
                const { shortDiscriminator: pairingCodeShortDiscriminator, passcode } =
                    ManualPairingCodeCodec.decode(pairingCode);
                shortDiscriminator = pairingCodeShortDiscriminator;
                setupPinCode = passcode;
                discriminator = undefined;
            }
        } else if (discriminator === undefined && shortDiscriminator === undefined) {
            discriminator = 3840;
        }

        const nodeId = nodeIdStr !== undefined ? NodeId(BigInt(nodeIdStr)) : undefined;
        if (this.theNode.commissioningController === undefined) {
            throw new Error("CommissioningController not initialized");
        }

        const options = {
            discovery: {
                knownAddress:
                    ip !== undefined && ipPort !== undefined
                        ? { ip, port: ipPort, type: "udp" }
                        : undefined,
                identifierData:
                    instanceId !== undefined
                        ? { instanceId }
                        : discriminator !== undefined
                            ? { longDiscriminator: discriminator }
                            : shortDiscriminator !== undefined
                                ? { shortDiscriminator }
                                : {},
                discoveryCapabilities: {
                    ble,
                    onIpNetwork: true,
                },
            },
            passcode: setupPinCode,
            ... this.nodeListener,
        } as NodeCommissioningOptions;

        options.commissioning = {
            nodeId: nodeId !== undefined ? NodeId(nodeId) : undefined,
            regulatoryLocation: GeneralCommissioning.RegulatoryLocationType.Outdoor, // Set to the most restrictive if relevant
            regulatoryCountryCode: "XX"
        };

        if (this.theNode.Store.has("WiFiSsid") && this.theNode.Store.has("WiFiPassword")) {
            options.commissioning.wifiNetwork = {
                wifiSsid: await this.theNode.Store.get<string>("WiFiSsid", ""),
                wifiCredentials: await this.theNode.Store.get<string>("WiFiPassword", ""),
            };
        }
        if (
            this.theNode.Store.has("ThreadName") &&
            this.theNode.Store.has("ThreadOperationalDataset")
        ) {
            options.commissioning.threadNetwork = {
                networkName: await this.theNode.Store.get<string>("ThreadName", ""),
                operationalDataset: await this.theNode.Store.get<string>(
                    "ThreadOperationalDataset",
                    "",
                ),
            };
        }

        const commissionedNodeId =
            await this.theNode.commissioningController.commissionNode(options);

        console.log("Commissioned Node:", commissionedNodeId);
        const node = await this.theNode.getNode(commissionedNodeId, this.nodeListener);
        return node.nodeId;
    }

    async disconnectNode(nodeId: number | string) {
        if (this.theNode.commissioningController === undefined) {
            console.log("Controller not initialized, nothing to disconnect.");
            return;
        }

        const node = await this.theNode.getNode(nodeId, this.nodeListener);
        if (node === undefined) {
            throw new Error(`Node ${nodeId} not found`);
        }
        await node.disconnect();
    }

    async reconnectNode(nodeId: number | string) {
        if (this.theNode.commissioningController === undefined) {
            console.log("Controller not initialized, nothing to disconnect.");
            return;
        }

        const node = await this.theNode.getNode(nodeId, this.nodeListener);
        if (node === undefined) {
            throw new Error(`Node ${nodeId} not found`);
        }
        node.triggerReconnect();
    }

    async getFabrics(nodeId: number | string) {
        if (this.theNode.commissioningController === undefined) {
            console.log("Controller not initialized, nothing to disconnect.");
            return;
        }

        const node = await this.theNode.getNode(nodeId, this.nodeListener);
        if (node === undefined) {
            throw new Error(`Node ${nodeId} not found`);
        }
        const operationalCredentialsCluster = node.getRootClusterClient(OperationalCredentialsCluster);

        if (operationalCredentialsCluster === undefined) {
            throw new Error(`OperationalCredentialsCluster for node ${nodeId} not found.`);
        }

        return await operationalCredentialsCluster.getFabricsAttribute(true, false);
    }

    async removeFabric(nodeId: number | string, index: number) {
        if (this.theNode.commissioningController === undefined) {
            console.log("Controller not initialized, nothing to disconnect.");
            return;
        }

        const node = await this.theNode.getNode(nodeId, this.nodeListener);
        if (node === undefined) {
            throw new Error(`Node ${nodeId} not found`);
        }
        const operationalCredentialsCluster = node.getRootClusterClient(OperationalCredentialsCluster);

        if (operationalCredentialsCluster === undefined) {
            throw new Error(`OperationalCredentialsCluster for node ${nodeId} not found.`);
        }


        const fabricInstance = FabricIndex(index);
        const ourFabricIndex = await operationalCredentialsCluster.getCurrentFabricIndexAttribute(true);

        if (ourFabricIndex == fabricInstance) {
            throw new Error("Will not delete our own fabric");
        }

        await operationalCredentialsCluster.commands.removeFabric({ fabricIndex: fabricInstance });
    }

    async removeNode(nodeId: number | string) {
        await this.theNode.commissioningController?.removeNode(NodeId(BigInt(nodeId)), true);
    }

    sessionInformation() {
        return this.theNode.commissioningController?.getActiveSessionInformation() || {}
    }

    async basicCommissioningWindow(nodeId: number | string, timeout = 900) {

        const node = await this.theNode.getNode(nodeId);

        await node.openBasicCommissioningWindow(timeout);

        console.log(`Basic Commissioning Window for node ${nodeId} opened`);
    }

    async enhancedCommissioningWindow(nodeId: number | string, timeout = 900) {
        const node = await this.theNode.getNode(nodeId);
        const data = await node.openEnhancedCommissioningWindow(timeout);

        console.log(`Enhanced Commissioning Window for node ${nodeId} opened`);
        const { qrPairingCode, manualPairingCode } = data;

        console.log(QrCode.get(qrPairingCode));
        console.log(
            `QR Code URL: https://project-chip.github.io/connectedhomeip/qrcode.html?data=${qrPairingCode}`,
        );
        console.log(`Manual pairing code: ${manualPairingCode}`);
        return data;
    }

    async logNode(nodeId: number | string) {
        const node = await this.theNode.getNode(nodeId);
        console.log("Logging structure of Node ", node.nodeId.toString());
        node.logStructure({});
    }
}