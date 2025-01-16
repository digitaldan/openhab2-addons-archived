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
 * FixedLabel
 *
 * @author Dan Cunningham - Initial contribution
 */
public class FixedLabelCluster extends BaseCluster {

    public static final String CLUSTER_NAME = "FixedLabel";
    public static final int CLUSTER_ID = 0x0040;

    public Integer clusterRevision; // 65533 ClusterRevision
    public List<LabelStruct> labelList; // 0 list R V
    // Structs

    /**
     * This is a string tuple with strings that are user defined.
     */
    public class LabelStruct {
        /**
         * The Label or Value semantic is not defined here. Label examples: &quot;room&quot;, &quot;zone&quot;,
         * &quot;group&quot;, &quot;direction&quot;.
         */
        public String label; // string
        /**
         * The Label or Value semantic is not defined here. The Value is a discriminator for a Label that may have
         * multiple instances. Label:Value examples: &quot;room&quot;:&quot;bedroom 2&quot;,
         * &quot;orientation&quot;:&quot;North&quot;, &quot;floor&quot;:&quot;2&quot;,
         * &quot;direction&quot;:&quot;up&quot;
         */
        public String value; // string

        public LabelStruct(String label, String value) {
            this.label = label;
            this.value = value;
        }
    }

    public FixedLabelCluster(BigInteger nodeId, int endpointId) {
        super(nodeId, endpointId, 64, "FixedLabel");
    }

    @Override
    public @NonNull String toString() {
        String str = "";
        str += "clusterRevision : " + clusterRevision + "\n";
        str += "labelList : " + labelList + "\n";
        return str;
    }
}
