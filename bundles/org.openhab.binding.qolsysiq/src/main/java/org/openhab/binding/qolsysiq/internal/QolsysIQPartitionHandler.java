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
package org.openhab.binding.qolsysiq.internal;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.qolsysiq.internal.client.dto.actions.AlarmAction;
import org.openhab.binding.qolsysiq.internal.client.dto.actions.AlarmActionType;
import org.openhab.binding.qolsysiq.internal.client.dto.actions.ArmingAction;
import org.openhab.binding.qolsysiq.internal.client.dto.actions.ArmingActionType;
import org.openhab.binding.qolsysiq.internal.client.dto.actions.InfoAction;
import org.openhab.binding.qolsysiq.internal.client.dto.actions.InfoActionType;
import org.openhab.binding.qolsysiq.internal.client.dto.events.AlarmEvent;
import org.openhab.binding.qolsysiq.internal.client.dto.events.ArmingEvent;
import org.openhab.binding.qolsysiq.internal.client.dto.events.SecureArmInfoEvent;
import org.openhab.binding.qolsysiq.internal.client.dto.models.AlarmType;
import org.openhab.binding.qolsysiq.internal.client.dto.models.Partition;
import org.openhab.binding.qolsysiq.internal.client.dto.models.PartitionStatus;
import org.openhab.core.library.types.StringType;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.binding.BaseBridgeHandler;
import org.openhab.core.thing.binding.BridgeHandler;
import org.openhab.core.types.Command;
import org.openhab.core.types.RefreshType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Dan Cunningham - Initial contribution
 */
@NonNullByDefault
public class QolsysIQPartitionHandler extends BaseBridgeHandler {
    private final Logger logger = LoggerFactory.getLogger(QolsysIQPartitionHandler.class);

    private int partitionId;

    public QolsysIQPartitionHandler(Bridge bridge) {
        super(bridge);
    }

    @Override
    public void initialize() {
        partitionId = getConfigAs(QolsysIQPartitionConfiguration.class).partitionId;
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
                } else if (channelUID.getId().equals(QolsysIQBindingConstants.CHANNEL_PARTITION_COMMAND_ARM)) {
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
                    logger.debug("Unknown arm command {}", command);
                }
            }

        }
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
        updatePartitionStatus(partition.status);
        setSecureArm(partition.secureArm);
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
}
