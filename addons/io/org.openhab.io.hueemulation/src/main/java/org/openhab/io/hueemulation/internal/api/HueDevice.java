package org.openhab.io.hueemulation.internal.api;

import java.util.HashMap;
import java.util.Map;

public class HueDevice {
    public HueState state;
    public String type = "Extended color light";
    public String name;
    public String modelid = "LCT001";
    public String uniqueid;
    public String manufacturername = "openHAB";
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
