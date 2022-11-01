package org.openhab.binding.qolsysiq.internal.handler;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.qolsysiq.internal.QolsysIQDiscoveryService;

/**
 *
 * @author Dan Cunningham
 *
 */
@NonNullByDefault
public interface QolsysIQDiscoveryHandler {
    public void setDiscoveryService(QolsysIQDiscoveryService service);
}
