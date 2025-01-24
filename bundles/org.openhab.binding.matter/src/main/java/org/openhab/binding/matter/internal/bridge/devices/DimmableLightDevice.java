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
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.matter.internal.bridge.MatterBridgeClient;
import org.openhab.core.items.GenericItem;
import org.openhab.core.items.GroupItem;
import org.openhab.core.items.Item;
import org.openhab.core.items.MetadataRegistry;
import org.openhab.core.library.items.DimmerItem;
import org.openhab.core.library.items.SwitchItem;
import org.openhab.core.library.types.HSBType;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.library.types.PercentType;
import org.openhab.core.types.State;

/**
 * The {@link DimmableLightDevice}
 *
 * @author Dan Cunningham - Initial contribution
 */
@NonNullByDefault
public class DimmableLightDevice extends GenericDevice {
    private ScheduledExecutorService updateScheduler = Executors.newSingleThreadScheduledExecutor();
    private @Nullable ScheduledFuture<?> updateTimer = null;
    private boolean lastOnOff = false;
    private @Nullable PercentType lastLevel;

    public DimmableLightDevice(MetadataRegistry metadataRegistry, MatterBridgeClient client, GenericItem item) {
        super(metadataRegistry, client, item);
    }

    @Override
    public String deviceType() {
        return "DimmableLight";
    }

    @Override
    public MatterDeviceOptions activate() {
        dispose();
        primaryItem.addStateChangeListener(this);
        MetaDataMapping primaryMetadata = metaDataMapping(primaryItem);
        Map<String, Object> attributeMap = primaryMetadata.getAttributeOptions();
        int level = Optional.ofNullable(primaryItem.getStateAs(PercentType.class)).orElseGet(() -> new PercentType(0))
                .intValue();
        attributeMap.put("levelControl.currentLevel", level);
        attributeMap.put("onOff.onOff", level > 0);
        return new MatterDeviceOptions(attributeMap, primaryMetadata.label);
    }

    @Override
    public void dispose() {
        primaryItem.removeStateChangeListener(this);
    }

    @Override
    public void handleMatterEvent(String clusterName, String attributeName, Object data) {
        switch (attributeName) {
            case "onOff":
                lastOnOff = Boolean.valueOf(data.toString());
                startTimer();
                break;
            case "currentLevel":
                lastLevel = levelToPercent(((Double) data).intValue());
                startTimer();
                break;
            default:
                break;
        }
    }

    public void updateState(Item item, State state) {
        if (state instanceof HSBType hsb) {
            setEndpointState("levelControl", "currentLevel", percentToLevel(hsb.getBrightness()));
            setEndpointState("onOff", "onOff", hsb.getBrightness().intValue() > 0);
        } else if (state instanceof PercentType percentType) {
            setEndpointState("onOff", "onOff", percentType.intValue() > 0);
            if (percentType.intValue() > 0) {
                setEndpointState("levelControl", "currentLevel", percentToLevel(percentType));
            }
        } else if (state instanceof OnOffType onOffType) {
            setEndpointState("onOff", "onOff", onOffType == OnOffType.ON ? true : false);
        }
    }

    private synchronized void updateItemState() {
        OnOffType onOffType = OnOffType.from(lastOnOff);
        PercentType lastLevel = this.lastLevel;
        if (primaryItem instanceof GroupItem groupItem) {
            groupItem.send(lastLevel != null ? lastLevel : onOffType);
        } else if (primaryItem instanceof DimmerItem) {
            if (lastLevel != null && lastOnOff) {
                ((DimmerItem) primaryItem).send(lastLevel);
            } else {
                ((SwitchItem) primaryItem).send(onOffType);
            }
        } else {
            ((SwitchItem) primaryItem).send(onOffType);
        }
        this.lastLevel = null;
    }

    private synchronized void startTimer() {
        ScheduledFuture<?> updateTimer = this.updateTimer;
        if (updateTimer != null) {
            updateTimer.cancel(true);
        }
        this.updateTimer = updateScheduler.schedule(this::updateItemState, 500, TimeUnit.MILLISECONDS);
    }
}
