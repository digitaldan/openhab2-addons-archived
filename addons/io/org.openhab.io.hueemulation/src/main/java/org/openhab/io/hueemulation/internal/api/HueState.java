package org.openhab.io.hueemulation.internal.api;

public class HueState {
    /**
     * on bool On/Off state of the light. On=true, Off=false
     * bri uint8 Brightness of the light. This is a scale from the minimum brightness the light is capable of, 1, to the
     * maximum capable brightness, 254.
     * hue uint16 Hue of the light. This is a wrapping value between 0 and 65535. Both 0 and 65535 are red, 25500 is
     * green and 46920 is blue.
     * sat uint8 Saturation of the light. 254 is the most saturated (colored) and 0 is the least saturated (white).
     * xy list 2..2 of float 4 The x and y coordinates of a color in CIE color space.
     * The first entry is the x coordinate and the second entry is the y coordinate. Both x and y are between 0 and 1.
     *
     * ct uint16 The Mired Color temperature of the light. 2012 connected lights are capable of 153 (6500K) to 500
     * (2000K).
     * alert string The alert effect, which is a temporary change to the bulb’s state. This can take one of the
     * following values:
     * “none” – The light is not performing an alert effect.
     * “select” – The light is performing one breathe cycle.
     * “lselect” – The light is performing breathe cycles for 15 seconds or until an "alert": "none" command is
     * received.
     * Note that this contains the last alert sent to the light and not its current state. i.e. After the breathe cycle
     * has finished the bridge does not reset the alert to "none".
     *
     * effect string The dynamic effect of the light, can either be “none” or “colorloop”.
     * If set to colorloop, the light will cycle through all hues using the current brightness and saturation settings.
     *
     * colormode string 2, 2 Indicates the color mode in which the light is working, this is the last command type it
     * received. Values are “hs” for Hue and Saturation, “xy” for XY and “ct” for Color Temperature. This parameter is
     * only present when the light supports at least one of the values.
     * reachable bool Indicates if a light can be reached by the bridge.
     *
     */
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
