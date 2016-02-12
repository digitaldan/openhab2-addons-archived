package org.openhab.binding.hueemulator.internal;

import java.net.InetAddress;

import org.openhab.binding.hueemulator.HueEmulatorBindingConstants;
import org.openhab.binding.hueemulator.upnp.UpnpDiscoveryServer;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HueEmulatorActivator implements BundleActivator {

    private final Logger logger = LoggerFactory.getLogger(HueEmulatorActivator.class);

    private static BundleContext context;
    private UpnpDiscoveryServer disco;

    /**
     * Called whenever the OSGi framework starts our bundle
     */
    @Override
    public void start(BundleContext bc) throws Exception {
        context = bc;
        disco = new UpnpDiscoveryServer("http://" + InetAddress.getLocalHost().getHostAddress().toString() + ":"
                + System.getProperty("org.osgi.service.http.port") + HueEmulatorBindingConstants.REST_PATH
                + "/discovery.xml");
        disco.start();
        logger.debug("HueEmulator has been started.");

    }

    /**
     * Called whenever the OSGi framework stops our bundle
     */
    @Override
    public void stop(BundleContext bc) throws Exception {
        context = null;
        disco.shutdown();
        logger.debug("HueEmulator has been stopped.");
    }

    /**
     * Returns the bundle context of this bundle
     *
     * @return the bundle context
     */
    public static BundleContext getContext() {
        return context;
    }
}
