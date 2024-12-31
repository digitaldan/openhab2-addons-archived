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
package org.openhab.binding.matter.internal.controller.devices.converter;

import static org.openhab.binding.matter.internal.MatterBindingConstants.CHANNEL_COLOR_COLOR;
import static org.openhab.binding.matter.internal.MatterBindingConstants.CHANNEL_COLOR_TEMPERATURE;
import static org.openhab.binding.matter.internal.MatterBindingConstants.CHANNEL_COLOR_TEMPERATURE_ABS;
import static org.openhab.binding.matter.internal.MatterBindingConstants.CHANNEL_LABEL_COLOR_COLOR;
import static org.openhab.binding.matter.internal.MatterBindingConstants.CHANNEL_LABEL_COLOR_TEMPERATURE;
import static org.openhab.binding.matter.internal.MatterBindingConstants.CHANNEL_LABEL_COLOR_TEMPERATURE_ABS;
import static org.openhab.binding.matter.internal.MatterBindingConstants.ITEM_TYPE_COLOR;
import static org.openhab.binding.matter.internal.MatterBindingConstants.ITEM_TYPE_DIMMER;
import static org.openhab.binding.matter.internal.MatterBindingConstants.ITEM_TYPE_NUMBER_TEMPERATURE;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.matter.internal.client.model.cluster.BaseCluster.MatterEnum;
import org.openhab.binding.matter.internal.client.model.cluster.ClusterCommand;
import org.openhab.binding.matter.internal.client.model.cluster.gen.ColorControlCluster;
import org.openhab.binding.matter.internal.client.model.cluster.gen.ColorControlCluster.ColorMode;
import org.openhab.binding.matter.internal.client.model.cluster.gen.ColorControlCluster.Options;
import org.openhab.binding.matter.internal.client.model.cluster.gen.LevelControlCluster;
import org.openhab.binding.matter.internal.client.model.cluster.gen.LevelControlCluster.OptionsBitmap;
import org.openhab.binding.matter.internal.client.model.cluster.gen.OnOffCluster;
import org.openhab.binding.matter.internal.client.model.ws.AttributeChangedMessage;
import org.openhab.binding.matter.internal.controller.MatterControllerClient;
import org.openhab.binding.matter.internal.handler.MatterBaseThingHandler;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.HSBType;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.library.types.PercentType;
import org.openhab.core.library.types.QuantityType;
import org.openhab.core.library.unit.Units;
import org.openhab.core.thing.Channel;
import org.openhab.core.thing.ChannelGroupUID;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.binding.builder.ChannelBuilder;
import org.openhab.core.types.Command;
import org.openhab.core.types.StateDescription;
import org.openhab.core.types.StateDescriptionFragmentBuilder;
import org.openhab.core.types.UnDefType;
import org.openhab.core.util.ColorUtil;

/**
 * The {@link MatterControllerClient}
 *
 * @author Dan Cunningham - Initial contribution
 * @author Chris Jackson - Original Zigbee binding color logic
 *
 */
@NonNullByDefault
public class ColorControlConverter extends GenericConverter<ColorControlCluster> {

    private @Nullable ColorMode lastColorMode;
    private boolean supportsHue = false;
    private boolean lastOnOff = true;
    private int lastHue = -1;
    private int lastSaturation = -1;
    private @Nullable ScheduledFuture<?> colorUpdateTimer = null;
    private int lastX = -1;
    private int lastY = -1;
    private boolean colorChanged = false;
    private HSBType lastHSB = new HSBType("0,0,0");
    private boolean supportsColorTemperature = false;
    private @Nullable Integer lastColorTemperatureMireds;
    private Integer colorTempPhysicalMinMireds = 0;
    private Integer colorTempPhysicalMaxMireds = 0;
    private Options optionsMask = new Options(true);
    private OptionsBitmap optionsBitmap = new OptionsBitmap(true, true);
    private ScheduledExecutorService colorUpdateScheduler = Executors.newSingleThreadScheduledExecutor();

    public ColorControlConverter(ColorControlCluster cluster, MatterBaseThingHandler handler, int endpointNumber,
            String labelPrefix) {
        super(cluster, handler, endpointNumber, labelPrefix);
    }

