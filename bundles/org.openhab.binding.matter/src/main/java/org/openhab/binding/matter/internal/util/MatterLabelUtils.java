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
package org.openhab.binding.matter.internal.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.matter.internal.client.dto.Endpoint;
import org.openhab.binding.matter.internal.client.dto.cluster.gen.BaseCluster;
import org.openhab.binding.matter.internal.client.dto.cluster.gen.BasicInformationCluster;
import org.openhab.binding.matter.internal.client.dto.cluster.gen.BridgedDeviceBasicInformationCluster;
import org.openhab.binding.matter.internal.client.dto.cluster.gen.DescriptorCluster;
import org.openhab.binding.matter.internal.client.dto.cluster.gen.DescriptorCluster.DeviceTypeStruct;
import org.openhab.binding.matter.internal.client.dto.cluster.gen.DeviceTypes;
import org.openhab.binding.matter.internal.client.dto.cluster.gen.FixedLabelCluster;

/**
 * @author Dan Cunningham - Initial contribution
 *
 */
@NonNullByDefault
public class MatterLabelUtils {

    public static String normalizeString(@Nullable String input) {
        /*
         * \\p{C}: Matches control characters (category Cc).
         * \\p{Z}: Matches whitespace characters.
         * &&[^\\u0020]: Excludes the regular space (\u0020) from removal.
         */
        return input == null ? "" : input.replaceAll("[\\p{C}\\p{Z}&&[^\\u0020]]", "").trim();
    }

    public static String labelForNode(Endpoint root) {
        String label = "";
        String vendorName = "";
        String productName = "";
        String nodeLabel = "";

        BaseCluster cluster = root.clusters.get(BasicInformationCluster.CLUSTER_NAME);
        if (cluster != null && cluster instanceof BasicInformationCluster basicCluster) {
            vendorName = normalizeString(basicCluster.vendorName);
            productName = normalizeString(basicCluster.productName);
            nodeLabel = normalizeString(basicCluster.nodeLabel);
        }

        if (!nodeLabel.isEmpty()) {
            label = nodeLabel;
        } else {
            label = productName.startsWith(vendorName) ? productName : vendorName + " " + productName;
        }

        return label.trim();
    }

    public static String labelForEndpoint(Endpoint endpoint) {
        Map<String, BaseCluster> clusters = endpoint.clusters;
        Object basicInfoObject = clusters.get(BasicInformationCluster.CLUSTER_NAME);

        Integer deviceTypeID = primaryDeviceTypeForEndpoint(endpoint);

        // labels will look like "Device Type : Custom Node Label Or Product Label"
        final StringBuffer label = new StringBuffer(splitAndCapitalize(DeviceTypes.DEVICE_MAPPING.get(deviceTypeID)))
                .append(": ");

        // Check if a "nodeLabel" is set, otherwise use the product label. This varies from vendor to vendor
        if (basicInfoObject != null) {
            BasicInformationCluster basicInfo = (BasicInformationCluster) basicInfoObject;
            String basicInfoString = normalizeString(basicInfo.nodeLabel);
            label.append(basicInfoString.length() > 0 ? basicInfoString : normalizeString(basicInfo.productLabel));
        }

        // Fixed labels are a way of vendors to label endpoints with additional meta data.
        if (clusters.get(FixedLabelCluster.CLUSTER_NAME) instanceof FixedLabelCluster fixedLabelCluster) {
            fixedLabelCluster.labelList
                    .forEach(fixedLabel -> label.append(" " + fixedLabel.label + " " + fixedLabel.value));
        }

        // label for the Group Channel
        return label.toString().trim();
    }

