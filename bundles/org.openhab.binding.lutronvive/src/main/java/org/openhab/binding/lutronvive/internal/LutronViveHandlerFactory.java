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

import static org.openhab.binding.lutronvive.internal.LutronViveBindingConstants.*;

import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingTypeUID;
import org.openhab.core.thing.binding.BaseThingHandlerFactory;
import org.openhab.core.thing.binding.ThingHandler;
import org.openhab.core.thing.binding.ThingHandlerFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

/**
 * The {@link LutronViveHandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author Dan Cunningham - Initial contribution
 */
@NonNullByDefault
@Component(configurationPid = "binding.lutronvive", service = ThingHandlerFactory.class)
public class LutronViveHandlerFactory extends BaseThingHandlerFactory {

    private static final Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = Set.of(THING_TYPE_HUB, THING_TYPE_AREA);

    private final HttpClient httpClient;

    @Activate
    public LutronViveHandlerFactory() {
        httpClient = new HttpClient(new SslContextFactory.Client(true));
        try {
            httpClient.start();
        } catch (Exception e) {
            throw new RuntimeException("Could not start HttpClient", e);
        }
    }

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID);
    }

    @Override
    protected @Nullable ThingHandler createHandler(Thing thing) {
        ThingTypeUID thingTypeUID = thing.getThingTypeUID();

        if (THING_TYPE_HUB.equals(thingTypeUID)) {
            return new LutronViveHubHandler((Bridge) thing, httpClient);
        }

        if (THING_TYPE_AREA.equals(thingTypeUID)) {
            return new LutronViveAreaHandler(thing);
        }

        return null;
    }
}
