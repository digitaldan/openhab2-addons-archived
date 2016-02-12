package org.openhab.binding.hueemulator.internal;

import java.util.Dictionary;
import java.util.Hashtable;

import org.eclipse.smarthome.core.events.EventPublisher;
import org.eclipse.smarthome.core.items.ItemRegistry;
import org.openhab.binding.hueemulator.HueEmulatorBindingConstants;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HueEmulatorHttpService {
    private static final Logger logger = LoggerFactory.getLogger(HueEmulatorHttpService.class);
    protected HttpService httpService;
    private ItemRegistry itemRegistry;
    private EventPublisher eventPublisher;

    protected void setItemRegistry(ItemRegistry itemRegistry) {
        this.itemRegistry = itemRegistry;
    }

    protected void unsetItemRegistry(ItemRegistry itemRegistry) {
        this.itemRegistry = null;
    }

    protected void setEventPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    protected void unsetEventPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = null;
    }

    protected void activate(ComponentContext componentContext) {
        try {
            Dictionary<String, String> servletParams = new Hashtable<String, String>();
            httpService.registerServlet(HueEmulatorBindingConstants.REST_PATH,
                    new HueEmulatorHttpServlet(itemRegistry, eventPublisher), servletParams, createHttpContext());
            logger.info("HueEmulator Running at " + HueEmulatorBindingConstants.REST_PATH);
        } catch (Exception e) {
            logger.error("Could not start HueEmulator Service", e);
        }
    }

    protected void deactivate(ComponentContext componentContext) {
        httpService.unregister(HueEmulatorBindingConstants.REST_PATH);
    }

    protected void setHttpService(HttpService httpService) {
        this.httpService = httpService;
    }

    protected void unsetHttpService(HttpService httpService) {
        this.httpService = null;
    }

    protected HttpContext createHttpContext() {
        HttpContext defaultHttpContext = httpService.createDefaultHttpContext();
        return defaultHttpContext;
    }

}