    public static String labelForBridgeEndpoint(Endpoint endpoint) {
        Map<String, BaseCluster> clusters = endpoint.clusters;
        Object basicInfoObject = clusters.get(BridgedDeviceBasicInformationCluster.CLUSTER_NAME);

        // labels will look like "Device Type : Custom Node Label Or Product Label"
        final StringBuffer label = new StringBuffer();
        // Check if a "nodeLabel" is set, otherwise use the product label. This varies from vendor to vendor
        if (basicInfoObject != null) {
            BridgedDeviceBasicInformationCluster basicInfo = (BridgedDeviceBasicInformationCluster) basicInfoObject;
            String nodeLabel = normalizeString(basicInfo.nodeLabel);
            String productLabel = normalizeString(basicInfo.productLabel);

            if (nodeLabel.length() > 0) {
                label.append(nodeLabel);
            } else {
                label.append(productLabel);
            }
        }

        if (label.length() == 0) {
            Integer deviceTypeID = primaryDeviceTypeForEndpoint(endpoint);
            String deviceTypeLabel = splitAndCapitalize(DeviceTypes.DEVICE_MAPPING.get(deviceTypeID));
            label.append(deviceTypeLabel + " (" + endpoint.number.toString() + ")");
        }

        // Fixed labels are a way of vendors to label endpoints with additional meta data.
        if (clusters.get(FixedLabelCluster.CLUSTER_NAME) instanceof FixedLabelCluster fixedLabelCluster) {
            fixedLabelCluster.labelList
                    .forEach(fixedLabel -> label.append(" " + fixedLabel.label + " " + fixedLabel.value));
        }

        // label for the Group Channel
        return label.toString().trim();
    }

    public static String splitAndCapitalize(@Nullable String camelCase) {
        if (camelCase == null) {
            return "";
        }
        return Pattern.compile("(?<=[a-z])(?=[A-Z])").splitAsStream(camelCase)
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
    }

    public static Integer primaryDeviceTypeForEndpoint(Endpoint endpoint) {
        // The matter spec requires a descriptor cluster and device type, so this should always be present
        DescriptorCluster descriptorCluster = (DescriptorCluster) endpoint.clusters.get(DescriptorCluster.CLUSTER_NAME);
        Integer deviceTypeID = -1;
        if (descriptorCluster != null && !descriptorCluster.deviceTypeList.isEmpty()) {
            for (DeviceTypeStruct ds : descriptorCluster.deviceTypeList) {
                // ignore bridge types
                if (!DeviceTypes.BridgedNode.equals(ds.deviceType) && !DeviceTypes.Aggregator.equals(ds.deviceType)) {
                    deviceTypeID = ds.deviceType;
                    break;
                }
            }
            if (deviceTypeID == -1) {
                deviceTypeID = descriptorCluster.deviceTypeList.get(0).deviceType;
            }
        }
        return deviceTypeID;
    }

    public static String formatMacAddress(@Nullable String mac) {
        if (mac == null) {
            return "";
        }
        return IntStream.range(0, mac.length()).filter(i -> i % 2 == 0).mapToObj(i -> mac.substring(i, i + 2))
                .collect(Collectors.joining(":"));
    }

    public static String formatIPv4Address(@Nullable String ipv4Hex) {
        if (ipv4Hex == null) {
            return "";
        }
        StringBuilder ipv4 = new StringBuilder();
        for (int i = 0; i < ipv4Hex.length(); i += 2) {
            int decimal = Integer.parseInt(ipv4Hex.substring(i, i + 2), 16);
            ipv4.append(decimal).append(".");
        }
        return ipv4.substring(0, ipv4.length() - 1); // Remove the trailing dot
    }

    public static String formatIPv6Address(@Nullable String ipv6Hex) {
        if (ipv6Hex == null) {
            return "";
        }
        try {
            byte[] bytes = new byte[ipv6Hex.length() / 2];
            for (int i = 0; i < ipv6Hex.length(); i += 2) {
                bytes[i / 2] = (byte) Integer.parseInt(ipv6Hex.substring(i, i + 2), 16);
            }
            InetAddress address = InetAddress.getByAddress(bytes);
            return address.getHostAddress();
        } catch (UnknownHostException e) {
            return ipv6Hex;
        }
    }
}
