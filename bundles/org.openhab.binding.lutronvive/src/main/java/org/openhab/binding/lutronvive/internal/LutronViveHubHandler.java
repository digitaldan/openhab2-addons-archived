/**
 * Copyright (c) 2010-2022 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.lutronvive.internal;

import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpMethod;
import org.json.JSONObject;
import org.openhab.binding.lutronvive.internal.api.request.ExecuteCommand;
import org.openhab.binding.lutronvive.internal.api.request.LoginRequest;
import org.openhab.binding.lutronvive.internal.api.response.Area;
import org.openhab.binding.lutronvive.internal.api.response.AreaDetail;
import org.openhab.binding.lutronvive.internal.api.response.AreaStatus;
import org.openhab.binding.lutronvive.internal.api.response.Login;
import org.openhab.binding.lutronvive.internal.api.response.Reference;
import org.openhab.binding.lutronvive.internal.api.response.SubscriptionEvent;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;
import org.openhab.core.thing.binding.BaseBridgeHandler;
import org.openhab.core.thing.binding.ThingHandler;
import org.openhab.core.thing.binding.ThingHandlerService;
import org.openhab.core.types.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.OkHttpClient;

/**
 * The {@link LutronViveHubHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Dan Cunningham - Initial contribution
 */
// @NonNullByDefault
public class LutronViveHubHandler extends BaseBridgeHandler {

    private final Logger logger = LoggerFactory.getLogger(LutronViveHubHandler.class);

    private LutronViveHubConfiguration config;
    private final HttpClient httpClient;
    private @Nullable Socket webSocket;
    private final Gson gson = new GsonBuilder().create();
    private @Nullable Login loginResponse;
    protected @Nullable Area[] areas;
    private @Nullable LutronViveDiscoveryService discoveryService;
    private @Nullable Future<?> connectFuture;