    @Override
    public Map<Channel, @Nullable StateDescription> createChannels(ChannelGroupUID thingUID) {
        Map<Channel, @Nullable StateDescription> map = new HashMap<>();

        map.put(ChannelBuilder.create(new ChannelUID(thingUID, CHANNEL_COLOR_COLOR.getId()), ITEM_TYPE_COLOR)
                .withType(CHANNEL_COLOR_COLOR).withLabel(formatLabel(CHANNEL_LABEL_COLOR_COLOR)).build(), null);

        // see Matter spec 3.2.6.1. For more information on color temperature
        if (initializingCluster.featureMap.colorTemperature) {
            map.put(ChannelBuilder.create(new ChannelUID(thingUID, CHANNEL_COLOR_TEMPERATURE.getId()), ITEM_TYPE_DIMMER)
                    .withType(CHANNEL_COLOR_TEMPERATURE).withLabel(formatLabel(CHANNEL_LABEL_COLOR_TEMPERATURE))
                    .build(), null);
            Optional.ofNullable(initializingCluster.colorTempPhysicalMinMireds)
                    .ifPresent(temp -> colorTempPhysicalMinMireds = temp);
            Optional.ofNullable(initializingCluster.colorTempPhysicalMaxMireds)
                    .ifPresent(temp -> colorTempPhysicalMaxMireds = temp);
            StateDescription stateDescription = null;
            if (colorTempPhysicalMinMireds < colorTempPhysicalMaxMireds) {
                stateDescription = StateDescriptionFragmentBuilder.create().withPattern("%.0f mirek")
                        .withMinimum(BigDecimal.valueOf(colorTempPhysicalMinMireds))
                        .withMaximum(BigDecimal.valueOf(colorTempPhysicalMaxMireds)).build().toStateDescription();
            }
            map.put(ChannelBuilder
                    .create(new ChannelUID(thingUID, CHANNEL_COLOR_TEMPERATURE_ABS.getId()),
                            ITEM_TYPE_NUMBER_TEMPERATURE)
                    .withType(CHANNEL_COLOR_TEMPERATURE_ABS).withLabel(formatLabel(CHANNEL_LABEL_COLOR_TEMPERATURE_ABS))
                    .build(), stateDescription);
        }
        return map;
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (command instanceof HSBType color) {
            PercentType brightness = color.getBrightness();
            ClusterCommand levelCommand = LevelControlCluster.moveToLevelWithOnOff(percentToLevel(brightness), 0,
                    optionsBitmap, optionsBitmap);
            handler.sendClusterCommand(endpointNumber, LevelControlCluster.CLUSTER_NAME, levelCommand);
            if (supportsHue) {
                changeColorHueSaturation(color);
            } else {
                changeColorXY(color);
            }
        } else if (command instanceof OnOffType onOffType) {
            ClusterCommand onOffCommand = onOffType == OnOffType.ON ? OnOffCluster.on() : OnOffCluster.off();
            handler.sendClusterCommand(endpointNumber, OnOffCluster.CLUSTER_NAME, onOffCommand);
        } else if (command instanceof PercentType percentType) {
            if (channelUID.getIdWithoutGroup().equals(CHANNEL_COLOR_TEMPERATURE.getId())) {
                ClusterCommand tempCommand = ColorControlCluster
                        .moveToColorTemperature(percentTypeToMireds(percentType), 0, optionsMask, optionsMask);
                handler.sendClusterCommand(endpointNumber, ColorControlCluster.CLUSTER_NAME, tempCommand);
            } else {
                ClusterCommand levelCommand = LevelControlCluster.moveToLevelWithOnOff(percentToLevel(percentType), 0,
                        optionsBitmap, optionsBitmap);
                handler.sendClusterCommand(endpointNumber, LevelControlCluster.CLUSTER_NAME, levelCommand);
            }
        } else if (channelUID.getIdWithoutGroup().equals(CHANNEL_COLOR_TEMPERATURE_ABS.getId())
                && command instanceof DecimalType decimal) {
            ClusterCommand tempCommand = ColorControlCluster.moveToColorTemperature(decimal.intValue(), 0, optionsMask,
                    optionsMask);
            handler.sendClusterCommand(endpointNumber, ColorControlCluster.CLUSTER_NAME, tempCommand);
        } else if (channelUID.getIdWithoutGroup().equals(CHANNEL_COLOR_TEMPERATURE_ABS.getId())
                && command instanceof QuantityType<?> quantity) {
            quantity = quantity.toInvertibleUnit(Units.MIRED);
            if (quantity != null) {
                ClusterCommand tempCommand = ColorControlCluster.moveToColorTemperature(quantity.intValue(), 0,
                        optionsMask, optionsMask);
                handler.sendClusterCommand(endpointNumber, ColorControlCluster.CLUSTER_NAME, tempCommand);
            }
        }
        super.handleCommand(channelUID, command);
    }

