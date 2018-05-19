/**
 * Copyright (c) 2014,2018 by the respective copyright holders.
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.hydrawise.internal;

import static org.openhab.binding.hydrawise.HydrawiseBindingConstants.*;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.config.discovery.DiscoveryService;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandlerFactory;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.eclipse.smarthome.core.thing.binding.ThingHandlerFactory;
import org.openhab.binding.hydrawise.discovery.HydrawiseCloudRelayAndSensorDiscoveryService;
import org.openhab.binding.hydrawise.discovery.HydrawiseControllerDiscoveryService;
import org.openhab.binding.hydrawise.discovery.HydrawiseLocalRelayDiscoveryService;
import org.openhab.binding.hydrawise.handler.HydrawiseCloudAccountHandler;
import org.openhab.binding.hydrawise.handler.HydrawiseCloudControllerHandler;
import org.openhab.binding.hydrawise.handler.HydrawiseLocalControllerHandler;
import org.openhab.binding.hydrawise.handler.HydrawiseRelayHandler;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Component;

/**
 * The {@link HydrawiseHandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author Dan Cunningham - Initial contribution
 */
@Component(service = ThingHandlerFactory.class, immediate = true, configurationPid = "binding.hydrawise")
@NonNullByDefault
public class HydrawiseHandlerFactory extends BaseThingHandlerFactory {

    private final Map<ThingUID, @Nullable ServiceRegistration<?>> discoveryServiceRegistrations = new HashMap<>();

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID);
    }

    @Override
    protected @Nullable ThingHandler createHandler(Thing thing) {
        ThingTypeUID thingTypeUID = thing.getThingTypeUID();

        if (HYDRAWISE_ACCOUNT_THING_TYPE.equals(thingTypeUID)) {
            HydrawiseCloudAccountHandler accountHandler = new HydrawiseCloudAccountHandler((Bridge) thing);
            HydrawiseControllerDiscoveryService discovery = new HydrawiseControllerDiscoveryService(accountHandler);
            this.discoveryServiceRegistrations.put(accountHandler.getThing().getUID(), bundleContext
                    .registerService(DiscoveryService.class.getName(), discovery, new Hashtable<String, Object>()));
            return accountHandler;
        }
        if (HYDRAWISE_CONTROLLER_THING_TYPE.equals(thingTypeUID)) {
            HydrawiseCloudControllerHandler controllerHandler = new HydrawiseCloudControllerHandler((Bridge) thing);
            HydrawiseCloudRelayAndSensorDiscoveryService discovery = new HydrawiseCloudRelayAndSensorDiscoveryService(
                    controllerHandler);
            this.discoveryServiceRegistrations.put(controllerHandler.getThing().getUID(), bundleContext
                    .registerService(DiscoveryService.class.getName(), discovery, new Hashtable<String, Object>()));
            return controllerHandler;
        }

        if (HYDRAWISE_LOCAL_CONTROLLER_THING_TYPE.equals(thingTypeUID)) {
            HydrawiseLocalControllerHandler controllerHandler = new HydrawiseLocalControllerHandler((Bridge) thing);
            HydrawiseLocalRelayDiscoveryService discovery = new HydrawiseLocalRelayDiscoveryService(controllerHandler);
            this.discoveryServiceRegistrations.put(controllerHandler.getThing().getUID(), bundleContext
                    .registerService(DiscoveryService.class.getName(), discovery, new Hashtable<String, Object>()));
            return controllerHandler;
        }

        if ((HYDRAWISE_RELAY_THING_TYPE).equals(thingTypeUID)) {
            return new HydrawiseRelayHandler(thing);
        }
        if ((HYDRAWISE_SENSOR_THING_TYPE).equals(thingTypeUID)) {
            // return new HydrawiseSensorHandler(thing);
        }

        return null;
    }

    @Override
    protected void removeHandler(ThingHandler thingHandler) {
        if (thingHandler instanceof HydrawiseCloudAccountHandler || thingHandler instanceof HydrawiseCloudControllerHandler) {
            unregisterDeviceDiscoveryService(thingHandler.getThing().getUID());
        }

    }

    private synchronized void unregisterDeviceDiscoveryService(ThingUID thingUID) {
        ServiceRegistration<?> serviceRegistration = this.discoveryServiceRegistrations.get(thingUID);
        if (serviceRegistration != null) {
            serviceRegistration.unregister();
            discoveryServiceRegistrations.remove(thingUID);
        }
    }
}