    public LutronViveHubHandler(Bridge thing, HttpClient httpClient) {
        super(thing);
        this.httpClient = httpClient;
        this.config = new LutronViveHubConfiguration();
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {

    }

    @Override
    public void initialize() {
        config = getConfigAs(LutronViveHubConfiguration.class);
        updateStatus(ThingStatus.UNKNOWN);
        scheduler.execute(() -> {
            connectIn(0);
        });
    }

    @Override
    public void dispose() {
        stopFuture(connectFuture);
        disconnectFromService();
    }

    @Override
    public Collection<Class<? extends ThingHandlerService>> getServices() {
        return Collections.singleton(LutronViveDiscoveryService.class);
    }

    private synchronized void connectIn(int seconds) {
        stopFuture(connectFuture);
        connectFuture = scheduler.schedule(() -> {
            login();
            loginStatus();
            connectWs();
        }, seconds, TimeUnit.SECONDS);
    }

    private void stopFuture(@Nullable Future<?> future) {
        if (future != null) {
            future.cancel(true);
        }
    }

    private void loginStatus() {
        Request request = httpClient.newRequest(baseURL() + "/session").header("x-requested-with", "XMLHttpRequest");
        try {
            ContentResponse response = request.send();
            logger.debug("Status response {}", response.getContentAsString());
        } catch (InterruptedException | TimeoutException | ExecutionException e) {
            logger.error("Status error", e);
        }
    }

    private void login() {

        LoginRequest loginRequest = new LoginRequest(config.username, config.password, true);

        Request request = httpClient.newRequest(baseURL() + "/login").method(HttpMethod.POST)
                .header("x-requested-with", "XMLHttpRequest")
                .content(new StringContentProvider(gson.toJson(loginRequest)), "application/json");
        try {
            ContentResponse response = request.send();
            logger.debug("Status response {}", response.getContentAsString());
            loginResponse = gson.fromJson(response.getContentAsString(), Login.class);

        } catch (InterruptedException | TimeoutException | ExecutionException | JsonSyntaxException e) {
            logger.error("Status error", e);
        }
    }

    public void updateAreas() {
        try {
            String result = sendGet("/areas", null);
            logger.debug("updateAreaDetail response {}", result);
            areas = gson.fromJson(result, Area[].class);
            subscribeAll();
            if (discoveryService != null) {
                discoveryService.discoverAreas(areas);
            }
            for (Area area : areas) {
                if (area != null) {
                    updateAreaStatus(area.href);
                    updateAreaDetail(area.href);
                }
            }
        } catch (InterruptedException | TimeoutException | ExecutionException | JsonSyntaxException e) {
            logger.error("Status error", e);
        }
    }

    public void updateAreaStatus(String href) {
        try {
            String result = sendGet("/areastatus?areaHref=" + href, null);
            logger.debug("updateAreaDetail response {}", result);
            AreaStatus status = gson.fromJson(result, AreaStatus.class);
            updateAreaHandler(status);
        } catch (InterruptedException | TimeoutException | ExecutionException | JsonSyntaxException e) {
            logger.error("Status error", e);
        }
    }

    public void updateAreaDetail(String href) {
        try {
            String result = sendGet("/areadetail?areaHref=" + href, null);
            logger.debug("updateAreaDetail response {}", result);
            AreaDetail detail = gson.fromJson(result, AreaDetail.class);
            updateAreaHander(detail);
        } catch (InterruptedException | TimeoutException | ExecutionException | JsonSyntaxException e) {
            logger.error("Status error", e);
        }
    }

    public void sendCommand(ExecuteCommand command) {
        // {"commandUrl":"/zonetypegroup/14/commandprocessor","commandType":"GoToDimmedLevel"}
        try {
            String json = gson.toJson(command);
            String[] cmd = { "commandparams", json };
            logger.debug("sending command {} ", json);
            String result = sendGet("/executecommand", cmd);
            logger.debug("sendCommand response {}", result);
        } catch (InterruptedException | TimeoutException | ExecutionException | JsonSyntaxException e) {
            logger.error("Status error", e);
        }
    }

    private String sendGet(String path, String[] extraHeader)
            throws InterruptedException, TimeoutException, ExecutionException {

        Request request = httpClient.newRequest(baseURL() + path).method(HttpMethod.GET)
                .header("x-requested-with", "XMLHttpRequest").header("x-csrf-token", loginResponse.csrfToken);
        if (extraHeader != null) {
            request.header(extraHeader[0], extraHeader[1]);
        }

        ContentResponse response = request.send();
        logger.debug("sendCommand response {}", response.getContentAsString());
        return response.getContentAsString();
    }

    public void connectWs() {
        disconnectFromService();

        final TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {

            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
                    throws CertificateException {
            }

            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
                    throws CertificateException {
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[] {};
            }
        } };

        try {
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            // Set default hostname
            HostnameVerifier hostnameVerifier = new HostnameVerifier() {

                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0])
                    .hostnameVerifier(hostnameVerifier).addInterceptor(chain -> {
                        String cookie = httpClient.getCookieStore().getCookies().stream().map(c -> c.toString())
                                .collect(Collectors.joining("; "));
                        final okhttp3.Request original = chain.request();
                        final okhttp3.Request authorized = original.newBuilder().addHeader("Cookie", cookie).build();
                        logger.debug("Headers {} ", authorized.headers().toString());
                        return chain.proceed(authorized);
                    }).build();

            IO.setDefaultOkHttpWebSocketFactory(okHttpClient);
            IO.setDefaultOkHttpCallFactory(okHttpClient);

            IO.Options options = new IO.Options();
            options.forceNew = true;
            options.transports = new String[] { "websocket" };
            webSocket = IO.socket("https://10.22.1.28:443/socket.io", options);

            webSocket.on(Socket.EVENT_CONNECTING, onEventConnecting);
            webSocket.on(Socket.EVENT_CONNECT, onEventConnect);
            webSocket.on(Socket.EVENT_CONNECT_ERROR, onEventConnectError);
            webSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onEventConnectTimeout);
            webSocket.on(Socket.EVENT_DISCONNECT, onEventDisconnect);
            webSocket.on(Socket.EVENT_RECONNECT, onEventReconnect);
            webSocket.on(Socket.EVENT_PING, onEventPing);
            webSocket.on(Socket.EVENT_PONG, onEventPong);
            webSocket.on("subscribe", onSubscribe);
            webSocket.on("subscriptionResponse", onSubscriptionResponse);

