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

package org.openhab.binding.matter.internal.client.model.cluster.gen;

import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNull;
import org.openhab.binding.matter.internal.client.model.cluster.ClusterCommand;

/**
 * OtaSoftwareUpdateProvider
 *
 * @author Dan Cunningham - Initial contribution
 */
public class OtaSoftwareUpdateProviderCluster extends BaseCluster {

    public static final String CLUSTER_NAME = "OtaSoftwareUpdateProvider";
    public static final int CLUSTER_ID = 0x0029;

    public Integer clusterRevision; // 65533 ClusterRevision

    // Enums
    /**
     * See Section 11.20.3.2, “Querying the OTA Provider” for the semantics of these values.
     */
    public enum StatusEnum implements MatterEnum {
        UPDATE_AVAILABLE(0, "UpdateAvailable"),
        BUSY(1, "Busy"),
        NOT_AVAILABLE(2, "NotAvailable"),
        DOWNLOAD_PROTOCOL_NOT_SUPPORTED(3, "DownloadProtocolNotSupported");

        public final Integer value;
        public final String label;

        private StatusEnum(Integer value, String label) {
            this.value = value;
            this.label = label;
        }

        @Override
        public Integer getValue() {
            return value;
        }

        @Override
        public String getLabel() {
            return label;
        }
    }

    /**
     * See Section 11.20.3.6, “Applying a software update” for the semantics of the values. This enumeration is used in
     * the Action field of the ApplyUpdateResponse command. See (Section 11.20.6.5.4.1, “Action Field”).
     */
    public enum ApplyUpdateActionEnum implements MatterEnum {
        PROCEED(0, "Proceed"),
        AWAIT_NEXT_ACTION(1, "AwaitNextAction"),
        DISCONTINUE(2, "Discontinue");

        public final Integer value;
        public final String label;

        private ApplyUpdateActionEnum(Integer value, String label) {
            this.value = value;
            this.label = label;
        }

        @Override
        public Integer getValue() {
            return value;
        }

        @Override
        public String getLabel() {
            return label;
        }
    }

    /**
     * Note that only HTTP over TLS (HTTPS) is supported (see RFC 7230). Using HTTP without TLS shall
     * NOT be supported, as there is no way to authenticate the involved participants.
     */
    public enum DownloadProtocolEnum implements MatterEnum {
        BDX_SYNCHRONOUS(0, "BdxSynchronous"),
        BDX_ASYNCHRONOUS(1, "BdxAsynchronous"),
        HTTPS(2, "Https"),
        VENDOR_SPECIFIC(3, "VendorSpecific");

        public final Integer value;
        public final String label;

        private DownloadProtocolEnum(Integer value, String label) {
            this.value = value;
            this.label = label;
        }

        @Override
        public Integer getValue() {
            return value;
        }

        @Override
        public String getLabel() {
            return label;
        }
    }

    public OtaSoftwareUpdateProviderCluster(BigInteger nodeId, int endpointId) {
        super(nodeId, endpointId, 41, "OtaSoftwareUpdateProvider");
    }

    // commands
    /**
     * Upon receipt, this command shall trigger an attempt to find an updated Software Image by the OTA Provider to
     * match the OTA Requestor’s constraints provided in the payload fields.
     */
    public static ClusterCommand queryImage(Integer vendorId, Integer productId, Integer softwareVersion,
            List<DownloadProtocolEnum> protocolsSupported, Integer hardwareVersion, String location,
            Boolean requestorCanConsent, String metadataForProvider) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("vendorId", vendorId);
        map.put("productId", productId);
        map.put("softwareVersion", softwareVersion);
        map.put("protocolsSupported", protocolsSupported);
        map.put("hardwareVersion", hardwareVersion);
        map.put("location", location);
        map.put("requestorCanConsent", requestorCanConsent);
        map.put("metadataForProvider", metadataForProvider);
        return new ClusterCommand("queryImage", map);
    }

    public static ClusterCommand applyUpdateRequest(String updateToken, Integer newVersion) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("updateToken", updateToken);
        map.put("newVersion", newVersion);
        return new ClusterCommand("applyUpdateRequest", map);
    }

    public static ClusterCommand notifyUpdateApplied(String updateToken, Integer softwareVersion) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("updateToken", updateToken);
        map.put("softwareVersion", softwareVersion);
        return new ClusterCommand("notifyUpdateApplied", map);
    }

    @Override
    public @NonNull String toString() {
        String str = "";
        str += "clusterRevision : " + clusterRevision + "\n";
        return str;
    }
}
