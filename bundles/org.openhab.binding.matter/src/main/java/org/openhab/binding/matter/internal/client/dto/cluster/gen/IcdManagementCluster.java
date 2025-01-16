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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNull;
import org.openhab.binding.matter.internal.client.dto.cluster.ClusterCommand;

/**
 * IcdManagement
 *
 * @author Dan Cunningham - Initial contribution
 */
public class IcdManagementCluster extends BaseCluster {

    public static final String CLUSTER_NAME = "IcdManagement";
    public static final int CLUSTER_ID = 0x0046;

    public Integer clusterRevision; // 65533 ClusterRevision
    public FeatureMap featureMap; // 65532 FeatureMap
    /**
     * Indicates the maximum interval in seconds the server can stay in idle mode. The IdleModeDuration shall NOT be
     * smaller than the ActiveModeDuration.
     */
    public Integer idleModeDuration; // 0 uint32 R V
    /**
     * Indicates the minimum interval in milliseconds the server typically will stay in active mode after initial
     * transition out of idle mode. The ActiveModeDuration does not include the ActiveModeThreshold.
     */
    public Integer activeModeDuration; // 1 uint32 R V
    /**
     * Indicates the minimum amount of time in milliseconds the server typically will stay active after network activity
     * when in active mode.
     */
    public Integer activeModeThreshold; // 2 uint16 R V
    /**
     * This attribute shall contain all clients registered to receive notification if their subscription is lost. The
     * maximum number of entries that can be in the list shall be ClientsSupportedPerFabric for each fabric supported on
     * the server, as indicated by the value of the SupportedFabrics attribute in the Operational Credentials cluster.
     */
    public List<MonitoringRegistrationStruct> registeredClients; // 3 list R F A
    /**
     * This attribute returns the value of the ICD Counter.
     */
    public Integer icdCounter; // 4 uint32 R A
    /**
     * Indicates the maximum number of entries that the server is able to store for each fabric in the RegisteredClients
     * attribute.
     */
    public Integer clientsSupportedPerFabric; // 5 uint16 R V
    /**
     * Indicates which user action(s) will trigger the ICD to switch to Active mode. If the attribute indicates support
     * for a trigger that is dependent on the UserActiveModeTriggerInstruction in the UserActiveModeTriggerHint table,
     * the UserActiveModeTriggerInstruction attribute shall be implemented and shall provide the required information,
     * unless specified otherwise in the requirement column of the UserActiveModeTriggerHint table.
     * ActuateSensorLightsBlink, ResetButtonLightsBlink and SetupButtonLightsBlink (i.e. bits 7, 9 and 14) have a
     * dependency on the UserActiveModeTriggerInstruction attribute but do not require the attribute to be present.
     * An ICD can indicate multiple ways of being put into Active Mode by setting multiple bits in the bitmap at the
     * same time. However, a device shall NOT set more than one bit which has a dependency on the
     * UserActiveModeTriggerInstruction attribute.
     */
    public UserActiveModeTriggerBitmap userActiveModeTriggerHint; // 6 UserActiveModeTriggerBitmap R V
    /**
     * The meaning of the attribute is dependent upon the UserActiveModeTriggerHint attribute value, and the conformance
     * is in indicated in the &quot;dependency&quot; column in UserActiveModeTriggerHint table. The
     * UserActiveModeTriggerInstruction attribute may give additional information on how to transition the device to
     * Active Mode. If the attribute is present, the value shall be encoded as a valid UTF-8 string with a maximum
     * length of 128 bytes. If the UserActiveModeTriggerHint has the ActuateSensorSeconds, ActuateSensorTimes,
     * ResetButtonSeconds, ResetButtonTimes, SetupButtonSeconds or SetupButtonTimes set, the string shall consist solely
     * of an encoding of N as a decimal
     * unsigned integer using the ASCII digits 0-9, and without leading zeros.
     * For example, given UserActiveModeTriggerHint&#x3D;&quot;2048&quot;, ResetButtonTimes is set which indicates
     * &quot;Press Reset Button for N seconds&quot;. Therefore, a value of
     * UserActiveModeTriggerInstruction&#x3D;&quot;10&quot; would indicate that N is 10 in that context.
     * When CustomInstruction is set by the UserActiveModeTriggerHint attribute, indicating presence of a custom string,
     * the ICD SHOULD perform localization (translation to user’s preferred language, as indicated in the Device’s
     * currently configured locale). The Custom Instruction option SHOULD NOT be used by an ICD that does not have
     * knowledge of the user’s language preference.
     * ### When the UserActiveModeTriggerHint key indicates a light to blink (ActuateSensorLightsBlink,
     * ResetButtonLightsBlink or SetupButtonLightsBlink), information on color of light may be made available via the
     * UserActiveModeTriggerInstruction attribute. When using such color indication in the
     * UserActiveModeTriggerInstruction attribute, only basic primary and secondary colors that could unambiguously be
     * decoded by a commissioner and understood by an end-user, but without worry of localization, SHOULD be used, e.g.
     * white, red, green, blue, orange, yellow, purple. The length of the attribute SHOULD be kept small.
     */
    public String userActiveModeTriggerInstruction; // 7 string R V
    /**
     * Indicates the operating mode of the ICD as specified in the OperatingModeEnum.
     * • If the ICD is operating as a LIT ICD, OperatingMode shall be LIT.
     * • If the ICD is operating as a SIT ICD, OperatingMode shall be SIT.
     */
    public OperatingModeEnum operatingMode; // 8 OperatingModeEnum R V
    // Structs

