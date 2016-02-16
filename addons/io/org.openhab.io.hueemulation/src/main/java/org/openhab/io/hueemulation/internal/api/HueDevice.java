package org.openhab.io.hueemulation.internal.api;

import java.util.HashMap;
import java.util.Map;

public class HueDevice {
    /**
     * state state object Details the state of the light, see the state table below for more details.
     * type string A fixed name describing the type of light e.g. “Extended color light”.
     * name string 0, 32 A unique, editable name given to the light.
     * modelid string 6, 6 The hardware model of the light.
     * uniqueid string 6, 32 As of 1.4. Unique id of the device. The MAC address of the device with a unique endpoint id
     * in the form: AA:BB:CC:DD:EE:FF:00:11-XX
     * manufacturername string 6, 32 As of 1.7. The manufacturer name.
     * luminaireuniqueid string 6, 32 As of 1.9. Unique ID of the luminaire the light is a part of in the format:
     * AA:BB:CC:DD-XX-YY. AA:BB:, ... represents the hex of the luminaireid, XX the lightsource position (incremental
     * but may contain gaps) and YY the lightpoint position (index of light in luminaire group). A gap in the lightpoint
     * position indicates an incomplete luminaire (light search required to discover missing light points in this case).
     * swversion string 8, 8 An identifier for the software version running on the light.
     * pointsymbol object This parameter is reserved for future functionality. As from 1.11 point symbols are no longer
     * returned.
     */
    public HueState state;
    public String type = "Extended color light";
    public String name;
    public String modelid = "LCT001";
    public String uniqueid;
    public String manufacturername = "openHAB";
    // public String luminaireuniqueid;
    public String swversion = "66009461";
    public Map<String, String> pointsymbol;

    public HueDevice(HueState state, String name, String uniqueid) {
        super();
        this.state = state;
        this.name = name;
        this.uniqueid = uniqueid;
        this.pointsymbol = new HashMap<String, String>();
        for (int i = 1; i < 9; i++) {
            this.pointsymbol.put(String.valueOf(i), "none");
        }
    }

}
