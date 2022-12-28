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
package ai.coqui.libstt;

/**
 *
 * @author Dan Cunningham
 *
 */
public final class STTStreamingState {
    private SWIGTYPE_p_StreamingState _sp;

    public STTStreamingState(SWIGTYPE_p_StreamingState sp) {
        this._sp = sp;
    }

    public SWIGTYPE_p_StreamingState get() {
        return this._sp;
    }
}
