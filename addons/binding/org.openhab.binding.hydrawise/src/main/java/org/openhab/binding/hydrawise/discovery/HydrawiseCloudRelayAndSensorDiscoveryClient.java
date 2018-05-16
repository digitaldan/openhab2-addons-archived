package org.openhab.binding.hydrawise.discovery;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.smarthome.config.discovery.AbstractDiscoveryService;
import org.eclipse.smarthome.config.discovery.DiscoveryResult;
import org.eclipse.smarthome.config.discovery.DiscoveryResultBuilder;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.openhab.binding.hydrawise.HydrawiseBindingConstants;
import org.openhab.binding.hydrawise.api.HydrawiseCloudApiClient;
import org.openhab.binding.hydrawise.api.HydrawiseAuthenticationException;
import org.openhab.binding.hydrawise.api.HydrawiseConnectionException;
import org.openhab.binding.hydrawise.api.model.Relay;
import org.openhab.binding.hydrawise.api.model.Sensor;
import org.openhab.binding.hydrawise.api.model.StatusScheduleResponse;
import org.openhab.binding.hydrawise.config.HydrawiseControllerConfiguration;
import org.openhab.binding.hydrawise.handler.HydrawiseCloudControllerHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HydrawiseCloudRelayAndSensorDiscoveryClient extends AbstractDiscoveryService {
    private final Logger logger = LoggerFactory.getLogger(HydrawiseCloudRelayAndSensorDiscoveryClient.class);
    private HydrawiseCloudControllerHandler controllerHandler;

    public HydrawiseCloudRelayAndSensorDiscoveryClient(HydrawiseCloudControllerHandler controllerHandler)
            throws IllegalArgumentException {
        super(HydrawiseBindingConstants.SUPPORTED_RELAY_AND_SENSOR_THING_TYPES_UIDS, 30, true);
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

        HydrawiseCloudApiClient apiClient = controllerHandler.getApiClient();
        HydrawiseControllerConfiguration config = controllerHandler.getConfiguration();
        if (config == null || apiClient == null) {
            logger.warn("Cannot discover relays/sensors, controller not configured");
            return;
        }
        try {
            StatusScheduleResponse status = apiClient.getStatusSchedule(config.controllerId);
            for (Relay relay : status.getRelays()) {
                if (controllerHandler.getRelayThing(relay.getRelayId()) == null) {
                    ThingUID thingUID = new ThingUID(HydrawiseBindingConstants.HYDRAWISE_RELAY_THING_TYPE, bridgeUID,
                            relay.getRelay().toString());
                    Map<String, Object> properties = new HashMap<>(1);
                    properties.put("relayId", relay.getRelayId());
                    properties.put("name", relay.getName());
                    properties.put("number", relay.getRelay());
                    DiscoveryResult discoveryResult = DiscoveryResultBuilder.create(thingUID).withProperties(properties)
                            .withBridge(bridgeUID).withLabel("Hydrawise Relay: " + relay.getName()).build();
                    thingDiscovered(discoveryResult);
                }
            }

            for (Sensor sensor : status.getSensors()) {
                if (controllerHandler.getSensorThing(sensor.getInput()) == null) {
                    ThingUID thingUID = new ThingUID(HydrawiseBindingConstants.HYDRAWISE_SENSOR_THING_TYPE, bridgeUID,
                            sensor.getInput().toString());
                    Map<String, Object> properties = new HashMap<>(1);
                    properties.put("sensorId", sensor.getInput());
                    properties.put("name", sensor.getName());
                    DiscoveryResult discoveryResult = DiscoveryResultBuilder.create(thingUID).withProperties(properties)
                            .withBridge(bridgeUID).withLabel("Hydrawise Sensor: " + sensor.getName()).build();
                    thingDiscovered(discoveryResult);
                }
            }
        } catch (HydrawiseConnectionException | HydrawiseAuthenticationException e) {
            logger.warn("Could not connect to hydrawise account: {}", e.getMessage());
        }
    }

}
