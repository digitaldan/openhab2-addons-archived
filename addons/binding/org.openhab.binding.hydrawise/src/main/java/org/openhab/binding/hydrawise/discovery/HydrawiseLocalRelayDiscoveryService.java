package org.openhab.binding.hydrawise.discovery;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.smarthome.config.discovery.AbstractDiscoveryService;
import org.eclipse.smarthome.config.discovery.DiscoveryResult;
import org.eclipse.smarthome.config.discovery.DiscoveryResultBuilder;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.openhab.binding.hydrawise.HydrawiseBindingConstants;
import org.openhab.binding.hydrawise.api.HydrawiseAuthenticationException;
import org.openhab.binding.hydrawise.api.HydrawiseConnectionException;
import org.openhab.binding.hydrawise.api.HydrawiseLocalApiClient;
import org.openhab.binding.hydrawise.api.model.LocalScheduleResponse;
import org.openhab.binding.hydrawise.api.model.Relay;
import org.openhab.binding.hydrawise.handler.HydrawiseLocalControllerHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HydrawiseLocalRelayDiscoveryService extends AbstractDiscoveryService {
    private final Logger logger = LoggerFactory.getLogger(HydrawiseLocalRelayDiscoveryService.class);
    private HydrawiseLocalControllerHandler controllerHandler;

    public HydrawiseLocalRelayDiscoveryService(HydrawiseLocalControllerHandler controllerHandler)
            throws IllegalArgumentException {
        super(Stream.of(HydrawiseBindingConstants.HYDRAWISE_RELAY_THING_TYPE).collect(Collectors.toSet()), 30, true);
        this.controllerHandler = controllerHandler;
    }

    @Override
    protected void startScan() {
        discover();
    }

    @Override
    protected void startBackgroundDiscovery() {
        discover();
    }

    protected void discover() {

        ThingUID bridgeUID = controllerHandler.getThing().getUID();

        HydrawiseLocalApiClient apiClient = controllerHandler.getApiClient();
        if (apiClient == null) {
            logger.warn("Cannot discover relays, controller not configured");
            return;
        }
        try {
            LocalScheduleResponse status = apiClient.getLocalSchedule();
            for (Relay relay : status.getRelays()) {
                if (controllerHandler.getRelayThing(relay.getRelayId()) == null) {
                    ThingUID thingUID = new ThingUID(HydrawiseBindingConstants.HYDRAWISE_RELAY_THING_TYPE, bridgeUID,
                            relay.getRelay().toString());
                    Map<String, Object> properties = new HashMap<>(1);
                    properties.put("relayId", relay.getRelayId());
                    properties.put("name", relay.getName());
                    properties.put("number", relay.getRelay());
                    DiscoveryResult discoveryResult = DiscoveryResultBuilder.create(thingUID).withProperties(properties)
                            .withBridge(bridgeUID).withLabel("Hydrawise Local Relay: " + relay.getName()).build();
                    thingDiscovered(discoveryResult);
                }
            }
        } catch (HydrawiseConnectionException | HydrawiseAuthenticationException e) {
            logger.warn("Could not connect to hydrawise account: {}", e.getMessage());
        }
    }

}
