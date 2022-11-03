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
package org.openhab.binding.qolsysiq.internal.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.qolsysiq.internal.client.dto.action.Action;
import org.openhab.binding.qolsysiq.internal.client.dto.event.AlarmEvent;
import org.openhab.binding.qolsysiq.internal.client.dto.event.ArmingEvent;
import org.openhab.binding.qolsysiq.internal.client.dto.event.Event;
import org.openhab.binding.qolsysiq.internal.client.dto.event.EventType;
import org.openhab.binding.qolsysiq.internal.client.dto.event.InfoEventType;
import org.openhab.binding.qolsysiq.internal.client.dto.event.SecureArmInfoEvent;
import org.openhab.binding.qolsysiq.internal.client.dto.event.SummaryInfoEvent;
import org.openhab.binding.qolsysiq.internal.client.dto.event.ZoneActiveEvent;
import org.openhab.binding.qolsysiq.internal.client.dto.event.ZoneEventType;
import org.openhab.binding.qolsysiq.internal.client.dto.event.ZoneUpdateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

/**
 *
 * @author Dan Cunningham - Initial contribution
 */
@NonNullByDefault
public class QolsysiqClient {
    private final Logger logger = LoggerFactory.getLogger(QolsysiqClient.class);

    private final static String MESSAGE_ACK = "ACK";

    private List<QolsysIQClientListener> listeners = Collections.synchronizedList(new ArrayList<>());
    private @Nullable SSLSocket socket;
    private @Nullable BufferedReader reader;
    private @Nullable BufferedWriter writer;
    private @Nullable Thread readerThread;

    private Gson gson = new GsonBuilder().registerTypeAdapter(Event.class, new EventDeserializer())
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
    private boolean connected;
    private String host;
    private int port;
    private int heartbeatSeconds;
    private ScheduledExecutorService scheduler;
    private @Nullable ScheduledFuture<?> heartbeatFuture;

    /**
     *
     * @param host
     * @param port
     * @param heartbeatSeconds
     * @param scheduler for the hearbeat task
     */
    public QolsysiqClient(String host, int port, int heartbeatSeconds, ScheduledExecutorService scheduler) {
        this.host = host;
        this.port = port;
        this.heartbeatSeconds = heartbeatSeconds;
        this.scheduler = scheduler;
    }

    public void connect() throws IOException {
        if (connected) {
            throw new IOException("Already Connected");
        }
        SSLSocketFactory sslsocketfactory;
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            // KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            sslContext.init(null, acceptAlltrustManagers(), null);
            sslsocketfactory = sslContext.getSocketFactory();
        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            throw new IOException(e);
        }

        SSLSocket socket = (SSLSocket) sslsocketfactory.createSocket(host, port);
        socket.startHandshake();
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.socket = socket;

