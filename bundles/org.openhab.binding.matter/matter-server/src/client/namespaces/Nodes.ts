import { NodeCommissioningOptions } from "@project-chip/matter.js";
import { GeneralCommissioning, OperationalCredentialsCluster } from "@matter/main/clusters";
import { ManualPairingCodeCodec, QrPairingCodeCodec, QrCode, NodeId, FabricIndex } from "@matter/types";

import { Logger } from "@matter/main";
import { ControllerNode } from "../ControllerNode";
const logger = Logger.get("matter");

/**
 * Methods not marked as private are intended to be exposed to websocket clients
 */
export class Nodes {

    constructor(private controllerNode: ControllerNode) {
    }

    async listNodes() {
        if (this.controllerNode.commissioningController === undefined) {
            throw new Error("CommissioningController not initialized");
        }
        return this.controllerNode.getCommissionedNodes();
    }
    
    async initializeNode(nodeId: string | number) {
        return this.controllerNode.initializeNode(nodeId);
    }
    
    async refreshNode(nodeId: string | number) {
         const node = await this.controllerNode.getNode(nodeId);
         if (node.initialized) {
            return this.controllerNode.sendSerializedNode(node);
        } else {
            throw new Error(`Node ${nodeId} not initialized`);
        }
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
        if (this.controllerNode.commissioningController === undefined) {
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
            passcode: setupPinCode
        } as NodeCommissioningOptions;

        options.commissioning = {
            nodeId: nodeId !== undefined ? NodeId(nodeId) : undefined,
            regulatoryLocation: GeneralCommissioning.RegulatoryLocationType.Outdoor, // Set to the most restrictive if relevant
            regulatoryCountryCode: "XX"
        };

        if (this.controllerNode.Store.has("WiFiSsid") && this.controllerNode.Store.has("WiFiPassword")) {
            options.commissioning.wifiNetwork = {
                wifiSsid: await this.controllerNode.Store.get<string>("WiFiSsid", ""),
                wifiCredentials: await this.controllerNode.Store.get<string>("WiFiPassword", ""),
            };
        }
        if (
            this.controllerNode.Store.has("ThreadName") &&
            this.controllerNode.Store.has("ThreadOperationalDataset")
        ) {
            options.commissioning.threadNetwork = {
                networkName: await this.controllerNode.Store.get<string>("ThreadName", ""),
                operationalDataset: await this.controllerNode.Store.get<string>(
                    "ThreadOperationalDataset",
                    "",
                ),
            };
        }

        const commissionedNodeId =
            await this.controllerNode.commissioningController.commissionNode(options);

        console.log("Commissioned Node:", commissionedNodeId);
        const node = await this.controllerNode.getNode(commissionedNodeId);
        return node.nodeId;
    }

    async disconnectNode(nodeId: number | string) {
        if (this.controllerNode.commissioningController === undefined) {
            console.log("Controller not initialized, nothing to disconnect.");
            return;
        }

        const node = await this.controllerNode.getNode(nodeId);
        if (node === undefined) {
            throw new Error(`Node ${nodeId} not found`);
        }
        await node.disconnect();
    }

    async reconnectNode(nodeId: number | string) {
        if (this.controllerNode.commissioningController === undefined) {
            console.log("Controller not initialized, nothing to disconnect.");
            return;
        }

        const node = await this.controllerNode.getNode(nodeId);
        if (node === undefined) {
            throw new Error(`Node ${nodeId} not found`);
        }
        node.triggerReconnect();
    }

    async getFabrics(nodeId: number | string) {
        if (this.controllerNode.commissioningController === undefined) {
            console.log("Controller not initialized, nothing to disconnect.");
            return;
        }

        const node = await this.controllerNode.getNode(nodeId);
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
        if (this.controllerNode.commissioningController === undefined) {
            console.log("Controller not initialized, nothing to disconnect.");
            return;
        }

        const node = await this.controllerNode.getNode(nodeId);
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
        await this.controllerNode.commissioningController?.removeNode(NodeId(BigInt(nodeId)), true);
    }

    sessionInformation() {
        return this.controllerNode.commissioningController?.getActiveSessionInformation() || {}
    }

    async basicCommissioningWindow(nodeId: number | string, timeout = 900) {

        const node = await this.controllerNode.getNode(nodeId);

        await node.openBasicCommissioningWindow(timeout);

        console.log(`Basic Commissioning Window for node ${nodeId} opened`);
    }

    async enhancedCommissioningWindow(nodeId: number | string, timeout = 900) {
        const node = await this.controllerNode.getNode(nodeId);
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
        const node = await this.controllerNode.getNode(nodeId);
        console.log("Logging structure of Node ", node.nodeId.toString());
        node.logStructure({});
    }
}