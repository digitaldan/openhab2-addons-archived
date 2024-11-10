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

import org.openhab.binding.matter.internal.client.model.cluster.BaseCluster;
import org.openhab.binding.matter.internal.client.model.cluster.gen.DataTypes.*;

/**
 * BridgedDeviceBasicInformation
 *
 * @author Dan Cunningham - Initial contribution
 */
public class BridgedDeviceBasicInformationCluster extends BaseCluster {

    public static final String CLUSTER_NAME = "BridgedDeviceBasicInformation";
    public static final int CLUSTER_ID = 0x0039;

    public Integer clusterRevision; // 65533 ClusterRevision
    public String vendorName; // 1
    public String vendorId; // 2
    public String productName; // 3
    public String nodeLabel; // 5
    public String hardwareVersion; // 7
    public String hardwareVersionString; // 8
    public String softwareVersion; // 9
    public String softwareVersionString; // 10
    public String manufacturingDate; // 11
    public String partNumber; // 12
    public String productUrl; // 13
    public String productLabel; // 14
    public String serialNumber; // 15
    public String reachable; // 17
    public String uniqueId; // 18
    public String productAppearance; // 20
    // Structs

    /**
     * This structure provides a description of the product’s appearance.
     */
    public class ProductAppearanceStruct {
        /**
         * This field shall indicate the visible finish of the product.
         */
        public ProductFinishEnum finish; // ProductFinishEnum
        /**
         * This field indicates the representative color of the visible parts of the product. If the product has no
         * representative color, the field shall be null.
         */
        public ColorEnum primaryColor; // ColorEnum

        public ProductAppearanceStruct(ProductFinishEnum finish, ColorEnum primaryColor) {
            this.finish = finish;
            this.primaryColor = primaryColor;
        }
    }

    /**
     * This structure provides constant values related to overall global capabilities of this Node, that are not
     * cluster-specific.
     */
    public class CapabilityMinimaStruct {
        /**
         * This field shall indicate the actual minimum number of concurrent CASE sessions that are supported per
         * fabric.
         * This value shall NOT be smaller than the required minimum indicated in Section 4.14.2.8, “Minimal Number of
         * CASE Sessions”.
         */
        public Integer caseSessionsPerFabric; // uint16
        /**
         * This field shall indicate the actual minimum number of concurrent subscriptions supported per fabric.
         * This value shall NOT be smaller than the required minimum indicated in Section 8.5.1, “Subscribe
         * Transaction”.
         */
        public Integer subscriptionsPerFabric; // uint16

        public CapabilityMinimaStruct(Integer caseSessionsPerFabric, Integer subscriptionsPerFabric) {
            this.caseSessionsPerFabric = caseSessionsPerFabric;
            this.subscriptionsPerFabric = subscriptionsPerFabric;
        }
    }

    // Enums
    /**
     * The data type of ProductFinishEnum is derived from enum8.
     */
    public enum ProductFinishEnum implements MatterEnum {
        OTHER(0, "Other"),
        MATTE(1, "Matte"),
        SATIN(2, "Satin"),
        POLISHED(3, "Polished"),
        RUGGED(4, "Rugged"),
        FABRIC(5, "Fabric");

        public final Integer value;
        public final String label;

        private ProductFinishEnum(Integer value, String label) {
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
     * The data type of ColorEnum is derived from enum8.
     */
    public enum ColorEnum implements MatterEnum {
        BLACK(0, "Black"),
        NAVY(1, "Navy"),
        GREEN(2, "Green"),
        TEAL(3, "Teal"),
        MAROON(4, "Maroon"),
        PURPLE(5, "Purple"),
        OLIVE(6, "Olive"),
        GRAY(7, "Gray"),
        BLUE(8, "Blue"),
        LIME(9, "Lime"),
        AQUA(10, "Aqua"),
        RED(11, "Red"),
        FUCHSIA(12, "Fuchsia"),
        YELLOW(13, "Yellow"),
        WHITE(14, "White"),
        NICKEL(15, "Nickel"),
        CHROME(16, "Chrome"),
        BRASS(17, "Brass"),
        COPPER(18, "Copper"),
        SILVER(19, "Silver"),
        GOLD(20, "Gold");

        public final Integer value;
        public final String label;

        private ColorEnum(Integer value, String label) {
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

    public BridgedDeviceBasicInformationCluster(BigInteger nodeId, int endpointId) {
        super(nodeId, endpointId, 57, "BridgedDeviceBasicInformation");
    }

    public String toString() {
        String str = "";
        str += "clusterRevision : " + clusterRevision + "\n";
        str += "vendorName : " + vendorName + "\n";
        str += "vendorId : " + vendorId + "\n";
        str += "productName : " + productName + "\n";
        str += "nodeLabel : " + nodeLabel + "\n";
        str += "hardwareVersion : " + hardwareVersion + "\n";
        str += "hardwareVersionString : " + hardwareVersionString + "\n";
        str += "softwareVersion : " + softwareVersion + "\n";
        str += "softwareVersionString : " + softwareVersionString + "\n";
        str += "manufacturingDate : " + manufacturingDate + "\n";
        str += "partNumber : " + partNumber + "\n";
        str += "productUrl : " + productUrl + "\n";
        str += "productLabel : " + productLabel + "\n";
        str += "serialNumber : " + serialNumber + "\n";
        str += "reachable : " + reachable + "\n";
        str += "uniqueId : " + uniqueId + "\n";
        str += "productAppearance : " + productAppearance + "\n";
        return str;
    }
}
