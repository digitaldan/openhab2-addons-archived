package org.openhab.binding.hueemulator.internal;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.smarthome.core.events.EventPublisher;
import org.eclipse.smarthome.core.items.Item;
import org.eclipse.smarthome.core.items.ItemNotFoundException;
import org.eclipse.smarthome.core.items.ItemRegistry;
import org.eclipse.smarthome.core.items.events.ItemEventFactory;
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.types.State;
import org.openhab.binding.hueemulator.HueEmulatorBindingConstants;
import org.openhab.binding.hueemulator.api.HueCreateUser;
import org.openhab.binding.hueemulator.api.HueDataStore;
import org.openhab.binding.hueemulator.api.HueDevice;
import org.openhab.binding.hueemulator.api.HueState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Emulates A Hue compatible HTTP API server
 *
 * @author Dan Cunningham
 *
 */
@SuppressWarnings("serial")
public class HueEmulatorHttpServlet extends HttpServlet {
    private Logger logger = LoggerFactory.getLogger(HueEmulatorHttpServlet.class);

    public final static String APPLICATION_XML = "application/xml";
    public final static String APPLICATION_JSON = "application/json";
    public final static String CHARSET = "utf-8";

    private static String NEW_CLIENT_RESP = "[{\"success\":{\"username\": \"%s\"}}]";
    private static String STATE_RESP = "[{\"success\":{\"/lights/%s/state/on\":%s}}]";

    private Pattern rootPattern = Pattern.compile("/(.*)");
    private Pattern lightsPattern = Pattern.compile("/(.*)/lights");
    private Pattern lightPattern = Pattern.compile("/(.*)/lights/(.*)");
    private Pattern lightStatePattern = Pattern.compile("/(.*)/lights/(.*)/state");

    private ItemRegistry itemRegistry;
    private EventPublisher eventPublisher;

    /**
     * /api/<username>/lights GET
     * /api/<username>/lights/new GET
     * /api/<username>/lights POST (search for new lights)
     * /api/<username>/lights/<id> GET
     * /api/<username>/lights/<id>/state PUT
     * /api/<username>/groups GET
     * /api/<username>/groups/<id> GET
     * /api/<username>/groups/<id>/action PUT
     * /api/<username>/scenes GET
     * /api POST (create user)
     * /api/<username>/config GET
     * /api/<username> GET (get all)
     */

    public HueEmulatorHttpServlet(ItemRegistry itemRegistry, EventPublisher eventPublisher) {
        this.itemRegistry = itemRegistry;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getRequestURI();
        path = path.substring(HueEmulatorBindingConstants.REST_PATH.length(), path.length());
        setHeaders(resp);
        PrintWriter out = resp.getWriter();
        ObjectMapper mapper = new ObjectMapper();

        logger.debug("doGet " + path);

        try {
            if (path.equals("/discovery.xml")) {
                resp.setContentType(APPLICATION_XML);
                out.print(discovery());
                return;
            }

            if (path.equals("")) {
                out.print(HueEmulatorBindingConstants.BINDING_ID);
                return;
            }

            Matcher matcher = lightsPattern.matcher(path);
            if (matcher.matches()) {
                logger.debug("Getting lights");
                matcher.reset();
                if (matcher.find()) {
                    String username = matcher.group(1);
                    mapper.writeValue(out, getHueDeviceNames(username));
                }
                return;
            }

            matcher = lightPattern.matcher(path);
            if (matcher.matches()) {
                matcher.reset();
                String username = "";
                String id = "";
                while (matcher.find()) {
                    username = matcher.group(1);
                    id = matcher.group(2);
                    logger.debug("Username {} id {}", username, id);
                }
                mapper.writeValue(out, getHueDevice(username, id));
                return;
            }

            matcher = rootPattern.matcher(path);
            if (matcher.matches()) {
                matcher.reset();
                if (matcher.find()) {
                    String username = matcher.group(1);
                    HueDataStore ds = new HueDataStore();
                    ds.lights = getHueDevices(username);
                    mapper.writeValue(out, ds);
                }
                return;
            }
        } finally {
            out.close();
        }
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getRequestURI();
        path = path.substring(HueEmulatorBindingConstants.REST_PATH.length(), path.length());
        setHeaders(resp);
        PrintWriter out = resp.getWriter();
        logger.debug("doPost " + path);
        if (path.equals("/")) {
            ObjectMapper mapper = new ObjectMapper();

            try {
                HueCreateUser user = mapper.readValue(req.getReader(), HueCreateUser.class);
                logger.debug("Create user: " + user.devicetype);
                String response = String.format(NEW_CLIENT_RESP, UUID.randomUUID());
                out.write(response);
            } catch (JsonGenerationException e) {
                logger.debug("error parsing json", e);
            } catch (JsonMappingException e) {
                logger.debug("error parsing json", e);
            } catch (IOException e) {
                logger.debug("error parsing json", e);
            }
        }
        out.close();
    }