            logger.debug("Listener: Opening connection {} ", webSocket.toString());
            webSocket.connect();
        } catch (URISyntaxException | KeyManagementException | NoSuchAlgorithmException e) {
            logger.info("Listener: URISyntaxException getting IO socket: {}", e.getMessage());
            return;
        }
    }

    private void disconnectFromService() {
        if (webSocket != null) {
            logger.debug("Listener: Disconnecting socket and removing event listeners for {}", webSocket.toString());
            webSocket.disconnect();
            webSocket.off();
            webSocket = null;
        }
    }

    private Emitter.Listener onEventConnecting = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            logger.debug("Listener: onEventConnecting");
        }
    };

    private Emitter.Listener onEventConnect = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            logger.debug("Listener: Connected!");
            updateAreas();
            updateStatus(ThingStatus.ONLINE);
        }
    };

    private Emitter.Listener onEventDisconnect = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            logger.debug("Listener: Disconnected");
            setOffline("Disconnected");
        }
    };

    private Emitter.Listener onEventConnectError = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            logger.debug("Listener: onEventConnectError!");
            setOffline("onEventConnectError");
        }
    };

    private Emitter.Listener onEventConnectTimeout = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            logger.debug("Listener: onEventConnectTimeout! ");
            setOffline("onEventConnectTimeout");
        }
    };

    private Emitter.Listener onEventReconnect = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            logger.debug("Listener: Received reconnect event");
        }
    };

    private Emitter.Listener onSubscribe = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            logger.debug("Listener: Received onSubscribe event  {}", Arrays.toString(args));
        }
    };

    private Emitter.Listener onSubscriptionResponse = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            logger.debug("Listener: Received onSubscriptionResponse event {}", Arrays.toString(args));
            SubscriptionEvent event = new SubscriptionEvent((String) args[0]);
            switch (event.getZoneType()) {
                case "system":
                    break;
                case "area":
                    switch (event.getEventType()) {
                        case "status":
                            AreaStatus status = gson.fromJson(((JSONObject) args[1]).toString(), AreaStatus.class);
                            updateAreaHandler(status);
                            // update area thing level for switch and dimmer
                            break;
                        // case "occupancysettings":
                        // OccupancySettings occupancy = gson.fromJson(((JSONObject) args[1]).toString(),
                        // OccupancySettings.class);
                        // // update area thing occupancy for contact
                        // break;
                    }
                    break;
            }

        }
    };

    private Emitter.Listener onEventPing = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            // logger.debug("Listener: Received PING event");
        }
    };

    private Emitter.Listener onEventPong = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            // logger.debug("Listener: Received PONG event");
        }
    };

    private void updateAreaHandler(AreaStatus status) {
        Reference ref = new Reference(status.href);
        LutronViveAreaHandler handler = getAreaHandler(ref.getZoneNumber());
        if (handler != null) {
            handler.updateAreaStatus(status);
        }

    }

    private void updateAreaHander(AreaDetail detail) {
        Reference ref = new Reference(detail.area.href);
        LutronViveAreaHandler handler = getAreaHandler(ref.getZoneNumber());
        if (handler != null) {
            handler.updateAreaDetail(detail);
        }
    }

    private LutronViveAreaHandler getAreaHandler(String id) {
        for (Thing thing : getThing().getThings()) {
            ThingHandler handler = thing.getHandler();
            if (handler != null && (handler instanceof LutronViveAreaHandler)
                    && ((LutronViveAreaHandler) handler).getId().equals(id)) {
                return ((LutronViveAreaHandler) handler);
            }
        }
        return null;
    }

    private void subscribeAll() {
        for (Area a : areas) {
            webSocket.emit("subscribe", "OccupancySettings", a.href + "/occupancysettings", "");
            webSocket.emit("subscribe", "AreaStatusDefinition", a.href + "/status", "");
        }
    }

    private void setOffline(String reason) {
        updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, reason);
        disconnectFromService();
        connectIn(30);
    }

    private void setOnline() {
        updateStatus(ThingStatus.ONLINE);
    }

    private String baseURL() {
        return "https://" + config.hostname;
    }

    protected void setDiscoveryService(LutronViveDiscoveryService discoveryService) {
        this.discoveryService = discoveryService;
    }
}
