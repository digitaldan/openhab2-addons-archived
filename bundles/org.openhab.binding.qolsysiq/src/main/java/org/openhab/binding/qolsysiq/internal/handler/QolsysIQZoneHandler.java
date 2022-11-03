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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.qolsysiq.internal.QolsysIQBindingConstants;
import org.openhab.binding.qolsysiq.internal.client.dto.event.ZoneActiveEvent;
import org.openhab.binding.qolsysiq.internal.client.dto.event.ZoneUpdateEvent;
import org.openhab.binding.qolsysiq.internal.client.dto.model.Zone;
import org.openhab.binding.qolsysiq.internal.client.dto.model.ZoneStatus;
import org.openhab.binding.qolsysiq.internal.config.QolsysIQZoneConfiguration;
import org.openhab.core.library.types.OpenClosedType;
import org.openhab.core.library.types.StringType;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.binding.BaseThingHandler;
import org.openhab.core.types.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Dan Cunningham - Initial contribution
 */
@NonNullByDefault
public class QolsysIQZoneHandler extends BaseThingHandler {
    private final Logger logger = LoggerFactory.getLogger(QolsysIQZoneHandler.class);

    private int zoneId;

    public QolsysIQZoneHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void initialize() {
        zoneId = getConfigAs(QolsysIQZoneConfiguration.class).id;
        updateStatus(ThingStatus.UNKNOWN);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        // No commands
    }

    public int zoneId() {
        return zoneId;
    }

    public void updateZone(Zone zone) {
        logger.debug("updateZone {}", zone.zoneId);
        updateState(QolsysIQBindingConstants.CHANNEL_ZONE_STATE, new StringType(zone.state.toString()));
        updateState(QolsysIQBindingConstants.CHANNEL_ZONE_STATUS,
                zone.status == ZoneStatus.OPEN ? OpenClosedType.OPEN : OpenClosedType.CLOSED);
        Map<String, Object> props = new HashMap<String, Object>();
        props.put("type", zone.type);
        props.put("name", zone.name);
        props.put("id", zone.id);
        props.put("zonePhysicalType", zone.zonePhysicalType);
        props.put("zoneAlarmType", zone.zoneAlarmType);
        props.put("zoneType", zone.zoneType.toString());
        props.put("partitionId", zone.partitionId);
        getThing().getConfiguration().setProperties(props);
        if (getThing().getStatus() != ThingStatus.ONLINE) {
            updateStatus(ThingStatus.ONLINE);
        }
    }

    public void zoneActiveEvent(ZoneActiveEvent event) {
        if (event.zone.zoneId == zoneId()) {
            updateState(QolsysIQBindingConstants.CHANNEL_ZONE_STATE, new StringType(event.zone.status.toString()));
        }
    }

    public void zoneUpdateEvent(ZoneUpdateEvent event) {
        if (event.zone.zoneId == zoneId()) {
            updateZone(event.zone);
        }
    }
}