        Thread readerThread = new Thread(this::readEvents, "QolsysiqClient read thread");
        readerThread.setDaemon(true);
        readerThread.start();
        this.readerThread = readerThread;
        connected = true;
        heartbeatFuture = scheduler.scheduleWithFixedDelay(() -> {
            if (connected) {
                try {
                    writeMessage("");
                } catch (IOException e) {
                    logger.debug("Problem sending heartbeat", e);
                    disconnectAndNotify(e);
                }
            }
        }, heartbeatSeconds, heartbeatSeconds, TimeUnit.SECONDS);
    }

    public void disconnect() {
        connected = false;

        ScheduledFuture<?> heartbeatFuture = this.heartbeatFuture;
        if (heartbeatFuture != null && !heartbeatFuture.isDone()) {
            heartbeatFuture.cancel(true);
        }

        Thread readerThread = this.readerThread;
        if (readerThread != null && readerThread.isAlive()) {
            readerThread.interrupt();
        }

        SSLSocket socket = this.socket;
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                logger.debug("Error closing SSL socket: {}", e.getMessage());
            }
            this.socket = null;
        }
        BufferedReader reader = this.reader;
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                logger.debug("Error closing reader: {}", e.getMessage());
            }
            this.reader = null;
        }
        BufferedWriter writer = this.writer;
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException e) {
                logger.debug("Error closing writer: {}", e.getMessage());
            }
        }
    }

    public void sendAction(Action action) throws IOException {
        logger.debug("sendAction {}", action.type);
        writeMessage(gson.toJson(action));
    }

    public void addListener(QolsysIQClientListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    public void removeListener(QolsysIQClientListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    public synchronized void writeMessage(String message) throws IOException {
        logger.trace("writeMessage: {}", message);
        BufferedWriter writer = this.writer;
        if (writer != null) {
            writer.write(message);
            writer.newLine();
            writer.flush();
        }
    }

    private void readEvents() {
        String message;
        BufferedReader reader = this.reader;
        try {
            while (connected && reader != null && (message = reader.readLine()) != null) {
                logger.trace("Message: {}", message);
                if (MESSAGE_ACK.equals(message)) {
                    continue;
                }
                try {
                    Event event = gson.fromJson(message, Event.class);
                    if (event == null) {
                        logger.debug("Could not deserialize message: {}", message);
                        continue;
                    }
                    synchronized (listeners) {
                        if (event instanceof AlarmEvent) {
                            listeners.forEach(listener -> listener.alarmEvent((AlarmEvent) event));
                        } else if (event instanceof ArmingEvent) {
                            listeners.forEach(listener -> listener.armingEvent((ArmingEvent) event));
                        } else if (event instanceof SecureArmInfoEvent) {
                            listeners.forEach(listener -> listener.secureArmInfoEvent((SecureArmInfoEvent) event));
                        } else if (event instanceof SummaryInfoEvent) {
                            listeners.forEach(listener -> listener.summaryInfoEvent((SummaryInfoEvent) event));
                        } else if (event instanceof ZoneActiveEvent) {
                            listeners.forEach(listener -> listener.zoneActiveEvent((ZoneActiveEvent) event));
                        } else if (event instanceof ZoneUpdateEvent) {
                            listeners.forEach(listener -> listener.zoneUpdateEvent((ZoneUpdateEvent) event));
                        }

                    }
                } catch (JsonSyntaxException e) {
                    logger.debug("Could not parse messge", e);
                }
            }
            if (connected) {
                throw new IOException("socket disconencted");
            }

        } catch (IOException e) {
            disconnectAndNotify(e);
        }
    }

    private void disconnectAndNotify(Exception e) {
        if (connected) {
            disconnect();
            synchronized (listeners) {
                listeners.forEach(listener -> listener.disconnected(e));
            }
        }
    }

    private TrustManager[] acceptAlltrustManagers() {
        return new TrustManager[] { new X509TrustManager() {
            @Override
            public void checkClientTrusted(final X509Certificate @Nullable [] chain, final @Nullable String authType) {
            }

            @Override
            public void checkServerTrusted(final X509Certificate @Nullable [] chain, final @Nullable String authType) {
            }

            @Override
            public X509Certificate @Nullable [] getAcceptedIssuers() {
                return null;
            }
        } };
    }

    class EventDeserializer implements JsonDeserializer<Event> {
        @Override
        public @Nullable Event deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            JsonElement event = jsonObject.get("event");
            if (event != null) {
                switch (EventType.valueOf(event.getAsString())) {
                    case ALARM:
                        return context.deserialize(jsonObject, AlarmEvent.class);
                    case ARMING:
                        return context.deserialize(jsonObject, ArmingEvent.class);
                    case INFO:

                        JsonElement infoType = jsonObject.get("info_type");
                        if (infoType != null) {
                            switch (InfoEventType.valueOf(infoType.getAsString())) {
                                case SECURE_ARM:
                                    return context.deserialize(jsonObject, SecureArmInfoEvent.class);
                                case SUMMARY:
                                    return context.deserialize(jsonObject, SummaryInfoEvent.class);
                            }
                        }
                        break;
                    case ZONE_EVENT:
                        JsonElement zoneEventType = jsonObject.get("zone_event_type");
                        if (zoneEventType != null) {
                            switch (ZoneEventType.valueOf(zoneEventType.getAsString())) {
                                case ZONE_ACTIVE:
                                    return context.deserialize(jsonObject, ZoneActiveEvent.class);
                                case ZONE_UPDATE:
                                    return context.deserialize(jsonObject, ZoneUpdateEvent.class);
                                default:
                                    break;

                            }
                        }
                }
            }

            return null;
        }
    }
}
