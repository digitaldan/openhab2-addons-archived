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

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * The {@link CoquiSTTConfiguration} class contains fields mapping thing configuration parameters.
 *
 * @author Dan Cunningham - Initial contribution
 */
@NonNullByDefault
public class CoquiSTTConfiguration {

    /**
     * /**
     * Single phrase mode.
     */
    public boolean singleUtteranceMode = true;
    /**
     * Max seconds to wait to force stop the transcription.
     */
    public int maxTranscriptionSeconds = 60;
    /**
     * Only works when singleUtteranceMode is disabled, max seconds without getting new transcriptions to stop
     * listening.
     */
    public int maxSilenceSeconds = 3;
    /**
     * Message to be told when no results.
     */
    public String noResultsMessage = "Sorry, I didn't understand you";
    /**
     * Message to be told when an error has happened.
     */
    public String errorMessage = "Sorry, something went wrong";

    /**
     *
     */
    public int beamWidth = 50;

    /**
     * list of hotwords and their boost value (float) eg: lights:20.0,dim:10.0,
     */
    public String hotWords = "";
}
