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

// 

package org.openhab.binding.matter.internal.client.model.cluster.gen;

import org.openhab.binding.matter.internal.client.model.cluster.types.*;

/**
 * AUTO-GENERATED by zap. DO NOT EDIT!
 *
 * DishwasherMode
 *
 * @author Dan Cunningham - Initial contribution
 */
public class DishwasherModeClusterTypes {

    public static final String CLUSTER_NAME = "DISHWASHER_MODE_CLUSTER";
    public static final int CLUSTER_ID = 0x0059;

    public class ModeTagStruct {
        public Integer mfgCode; // vendor_id
        public Integer value; // enum16

        public ModeTagStruct(Integer mfgCode, Integer value) {
            this.mfgCode = mfgCode;
            this.value = value;
        }
    }

    public class ModeOptionStruct {
        public String label; // char_string
        public Integer mode; // int8u
        public ModeTagStruct[] modeTags; // ModeTagStruct

        public ModeOptionStruct(String label, Integer mode, ModeTagStruct[] modeTags) {
            this.label = label;
            this.mode = mode;
            this.modeTags = modeTags;
        }
    }

    // ZCL Enums
    public enum ModeTag {
        NORMAL(16384, "Normal"),
        HEAVY(16385, "Heavy"),
        LIGHT(16386, "Light"),
        UNKNOWN_VALUE(0, "UnknownValue");

        public final int value;
        public final String label;

        private ModeTag(int value, String label) {
            this.value = value;
            this.label = label;
        }
    };

    // ZCL Bitmaps
    public static class Feature {
        public boolean onOff;

        public Feature(boolean onOff) {
            this.onOff = onOff;
        }
    }

    public static class ChangeToModeCommandOptions {
        public Integer newMode;

        public ChangeToModeCommandOptions(Integer newMode) {
            this.newMode = newMode;
        }
    }

    public static class ChangeToModeResponseCommandOptions {
        public Integer status;
        public String statusText;

        public ChangeToModeResponseCommandOptions(Integer status, String statusText) {
            this.status = status;
            this.statusText = statusText;
        }
    }
}