    public class MonitoringRegistrationStruct {
        /**
         * This field shall indicate the NodeID of the Node to which Check-In messages will be sent when the
         * MonitoredSubject is not subscribed.
         */
        public BigInteger checkInNodeId; // node-id
        /**
         * This field shall indicate the monitored Subject ID. This field shall be used to determine if a particular
         * client has an active subscription for the given entry. The MonitoredSubject, when it is a NodeID, may be the
         * same as the CheckInNodeID. The MonitoredSubject gives the registering client the flexibility of having a
         * different CheckInNodeID from the MonitoredSubject. A subscription shall count as an active subscription for
         * this entry if:
         * • It is on the associated fabric of this entry, and
         * • The subject of this entry matches the ISD of the SubscriptionRequest message that created the subscription.
         * Matching shall be determined using the subject_matches function defined in the Access Control Privilege
         * Granting Algorithm.
         * For example, if the MonitoredSubject is Node ID 0x1111_2222_3333_AAAA, and one of the subscribers to the
         * server on the entry’s associated fabric bears that Node ID, then the entry matches.
         * Another example is if the MonitoredSubject has the value 0xFFFF_FFFD_AA12_0002, and one of the subscribers to
         * the server on the entry’s associated fabric bears the CASE Authenticated TAG value 0xAA12 and the version
         * 0x0002 or higher within its NOC, then the entry matches.
         */
        public BigInteger monitoredSubject; // subject-id
        public String key; //
        public Integer fabricIndex; // FabricIndex

        public MonitoringRegistrationStruct(BigInteger checkInNodeId, BigInteger monitoredSubject, String key,
                Integer fabricIndex) {
            this.checkInNodeId = checkInNodeId;
            this.monitoredSubject = monitoredSubject;
            this.key = key;
            this.fabricIndex = fabricIndex;
        }
    }

    // Enums
    public enum OperatingModeEnum implements MatterEnum {
        SIT(0, "Sit"),
        LIT(1, "Lit");

        public final Integer value;
        public final String label;

