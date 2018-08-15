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
    private static String SET_ZONE_URL = BASE_URL + "setzone.php?period_id=999";
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

    public String stopRelay(int relayId)
            throws HydrawiseConnectionException, HydrawiseAuthenticationException, HydrawiseCommandException {
        return relayCommand(
                new HydrawiseZoneCommandBuilder(SET_ZONE_URL, apiKey).action("stop").relayId(relayId).toString());
    }

    public String stopAllRelays(int controllerId)
            throws HydrawiseConnectionException, HydrawiseAuthenticationException, HydrawiseCommandException {
        return relayCommand(new HydrawiseZoneCommandBuilder(SET_ZONE_URL, apiKey).action("stopall")
                .controllerId(controllerId).toString());
    }

    public String runRelay(int relayId)
            throws HydrawiseConnectionException, HydrawiseAuthenticationException, HydrawiseCommandException {
        return relayCommand(
                new HydrawiseZoneCommandBuilder(SET_ZONE_URL, apiKey).action("run").relayId(relayId).toString());
    }

    public String runRelay(int seconds, int relayId)
            throws HydrawiseConnectionException, HydrawiseAuthenticationException, HydrawiseCommandException {
        return relayCommand(new HydrawiseZoneCommandBuilder(SET_ZONE_URL, apiKey).action("run").relayId(relayId)
                .duration(seconds).toString());
    }

    public String runAllRelays(int controllerId)
            throws HydrawiseConnectionException, HydrawiseAuthenticationException, HydrawiseCommandException {
        return relayCommand(new HydrawiseZoneCommandBuilder(SET_ZONE_URL, apiKey).action("runall")
                .controllerId(controllerId).toString());
    }

    public String runAllRelays(int seconds, int controllerId)
            throws HydrawiseConnectionException, HydrawiseAuthenticationException, HydrawiseCommandException {
        return relayCommand(new HydrawiseZoneCommandBuilder(SET_ZONE_URL, apiKey).action("runall")
                .controllerId(controllerId).duration(seconds).toString());
    }

    public String suspendRelay(int relayId)
            throws HydrawiseConnectionException, HydrawiseAuthenticationException, HydrawiseCommandException {
        return relayCommand(
                new HydrawiseZoneCommandBuilder(SET_ZONE_URL, apiKey).action("suspend").relayId(relayId).toString());
    }

    public String suspendRelay(int seconds, int relayId)
            throws HydrawiseConnectionException, HydrawiseAuthenticationException, HydrawiseCommandException {
        return relayCommand(new HydrawiseZoneCommandBuilder(SET_ZONE_URL, apiKey).action("suspend").relayId(relayId)
                .duration(seconds).toString());
    }

    public String suspendAllRelays(int seconds, int controllerId)
            throws HydrawiseConnectionException, HydrawiseAuthenticationException, HydrawiseCommandException {
        return relayCommand(new HydrawiseZoneCommandBuilder(SET_ZONE_URL, apiKey).action("suspendall")
                .controllerId(controllerId).duration(seconds).toString());
    }

    private String relayCommand(String url)
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
