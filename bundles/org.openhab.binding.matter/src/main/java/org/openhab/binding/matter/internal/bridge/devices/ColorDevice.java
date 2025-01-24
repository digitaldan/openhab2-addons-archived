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
    private ScheduledExecutorService colorUpdateScheduler = Executors.newSingleThreadScheduledExecutor();
    private @Nullable ScheduledFuture<?> colorUpdateTimer = null;
    private HSBType lastHSB = new HSBType();
    private boolean lastOnOff = true;

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
                lastHSB = hsbType;
                Float currentHue = toHue(hsbType.getHue());
                Float currentSaturation = toSaturation(hsbType.getSaturation());
                Integer currentLevel = toBrightness(hsbType.getBrightness());
                lastOnOff = currentLevel > 0;
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
        if (primaryItem instanceof ColorItem colorItem) {
            switch (attributeName) {
                case "onOff":
                    lastOnOff = Boolean.valueOf(data.toString());
                    startColorTimer(this::updateOnOff);
                    break;
                case "currentHue":
                case "currentSaturation":
                case "currentLevel":
                    updateHSB(colorItem, clusterName, attributeName, data);
                    startColorTimer(this::updatePrimaryHSB);
                    break;
                case "colorTemperatureMireds":
                    Double kelvin = 1e6 / (Double) data;
                    HSBType ctHSB = ColorUtil.xyToHsb(ColorUtil.kelvinToXY(Math.max(1000, Math.min(kelvin, 10000))));
                    lastHSB = new HSBType(ctHSB.getHue(), ctHSB.getSaturation(), lastHSB.getBrightness());
                    updatePrimaryHSB();
                    break;
                default:
                    break;
            }
        }
    }

    private synchronized void startColorTimer(Runnable runnable) {
        ScheduledFuture<?> colorUpdateTimer = this.colorUpdateTimer;
        if (colorUpdateTimer != null) {
            colorUpdateTimer.cancel(true);
        }
        this.colorUpdateTimer = colorUpdateScheduler.schedule(runnable, 500, TimeUnit.MILLISECONDS);
    }

    private synchronized void updateOnOff() {
        if (primaryItem instanceof ColorItem colorItem) {
            colorItem.send(OnOffType.from(lastOnOff));
        }
    }

    private synchronized void updateHSB(ColorItem colorItem, String clusterName, String attributeName, Object data) {
        HSBType hsb = this.lastHSB;
        DecimalType h = hsb.getHue();
        PercentType s = hsb.getSaturation();
        PercentType b = hsb.getBrightness();
        Double value = (Double) data;
        switch (attributeName) {
            case "currentHue":
                float hueValue = value == 0 ? 0.0f : value.floatValue() * 360.0f / 254.0f;
                h = new DecimalType(Float.valueOf(hueValue).toString());
                break;
            case "currentSaturation":
                float saturationValue = value == 0 ? 0.0f : value.floatValue() / 254.0f * 100.0f;
                s = new PercentType(Float.valueOf(saturationValue).toString());
                break;
            case "currentLevel":
                b = levelToPercent(value.intValue());
                break;
            default:
                break;
        }
        lastHSB = new HSBType(h, s, b);
    }

    public void updateState(Item item, State state) {
        if (state instanceof HSBType hsb) {
            lastHSB = hsb;
            if (hsb.getBrightness().intValue() == 0) {
                setEndpointState("onOff", "onOff", false);
            } else {
                setEndpointState("onOff", "onOff", true);
                setEndpointState("levelControl", "currentLevel", toBrightness(hsb.getBrightness()));
                setEndpointState("colorControl", "currentHue", toHue(hsb.getHue()));
                setEndpointState("colorControl", "currentSaturation", toSaturation(hsb.getSaturation()));
            }
        } else if (state instanceof PercentType percentType) {
            setEndpointState("onOff", "onOff", percentType.intValue() > 0);
            if (percentType.intValue() > 0) {
                setEndpointState("levelControl", "currentLevel", toBrightness(percentType));
            }
        } else if (state instanceof OnOffType onOffType) {
            setEndpointState("onOff", "onOff", onOffType == OnOffType.ON ? true : false);
        }
    }

    private void updatePrimaryHSB() {
        if (primaryItem instanceof ColorItem colorItem) {
            logger.debug("Updating primary HSB: {} {}", lastHSB, lastOnOff);
            if (lastOnOff) {
                colorItem.send(lastHSB);
            } else {
                colorItem.send(OnOffType.OFF);
            }
        }
    }

    private Float toHue(DecimalType h) {
        return h.floatValue() * 254.0f / 360.0f;
    }

    private Float toSaturation(PercentType s) {
        return s.floatValue() * 254.0f / 100.0f;
    }

    private Integer toBrightness(PercentType b) {
        return percentToLevel(b);
    }
}
