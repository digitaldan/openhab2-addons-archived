package org.openhab.io.hueemulation.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.util.Collection;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.eclipse.smarthome.config.core.ConfigConstants;
import org.eclipse.smarthome.core.events.EventPublisher;
import org.eclipse.smarthome.core.items.Item;
import org.eclipse.smarthome.core.items.ItemNotFoundException;
import org.eclipse.smarthome.core.items.ItemRegistry;
import org.eclipse.smarthome.core.items.events.ItemEventFactory;
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.types.State;
import org.openhab.io.hueemulation.internal.api.HueCreateUser;
import org.openhab.io.hueemulation.internal.api.HueDataStore;
import org.openhab.io.hueemulation.internal.api.HueDevice;
import org.openhab.io.hueemulation.internal.api.HueErrorResponse;
import org.openhab.io.hueemulation.internal.api.HueState;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

/**
 * Emulates A Hue compatible HTTP API server
 *
 * @author Dan Cunningham
 *
 */
@SuppressWarnings("serial")
public class HueEmulationServlet extends HttpServlet {
    private Logger logger = LoggerFactory.getLogger(HueEmulationServlet.class);

    private static final String PATH = "/api";

    private static final String HOMEKIT_PREFIX = "homekit:";

    private static final String METHOD_POST = "POST";
    private static final String METHOD_PUT = "PUT";
    private static final String APPLICATION_XML = "application/xml";
    private static final String APPLICATION_JSON = "application/json";
    private static final String CHARSET = "utf-8";

    private static String NEW_CLIENT_RESP = "[{\"success\":{\"username\": \"%s\"}}]";
    private static String STATE_RESP = "[{\"success\":{\"/lights/%s/state/on\":%s}}]";

    // This parses "/api/{username}/{lights}/{id}/{state}"
    private Pattern pathPattern = Pattern.compile(PATH + "/([^/]+)(?:(?:/(lights)/?([^/]+)?/?(state)?)?)?");

    private HttpService httpService;
    private ItemRegistry itemRegistry;
    private EventPublisher eventPublisher;
    private HueEmulationUpnpServer disco;

    private boolean inclusionEnable = true;

    protected void activate(Map<String, Object> config) {
        try {
            Dictionary<String, String> servletParams = new Hashtable<String, String>();
            httpService.registerServlet(PATH, this, servletParams, createHttpContext());
            disco = new HueEmulationUpnpServer(PATH + "/discovery.xml");
            disco.start();
            logger.info("Started Hue Emulation service at " + PATH);
        } catch (Exception e) {
            logger.error("Could not start Hue Emulation service: {}", e.getMessage(), e);
        }
        modified(config);
    }

    protected void modified(Map<String, ?> config) {
        // process the config params here
    }

    protected void deactivate(ComponentContext componentContext) {
        try {
            httpService.unregister(PATH);
        } catch (IllegalArgumentException ignored) {
        }
        if (disco != null) {
            disco.shutdown();
        }
    }

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

    protected void setHttpService(HttpService httpService) {
        this.httpService = httpService;
    }

    protected void unsetHttpService(HttpService httpService) {
        this.httpService = null;
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getRequestURI();
        logger.debug("{} {}", req.getMethod(), path);
        setHeaders(resp);
        PrintWriter out = resp.getWriter();

        // UPNP discovery document
        if (path.equals(PATH + "/discovery.xml")) {
            resp.setContentType(APPLICATION_XML);
            out.print(discoveryXML());
            return;
        }

        // request for API key
        if (path.equals(PATH)) {
            if (inclusionEnable) {
                apiConfig(req, resp);
            } else {
                apiServerError(req, resp, HueErrorResponse.UNAUTHORIZED,
                        "Not Authorized. Include button must be pressed to add users.");
            }
            return;
        }

        // All other API requests
        Matcher m = pathPattern.matcher(path);
        if (m.matches()) {
            String userName = m.group(1);
            boolean lightsReq = m.group(2) != null;
            String id = m.group(3);
            boolean stateReq = m.group(4) != null;

            // check the user, if inclusion is on then add the user to our list
            if (!authorizeUser(userName, inclusionEnable)) {
                apiServerError(req, resp, HueErrorResponse.UNAUTHORIZED, "Not Authorized");
                return;
            }
            // everything is JSON from here
            resp.setContentType(APPLICATION_JSON);
            if (stateReq) {
                apiState(id, req, resp);
            } else if (id != null) {
                apiLight(id, req, resp);
            } else if (lightsReq) {
                apiLights(req, resp);
            } else if (userName != null) {
                apiDataStore(req, resp);
            } else {
                apiServerError(req, resp, HueErrorResponse.NOT_AVAILABLE, "Hue Resource Not Availabale");
            }
        }

    }

