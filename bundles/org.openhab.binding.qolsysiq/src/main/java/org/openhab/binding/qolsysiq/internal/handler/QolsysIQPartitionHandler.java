/**
 * Copyright (c) 2010-2022 Contributors to the openHAB project
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
package org.openhab.binding.qolsysiq.internal.handler;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.qolsysiq.internal.QolsysIQBindingConstants;
import org.openhab.binding.qolsysiq.internal.client.dto.action.AlarmAction;
import org.openhab.binding.qolsysiq.internal.client.dto.action.AlarmActionType;
import org.openhab.binding.qolsysiq.internal.client.dto.action.ArmingAction;
import org.openhab.binding.qolsysiq.internal.client.dto.action.ArmingActionType;
import org.openhab.binding.qolsysiq.internal.client.dto.action.InfoAction;
import org.openhab.binding.qolsysiq.internal.client.dto.action.InfoActionType;
import org.openhab.binding.qolsysiq.internal.client.dto.event.AlarmEvent;
import org.openhab.binding.qolsysiq.internal.client.dto.event.ArmingEvent;
import org.openhab.binding.qolsysiq.internal.client.dto.event.SecureArmInfoEvent;
import org.openhab.binding.qolsysiq.internal.client.dto.model.AlarmType;
import org.openhab.binding.qolsysiq.internal.client.dto.model.Partition;
import org.openhab.binding.qolsysiq.internal.client.dto.model.PartitionStatus;
import org.openhab.binding.qolsysiq.internal.client.dto.model.Zone;
import org.openhab.binding.qolsysiq.internal.config.QolsysIQPartitionConfiguration;
import org.openhab.binding.qolsysiq.internal.discovery.QolsysIQChildDiscoveryService;
import org.openhab.core.library.types.StringType;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingUID;
import org.openhab.core.thing.binding.BaseBridgeHandler;
import org.openhab.core.thing.binding.BridgeHandler;
import org.openhab.core.thing.binding.ThingHandler;
import org.openhab.core.thing.binding.ThingHandlerService;
import org.openhab.core.types.Command;
import org.openhab.core.types.RefreshType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Dan Cunningham - Initial contribution
 */
@NonNullByDefault
public class QolsysIQPartitionHandler extends BaseBridgeHandler implements QolsysIQChildDiscoveryHandler {
    private final Logger logger = LoggerFactory.getLogger(QolsysIQPartitionHandler.class);
    private Map<Integer, Zone> zones = Collections.synchronizedMap(new HashMap<Integer, Zone>());
    private @Nullable QolsysIQChildDiscoveryService discoveryService;
    private int partitionId;

    public QolsysIQPartitionHandler(Bridge bridge) {
        super(bridge);
    }

    @Override
    public void initialize() {
        partitionId = getConfigAs(QolsysIQPartitionConfiguration.class).id;
        updateStatus(ThingStatus.UNKNOWN);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        Bridge bridge = getBridge();
        if (bridge != null) {
            BridgeHandler handler = bridge.getHandler();
            if (handler != null && handler instanceof QolsysIQPanelHandler) {
                QolsysIQPanelHandler panel = (QolsysIQPanelHandler) handler;
                if (command instanceof RefreshType) {
                    panel.sendAction(new InfoAction(InfoActionType.SUMMARY, ""));
                    return;
                }

                if (channelUID.getId().equals(QolsysIQBindingConstants.CHANNEL_PARTITION_COMMAND_ALARM)) {
                    panel.sendAction(new AlarmAction(AlarmActionType.valueOf(command.toString()), ""));
                    return;
                }

                ArmingActionType armingType = null;
                String code = null;

                if (channelUID.getId().equals(QolsysIQBindingConstants.CHANNEL_PARTITION_COMMAND_DISARM)) {
                    armingType = ArmingActionType.DISARM;
                    code = command.toString();
                } else if (channelUID.getId().equals(QolsysIQBindingConstants.CHANNEL_PARTITION_COMMAND_ARM)
                        || channelUID.getId().equals(QolsysIQBindingConstants.CHANNEL_PARTITION_STATUS)) {
                    String armingTypeName = command.toString();
                    if (armingTypeName.contains(":")) {
                        String[] split = armingTypeName.split(":");
                        armingTypeName = split[0];
                        code = split[1];
                    }
                    armingType = ArmingActionType.valueOf(armingTypeName);
                }

                if (armingType != null) {
                    panel.sendAction(new ArmingAction(armingType, "", partitionId(), code));
                } else {
                    logger.debug("Unknown arm command {} to channel {}", command, channelUID);
                }
            }

        }
    }

