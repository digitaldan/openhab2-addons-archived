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
package org.openhab.binding.upnpcontrol.internal.actions;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.jupnp.UpnpService;
import org.jupnp.model.message.header.UDNHeader;
import org.jupnp.model.types.UDN;
import org.openhab.binding.upnpcontrol.internal.handler.UpnpHandler;
import org.openhab.core.automation.annotation.ActionOutput;
import org.openhab.core.automation.annotation.ActionOutputs;
import org.openhab.core.automation.annotation.RuleAction;
import org.openhab.core.thing.binding.ThingActions;
import org.openhab.core.thing.binding.ThingActionsScope;
import org.openhab.core.thing.binding.ThingHandler;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Dan Cunningham - Initial contribution
 */
@NonNullByDefault
@Component(scope = ServiceScope.PROTOTYPE, service = UpnpDeviceActions.class)
@ThingActionsScope(name = "upnpcontrol")
public class UpnpDeviceActions implements ThingActions {
    private final Logger logger = LoggerFactory.getLogger(UpnpDeviceActions.class);

    private final UpnpService upnpService;
    private @Nullable UpnpHandler handler;

    @Activate
    public UpnpDeviceActions(@Reference UpnpService upnpService) {
        this.upnpService = upnpService;
    }

    @Override
    public void setThingHandler(@Nullable ThingHandler handler) {
        this.handler = (UpnpHandler) handler;
    }

    @Override
    public @Nullable ThingHandler getThingHandler() {
        return handler;
    }

    @RuleAction(label = "Send Discovery Packet", description = "Sends a discovery packet to the device")
    public @Nullable @ActionOutputs({
            @ActionOutput(name = "result", label = "Result from action", type = "java.lang.String") }) String reconnectNode() {
        UpnpHandler handler = this.handler;
        if (handler != null) {
            UDN udn = new UDN(handler.getUDN());
            logger.debug("Sending discovery packet to device with UDN: {}", udn);
            upnpService.getControlPoint().search(new UDNHeader(udn));
            return "ok";
        }
        return "error: handler not found";
    }
}
