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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.measure.quantity.Temperature;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.matter.internal.client.AttributeListener;
import org.openhab.binding.matter.internal.client.EventTriggeredListener;
import org.openhab.binding.matter.internal.client.dto.cluster.gen.BaseCluster;
import org.openhab.binding.matter.internal.client.dto.ws.AttributeChangedMessage;
import org.openhab.binding.matter.internal.client.dto.ws.EventTriggeredMessage;
import org.openhab.binding.matter.internal.handler.MatterBaseThingHandler;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.PercentType;
import org.openhab.core.library.types.QuantityType;
import org.openhab.core.library.unit.ImperialUnits;
import org.openhab.core.library.unit.SIUnits;
import org.openhab.core.thing.Channel;
import org.openhab.core.thing.ChannelGroupUID;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.types.Command;
import org.openhab.core.types.RefreshType;
import org.openhab.core.types.State;
import org.openhab.core.types.StateDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link GenericConverter}
 *
 * @author Dan Cunningham - Initial contribution
 *
 *         Converters are responsible for converting Matter cluster commands and attributes into openHAB commands and
 *         vice versa.
 */
@NonNullByDefault
public abstract class GenericConverter<T extends BaseCluster> implements AttributeListener, EventTriggeredListener {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private static final BigDecimal TEMPERATURE_MULTIPLIER = new BigDecimal(100);
    // This cluster is used for initializing the converter, but is not kept updated as values change over time.
    protected T initializingCluster;
    protected MatterBaseThingHandler handler;
    protected int endpointNumber;
    protected String labelPrefix;
    // used to REFRESH channels
    protected ConcurrentHashMap<String, State> stateCache = new ConcurrentHashMap<>();

    public GenericConverter(T cluster, MatterBaseThingHandler handler, int endpointNumber, String labelPrefix) {
        this.initializingCluster = cluster;
        this.handler = handler;
        this.endpointNumber = endpointNumber;
        this.labelPrefix = labelPrefix;
    }

    public abstract Map<Channel, @Nullable StateDescription> createChannels(ChannelGroupUID thingUID);

    /**
     * Updates all the channel states of a cluster
     */
    public abstract void initState();

    public void handleCommand(ChannelUID channelUID, Command command) {
        if (command instanceof RefreshType) {
            stateCache.forEach((channelId, state) -> handler.updateState(endpointNumber, channelId, state));
        }
    }

    @Override
    public void onEvent(AttributeChangedMessage message) {
    }

    @Override
    public void onEvent(EventTriggeredMessage message) {
    }

    public T getInitializingCluster() {
        return initializingCluster;
    }

    public final void updateState(String channelId, State state) {
        handler.updateState(endpointNumber, channelId, state);
        stateCache.put(channelId, state);
    }

    public final void triggerChannel(String channelId, String event) {
        handler.triggerChannel(endpointNumber, channelId, event);
    }

    /**
     * Converts a ZigBee 8 bit level as used in Level Control cluster and others to a percentage
     *
     * @param level an integer between 0 and 254
     * @return the scaled {@link PercentType}
     */
    public static PercentType levelToPercent(int level) {
        int result = (int) Math.round(level * 100.0 / 254.0);
        return level == 0 ? PercentType.ZERO : new PercentType(Math.max(result, 1));
    }

    /**
     * Converts a {@link DecimalType} to an 8 bit level scaled between 0 and 254
     *
     * @param percent the {@link DecimalType} to convert
     * @return a scaled value between 0 and 254
     */

    public static int percentToLevel(PercentType percent) {
        return (int) (percent.floatValue() * 254.0f / 100.0f + 0.5f);
    }

    /**
     * Converts a {@link Command} to a ZigBee / Matter temperature integer
     *
     * @param command the {@link Command} to convert
     * @return the {@link Command} or null if the conversion was not possible
     */
    public static @Nullable Integer temperatureToValue(Command command) {
        BigDecimal value = null;
        if (command instanceof QuantityType<?> quantity) {
            if (quantity.getUnit() == SIUnits.CELSIUS) {
                value = quantity.toBigDecimal();
            } else if (quantity.getUnit() == ImperialUnits.FAHRENHEIT) {
                QuantityType<?> celsius = quantity.toUnit(SIUnits.CELSIUS);
                if (celsius == null) {
                    return null;
                }
                value = celsius.toBigDecimal();
            } else {
                return null;
            }
        } else if (command instanceof Number number) {
            // No scale, so assumed to be Celsius
            value = BigDecimal.valueOf(number.doubleValue());
        }
        if (value == null) {
            return null;
        }
        // originally this used RoundingMode.CEILING, if there are accuracy problems, we may want to revisit that
        return value.setScale(2, RoundingMode.HALF_UP).multiply(TEMPERATURE_MULTIPLIER).intValue();
    }

    /**
     * Converts an integer value into a {@link QuantityType}. The temperature as an integer is assumed to be multiplied
     * by 100 as per the ZigBee / Matter standard format.
     *
     * @param value the integer value to convert
     * @return the {@link QuantityType}
     */
    public static QuantityType<Temperature> valueToTemperature(int value) {
        return new QuantityType<>(BigDecimal.valueOf(value, 2), SIUnits.CELSIUS);
    }

    protected String formatLabel(String channelLabel) {
        if (labelPrefix.trim().length() > 0) {
            return labelPrefix.trim() + " - " + channelLabel;
        }
        return channelLabel;
    }
}
