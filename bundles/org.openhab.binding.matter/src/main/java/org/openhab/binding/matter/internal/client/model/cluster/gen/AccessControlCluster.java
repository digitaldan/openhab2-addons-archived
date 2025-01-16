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
import java.util.List;

import org.eclipse.jdt.annotation.NonNull;

/**
 * AccessControl
 *
 * @author Dan Cunningham - Initial contribution
 */
public class AccessControlCluster extends BaseCluster {

    public static final String CLUSTER_NAME = "AccessControl";
    public static final int CLUSTER_ID = 0x001F;

    public Integer clusterRevision; // 65533 ClusterRevision
    /**
     * An attempt to add an Access Control Entry when no more entries are available shall result in a RESOURCE_EXHAUSTED
     * error being reported and the ACL attribute shall NOT have the entry added to it. See access control limits.
     * See the AccessControlEntriesPerFabric attribute for the actual value of the number of entries per fabric
     * supported by the server.
     * Each Access Control Entry codifies a single grant of privilege on this Node, and is used by the Access Control
     * Privilege Granting algorithm to determine if a subject has privilege to interact with targets on the Node.
     */
    public List<AccessControlEntryStruct> acl; // 0 list RW F A
    /**
     * If present, the Access Control Extensions may be used by Administrators to store arbitrary data related to
     * fabric’s Access Control Entries.
     * The Access Control Extension list shall support a single extension entry per supported fabric.
     */
    public List<AccessControlExtensionStruct> extension; // 1 list RW F A
    /**
     * This attribute shall provide the minimum number of Subjects per entry that are supported by this server.
     * Since reducing this value over time may invalidate ACL entries already written, this value shall NOT decrease
     * across time as software updates occur that could impact this value. If this is a concern for a given
     * implementation, it is recommended to only use the minimum value required and avoid reporting a higher value than
     * the required minimum.
     */
    public Integer subjectsPerAccessControlEntry; // 2 uint16 R V
    /**
     * This attribute shall provide the minimum number of Targets per entry that are supported by this server.
     * Since reducing this value over time may invalidate ACL entries already written, this value shall NOT decrease
     * across time as software updates occur that could impact this value. If this is a concern for a given
     * implementation, it is recommended to only use the minimum value required and avoid reporting a higher value than
     * the required minimum.
     */
    public Integer targetsPerAccessControlEntry; // 3 uint16 R V
    /**
     * This attribute shall provide the minimum number of ACL Entries per fabric that are supported by this server.
     * Since reducing this value over time may invalidate ACL entries already written, this value shall NOT decrease
     * across time as software updates occur that could impact this value. If this is a concern for a given
     * implementation, it is recommended to only use the minimum value required and avoid reporting a higher value than
     * the required minimum.
     */
    public Integer accessControlEntriesPerFabric; // 4 uint16 R V
    // Structs

    /**
     * The cluster shall send AccessControlEntryChanged events whenever its ACL attribute data is changed by an
     * Administrator.
     * • Each added entry shall generate an event with ChangeType Added.
     * • Each changed entry shall generate an event with ChangeType Changed.
     * • Each removed entry shall generate an event with ChangeType Removed.
     */
    public class AccessControlEntryChanged {
        /**
         * The Node ID of the Administrator that made the change, if the change occurred via a CASE session.
         * Exactly one of AdminNodeID and AdminPasscodeID shall be set, depending on whether the change occurred via a
         * CASE or PASE session; the other shall be null.
         */
        public BigInteger adminNodeId; // node-id
        /**
         * The Passcode ID of the Administrator that made the change, if the change occurred via a PASE session.
         * Non-zero values are reserved for future use (see PasscodeId generation in PBKDFParamRequest).
         * Exactly one of AdminNodeID and AdminPasscodeID shall be set, depending on whether the change occurred via a
         * CASE or PASE session; the other shall be null.
         */
        public Integer adminPasscodeId; // uint16
        /**
         * The type of change as appropriate.
         */
        public ChangeTypeEnum changeType; // ChangeTypeEnum
        /**
         * The latest value of the changed entry.
         * This field SHOULD be set if resources are adequate for it; otherwise it shall be set to NULL if resources are
         * scarce.
         */
        public AccessControlEntryStruct latestValue; // AccessControlEntryStruct
        public Integer fabricIndex; // FabricIndex

        public AccessControlEntryChanged(BigInteger adminNodeId, Integer adminPasscodeId, ChangeTypeEnum changeType,
                AccessControlEntryStruct latestValue, Integer fabricIndex) {
            this.adminNodeId = adminNodeId;
            this.adminPasscodeId = adminPasscodeId;
            this.changeType = changeType;
            this.latestValue = latestValue;
            this.fabricIndex = fabricIndex;
        }
    }