    @Override
    public void onEvent(AttributeChangedMessage message) {
        Integer numberValue = message.value instanceof Number number ? number.intValue() : 0;
        switch (message.path.attributeName) {
            case "currentX":
                lastX = numberValue;
                colorChanged = true;
                break;
            case "currentY":
                lastY = numberValue;
                colorChanged = true;
                break;
            case "currentHue":
                lastHue = numberValue;
                colorChanged = true;
                break;
            case "currentSaturation":
                lastSaturation = numberValue;
                colorChanged = true;
                break;
            case "colorTemperatureMireds":
                lastColorTemperatureMireds = numberValue;
                colorChanged = true;
                break;
            case "colorMode":
                try {
                    lastColorMode = MatterEnum.fromValue(ColorMode.class, numberValue);
                } catch (IllegalArgumentException e) {
                    lastColorMode = null;
                }
                colorChanged = true;
                break;
            case "enhancedCurrentHue":
            case "enhancedColorMode":
                break;
            case "currentLevel":
                updateBrightness(levelToPercent(numberValue));
                break;
            case "onOff":
                updateOnOff((Boolean) message.value);
                break;
            default:
                logger.debug("Unknown attribute {}", message.path.attributeName);
        }
        if (colorChanged) {
            if (colorUpdateTimer != null) {
                colorUpdateTimer.cancel(true);
            }
            colorUpdateTimer = colorUpdateScheduler.schedule(() -> updateColor(), 500, TimeUnit.MILLISECONDS);
        }
        super.onEvent(message);
    }

    @Override
    public void initState() {
        initState(true, 100);
    }

    public void initState(boolean onOff, int brightness) {
        lastHSB = new HSBType(lastHSB.getHue(), lastHSB.getSaturation(), new PercentType(brightness));
        lastColorMode = initializingCluster.colorMode;
        lastOnOff = onOff;
        supportsHue = initializingCluster.featureMap.hueSaturation;
        lastX = initializingCluster.currentX != null ? initializingCluster.currentX : 0;
        lastY = initializingCluster.currentY != null ? initializingCluster.currentY : 0;
        lastHue = initializingCluster.currentHue != null ? initializingCluster.currentHue : 0;
        lastSaturation = initializingCluster.currentSaturation != null ? initializingCluster.currentSaturation : 0;
        supportsColorTemperature = initializingCluster.featureMap.colorTemperature;
        lastColorTemperatureMireds = initializingCluster.colorTemperatureMireds;
        Optional.ofNullable(initializingCluster.colorTempPhysicalMaxMireds)
                .ifPresent(temp -> colorTempPhysicalMaxMireds = temp);
        Optional.ofNullable(initializingCluster.colorTempPhysicalMinMireds)
                .ifPresent(temp -> colorTempPhysicalMinMireds = temp);
        updateColor();
    }

    // These functions are borrowed from the Zigbee openHAB binding

    private void updateOnOff(boolean onOff) {
        lastOnOff = onOff;
        HSBType hsb = new HSBType(lastHSB.getHue(), lastHSB.getSaturation(),
                lastOnOff ? lastHSB.getBrightness() : new PercentType(0));
        updateState(CHANNEL_COLOR_COLOR, hsb);
    }

    private void updateBrightness(PercentType brightness) {
        // Extra temp variable to avoid thread sync concurrency issues on lastHSB
        HSBType oldHSB = lastHSB;
        HSBType newHSB = new HSBType(oldHSB.getHue(), oldHSB.getSaturation(), brightness);
        lastHSB = newHSB;
        updateState(CHANNEL_COLOR_COLOR, newHSB);
    }

    private void updateColorHSB() {
        float hueValue = lastHue * 360.0f / 254.0f;
        float saturationValue = lastSaturation * 100.0f / 254.0f;
        DecimalType hue = new DecimalType(Float.valueOf(hueValue).toString());
        PercentType saturation = new PercentType(Float.valueOf(saturationValue).toString());
        updateColorHSB(hue, saturation);
        if (supportsColorTemperature) {
            updateState(CHANNEL_COLOR_TEMPERATURE, UnDefType.UNDEF);
            updateState(CHANNEL_COLOR_TEMPERATURE_ABS, UnDefType.UNDEF);
        }
    }

    private void updateColorHSB(DecimalType hue, PercentType saturation) {
        // Extra temp variable to avoid thread sync concurrency issues on lastHSB
        HSBType oldHSB = lastHSB;
        HSBType newHSB = new HSBType(hue, saturation, oldHSB.getBrightness());
        lastHSB = newHSB;
        if (!lastOnOff) {
            updateState(CHANNEL_COLOR_COLOR, new HSBType(newHSB.getHue(), newHSB.getSaturation(), new PercentType(0)));
        } else {
            updateState(CHANNEL_COLOR_COLOR, newHSB);
        }
    }

