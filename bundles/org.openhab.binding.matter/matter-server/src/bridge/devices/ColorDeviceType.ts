import { Endpoint } from "@matter/node";
import { ExtendedColorLightDevice } from "@matter/node/devices/extended-color-light";
import { BridgedDeviceBasicInformationServer } from "@matter/node/behaviors/bridged-device-basic-information";
import { GenericDeviceType } from './GenericDeviceType'; // Adjust the path as needed
import { ColorControlServer } from "@matter/main/behaviors";
import { ColorControl, LevelControl, OnOff } from "@matter/main/clusters";

export class ColorDeviceType extends GenericDeviceType {

    override createEndpoint(clusterValues: Record<string, any>) {
        const endpoint = new Endpoint(ExtendedColorLightDevice.with(
            //setLocally=true for createOnOffServer otherwise moveToHueAndSaturationLogic will not be called b/c matter.js thinks the device is OFF.
            this.createOnOffServer(true).with(OnOff.Feature.Lighting),
            this.createLevelControlServer().with(LevelControl.Feature.Lighting),
            this.createColorControlServer().with(ColorControl.Feature.HueSaturation, ColorControl.Feature.ColorTemperature),
            ...this.defaultClusterServers()), {
            ...this.endPointDefaults(),
            ...clusterValues
        });
        return endpoint;
    }

    override defaultClusterValues() {
        return {
            levelControl: {
                currentLevel: 0
            },
            onOff: {
                onOff: false
            },
            colorControl: {
                currentHue: 0,
                currentSaturation: 0,
                coupleColorTempToLevelMinMireds: 0,
                startUpColorTemperatureMireds: 0
            }
        }
    }

    protected createColorControlServer(): typeof ColorControlServer {
        const parent = this;
        return class extends ColorControlServer {
            override async moveToColorTemperatureLogic(targetMireds: number, transitionTime: number) {
                await parent.sendBridgeEvent("colorControl", "colorTemperatureMireds", targetMireds);
                return super.moveToColorTemperatureLogic(targetMireds, transitionTime);
            }

            override async moveToHueAndSaturationLogic(targetHue: number, targetSaturation: number, transitionTime: number) {
                await parent.sendBridgeEvent("colorControl", "currentHue", targetHue);
                await parent.sendBridgeEvent("colorControl", "currentSaturation", targetSaturation);
            }            
        };
    }
}