    /**
     * The cluster shall send AccessControlExtensionChanged events whenever its extension attribute data is changed by
     * an Administrator.
     * • Each added extension shall generate an event with ChangeType Added.
     * • Each changed extension shall generate an event with ChangeType Changed.
     * • Each removed extension shall generate an event with ChangeType Removed.
     */
    public class AccessControlExtensionChanged {
        /**
         * The Node ID of the Administrator that made the change, if the change occurred via a CASE session.
         * Exactly one of AdminNodeID and AdminPasscodeID shall be set, depending on whether the change occurred via a
         * CASE or PASE session; the other shall be null.
         */
        public BigInteger adminNodeId; // node-id
        /**
         * The Passcode ID of the Administrator that made the change, if the change occurred via a PASE session.
         * Non-zero values are reserved for future use (see PasscodeId generation in PBKDFParamRequest).
         * Exactly one of AdminNodeID and AdminPasscodeID shall be set, depending on whether the change occurred via a
         * CASE or PASE session; the other shall be null.
         */
        public Integer adminPasscodeId; // uint16
        /**
         * The type of change as appropriate.
         */
        public ChangeTypeEnum changeType; // ChangeTypeEnum
        /**
         * The latest value of the changed extension.
         * This field SHOULD be set if resources are adequate for it; otherwise it shall be set to NULL if resources are
         * scarce.
         */
        public AccessControlExtensionStruct latestValue; // AccessControlExtensionStruct
        public Integer fabricIndex; // FabricIndex

        public AccessControlExtensionChanged(BigInteger adminNodeId, Integer adminPasscodeId, ChangeTypeEnum changeType,
                AccessControlExtensionStruct latestValue, Integer fabricIndex) {
            this.adminNodeId = adminNodeId;
            this.adminPasscodeId = adminPasscodeId;
            this.changeType = changeType;
            this.latestValue = latestValue;
            this.fabricIndex = fabricIndex;
        }
    }

    public class AccessControlTargetStruct {
        public Integer cluster; // cluster-id
        public Integer endpoint; // endpoint-no
        public Integer deviceType; // devtype-id

        public AccessControlTargetStruct(Integer cluster, Integer endpoint, Integer deviceType) {
            this.cluster = cluster;
            this.endpoint = endpoint;
            this.deviceType = deviceType;
        }
    }

    public class AccessControlEntryStruct {
        /**
         * The privilege field shall specify the level of privilege granted by this Access Control Entry.
         * NOTE The Proxy View privilege is provisional.
         * Each privilege builds upon its predecessor, expanding the set of actions that can be performed upon a Node.
         * Administer is the highest privilege, and is special as it pertains to the administration of privileges
         * itself, via the Access Control Cluster.
         * When a Node is granted a particular privilege, it is also implicitly granted all logically lower privilege
         * levels as well. The following diagram illustrates how the higher privilege levels subsume the lower privilege
         * levels:
         * Figure 43. Access Control Privilege Levels
         * Individual clusters shall define whether attributes are readable, writable, or both readable and writable.
         * Clusters also shall define which privilege is minimally required to be able to perform a particular read or
         * write action on those attributes, or invoke particular commands. Device type specifications may further
         * restrict the privilege required.
         * The Access Control Cluster shall require the Administer privilege to observe and modify the Access Control
         * Cluster itself. The Administer privilege shall NOT be used on Access Control Entries which use the Group auth
         * mode.
         */
        public AccessControlEntryPrivilegeEnum privilege; // AccessControlEntryPrivilegeEnum
        /**
         * The AuthMode field shall specify the authentication mode required by this Access Control Entry.
         */
        public AccessControlEntryAuthModeEnum authMode; // AccessControlEntryAuthModeEnum
        /**
         * The subjects field shall specify a list of Subject IDs, to which this Access Control Entry grants access.
         * Device types may impose additional constraints on the minimum number of subjects per Access Control Entry.
         * An attempt to create an entry with more subjects than the node can support shall result in a
         * RESOURCE_EXHAUSTED error and the entry shall NOT be created.
         * ### Subject ID shall be of type uint64 with semantics depending on the entry’s AuthMode as follows:
         * Subject Semantics
         * An empty subjects list indicates a wildcard; that is, this entry shall grant access to any Node that
         * successfully authenticates via AuthMode. The subjects list shall NOT be empty if the entry’s AuthMode is
         * PASE.
         * The PASE AuthMode is reserved for future use (see Section 6.6.2.8, “Bootstrapping of the Access Control
         * Cluster”). An attempt to write an entry with AuthMode set to PASE shall fail with a status code of
         * CONSTRAINT_ERROR.
         * For PASE authentication, the Passcode ID identifies the required passcode verifier, and shall be 0 for the
         * default commissioning passcode.
         * For CASE authentication, the Subject ID is a distinguished name within the Operational Certificate shared
         * during CASE session establishment, the type of which is determined by its range to be one of:
         * • a Node ID, which identifies the required source node directly (by ID)
         * • a CASE Authenticated Tag, which identifies the required source node indirectly (by tag)
         * For Group authentication, the Group ID identifies the required group, as defined in the Group Key Management
         * Cluster.
         */
        public List<BigInteger> subjects; // list
        /**
         * The targets field shall specify a list of AccessControlTargetStruct, which define the clusters on this Node
         * to which this Access Control Entry grants access.
         * Device types may impose additional constraints on the minimum number of targets per Access Control Entry.
         * An attempt to create an entry with more targets than the node can support shall result in a
         * RESOURCE_EXHAUSTED error and the entry shall NOT be created.
         * A single target shall contain at least one field (Cluster, Endpoint, or DeviceType), and shall NOT contain
         * both an Endpoint field and a DeviceType field.
         * A target grants access based on the presence of fields as follows:
         * Target Semantics
         * An empty targets list indicates a wildcard: that is, this entry shall grant access to all cluster instances
         * on all endpoints on this Node.
         */
        public List<AccessControlTargetStruct> targets; // list
        public Integer fabricIndex; // FabricIndex

