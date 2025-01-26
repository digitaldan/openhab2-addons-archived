import { Endpoint } from "@matter/main";
import { BridgeController } from "../BridgeController";
import { EventType, BridgeEvent, BridgeEventType } from '../../MessageTypes';
import { OnOffServer } from '@matter/node/behaviors/on-off';
import { BridgedDeviceBasicInformationServer } from "@matter/node/behaviors/bridged-device-basic-information";
import { FixedLabelServer , LevelControlServer} from "@matter/main/behaviors";
import { Logger } from "@matter/main";

const logger = Logger.get("GenericDevice");

export abstract class GenericDeviceType {

    protected updateLocks = new Set<string>();
    endpoint: Endpoint;

    constructor(protected bridgeController: BridgeController, protected attributeMap: Record<string, any>, protected endpointId: string, protected nodeLabel: string, protected productName: string, protected productLabel: string, protected serialNumber: string) {
        this.nodeLabel = this.#truncateString(nodeLabel);
        this.productLabel = this.#truncateString(productLabel);
        this.productName = this.#truncateString(productName);
        this.serialNumber = this.#truncateString(serialNumber);
        const attributes = this.#mergeWithDefaults(this.defaultClusterValues(), attributeMap);
        this.endpoint = this.createEndpoint(this.#generateAttributes(this.defaultClusterValues(), attributeMap));
    }

    abstract defaultClusterValues(): Record<string, any>;
    abstract createEndpoint(clusterValues: Record<string, any>): Endpoint;

    public async updateState(clusterName: string, attributeName: string, attributeValue: any) {
        const args = {} as { [key: string]: any }
        args[clusterName] = {} as { [key: string]: any }
        args[clusterName][attributeName] = attributeValue
        await this.endpoint.set(args);
    }

    protected sendBridgeEvent(clusterName: string, attributeName: string, attributeValue: any) {
        const be: BridgeEvent = {
            type: BridgeEventType.AttributeChanged,
            data: {
                endpointId: this.endpoint.id,
                clusterName: clusterName,
                attributeName: attributeName,
                data: attributeValue,
            }
        }
        this.sendEvent(EventType.BridgeEvent, be)
    }

    protected sendEvent(eventName: string, data: any) {
        logger.debug(`Sending event: ${eventName} with data: ${data}`);
        this.bridgeController.ws.sendEvent(eventName, data)
    }

    protected endPointDefaults() {
        return {
            id: this.endpointId,
            bridgedDeviceBasicInformation: {
                nodeLabel: this.nodeLabel,
                productName: this.productName,
                productLabel: this.productLabel,
                serialNumber: this.serialNumber,
                reachable: true,
            }
        }
    }
    
    //note that these overrides assume openHAB will be sending the state back when changed as we will not set it here prematurely  
    //other wise we would want to call super.on() and so on (same for level control or any other cluster behavior)to set local state
    protected createOnOffServer(setLocally: boolean = false): typeof OnOffServer {
        const parent = this;
        return class extends OnOffServer {
            override async on() {
                await parent.sendBridgeEvent("onOff", "onOff", true);
                if(setLocally){
                    await super.on();
                }
            }
            override async off() {
                await parent.sendBridgeEvent("onOff", "onOff", false);
                if(setLocally){
                    await super.off();
                }
            }
        };
    }

    protected createLevelControlServer(): typeof LevelControlServer {
        const parent = this;
        return class extends LevelControlServer {
            override async moveToLevelLogic(level: number, transitionTime: number | null, withOnOff: boolean){
                await parent.sendBridgeEvent("levelControl", "currentLevel", level);
            }
        };
    }

    protected defaultClusterServers() {
        return [
            BridgedDeviceBasicInformationServer,
            FixedLabelServer
        ]
    }

    #truncateString(str: string, maxLength: number = 32): string {
        return str.slice(0, maxLength);
    }

    #generateAttributes<T extends Record<string, any>, U extends Partial<T>>(defaults: T, overrides: U): T {
        const alwaysAdd = ["fixedLabel"]
        let entries = this.#mergeWithDefaults(defaults, overrides);
        // Ensure entries include the values from overrides for the keys in alwaysAdd
        alwaysAdd.forEach((key) => {
            if (key in overrides) {
                entries[key as keyof T] = overrides[key as keyof T]!;
            }
        });
        return entries;
    }


    #mergeWithDefaults<T extends Record<string, any>, U extends Partial<T>>(defaults: T, overrides: U): T {
        function isPlainObject(value: any): value is Record<string, any> {
            return value && typeof value === 'object' && !Array.isArray(value);
        }
        return Object.keys(defaults).reduce((result, key) => {
            const defaultValue = defaults[key];
            const overrideValue = overrides[key];

            // If both defaultValue and overrideValue are objects, merge them recursively
            if (
                isPlainObject(defaultValue) &&
                isPlainObject(overrideValue)
            ) {
                result[key] = this.#mergeWithDefaults(defaultValue, overrideValue);
            } else {
                // Otherwise, use the override value if it exists, else the default value
                result[key] = key in overrides ? overrideValue : defaultValue;
            }

            return result;
        }, {} as Record<string, any>) as T;
    }

}