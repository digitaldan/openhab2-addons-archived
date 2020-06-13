/**
 * Copyright (c) 2010-2020 Contributors to the openHAB project
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
package org.openhab.binding.hydrawise.internal.discovery;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.smarthome.config.discovery.AbstractDiscoveryService;
import org.eclipse.smarthome.config.discovery.DiscoveryResultBuilder;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.openhab.binding.hydrawise.internal.HydrawiseAccountHandler;
import org.openhab.binding.hydrawise.internal.HydrawiseBindingConstants;
import org.openhab.binding.hydrawise.internal.HydrawiseControllerListener;
import org.openhab.binding.hydrawise.internal.api.graphql.schema.Controller;
import org.openhab.binding.hydrawise.internal.api.graphql.schema.Customer;

/**
 *
 * @author Dan Cunningham
 *
 */
@NonNullByDefault
public class HydrawiseCloudControllerDiscoveryService extends AbstractDiscoveryService
        implements HydrawiseControllerListener {

    private static final int TIMEOUT = 5;
    HydrawiseAccountHandler bridge;

    public HydrawiseCloudControllerDiscoveryService(HydrawiseAccountHandler bridge) {
        super(Collections.singleton(HydrawiseBindingConstants.THING_TYPE_CONTROLLER), TIMEOUT, true);
        bridge.addControllerListeners(this);
        this.bridge = bridge;
    }

    @Override
    protected void startScan() {
        // todo get last data
        Customer data = bridge.lastData();
        if (data != null) {
            data.controllers.forEach(controller -> addDiscoveryResults(controller));
        }
    }

    @Override
    public void deactivate() {
        removeOlderResults(new Date().getTime(), bridge.getThing().getUID());
    }

    @Override
    protected synchronized void stopScan() {
        super.stopScan();
        removeOlderResults(getTimestampOfLastScan(), bridge.getThing().getUID());
    }

    private void addDiscoveryResults(Controller controller) {
        String label = String.format("Hydrawise Controller %s", controller.name);
        int id = controller.id;
        ThingUID bridgeUID = bridge.getThing().getUID();
        ThingUID thingUID = new ThingUID(HydrawiseBindingConstants.THING_TYPE_CONTROLLER, bridgeUID,
                String.valueOf(id));

        thingDiscovered(DiscoveryResultBuilder.create(thingUID).withLabel(label).withBridge(bridgeUID)
                .withProperty(HydrawiseBindingConstants.CONFIG_CONTROLLER_ID, id).build());

    }

    @Override
    public void onData(@NonNull List<@NonNull Controller> controllers) {
        controllers.forEach(controller -> addDiscoveryResults(controller));
    }
}
