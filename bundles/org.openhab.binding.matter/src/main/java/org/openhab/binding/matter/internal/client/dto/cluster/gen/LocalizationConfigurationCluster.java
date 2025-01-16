/**
 * Copyright (c) 2010-2024 Contributors to the openHAB project
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

// AUTO-GENERATED, DO NOT EDIT!

package org.openhab.binding.matter.internal.client.dto.cluster.gen;

import java.math.BigInteger;
import java.util.List;

import org.eclipse.jdt.annotation.NonNull;

/**
 * LocalizationConfiguration
 *
 * @author Dan Cunningham - Initial contribution
 */
public class LocalizationConfigurationCluster extends BaseCluster {

    public static final String CLUSTER_NAME = "LocalizationConfiguration";
    public static final int CLUSTER_ID = 0x002B;

    public Integer clusterRevision; // 65533 ClusterRevision
    /**
     * The ActiveLocale attribute shall represent the locale that the Node is currently configured to use when conveying
     * information. The ActiveLocale attribute shall be a Language Tag as defined by BCP47
     * [https://tools.ietf.org/rfc/bcp/bcp47.txt]. The ActiveLocale attribute shall have a default value assigned by the
     * Vendor and shall be a value contained within the SupportedLocales attribute.
     * An attempt to write a value to ActiveLocale that is not present in SupportedLocales shall result in
     * a CONSTRAINT_ERROR error.
     */
    public String activeLocale; // 0 string RW VM
    /**
     * The SupportedLocales attribute shall represent a list of locale strings that are valid values for the
     * ActiveLocale attribute. The list shall NOT contain any duplicate entries. The ordering of items within the list
     * SHOULD NOT express any meaning.
     */
    public List<String> supportedLocales; // 1 list R V

    public LocalizationConfigurationCluster(BigInteger nodeId, int endpointId) {
        super(nodeId, endpointId, 43, "LocalizationConfiguration");
    }

    @Override
    public @NonNull String toString() {
        String str = "";
        str += "clusterRevision : " + clusterRevision + "\n";
        str += "activeLocale : " + activeLocale + "\n";
        str += "supportedLocales : " + supportedLocales + "\n";
        return str;
    }
}
