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
package org.openhab.binding.qolsysiq.internal.client;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.qolsysiq.internal.client.dto.events.AlarmEvent;
import org.openhab.binding.qolsysiq.internal.client.dto.events.ArmingEvent;
import org.openhab.binding.qolsysiq.internal.client.dto.events.SecureArmInfoEvent;
import org.openhab.binding.qolsysiq.internal.client.dto.events.SummaryInfoEvent;
import org.openhab.binding.qolsysiq.internal.client.dto.events.ZoneActiveEvent;
import org.openhab.binding.qolsysiq.internal.client.dto.events.ZoneUpdateEvent;

/**
 *
 * @author Dan Cunningham - Initial contribution
 */
@NonNullByDefault
public interface QolsysIQClientListener {

    void disconnected(Exception reason);

    void alarmEvent(AlarmEvent event);

    void armingEvent(ArmingEvent event);

    void summaryInfoEvent(SummaryInfoEvent event);

    void secureArmInfoEvent(SecureArmInfoEvent event);

    void zoneActiveEvent(ZoneActiveEvent event);

    void zoneUpdateEvent(ZoneUpdateEvent event);
}
