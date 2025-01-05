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

import org.eclipse.jdt.annotation.NonNull;
import org.openhab.binding.matter.internal.client.model.cluster.ClusterCommand;

/**
 * WiFiNetworkDiagnostics
 *
 * @author Dan Cunningham - Initial contribution
 */
public class WiFiNetworkDiagnosticsCluster extends BaseCluster {

    public static final String CLUSTER_NAME = "WiFiNetworkDiagnostics";
    public static final int CLUSTER_ID = 0x0036;

    public Integer clusterRevision; // 65533 ClusterRevision
    public FeatureMap featureMap; // 65532 FeatureMap
    /**
     * The BSSID attribute shall indicate the BSSID for which the Wi-Fi network the Node is currently connected.
     */
    public String bssid; // 0 octstr R V
    /**
     * The SecurityType attribute shall indicate the current type of Wi-Fi security used.
     */
    public SecurityTypeEnum securityType; // 1 SecurityTypeEnum R V
    /**
     * The WiFiVersion attribute shall indicate the current 802.11 standard version in use by the Node, per the table
     * below.
     */
    public WiFiVersionEnum wiFiVersion; // 2 WiFiVersionEnum R V
    /**
     * The ChannelNumber attribute shall indicate the channel that Wi-Fi communication is currently operating on.
     */
    public Integer channelNumber; // 3 uint16 R V
    /**
     * The RSSI attribute shall indicate the current RSSI of the Node’s Wi-Fi radio in dBm.
     */
    public Integer rssi; // 4 int8 R V
    /**
     * The BeaconLostCount attribute shall indicate the count of the number of missed beacons the Node has detected. If
     * the Node does not have an ability to count beacons expected and not received, this value may remain set to zero.
     */
    public Integer beaconLostCount; // 5 uint32 R V
    /**
     * The BeaconRxCount attribute shall indicate the count of the number of received beacons. The
     * total number of expected beacons that could have been received during the interval since association SHOULD match
     * the sum of BeaconRxCount and BeaconLostCount. If the Node does not have an ability to report count of beacons
     * received, this value may remain set to zero.
     */
    public Integer beaconRxCount; // 6 uint32 R V
    /**
     * The PacketMulticastRxCount attribute shall indicate the number of multicast packets received by the Node.
     */
    public Integer packetMulticastRxCount; // 7 uint32 R V
    /**
     * The PacketMulticastTxCount attribute shall indicate the number of multicast packets transmitted by the Node.
     */
    public Integer packetMulticastTxCount; // 8 uint32 R V
    /**
     * The PacketUnicastRxCount attribute shall indicate the number of unicast packets received by the Node.
     */
    public Integer packetUnicastRxCount; // 9 uint32 R V
    /**
     * The PacketUnicastTxCount attribute shall indicate the number of unicast packets transmitted by the Node.
     */
    public Integer packetUnicastTxCount; // 10 uint32 R V
    /**
     * The CurrentMaxRate attribute shall indicate the current maximum PHY rate of transfer of data in bits-per-second.
     */
    public BigInteger currentMaxRate; // 11 uint64 R V
    /**
     * The OverrunCount attribute shall indicate the number of packets dropped either at ingress or egress, due to lack
     * of buffer memory to retain all packets on the network interface. The OverrunCount attribute shall be reset to 0
     * upon a reboot of the Node.
     */
    public BigInteger overrunCount; // 12 uint64 R V

    // Enums
    public enum SecurityTypeEnum implements MatterEnum {
        UNSPECIFIED(0, "Unspecified"),
        NONE(1, "None"),
        WEP(2, "Wep"),
        WPA(3, "Wpa"),
        WPA2(4, "Wpa2"),
        WPA3(5, "Wpa3");

        public final Integer value;
        public final String label;

        private SecurityTypeEnum(Integer value, String label) {
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

    public enum WiFiVersionEnum implements MatterEnum {
        A(0, "A"),
        B(1, "B"),
        G(2, "G"),
        N(3, "N"),
        AC(4, "Ac"),
        AX(5, "Ax"),
        AH(6, "Ah");

        public final Integer value;
        public final String label;

        private WiFiVersionEnum(Integer value, String label) {
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

    public enum AssociationFailureCauseEnum implements MatterEnum {
        UNKNOWN(0, "Unknown"),
        ASSOCIATION_FAILED(1, "AssociationFailed"),
        AUTHENTICATION_FAILED(2, "AuthenticationFailed"),
        SSID_NOT_FOUND(3, "SsidNotFound");

        public final Integer value;
        public final String label;

        private AssociationFailureCauseEnum(Integer value, String label) {
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

    public enum ConnectionStatusEnum implements MatterEnum {
        CONNECTED(0, "Connected"),
        NOT_CONNECTED(1, "NotConnected");

        public final Integer value;
        public final String label;

        private ConnectionStatusEnum(Integer value, String label) {
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

    // Bitmaps
    public static class FeatureMap {
        /**
         * PacketCounts
         * Node makes available the counts for the number of received and transmitted packets on the Wi-Fi interface.
         */
        public boolean packetCounts;
        /**
         * ErrorCounts
         * Node makes available the counts for the number of errors that have occurred during the reception and
         * transmission of packets on the Wi-Fi interface.
         */
        public boolean errorCounts;

        public FeatureMap(boolean packetCounts, boolean errorCounts) {
            this.packetCounts = packetCounts;
            this.errorCounts = errorCounts;
        }
    }

    public WiFiNetworkDiagnosticsCluster(BigInteger nodeId, int endpointId) {
        super(nodeId, endpointId, 54, "WiFiNetworkDiagnostics");
    }

    // commands
    /**
     * Reception of this command shall reset the following attributes to 0:
     * • BeaconLostCount
     * • BeaconRxCount
     * • PacketMulticastRxCount
     * • PacketMulticastTxCount
     * • PacketUnicastRxCount
     * • PacketUnicastTxCount
     * This command has no associated data.
     */
    public static ClusterCommand resetCounts() {
        return new ClusterCommand("resetCounts");
    }

    @Override
    public @NonNull String toString() {
        String str = "";
        str += "clusterRevision : " + clusterRevision + "\n";
        str += "featureMap : " + featureMap + "\n";
        str += "bssid : " + bssid + "\n";
        str += "securityType : " + securityType + "\n";
        str += "wiFiVersion : " + wiFiVersion + "\n";
        str += "channelNumber : " + channelNumber + "\n";
        str += "rssi : " + rssi + "\n";
        str += "beaconLostCount : " + beaconLostCount + "\n";
        str += "beaconRxCount : " + beaconRxCount + "\n";
        str += "packetMulticastRxCount : " + packetMulticastRxCount + "\n";
        str += "packetMulticastTxCount : " + packetMulticastTxCount + "\n";
        str += "packetUnicastRxCount : " + packetUnicastRxCount + "\n";
        str += "packetUnicastTxCount : " + packetUnicastTxCount + "\n";
        str += "currentMaxRate : " + currentMaxRate + "\n";
        str += "overrunCount : " + overrunCount + "\n";
        return str;
    }
}
