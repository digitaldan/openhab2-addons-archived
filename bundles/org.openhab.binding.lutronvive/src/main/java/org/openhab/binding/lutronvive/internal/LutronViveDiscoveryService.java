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
package org.openhab.binding.lutronvive.internal;

import java.util.Set;

import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.lutronvive.internal.api.response.Area;
import org.openhab.core.config.discovery.AbstractDiscoveryService;
import org.openhab.core.config.discovery.DiscoveryResult;
import org.openhab.core.config.discovery.DiscoveryResultBuilder;
import org.openhab.core.config.discovery.DiscoveryService;
import org.openhab.core.thing.ThingTypeUID;
import org.openhab.core.thing.ThingUID;
import org.openhab.core.thing.binding.ThingHandler;
import org.openhab.core.thing.binding.ThingHandlerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LutronViveDiscoveryService extends AbstractDiscoveryService
        implements DiscoveryService, ThingHandlerService {
    private final Logger logger = LoggerFactory.getLogger(LutronViveDiscoveryService.class);
    private static final Set<ThingTypeUID> SUPPORTED_DISCOVERY_THING_TYPES_UIDS = Set
            .of(LutronViveBindingConstants.THING_TYPE_HUB, LutronViveBindingConstants.THING_TYPE_AREA);
    private @Nullable LutronViveHubHandler accountHandler;

    public LutronViveDiscoveryService() {
        super(SUPPORTED_DISCOVERY_THING_TYPES_UIDS, 0, false);
    }

    @Override
    public Set<ThingTypeUID> getSupportedThingTypes() {
        return SUPPORTED_DISCOVERY_THING_TYPES_UIDS;
    }

    @Override
    public void startScan() {
    }

    public void discoverAreas(Area[] areas) {

        for (Area area : areas) {
            ThingTypeUID thingTypeUID = new ThingTypeUID(LutronViveBindingConstants.BINDING_ID, "area");
            String num = area.href.split("/")[2];
            ThingUID thingUID = new ThingUID(thingTypeUID, accountHandler.getThing().getUID(), num);
            logger.debug("DISCO {}", thingUID.getAsString());
            DiscoveryResult result = DiscoveryResultBuilder.create(thingUID).withLabel("Lutron Vive " + area.name)
                    .withProperty("id", num).withRepresentationProperty("id")
                    .withBridge(accountHandler.getThing().getUID()).build();
            thingDiscovered(result);
        }
    }

    @Override
    public void startBackgroundDiscovery() {
        startScan();
    }

    @Override
    public void setThingHandler(ThingHandler handler) {
        if (handler instanceof LutronViveHubHandler) {
            accountHandler = (LutronViveHubHandler) handler;
            accountHandler.setDiscoveryService(this);
        }
    }

    @Override
    public @Nullable ThingHandler getThingHandler() {
        return accountHandler;
    }

    @Override
    public void activate() {
        super.activate(null);
    }

    @Override
    public void deactivate() {
        super.deactivate();
    }
}
