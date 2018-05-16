package org.openhab.binding.hydrawise.api;

import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.openhab.binding.hydrawise.api.model.CustomerDetailsResponse;
import org.openhab.binding.hydrawise.api.model.Response;
import org.openhab.binding.hydrawise.api.model.SetControllerResponse;
import org.openhab.binding.hydrawise.api.model.SetZoneResponse;
import org.openhab.binding.hydrawise.api.model.StatusScheduleResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

public class HydrawiseCloudApiClient {
    private final Logger logger = LoggerFactory.getLogger(HydrawiseCloudApiClient.class);

    private static String BASE_URL = "https://app.hydrawise.com/api/v1/";
    private static String STATUS_SCHEDUE_URL = BASE_URL + "statusschedule.php?api_key=%s&controller_id=%d&hours=168";
    private static String CUSTOMER_DETAILS_URL = BASE_URL + "customerdetails.php?api_key=%s&type=controllers";
    private static String SET_CONTROLLER_URL = BASE_URL + "setcontroller.php?api_key=%s&controller_id=%d&json=true";
    private static String SET_ZONE_URL = BASE_URL + "setzone.php?api_key=%s&%s";
    private static int TIMEOUT = 30;
    private String apiKey;
    private HttpClient httpClient;
    private Gson gson = new Gson();

    public HydrawiseCloudApiClient(String apiKey) {
        this.apiKey = apiKey;
        SslContextFactory sslContextFactory = new SslContextFactory(true);
        httpClient = new HttpClient(sslContextFactory);
    }

    public void stopClient() {
        if (httpClient.isStarted()) {
            try {
                httpClient.stop();
            } catch (Exception e) {
                logger.error("Could not stop http client", e);
            }
        }
    }

    public StatusScheduleResponse getStatusSchedule(int controllerId)
            throws HydrawiseConnectionException, HydrawiseAuthenticationException {
        String json = doGet(String.format(STATUS_SCHEDUE_URL, apiKey, controllerId));
        StatusScheduleResponse response = gson.fromJson(json, StatusScheduleResponse.class);
        throwExceptionIfResponseError(response);
        return response;
    }

    public CustomerDetailsResponse getCustomerDetails()
            throws HydrawiseConnectionException, HydrawiseAuthenticationException {
        String json = doGet(String.format(CUSTOMER_DETAILS_URL, apiKey));
        CustomerDetailsResponse response = gson.fromJson(json, CustomerDetailsResponse.class);
        throwExceptionIfResponseError(response);
        return response;
    }

    public SetControllerResponse setController(int id)
            throws HydrawiseConnectionException, HydrawiseAuthenticationException, HydrawiseCommandException {
        String json = doGet(String.format(SET_CONTROLLER_URL, apiKey, id));
        SetControllerResponse response = gson.fromJson(json, SetControllerResponse.class);
        throwExceptionIfResponseError(response);
        if (!response.getMessage().equals("OK")) {
            throw new HydrawiseCommandException(response.getMessage());
        }
        return response;
    }

    public String stopZone(int relayId)
            throws HydrawiseConnectionException, HydrawiseAuthenticationException, HydrawiseCommandException {
        return zoneCommand(String.format(SET_ZONE_URL, apiKey, "action=stop&relay_id=" + relayId));
    }

    public String stopAllRelays(int controllerId)
            throws HydrawiseConnectionException, HydrawiseAuthenticationException, HydrawiseCommandException {
        return zoneCommand(String.format(SET_ZONE_URL, apiKey, "action=stopall&controller_id=" + controllerId));
    }

    public String runZone(int relayId)
            throws HydrawiseConnectionException, HydrawiseAuthenticationException, HydrawiseCommandException {
        return runZone(0, relayId);
    }

    public String runZone(int seconds, int relayId)
            throws HydrawiseConnectionException, HydrawiseAuthenticationException, HydrawiseCommandException {
        String url = String.format(SET_ZONE_URL, apiKey, "action=run&period=999&relay_id=" + relayId);
        if (seconds > 0) {
            url += "&custom=";
        }
        return zoneCommand(url);
    }

    public String runAllRelays(int controllerId)
            throws HydrawiseConnectionException, HydrawiseAuthenticationException, HydrawiseCommandException {
        return runAllRelays(0, controllerId);
    }

    public String runAllRelays(int seconds, int controllerId)
            throws HydrawiseConnectionException, HydrawiseAuthenticationException, HydrawiseCommandException {
        String url = String.format(SET_ZONE_URL, apiKey, "action=runall&period=999&controller_id=" + controllerId);
        if (seconds > 0) {
            url += "&custom=";
        }
        return zoneCommand(url);
    }

    public String suspendZone(int relayId)
            throws HydrawiseConnectionException, HydrawiseAuthenticationException, HydrawiseCommandException {
        return suspendZone(0, relayId);
    }

    public String suspendZone(int seconds, int relayId)
            throws HydrawiseConnectionException, HydrawiseAuthenticationException, HydrawiseCommandException {
        String url = String.format(SET_ZONE_URL, apiKey, "action=suspend&period=999&relay_id=" + relayId);
        if (seconds > 0) {
            url += "&custom=";
        }
        return zoneCommand(url);
    }

    public String suspendAllRelays(int seconds, int controllerId)
            throws HydrawiseConnectionException, HydrawiseAuthenticationException, HydrawiseCommandException {
        String url = String.format(SET_ZONE_URL, apiKey, "action=suspendall&period=999&controller_id=" + controllerId);
        if (seconds > 0) {
            url += "&custom=";
        }
        return zoneCommand(url);
    }

    private String zoneCommand(String url)
            throws HydrawiseConnectionException, HydrawiseAuthenticationException, HydrawiseCommandException {
        String json = doGet(url);
        SetZoneResponse response = gson.fromJson(json, SetZoneResponse.class);
        throwExceptionIfResponseError(response);
        if (response.getMessageType().equals("error")) {
            throw new HydrawiseCommandException(response.getMessage());
        }
        return response.getMessage();
    }

    private String doGet(String url) throws HydrawiseConnectionException {
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
        if (response.getStatus() != 200) {
            throw new HydrawiseConnectionException(
                    "Could not connect to Hydrawise API.  Response code " + response.getStatus());
        }
        String stringResponse = response.getContentAsString();
        logger.trace("Response: {}", stringResponse);
        return stringResponse;
    }

    private void throwExceptionIfResponseError(Response response)
            throws HydrawiseConnectionException, HydrawiseAuthenticationException {
        String error = response.getErrorMsg();
        if (error != null) {
            if (error.equalsIgnoreCase("unauthorised")) {
                throw new HydrawiseAuthenticationException();
            } else {
                throw new HydrawiseConnectionException(response.getErrorMsg());
            }
        }
    }
}
