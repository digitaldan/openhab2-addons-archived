package org.openhab.binding.qolsysiq.internal;

import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.qolsysiq.internal.handler.QolsysIQDiscoveryHandler;
import org.openhab.core.config.discovery.AbstractDiscoveryService;
import org.openhab.core.config.discovery.DiscoveryResult;
import org.openhab.core.config.discovery.DiscoveryResultBuilder;
import org.openhab.core.config.discovery.DiscoveryService;
import org.openhab.core.thing.ThingTypeUID;
import org.openhab.core.thing.ThingUID;
import org.openhab.core.thing.binding.ThingHandler;
import org.openhab.core.thing.binding.ThingHandlerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Dan Cunningham
 *
 */
@NonNullByDefault
public class QolsysIQDiscoveryService extends AbstractDiscoveryService
        implements DiscoveryService, ThingHandlerService {
    private final Logger logger = LoggerFactory.getLogger(QolsysIQDiscoveryService.class);

    private static final Set<ThingTypeUID> SUPPORTED_DISCOVERY_THING_TYPES_UIDS = Set
            .of(QolsysIQBindingConstants.THING_TYPE_PARTITION, QolsysIQBindingConstants.THING_TYPE_ZONE);

    private @Nullable ThingHandler thingHandler;

    public QolsysIQDiscoveryService() throws IllegalArgumentException {
        super(SUPPORTED_DISCOVERY_THING_TYPES_UIDS, 0, false);
    }

    @Override
    public void setThingHandler(ThingHandler handler) {
        logger.debug("setThingHandler {}", handler.getThing());
        if (handler instanceof QolsysIQDiscoveryHandler) {
            ((QolsysIQDiscoveryHandler) handler).setDiscoveryService(this);
            this.thingHandler = handler;
        }
    }

    @Override
    public @Nullable ThingHandler getThingHandler() {
        return thingHandler;
    }

    @Override
    protected void startScan() {

    }

    @Override
    public void activate() {
        super.activate(null);
    }

    @Override
    public void deactivate() {
        super.deactivate();
    }

    public void discoverQolsysIQThing(ThingUID thingUID, ThingUID bridgeUID, String id, String label) {
        DiscoveryResult result = DiscoveryResultBuilder.create(thingUID).withLabel(label).withProperty("id", id)
                .withRepresentationProperty("id").withBridge(bridgeUID).build();
        thingDiscovered(result);
    }
    // public void discoverPartitions(List<Partition> paritions, Bridge bridge) {
    // paritions.forEach(partition -> {
    // ThingUID thingUID = new ThingUID(QolsysIQBindingConstants.THING_TYPE_PARTITION, bridge.getUID(),
    // partition.partitionId + "");
    // DiscoveryResult result = DiscoveryResultBuilder.create(thingUID)
    // .withLabel("Qolsys IQ Partition: " + partition.name)
    // .withProperty(Thing.PROPERTY_SERIAL_NUMBER, partition.partitionId)
    // .withRepresentationProperty(Thing.PROPERTY_SERIAL_NUMBER).withBridge(bridge.getUID()).build();
    // thingDiscovered(result);
    // });
    //
    // }
    //
    // public void discoverZones(List<Zone> zones, Bridge bridge) {
    // zones.forEach(zone -> {
    // ThingUID thingUID = new ThingUID(QolsysIQBindingConstants.THING_TYPE_ZONE, bridge.getUID(),
    // zone.partitionId + "");
    // DiscoveryResult result = DiscoveryResultBuilder.create(thingUID).withLabel("Qolsys IQ Zone: " + zone.name)
    // .withProperty(Thing.PROPERTY_SERIAL_NUMBER, zone.zoneId)
    // .withRepresentationProperty(Thing.PROPERTY_SERIAL_NUMBER).withBridge(bridge.getUID()).build();
    // thingDiscovered(result);
    // });
    // }

}
