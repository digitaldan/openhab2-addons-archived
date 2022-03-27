package org.openhab.binding.lutronvive.internal;

import org.openhab.binding.lutronvive.internal.api.request.ExecuteCommand;
import org.openhab.binding.lutronvive.internal.api.response.AreaDetail;
import org.openhab.binding.lutronvive.internal.api.response.AreaStatus;
import org.openhab.binding.lutronvive.internal.api.response.AreaZoneTypeGroup;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.library.types.PercentType;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.binding.BaseThingHandler;
import org.openhab.core.thing.binding.BridgeHandler;
import org.openhab.core.types.Command;
import org.openhab.core.types.RefreshType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LutronViveAreaHandler extends BaseThingHandler {
    private final Logger logger = LoggerFactory.getLogger(LutronViveAreaHandler.class);
    private AreaStatus areaStatus;
    private AreaDetail detail;
    private String id;

    public LutronViveAreaHandler(Thing thing) {
        super(thing);
        id = getConfigAs(LutronViveAreaConfiguration.class).id;
        areaStatus = new AreaStatus();
    }

    @Override
    public void initialize() {
        updateStatus(ThingStatus.UNKNOWN);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (command instanceof RefreshType) {
            updateState();
            return;
        }
        Bridge bridge = getBridge();
        logger.debug("handleCommand {} {} {}", command, bridge, detail);
        if (bridge != null && detail != null) {
            BridgeHandler handler = bridge.getHandler();
            if (handler != null) {
                int level = 0;
                AreaZoneTypeGroup ztg = detail.area.zoneTypeGroups.stream().filter(x -> "Dimmed".equals(x.type))
                        .findFirst().orElse(null);
                logger.debug("handleCommand ZoneTypeGroup {} ", ztg);
                if (ztg == null) {
                    return;
                }
                if (command instanceof OnOffType) {
                    level = command == OnOffType.ON ? 100 : 0;
                } else if (command instanceof PercentType) {
                    level = ((PercentType) command).intValue();
                } else {
                    logger.debug("Command {] not supported", command);
                    return;
                }
                ExecuteCommand exe = new ExecuteCommand(ztg.href + "/commandprocessor",
                        ExecuteCommand.CommandType.GOTODIMMEDLEVEL, level);
                ((LutronViveHubHandler) handler).sendCommand(exe);
            }
        }
    }

    public String getId() {
        return id;
    }

    protected void updateState() {
        if (areaStatus.level != null) {
            updateState("dimmer", new PercentType(areaStatus.level));
            updateState("switch", areaStatus.level > 0 ? OnOffType.ON : OnOffType.OFF);
        }
        if (areaStatus.occupancyStatus != null) {
            updateState("occupancy", "Occupied".equals(areaStatus.occupancyStatus) ? OnOffType.ON : OnOffType.OFF);
        }
    }

    public void updateAreaStatus(AreaStatus status) {
        logger.debug("handleUpdate {} level: {} occupancyStatus: {} ", status.href, status.level,
                status.occupancyStatus);
        if (status.level != null) {
            this.areaStatus.level = status.level;
        }
        if (status.occupancyStatus != null) {
            this.areaStatus.occupancyStatus = status.occupancyStatus;
        }
        updateStatus(ThingStatus.ONLINE);
        updateState();
    }

    public void updateAreaDetail(AreaDetail detail) {
        this.detail = detail;
    }
}
