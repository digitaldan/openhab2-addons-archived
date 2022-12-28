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
package org.openhab.voice.coquistt.internal;

import static org.openhab.voice.coquistt.internal.CoquiSTTConstants.*;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.core.audio.AudioFormat;
import org.openhab.core.audio.AudioStream;
import org.openhab.core.common.ThreadPoolManager;
import org.openhab.core.config.core.ConfigurableService;
import org.openhab.core.config.core.Configuration;
import org.openhab.core.voice.RecognitionStartEvent;
import org.openhab.core.voice.RecognitionStopEvent;
import org.openhab.core.voice.STTException;
import org.openhab.core.voice.STTListener;
import org.openhab.core.voice.STTService;
import org.openhab.core.voice.STTServiceHandle;
import org.openhab.core.voice.SpeechRecognitionErrorEvent;
import org.openhab.core.voice.SpeechRecognitionEvent;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ai.coqui.libstt.STTModel;
import ai.coqui.libstt.STTStreamingState;

/**
 * @author Dan Cunningham
 *
 */
@NonNullByDefault
@Component(configurationPid = SERVICE_PID, property = Constants.SERVICE_PID + "=" + SERVICE_PID)
@ConfigurableService(category = SERVICE_CATEGORY, label = SERVICE_NAME
        + " Speech-to-Text", description_uri = SERVICE_CATEGORY + ":" + SERVICE_ID)
public class CoquiSTTService implements STTService {

    private final Logger logger = LoggerFactory.getLogger(CoquiSTTService.class);
    private static final String MODEL_NAME = "model.tflite";
    private static final String SCORER_NAME = "large_vocabulary.scorer";
    private final ScheduledExecutorService executor = ThreadPoolManager.getScheduledPool("OH-voice-coquistt");

    @Nullable
    private STTModel model;

    private CoquiSTTConfiguration config = new CoquiSTTConfiguration();

    @Activate
    protected void activate(Map<String, Object> config) {
        modified(config);
    }

    @SuppressWarnings("null")
    @Modified
    protected void modified(Map<String, Object> config) {
        this.config = new Configuration(config).as(CoquiSTTConfiguration.class);

        deactivate();
        URL modelUrl = CoquiSTTService.class.getClassLoader().getResource(MODEL_NAME);
        URL scorerUrl = CoquiSTTService.class.getClassLoader().getResource(SCORER_NAME);
        if (modelUrl != null && scorerUrl != null) {
            try {
                extractFromBundle(modelUrl, new File(MODEL_NAME));
                extractFromBundle(scorerUrl, new File(SCORER_NAME));
                model = new STTModel(MODEL_NAME);
                model.enableExternalScorer(SCORER_NAME);
                model.setBeamWidth(this.config.beamWidth);
            } catch (IOException e) {
                logger.debug("Could not save model", e);
            }

        }
    }

    @Deactivate
    protected void deactivate() {
        STTModel model = this.model;
        if (model != null) {
            model.freeModel();
            model = null;
        }
    }

    @Override
    public String getId() {
        // TODO Auto-generated method stub
        return SERVICE_ID;
    }

    @Override
    public String getLabel(@Nullable Locale locale) {
        // TODO Auto-generated method stub
        return SERVICE_NAME;
    }

    @Override
    public Set<@NonNull Locale> getSupportedLocales() {
        // TODO Auto-generated method stub
        return Set.of(Locale.US);
    }

    @Override
    public Set<@NonNull AudioFormat> getSupportedFormats() {
        return Set
                .of(new AudioFormat(AudioFormat.CONTAINER_WAVE, AudioFormat.CODEC_PCM_SIGNED, false, 16, null, 16000L));
    }

    @Override
    public STTServiceHandle recognize(STTListener sttListener, AudioStream audioStream, Locale locale,
            Set<@NonNull String> grammars) throws STTException {
        AtomicBoolean aborted = new AtomicBoolean(false);
        process(sttListener, audioStream, aborted, locale, grammars);
        return new STTServiceHandle() {
            @Override
            public void abort() {
                aborted.set(true);
            }
        };
    }

    private Future<?> process(STTListener sttListener, AudioStream audioStream, AtomicBoolean aborted, Locale locale,
            Set<String> grammars) {
        return executor.submit(() -> {
            STTModel model = this.model;
            if (model == null) {
                sttListener.sttEventReceived(new SpeechRecognitionErrorEvent("No Model"));
                return;
            }
            long startTime = System.currentTimeMillis();
            long lastSpeechTime = startTime;
            long maxTranscriptionMillis = (config.maxTranscriptionSeconds * 1000L);
            long maxSilenceMillis = (config.maxSilenceSeconds * 1000L);
            boolean started = false;
            int readBytes = 2048;
            String intermediateResults = "";
            STTStreamingState stream = model.createStream();
            while (!aborted.get()) {
                byte[] data = new byte[readBytes];
                try {
                    int dataN = audioStream.read(data);
                    logger.trace("Read {} bytes", dataN);
                    if (aborted.get()) {
                        logger.debug("Stops listening, aborted");
                        model.finishStream(stream);
                        break;
                    }
                    if (isExpiredInterval(maxTranscriptionMillis, startTime)) {
                        String decoded = model.finishStream(stream);
                        logger.debug("Stops listening, max transcription time reached: {} ", decoded);
                        sttListener.sttEventReceived(new RecognitionStopEvent());
                        if (decoded != null && decoded.length() > 0) {
                            sttListener.sttEventReceived(new SpeechRecognitionEvent(decoded, 1));
                        }

                        break;
                    }

                    if (isExpiredInterval(maxSilenceMillis, lastSpeechTime)) {
                        String decoded = model.finishStream(stream);
                        logger.debug("Stops listening, max silence time reached {}", decoded);
                        sttListener.sttEventReceived(new RecognitionStopEvent());
                        if (decoded != null && decoded.length() > 0) {
                            sttListener.sttEventReceived(new SpeechRecognitionEvent(decoded, 1));
                        }
                        break;
                    }
                    if (dataN != readBytes) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                        }
                        continue;
                    }

                    short[] shorts = new short[data.length / 2];
                    ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts);
                    model.feedAudioContent(stream, shorts, shorts.length);
                    String decoded = model.intermediateDecode(stream);
                    logger.debug("DECODED '{}'", decoded);
                    if (decoded != null && !intermediateResults.equals(decoded)) {
                        intermediateResults = decoded;
                        lastSpeechTime = System.currentTimeMillis();
                        if (!started) {
                            sttListener.sttEventReceived(new RecognitionStartEvent());
                            started = true;
                        }
                    }
                } catch (IOException e) {
                    logger.debug("Could not read audio data", e);
                    sttListener.sttEventReceived(new SpeechRecognitionErrorEvent("Could not read audio data"));
                }
            }
        });
    }

    private boolean isExpiredInterval(long interval, long referenceTime) {
        return System.currentTimeMillis() - referenceTime > interval;
    }

    private void extractFromBundle(URL resourceUrl, File targetFile) throws IOException {
        InputStream in = new BufferedInputStream(resourceUrl.openStream());
        OutputStream out = new BufferedOutputStream(new FileOutputStream(targetFile));
        byte[] buffer = new byte[1024];
        int lengthRead;
        while ((lengthRead = in.read(buffer)) > 0) {
            out.write(buffer, 0, lengthRead);
            out.flush();
        }
        in.close();
        out.close();
    }
}