    /**
     * Hue API call to set the state of a light
     *
     * @param id
     * @param req
     * @param resp
     * @throws IOException
     */
    private void apiState(String id, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!req.getMethod().equals(METHOD_PUT)) {
            apiServerError(req, resp, HueErrorResponse.METHOD_NOT_AVAILABLE, "Only PUT allowed for this resource");
            return;
        }
        try {
            Gson gson = new Gson();
            // will throw exception if not found
            itemRegistry.getItem(id);
            HueState state = gson.fromJson(req.getReader(), HueState.class);
            logger.debug("State " + state);
            if (state.bri > -1) {
                eventPublisher.post(ItemEventFactory.createCommandEvent(id,
                        new DecimalType((int) Math.round(state.bri / 255.0 * 100))));
            } else {
                eventPublisher.post(ItemEventFactory.createCommandEvent(id, state.on ? OnOffType.ON : OnOffType.OFF));
            }
            PrintWriter out = resp.getWriter();
            out.write(String.format(STATE_RESP, id, String.valueOf(state.on)));
            out.close();
        } catch (ItemNotFoundException e) {
            logger.warn("Item not found: " + id);
            apiServerError(req, resp, HueErrorResponse.NOT_AVAILABLE, "The Hue device could not be found");
        }
    }

    /**
     * Hue API call to get the state of a single light
     *
     * @param id
     * @param req
     * @param resp
     * @throws IOException
     */
    private void apiLight(String id, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            Gson gson = new Gson();
            Item item = itemRegistry.getItem(id);
            PrintWriter out = resp.getWriter();
            out.write(gson.toJson(itemToDevice(item)));
            out.close();
        } catch (ItemNotFoundException e) {
            logger.warn("Item not found: " + id);
            apiServerError(req, resp, HueErrorResponse.NOT_AVAILABLE, "Item not found " + id);
        }
    }

    /**
     * Hue API call to get a listing of all lights
     *
     * @param req
     * @param resp
     * @throws IOException
     */
    public void apiLights(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PrintWriter out = resp.getWriter();
        Gson gson = new Gson();
        out.write(gson.toJson(getHueDeviceNames()));
        out.close();
    }

    /**
     * HUE API call to get the Data Store of the bridge (only lights supported for now)
     *
     * @param req
     * @param resp
     * @throws IOException
     */
    public void apiDataStore(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PrintWriter out = resp.getWriter();
        Gson gson = new Gson();
        HueDataStore ds = new HueDataStore();
        ds.lights = getHueDevices();
        out.write(gson.toJson(ds));
    }

    /**
     * Hue API call to configure a user
     *
     * @param req
     * @param resp
     * @throws IOException
     */
    public void apiConfig(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!req.getMethod().equals(METHOD_POST)) {
            apiServerError(req, resp, HueErrorResponse.METHOD_NOT_AVAILABLE, "Only POST allowed for this resource");
            return;
        }
        PrintWriter out = resp.getWriter();
        Gson gson = new Gson();

        HueCreateUser user = gson.fromJson(req.getReader(), HueCreateUser.class);
        logger.debug("Create user: " + user.devicetype);
        if (user.username == null | user.username.length() == 0) {
            user.username = UUID.randomUUID().toString();
        }
        String response = String.format(NEW_CLIENT_RESP, user.username);
        out.write(response);
        authorizeUser(user.username, true);
        out.close();
    }

    /**
     * Hue API error response
     *
     * @param req
     * @param resp
     * @param error
     * @param description
     * @throws IOException
     */
    public void apiServerError(HttpServletRequest req, HttpServletResponse resp, int error, String description)
            throws IOException {
        PrintWriter out = resp.getWriter();
        Gson gson = new Gson();
        HueErrorResponse e = new HueErrorResponse(error, req.getRequestURI(), description);
        out.write(gson.toJson(e));
    }

    /**
     * Generates the XML Discovery document
     *
     * @return
     *         XML document
     */
    public String discoveryXML() {
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
    private Map<String, HueDevice> getHueDevices() {
        Collection<Item> items = getTagedItems();
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
    public Map<String, String> getHueDeviceNames() {
        Collection<Item> items = getTagedItems();
        Map<String, String> devices = new HashMap<String, String>();
        Iterator<Item> it = items.iterator();
        while (it.hasNext()) {
            Item item = it.next();
            devices.put(item.getName(), item.getLabel());
        }
        return devices;
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

    private Collection<Item> getTagedItems() {
        Collection<Item> items = new LinkedList<Item>();
        for (Item item : itemRegistry.getItems()) {
            for (String tag : item.getTags()) {
                if (tag.indexOf(HOMEKIT_PREFIX) == 0) {
                    items.add(item);
                    break;
                }
            }
        }
        return items;
    }

    private synchronized boolean authorizeUser(String userName, boolean addIfNotFound) {
        try {
            File file = new File(ConfigConstants.getUserDataFolder() + File.separator + "hueemulator/usernames");
            file.getParentFile().mkdirs();
            List<String> userNames;
            if (file.exists()) {
                userNames = IOUtils.readLines(new FileInputStream(file));
            } else {
                userNames = new LinkedList<String>();
            }
            if (userNames.contains(userName)) {
                return true;
            } else if (addIfNotFound) {
                userNames.add(userName);
                IOUtils.writeLines(userNames, null, new FileOutputStream(file));
                return true;
            }
        } catch (IOException e) {
            logger.error("Could not write username to file", e);
        }
        return false;
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

    protected HttpContext createHttpContext() {
        HttpContext defaultHttpContext = httpService.createDefaultHttpContext();
        return defaultHttpContext;
    }
}