        private OperatingModeEnum(Integer value, String label) {
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
    /**
     * See the UserActiveModeTriggerHint table for requirements associated to each bit.
     */
    public static class UserActiveModeTriggerBitmap {
        public boolean powerCycle;
        public boolean settingsMenu;
        public boolean customInstruction;
        public boolean deviceManual;
        public boolean actuateSensor;
        public boolean actuateSensorSeconds;
        public boolean actuateSensorTimes;
        public boolean actuateSensorLightsBlink;
        public boolean resetButton;
        public boolean resetButtonLightsBlink;
        public boolean resetButtonSeconds;
        public boolean resetButtonTimes;
        public boolean setupButton;
        public boolean setupButtonSeconds;
        public boolean setupButtonLightsBlink;
        public boolean setupButtonTimes;
        public boolean appDefinedButton;

        public UserActiveModeTriggerBitmap(boolean powerCycle, boolean settingsMenu, boolean customInstruction,
                boolean deviceManual, boolean actuateSensor, boolean actuateSensorSeconds, boolean actuateSensorTimes,
                boolean actuateSensorLightsBlink, boolean resetButton, boolean resetButtonLightsBlink,
                boolean resetButtonSeconds, boolean resetButtonTimes, boolean setupButton, boolean setupButtonSeconds,
                boolean setupButtonLightsBlink, boolean setupButtonTimes, boolean appDefinedButton) {
            this.powerCycle = powerCycle;
            this.settingsMenu = settingsMenu;
            this.customInstruction = customInstruction;
            this.deviceManual = deviceManual;
            this.actuateSensor = actuateSensor;
            this.actuateSensorSeconds = actuateSensorSeconds;
            this.actuateSensorTimes = actuateSensorTimes;
            this.actuateSensorLightsBlink = actuateSensorLightsBlink;
            this.resetButton = resetButton;
            this.resetButtonLightsBlink = resetButtonLightsBlink;
            this.resetButtonSeconds = resetButtonSeconds;
            this.resetButtonTimes = resetButtonTimes;
            this.setupButton = setupButton;
            this.setupButtonSeconds = setupButtonSeconds;
            this.setupButtonLightsBlink = setupButtonLightsBlink;
            this.setupButtonTimes = setupButtonTimes;
            this.appDefinedButton = appDefinedButton;
        }
    }

    public static class FeatureMap {
        /**
         * CheckInProtocolSupport
         * When this feature is supported, the device shall support all the associated commands and attributes to
         * properly support the Check-In Protocol.
         */
        public boolean checkInProtocolSupport;
        /**
         * UserActiveModeTrigger
         * This feature is supported if and only if the device has a user active mode trigger.
         */
        public boolean userActiveModeTrigger;
        /**
         * LongIdleTimeSupport
         * This feature is supported if and only the device is a Long Idle Time ICD.
         * NOTE In this version of the specification, the support for the feature is provisional.
         */
        public boolean longIdleTimeSupport;

        public FeatureMap(boolean checkInProtocolSupport, boolean userActiveModeTrigger, boolean longIdleTimeSupport) {
            this.checkInProtocolSupport = checkInProtocolSupport;
            this.userActiveModeTrigger = userActiveModeTrigger;
            this.longIdleTimeSupport = longIdleTimeSupport;
        }
    }

    public IcdManagementCluster(BigInteger nodeId, int endpointId) {
        super(nodeId, endpointId, 70, "IcdManagement");
    }

    // commands
    /**
     * This command allows a client to register itself with the ICD to be notified when the device is available for
     * communication.
     */
    public static ClusterCommand registerClient(BigInteger checkInNodeId, BigInteger monitoredSubject, String key,
            String verificationKey) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("checkInNodeId", checkInNodeId);
        map.put("monitoredSubject", monitoredSubject);
        map.put("key", key);
        map.put("verificationKey", verificationKey);
        return new ClusterCommand("registerClient", map);
    }

    /**
     * This command allows a client to unregister itself with the ICD. Example: a client that is leaving the network
     * (e.g. running on a phone which is leaving the home) can (and should) remove its subscriptions and send this
     * UnregisterClient command before leaving to prevent the burden on the ICD of an absent client.
     */
    public static ClusterCommand unregisterClient(BigInteger checkInNodeId, String verificationKey) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("checkInNodeId", checkInNodeId);
        map.put("verificationKey", verificationKey);
        return new ClusterCommand("unregisterClient", map);
    }

    /**
     * This command allows a client to request that the server stays in active mode for at least a given time duration
     * (in milliseconds) from when this command is received.
     * This StayActiveDuration may be longer than the ActiveModeThreshold value and would, typically, be used by the
     * client to request the server to stay active and responsive for this period to allow a sequence of message
     * exchanges during that period. The client may slightly overestimate the duration it wants the ICD to be active
     * for, in order to account for network delays.
     */
    public static ClusterCommand stayActiveRequest(Integer stayActiveDuration) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("stayActiveDuration", stayActiveDuration);
        return new ClusterCommand("stayActiveRequest", map);
    }

    @Override
    public @NonNull String toString() {
        String str = "";
        str += "clusterRevision : " + clusterRevision + "\n";
        str += "featureMap : " + featureMap + "\n";
        str += "idleModeDuration : " + idleModeDuration + "\n";
        str += "activeModeDuration : " + activeModeDuration + "\n";
        str += "activeModeThreshold : " + activeModeThreshold + "\n";
        str += "registeredClients : " + registeredClients + "\n";
        str += "icdCounter : " + icdCounter + "\n";
        str += "clientsSupportedPerFabric : " + clientsSupportedPerFabric + "\n";
        str += "userActiveModeTriggerHint : " + userActiveModeTriggerHint + "\n";
        str += "userActiveModeTriggerInstruction : " + userActiveModeTriggerInstruction + "\n";
        str += "operatingMode : " + operatingMode + "\n";
        return str;
    }
}