    @Override
    public void childHandlerInitialized(ThingHandler childHandler, Thing childThing) {
        if (childHandler instanceof QolsysIQZoneHandler) {
            QolsysIQZoneHandler handler = (QolsysIQZoneHandler) childHandler;
            Zone z = zones.get(handler.zoneId());
            if (z != null) {
                handler.updateZone(z);
            }
        }
    }

    @Override
    public Collection<Class<? extends ThingHandlerService>> getServices() {
        return Collections.singleton(QolsysIQChildDiscoveryService.class);
    }

    @Override
    public void setDiscoveryService(QolsysIQChildDiscoveryService service) {
        this.discoveryService = service;
    }

    @Override
    public void discoverChildDevices() {
        zones.forEach((k, z) -> {
            QolsysIQChildDiscoveryService discoveryService = this.discoveryService;
            if (discoveryService != null) {
                ThingUID bridgeUID = getThing().getUID();
                ThingUID thingUID = new ThingUID(QolsysIQBindingConstants.THING_TYPE_ZONE, bridgeUID, z.zoneId + "");
                discoveryService.discoverQolsysIQChildThing(thingUID, bridgeUID, String.valueOf(z.zoneId),
                        "Qolsys IQ Zone: " + z.name);
            }
        });
    }

    public int partitionId() {
        return partitionId;
    }

    public void alarmEvent(AlarmEvent event) {
        updatePartitionStatus(PartitionStatus.ALARM);
        updateState(QolsysIQBindingConstants.CHANNEL_PARTITION_ALARM_STATE, new StringType(event.alarmType.toString()));
    }

    public void armingEvent(ArmingEvent event) {
        updatePartitionStatus(event.armingType);
    }

    public void secureArmInfoEvent(SecureArmInfoEvent event) {
        setSecureArm(event.value);
    }

    public void updatePartition(Partition partition) {
        logger.debug("updatePartition {}", partition.partitionId);
        zones.clear();
        partition.zoneList.forEach(z -> {
            zones.put(z.zoneId, z);
            QolsysIQZoneHandler zoneHandler = zoneHandler(z.zoneId);
            if (zoneHandler != null) {
                zoneHandler.updateZone(z);
            }
        });
        discoverChildDevices();
        updatePartitionStatus(partition.status);
        setSecureArm(partition.secureArm);
        if (getThing().getStatus() != ThingStatus.ONLINE) {
            updateStatus(ThingStatus.ONLINE);
        }
    }

    private void updatePartitionStatus(PartitionStatus status) {
        updateState(QolsysIQBindingConstants.CHANNEL_PARTITION_STATUS, new StringType(status.toString()));
        if (status == PartitionStatus.DISARM) {
            updateState(QolsysIQBindingConstants.CHANNEL_PARTITION_ALARM_STATE,
                    new StringType(AlarmType.NONE.toString()));
        }
    }

    private void setSecureArm(Boolean secure) {
        Map<String, Object> props = new HashMap<String, Object>();
        props.put("secureArm", secure);
        getThing().getConfiguration().setProperties(props);
    }

    private void updateExitDelay() {
        // if exit delay is not null or > 0 start timer
        // other wise kill timer, set to 0;
    }

    private @Nullable QolsysIQZoneHandler zoneHandler(int zoneId) {
        for (Thing thing : getThing().getThings()) {
            ThingHandler handler = thing.getHandler();
            if (handler != null && handler instanceof QolsysIQZoneHandler) {
                if (((QolsysIQZoneHandler) handler).zoneId() == zoneId) {
                    return (QolsysIQZoneHandler) handler;
                }
            }
        }
        return null;
    }
}
