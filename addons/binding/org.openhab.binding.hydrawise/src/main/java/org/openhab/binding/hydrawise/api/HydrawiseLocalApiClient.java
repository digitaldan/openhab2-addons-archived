package org.openhab.binding.hydrawise.api;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.AuthenticationStore;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.util.BasicAuthentication;
import org.eclipse.jetty.http.HttpMethod;
import org.openhab.binding.hydrawise.api.model.LocalScheduleResponse;
import org.openhab.binding.hydrawise.api.model.SetZoneResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

public class HydrawiseLocalApiClient {
    private final Logger logger = LoggerFactory.getLogger(HydrawiseLocalApiClient.class);

    /**
     * tcpdump -i any host 192.168.90.85 -s 65535 -w /tmp/out.pcap
     * local commands
     * /set_manual_data.php?period_id=998&action=suspend&relay_id=544997&relay=3&custom=1525417199&_=1525410369032
     * HTTP/1.1
     * http://192.168.90.85/get_sched_json.php?hours=720&cache=1&_=1525410369027
     *
     * 1525417199 looks like an epoch time
     */
    private static String GET_LOCAL_DATA_URL = "%s/get_sched_json.php?hours=720";
    private static String SET_LOCAL_DATA_URL = "%s/set_manual_data.php?period_id=998";

    private static int TIMEOUT = 30;
    private HttpClient httpClient;
    private Gson gson = new Gson();
    private String localSetURL;
    private String localGetURL;

    public HydrawiseLocalApiClient(String host, String username, String password) {
        String url = "http://" + host;
        localSetURL = String.format(SET_LOCAL_DATA_URL, url);
        localGetURL = String.format(GET_LOCAL_DATA_URL, url);
        httpClient = new HttpClient();
        AuthenticationStore auth = httpClient.getAuthenticationStore();
        URI uri = URI.create(url);
        auth.addAuthenticationResult(new BasicAuthentication.BasicResult(uri, username, password));
    }

    public void stopClient() {
        if (httpClient != null && httpClient.isStarted()) {
            try {
                httpClient.getAuthenticationStore().clearAuthentications();
                httpClient.stop();
            } catch (Exception e) {
                logger.error("Could not stop http client", e);
            }
        }
    }

    public LocalScheduleResponse getLocalSchedule()
            throws HydrawiseConnectionException, HydrawiseAuthenticationException {
        String json = doGet(localGetURL);
        LocalScheduleResponse response = gson.fromJson(json, LocalScheduleResponse.class);
        return response;
    }

    public String stopZone(int number)
            throws HydrawiseConnectionException, HydrawiseAuthenticationException, HydrawiseCommandException {
        return zoneCommand(new HydrawiseCommandBuilder(localSetURL).action("stop").relayNumber(number).toString());
    }

    public String runZone(int number)
            throws HydrawiseConnectionException, HydrawiseAuthenticationException, HydrawiseCommandException {
        return zoneCommand(new HydrawiseCommandBuilder(localSetURL).action("run").relayNumber(number).toString());
    }

    public String runZone(int seconds, int number)
            throws HydrawiseConnectionException, HydrawiseAuthenticationException, HydrawiseCommandException {
        return zoneCommand(new HydrawiseCommandBuilder(localSetURL).action("run").relayNumber(number).duration(seconds)
                .toString());
    }

    public String stopAllZones()
            throws HydrawiseConnectionException, HydrawiseAuthenticationException, HydrawiseCommandException {
        return zoneCommand(new HydrawiseCommandBuilder(localSetURL).action("stopall").toString());
    }

    public String runAllZones()
            throws HydrawiseConnectionException, HydrawiseAuthenticationException, HydrawiseCommandException {
        return zoneCommand(new HydrawiseCommandBuilder(localSetURL).action("runall").toString());
    }

    public String runAllZones(int seconds)
            throws HydrawiseConnectionException, HydrawiseAuthenticationException, HydrawiseCommandException {
        return zoneCommand(new HydrawiseCommandBuilder(localSetURL).action("runall").duration(seconds).toString());
    }

    private String zoneCommand(String url)
            throws HydrawiseConnectionException, HydrawiseAuthenticationException, HydrawiseCommandException {
        String json = doGet(url);
        SetZoneResponse response = gson.fromJson(json, SetZoneResponse.class);
        if (response.getMessageType().equals("error")) {
            throw new HydrawiseCommandException(response.getMessage());
        }
        return response.getMessage();
    }

    private String doGet(String url) throws HydrawiseConnectionException, HydrawiseAuthenticationException {
        logger.debug("Getting {}", url);
        ContentResponse response;
        try {
            if (!httpClient.isStarted()) {
                httpClient.start();
            }
            response = httpClient.newRequest(url).method(HttpMethod.GET).timeout(TIMEOUT, TimeUnit.SECONDS).send();
        } catch (Exception e) {
            throw new HydrawiseConnectionException(e);
        }
        if (response.getStatus() == 401) {
            throw new HydrawiseAuthenticationException();
        }
        if (response.getStatus() != 200) {
            throw new HydrawiseConnectionException("Error from controller.  Response code " + response.getStatus());
        }
        String stringResponse = response.getContentAsString();
        logger.trace("Response: {}", stringResponse);
        return stringResponse;
    }

}
