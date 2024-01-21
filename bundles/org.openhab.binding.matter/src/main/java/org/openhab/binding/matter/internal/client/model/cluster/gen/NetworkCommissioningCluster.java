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

// AUTO-GENERATED by zap. DO NOT EDIT!

package org.openhab.binding.matter.internal.client.model.cluster.gen;

import java.util.List;
import java.util.Map;

import org.openhab.binding.matter.internal.client.model.cluster.BaseCluster;
import org.openhab.binding.matter.internal.client.model.cluster.gen.NetworkCommissioningClusterTypes.*;

/**
 * NetworkCommissioning
 *
 * @author Dan Cunningham - Initial contribution
 */
public class NetworkCommissioningCluster extends BaseCluster {

    public static final String CLUSTER_NAME = "NetworkCommissioning";
    public static final int CLUSTER_ID = 0x0031;

    public Integer maxNetworks; // 0 int8u reportable
    public NetworkInfoStruct[] networks; // 1 NetworkInfoStruct reportable
    public Integer scanMaxTimeSeconds; // 2 int8u reportable
    public Integer connectMaxTimeSeconds; // 3 int8u reportable
    public Boolean interfaceEnabled; // 4 boolean reportable writable
    public NetworkCommissioningStatusEnum lastNetworkingStatus; // 5 NetworkCommissioningStatusEnum reportable
    public String lastNetworkID; // 6 octet_string reportable
    public Integer lastConnectErrorValue; // 7 int32s reportable
    public WiFiBandEnum supportedWiFiBands; // 8 WiFiBandEnum reportable
    public ThreadCapabilitiesBitmap supportedThreadFeatures; // 9 ThreadCapabilitiesBitmap reportable
    public Integer threadVersion; // 10 int16u reportable
    public List<Integer> generatedCommandList; // 65528 command_id reportable
    public List<Integer> acceptedCommandList; // 65529 command_id reportable
    public List<Integer> eventList; // 65530 event_id reportable
    public List<Integer> attributeList; // 65531 attrib_id reportable
    public Map<String, Boolean> featureMap; // 65532 bitmap32 reportable
    public Integer clusterRevision; // 65533 int16u reportable

    public NetworkCommissioningCluster(long nodeId, int endpointId) {
        super(nodeId, endpointId, 98, "NetworkCommissioning");
    }

    public String toString() {
        String str = "";
        str += "maxNetworks : " + maxNetworks + "\n";
        str += "networks : " + networks + "\n";
        str += "scanMaxTimeSeconds : " + scanMaxTimeSeconds + "\n";
        str += "connectMaxTimeSeconds : " + connectMaxTimeSeconds + "\n";
        str += "interfaceEnabled : " + interfaceEnabled + "\n";
        str += "lastNetworkingStatus : " + lastNetworkingStatus + "\n";
        str += "lastNetworkID : " + lastNetworkID + "\n";
        str += "lastConnectErrorValue : " + lastConnectErrorValue + "\n";
        str += "supportedWiFiBands : " + supportedWiFiBands + "\n";
        str += "supportedThreadFeatures : " + supportedThreadFeatures + "\n";
        str += "threadVersion : " + threadVersion + "\n";
        str += "generatedCommandList : " + generatedCommandList + "\n";
        str += "acceptedCommandList : " + acceptedCommandList + "\n";
        str += "eventList : " + eventList + "\n";
        str += "attributeList : " + attributeList + "\n";
        str += "featureMap : " + featureMap + "\n";
        str += "clusterRevision : " + clusterRevision + "\n";
        return str;
    }
}
