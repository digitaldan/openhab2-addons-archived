package org.openhab.io.hueemulation.internal.api;

public class HueState {
    public boolean on;
    public short bri = -1;
    public int hue = 0;
    public short sat = 0;
    public double[] xy = { 0, 0 };
    public int ct = 500;
    public String alert = "none";
    public String effect = "none";
    public String colormode = "ct";
    public boolean reachable = true;

    public HueState() {
        super();
    }

    public HueState(boolean on, short bri) {
        super();
        this.on = on;
        this.bri = bri;
    }

    @Override
    public String toString() {
        String xyString = "{";
        for (double d : xy) {
            xyString += d + " ";
        }
        xyString += "}";
        return "[on: " + on + " bri: " + bri + " hue: " + hue + " sat: " + sat + " xy: " + xyString + " ct: " + ct
                + " alert: " + alert + " effect: " + effect + " colormode: " + colormode + " reachable: " + reachable;
    }
}
