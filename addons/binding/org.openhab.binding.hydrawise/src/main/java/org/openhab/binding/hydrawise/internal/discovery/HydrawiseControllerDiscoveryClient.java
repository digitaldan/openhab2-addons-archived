package org.openhab.binding.hydrawise.internal.discovery;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.smarthome.config.discovery.AbstractDiscoveryService;
import org.eclipse.smarthome.config.discovery.DiscoveryResult;
import org.eclipse.smarthome.config.discovery.DiscoveryResultBuilder;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.openhab.binding.hydrawise.HydrawiseBindingConstants;
import org.openhab.binding.hydrawise.internal.HydrawiseAccountHandler;
import org.openhab.binding.hydrawise.internal.api.HydrawiseApiClient;
import org.openhab.binding.hydrawise.internal.api.HydrawiseAuthenticationException;
import org.openhab.binding.hydrawise.internal.api.HydrawiseConnectionException;
import org.openhab.binding.hydrawise.internal.model.Controller;
import org.openhab.binding.hydrawise.internal.model.CustomerDetailsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HydrawiseControllerDiscoveryClient extends AbstractDiscoveryService {
    private final Logger logger = LoggerFactory.getLogger(HydrawiseControllerDiscoveryClient.class);
    private HydrawiseAccountHandler accountHandler;
    private static final int TIMEOUT = 30;

    public HydrawiseControllerDiscoveryClient(HydrawiseAccountHandler accountHandler) throws IllegalArgumentException {
        super(Collections.singleton(HydrawiseBindingConstants.HYDRAWISE_CONTROLLER_THING_TYPE), TIMEOUT);
        this.accountHandler = accountHandler;
        logger.trace("init");
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
        logger.trace("Start scan");
        ThingUID bridgeUID = accountHandler.getThing().getUID();
        HydrawiseApiClient apiClient = accountHandler.getApiClient();
        if (apiClient == null) {
            logger.trace("bridge is not yet initalized, skipping scan");
            return;
        }
        try {
            CustomerDetailsResponse customerDetails = apiClient.getCustomerDetails();
            for (Controller controller : customerDetails.getControllers()) {
                if (accountHandler.getControllerThing(controller.getControllerId()) == null) {
                    ThingUID thingUID = new ThingUID(HydrawiseBindingConstants.HYDRAWISE_CONTROLLER_THING_TYPE,
                            bridgeUID, controller.getControllerId().toString());
                    Map<String, Object> properties = new HashMap<>(1);
                    properties.put("controllerId", controller.getControllerId());
                    properties.put("name", controller.getName());
                    properties.put("address", controller.getAddress());
                    DiscoveryResult discoveryResult = DiscoveryResultBuilder.create(thingUID).withProperties(properties)
                            .withBridge(bridgeUID).withLabel("Hydrawise " + controller.getDescription()).build();
                    thingDiscovered(discoveryResult);
                }
            }
        } catch (HydrawiseConnectionException | HydrawiseAuthenticationException e) {
            logger.warn("Could not connect to hydrawise account: {}", e.getMessage());
        }
    }

}
