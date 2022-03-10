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
package org.openhab.binding.lutronvive.internal.api.request;

/**
 *
 * @author Dan Cunningham - Initial contribution
 */
public class LoginRequest {
    public String loginId;
    public String password;
    public boolean isRememberMe;

    public LoginRequest(String loginId, String password, boolean isRememberMe) {
        super();
        this.loginId = loginId;
        this.password = password;
        this.isRememberMe = isRememberMe;
    }
}