    private void updateColorXY() {
        float xValue = lastX / 65536.0f;
        float yValue = lastY / 65536.0f;
        PercentType x = new PercentType(Float.valueOf(xValue * 100.0f).toString());
        PercentType y = new PercentType(Float.valueOf(yValue * 100.0f).toString());
        updateColorXY(x, y);
        if (supportsColorTemperature) {
            updateState(CHANNEL_COLOR_TEMPERATURE, UnDefType.UNDEF);
            updateState(CHANNEL_COLOR_TEMPERATURE_ABS, UnDefType.UNDEF);
        }
    }

    private void updateColorXY(PercentType x, PercentType y) {
        try {
            HSBType color = ColorUtil.xyToHsb(new double[] { x.floatValue() / 100.0f, y.floatValue() / 100.0f });
            updateColorHSB(color.getHue(), color.getSaturation());
        } catch (IllegalArgumentException e) {
            updateState(CHANNEL_COLOR_COLOR, UnDefType.UNDEF);
        }
    }

    private void updateColorTemperature() {
        Integer mirek = lastColorTemperatureMireds;
        if (mirek != null) {
            if (mirek == 0) {
                updateState(CHANNEL_COLOR_TEMPERATURE, UnDefType.UNDEF);
                updateState(CHANNEL_COLOR_TEMPERATURE_ABS, UnDefType.UNDEF);
            } else {
                updateState(CHANNEL_COLOR_TEMPERATURE, miredsToPercenType(mirek));
                updateState(CHANNEL_COLOR_TEMPERATURE_ABS, QuantityType.valueOf(Double.valueOf(mirek), Units.MIRED));
                try {
                    HSBType color = ColorUtil.xyToHsb(ColorUtil.kelvinToXY(1000000.0 / mirek));
                    updateColorHSB(color.getHue(), color.getSaturation());
                } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
                    updateState(CHANNEL_COLOR_COLOR, UnDefType.UNDEF);
                }
            }
        }
    }

    private void updateColor() {
        ColorMode mode = lastColorMode;
        if (mode != null) {
            switch (mode) {
                case CURRENT_HUE_AND_CURRENT_SATURATION:
                case CURRENT_XAND_CURRENT_Y:
                    if (supportsHue) {
                        updateColorHSB();
                    } else {
                        updateColorXY();
                    }
                    break;
                case COLOR_TEMPERATURE_MIREDS:
                    if (useColorTemperature()) {
                        updateColorTemperature();
                    } else {
                        updateColorXY();
                    }
                    break;
            }
        }
        colorChanged = false;
    }

    private boolean useColorTemperature() {
        return supportsColorTemperature && lastColorTemperatureMireds != null && lastColorTemperatureMireds > 0;
    }

    private void changeColorHueSaturation(HSBType color) {
        int hue = (int) (color.getHue().floatValue() * 254.0f / 360.0f + 0.5f);
        int saturation = percentToLevel(color.getSaturation());
        handler.sendClusterCommand(endpointNumber, ColorControlCluster.CLUSTER_NAME,
                ColorControlCluster.moveToHueAndSaturation(hue, saturation, 0, optionsMask, optionsMask));
    }

    private void changeColorXY(HSBType color) {
        PercentType xy[] = color.toXY();
        int x = (int) (xy[0].floatValue() / 100.0f * 65536.0f + 0.5f); // up to 65279
        int y = (int) (xy[1].floatValue() / 100.0f * 65536.0f + 0.5f); // up to 65279
        handler.sendClusterCommand(endpointNumber, ColorControlCluster.CLUSTER_NAME,
                ColorControlCluster.moveToColor(x, y, 0, optionsMask, optionsMask));
    }

    private PercentType miredsToPercenType(Integer mireds) {
        if (mireds == 0 || colorTempPhysicalMaxMireds - colorTempPhysicalMinMireds == 0) {
            return new PercentType(0);
        }
        return new PercentType((int) (((double) (mireds - colorTempPhysicalMinMireds)
                / (colorTempPhysicalMaxMireds - colorTempPhysicalMinMireds)) * 100));
    }

    private Integer percentTypeToMireds(PercentType percent) {
        return (int) ((percent.doubleValue() / 100) * (colorTempPhysicalMaxMireds - colorTempPhysicalMinMireds))
                + colorTempPhysicalMinMireds;
    }
}