        public AccessControlEntryStruct(AccessControlEntryPrivilegeEnum privilege,
                AccessControlEntryAuthModeEnum authMode, List<BigInteger> subjects,
                List<AccessControlTargetStruct> targets, Integer fabricIndex) {
            this.privilege = privilege;
            this.authMode = authMode;
            this.subjects = subjects;
            this.targets = targets;
            this.fabricIndex = fabricIndex;
        }
    }

    public class AccessControlExtensionStruct {
        /**
         * This field may be used by manufacturers to store arbitrary TLV-encoded data related to a fabric’s Access
         * Control Entries.
         * The contents shall consist of a top-level anonymous list; each list element shall include a profile-specific
         * tag encoded in fully-qualified form.
         * Administrators may iterate over this list of elements, and interpret selected elements at their discretion.
         * The content of each element is not specified, but may be coordinated among manufacturers at their discretion.
         */
        public String data; // octstr
        public Integer fabricIndex; // FabricIndex

        public AccessControlExtensionStruct(String data, Integer fabricIndex) {
            this.data = data;
            this.fabricIndex = fabricIndex;
        }
    }

    // Enums
    public enum ChangeTypeEnum implements MatterEnum {
        CHANGED(0, "Changed"),
        ADDED(1, "Added"),
        REMOVED(2, "Removed");

        public final Integer value;
        public final String label;

        private ChangeTypeEnum(Integer value, String label) {
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
     * Proxy View Value
     * This value implicitly grants View privileges
     */
    public enum AccessControlEntryPrivilegeEnum implements MatterEnum {
        VIEW(1, "View"),
        PROXY_VIEW(2, "ProxyView"),
        OPERATE(3, "Operate"),
        MANAGE(4, "Manage"),
        ADMINISTER(5, "Administer");

        public final Integer value;
        public final String label;

        private AccessControlEntryPrivilegeEnum(Integer value, String label) {
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

    public enum AccessControlEntryAuthModeEnum implements MatterEnum {
        PASE(1, "Pase"),
        CASE(2, "Case"),
        GROUP(3, "Group");

        public final Integer value;
        public final String label;

        private AccessControlEntryAuthModeEnum(Integer value, String label) {
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

    public AccessControlCluster(BigInteger nodeId, int endpointId) {
        super(nodeId, endpointId, 31, "AccessControl");
    }

    @Override
    public @NonNull String toString() {
        String str = "";
        str += "clusterRevision : " + clusterRevision + "\n";
        str += "acl : " + acl + "\n";
        str += "extension : " + extension + "\n";
        str += "subjectsPerAccessControlEntry : " + subjectsPerAccessControlEntry + "\n";
        str += "targetsPerAccessControlEntry : " + targetsPerAccessControlEntry + "\n";
        str += "accessControlEntriesPerFabric : " + accessControlEntriesPerFabric + "\n";
        return str;
    }
}
