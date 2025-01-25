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
package org.openhab.binding.matter.internal.bridge.devices;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.matter.internal.bridge.MatterBridgeClient;
import org.openhab.core.items.GenericItem;
import org.openhab.core.items.Item;
import org.openhab.core.items.MetadataRegistry;
import org.openhab.core.library.items.ColorItem;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.HSBType;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.library.types.PercentType;
import org.openhab.core.types.State;
import org.openhab.core.util.ColorUtil;

/**
 * The {@link ColorDevice}
 *
 * @author Dan Cunningham - Initial contribution
 */
@NonNullByDefault
public class ColorDevice extends GenericDevice {
    private static final int DELAY_MILLIS = 500;
    private ScheduledExecutorService colorUpdateScheduler = Executors.newSingleThreadScheduledExecutor();
    private @Nullable ScheduledFuture<?> colorUpdateTimer = null;
    private @Nullable Boolean lastOnOff;
    private @Nullable DecimalType lastH;
    private @Nullable PercentType lastS;
    private @Nullable PercentType lastB;

    public ColorDevice(MetadataRegistry metadataRegistry, MatterBridgeClient client, GenericItem item) {
        super(metadataRegistry, client, item);
    }

    @Override
    public String deviceType() {
        return "ColorLight";
    }

    @Override
    public MatterDeviceOptions activate() {
        dispose();
        primaryItem.addStateChangeListener(this);
        MetaDataMapping primaryMetadata = metaDataMapping(primaryItem);
        Map<String, Object> attributeMap = primaryMetadata.getAttributeOptions();
        if (primaryItem instanceof ColorItem colorItem) {
            HSBType hsbType = colorItem.getStateAs(HSBType.class);
            if (hsbType != null) {
                Integer currentHue = toHue(hsbType.getHue());
                Integer currentSaturation = toSaturation(hsbType.getSaturation());
                Integer currentLevel = toBrightness(hsbType.getBrightness());
                attributeMap.put("levelControl.currentLevel", currentLevel);
                attributeMap.put("colorControl.currentHue", currentHue);
                attributeMap.put("colorControl.currentSaturation", currentSaturation);
                attributeMap.put("onOff.onOff", currentLevel > 0);
            }
        }
        return new MatterDeviceOptions(attributeMap, primaryMetadata.label);
    }

    @Override
    public void dispose() {
        primaryItem.removeStateChangeListener(this);
        if (colorUpdateTimer != null) {
            colorUpdateTimer.cancel(true);
        }
    }

    @Override
    public void handleMatterEvent(String clusterName, String attributeName, Object data) {
        Double value = Double.valueOf(0);
        if (data instanceof Double d) {
            value = d;
        }
        switch (attributeName) {
            case "onOff":
                lastOnOff = Boolean.valueOf(data.toString());
                startColorTimer();
                break;
            case "currentHue":
                float hueValue = value == 0 ? 0.0f : value.floatValue() * 360.0f / 254.0f;
                lastH = new DecimalType(Float.valueOf(hueValue).toString());
                break;
            case "currentSaturation":
                float saturationValue = value == 0 ? 0.0f : value.floatValue() / 254.0f * 100.0f;
                lastS = new PercentType(Float.valueOf(saturationValue).toString());
                break;
            case "currentLevel":
                lastB = levelToPercent(value.intValue());
                break;
            case "colorTemperatureMireds":
                Double kelvin = 1e6 / (Double) data;
                HSBType ctHSB = ColorUtil.xyToHsb(ColorUtil.kelvinToXY(Math.max(1000, Math.min(kelvin, 10000))));
                lastH = ctHSB.getHue();
                lastS = ctHSB.getSaturation();
                break;
            default:
                break;
        }
        startColorTimer();
    }

    private synchronized void startColorTimer() {
        ScheduledFuture<?> colorUpdateTimer = this.colorUpdateTimer;
        if (colorUpdateTimer != null) {
            colorUpdateTimer.cancel(true);
        }
        this.colorUpdateTimer = colorUpdateScheduler.schedule(this::updateHSB, DELAY_MILLIS, TimeUnit.MILLISECONDS);
    }

    private synchronized void updateHSB() {
        if (primaryItem instanceof ColorItem colorItem) {
            Boolean lastOnOff = this.lastOnOff;
            if (lastOnOff != null) {
                this.lastOnOff = null;
                colorItem.send(OnOffType.from(lastOnOff));
                if (lastOnOff.booleanValue()) {
                    // wait for the device to turn on otherwise brightness will be 0 and not the last brightness
                    this.colorUpdateTimer = colorUpdateScheduler.schedule(this::updateHSB, DELAY_MILLIS,
                            TimeUnit.MILLISECONDS);
                }
                return;
            }

            HSBType hsb = colorItem.getStateAs(HSBType.class);
            if (hsb == null) {
                return;
            }

            DecimalType h = hsb.getHue();
            PercentType s = hsb.getSaturation();
            PercentType b = hsb.getBrightness();

            DecimalType lastH = this.lastH;
            PercentType lastS = this.lastS;
            PercentType lastB = this.lastB;

            if (lastH == null && lastS == null && lastB == null) {
                return;
            }
            if (lastH != null) {
                h = lastH;
            }
            if (lastS != null) {
                s = lastS;
            }
            if (lastB != null) {
                b = lastB;
            }
            colorItem.send(new HSBType(h, s, b));

        }
        this.lastH = null;
        this.lastS = null;
        this.lastB = null;
    }

    @Override
    public void updateState(Item item, State state) {
        if (state instanceof HSBType hsb) {
            if (hsb.getBrightness().intValue() == 0) {
                setEndpointState("onOff", "onOff", false);
            } else {
                setEndpointState("onOff", "onOff", true);
                setEndpointState("levelControl", "currentLevel", toBrightness(hsb.getBrightness()));
            }
            setEndpointState("colorControl", "currentHue", toHue(hsb.getHue()));
            setEndpointState("colorControl", "currentSaturation", toSaturation(hsb.getSaturation()));
        }
    }

    private Integer toHue(DecimalType h) {
        return Math.round(h.floatValue() * 254.0f / 360.0f);
    }

    private Integer toSaturation(PercentType s) {
        return Math.round(s.floatValue() * 254.0f / 100.0f);
    }

    private Integer toBrightness(PercentType b) {
        return percentToLevel(b);
    }
}