    @Override
    public void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getRequestURI();
        path = path.substring(HueEmulatorBindingConstants.REST_PATH.length(), path.length());
        setHeaders(resp);
        PrintWriter out = resp.getWriter();
        logger.debug("doPut " + path);

        Matcher matcher = lightStatePattern.matcher(path);
        if (matcher.matches()) {
            matcher.reset();
            String username = "";
            String id = "";
            while (matcher.find()) {
                username = matcher.group(1);
                id = matcher.group(2);
                logger.debug("Username {} id {}", username, id);
            }
            ObjectMapper mapper = new ObjectMapper();
            try {

                // will throw exception if not found
                itemRegistry.getItem(id);
                HueState state = mapper.readValue(req.getReader(), HueState.class);
                logger.debug("State " + state);
                if (state.bri > -1) {
                    eventPublisher.post(ItemEventFactory.createCommandEvent(id,
                            new DecimalType((int) Math.round(state.bri / 255.0 * 100))));
                } else {
                    eventPublisher
                            .post(ItemEventFactory.createCommandEvent(id, state.on ? OnOffType.ON : OnOffType.OFF));
                }
                out.write(String.format(STATE_RESP, id, String.valueOf(state.on)));
            } catch (ItemNotFoundException e) {
                resp.sendError(404);
            }
            mapper.writeValue(out, getHueDevice(username, id));
            return;
        }
        out.close();
    }

    /**
     * Generates the XML Discovery document
     *
     * @return
     *         XML document
     */
    public String discovery() {
        try {
            String discoString = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("discovery.xml"),
                    "UTF-8");
            String resp = String.format(discoString, InetAddress.getLocalHost().getHostAddress(),
                    System.getProperty("org.osgi.service.http.port"), "122234567890");
            return resp;
        } catch (Exception e) {
            logger.error("Could not read xml file", e);
            return "";
        }
    }

    /**
     * Returns a map of all our items that have voice tags.
     *
     * @param username
     * @return
     *         Map <item name, HueDevice>
     */
    private Map<String, HueDevice> getHueDevices(String username) {
        Collection<Item> items = itemRegistry.getItemsByTag(HueEmulatorBindingConstants.TAG);
        Map<String, HueDevice> devices = new HashMap<String, HueDevice>();
        Iterator<Item> it = items.iterator();
        while (it.hasNext()) {
            Item item = it.next();
            devices.put(item.getName(), itemToDevice(item));
        }
        return devices;
    }

    /**
     * Returns the item name and voice name of each item
     *
     * @param username
     * @return
     *         Map<item name, item voice tag>
     */
    public Map<String, String> getHueDeviceNames(String username) {
        Collection<Item> items = itemRegistry.getItemsByTag(HueEmulatorBindingConstants.TAG);
        Map<String, String> devices = new HashMap<String, String>();
        Iterator<Item> it = items.iterator();
        while (it.hasNext()) {
            Item item = it.next();
            devices.put(item.getName(), item.getLabel());
        }
        return devices;
    }

    /**
     * Gets a Hue Device from an id / item name
     *
     * @param username
     * @param id
     * @return
     *         HueDevice
     */
    public HueDevice getHueDevice(String username, String hueId) {
        try {
            Item item = itemRegistry.getItem(hueId);
            return itemToDevice(item);
        } catch (ItemNotFoundException e) {
            logger.warn("Item not found: " + hueId, e);
            return null;
        }
    }

    /**
     * Converts an VoiceItem to a HueDevice
     *
     * @param voiceItem
     * @return
     *         HueDevice
     */
    private HueDevice itemToDevice(Item item) {
        State itemState = item.getState();
        short bri = 0;
        if (itemState instanceof DecimalType) {
            bri = (short) ((((DecimalType) itemState).intValue() * 255) / 100);
        } else if (itemState instanceof OnOffType) {
            bri = (short) (((OnOffType) itemState) == OnOffType.ON ? 255 : 0);
        }
        HueState hueState = new HueState(bri > 0, bri);
        HueDevice d = new HueDevice(hueState, item.getLabel(), item.getName());
        return d;
    }

    /**
     * Sets Hue API Headers
     *
     * @param response
     */
    private void setHeaders(HttpServletResponse response) {
        response.setCharacterEncoding(CHARSET);
        response.setContentType(APPLICATION_JSON);
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
    }
}
