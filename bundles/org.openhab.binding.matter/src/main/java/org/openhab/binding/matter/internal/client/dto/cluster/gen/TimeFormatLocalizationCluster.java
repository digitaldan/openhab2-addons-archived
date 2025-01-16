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
 * TimeFormatLocalization
 *
 * @author Dan Cunningham - Initial contribution
 */
public class TimeFormatLocalizationCluster extends BaseCluster {

    public static final String CLUSTER_NAME = "TimeFormatLocalization";
    public static final int CLUSTER_ID = 0x002C;

    public Integer clusterRevision; // 65533 ClusterRevision
    public FeatureMap featureMap; // 65532 FeatureMap
    /**
     * Indicates the format that the Node is currently configured to use when conveying the hour unit of time.
     * If not UseActiveLocale, this value shall take priority over any unit implied through the ActiveLocale attribute.
     * If UseActiveLocale, any unit implied through the ActiveLocale attribute is used as the hour format, and if
     * ActiveLocale is not present, the hour format is unknown.
     */
    public HourFormatEnum hourFormat; // 0 HourFormatEnum RW VM
    /**
     * Indicates the calendar format that the Node is currently configured to use when conveying dates.
     * If not UseActiveLocale, this value shall take priority over any unit implied through the ActiveLocale attribute.
     * If UseActiveLocale, any unit implied through the ActiveLocale attribute is used as the calendar type, and if
     * ActiveLocale is not present, the calendar type is unknown.
     */
    public CalendarTypeEnum activeCalendarType; // 1 CalendarTypeEnum RW VM
    /**
     * Indicates a list of CalendarTypeEnum values that are supported by the Node. The list shall NOT contain any
     * duplicate entries. The ordering of items within the list SHOULD NOT express any meaning. The maximum length of
     * the SupportedCalendarTypes list shall be equivalent to the number of enumerations within CalendarTypeEnum.
     */
    public List<CalendarTypeEnum> supportedCalendarTypes; // 2 list R V

    // Enums
    public enum HourFormatEnum implements MatterEnum {
        V12HR(0, "12Hr"),
        V24HR(1, "24Hr"),
        USE_ACTIVE_LOCALE(255, "UseActiveLocale");

        public final Integer value;
        public final String label;

        private HourFormatEnum(Integer value, String label) {
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

    public enum CalendarTypeEnum implements MatterEnum {
        BUDDHIST(0, "Buddhist"),
        CHINESE(1, "Chinese"),
        COPTIC(2, "Coptic"),
        ETHIOPIAN(3, "Ethiopian"),
        GREGORIAN(4, "Gregorian"),
        HEBREW(5, "Hebrew"),
        INDIAN(6, "Indian"),
        ISLAMIC(7, "Islamic"),
        JAPANESE(8, "Japanese"),
        KOREAN(9, "Korean"),
        PERSIAN(10, "Persian"),
        TAIWANESE(11, "Taiwanese"),
        USE_ACTIVE_LOCALE(255, "UseActiveLocale");

        public final Integer value;
        public final String label;

        private CalendarTypeEnum(Integer value, String label) {
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
         * CalendarFormat
         * The Node can be configured to use different calendar formats when conveying values to a user.
         */
        public boolean calendarFormat;

        public FeatureMap(boolean calendarFormat) {
            this.calendarFormat = calendarFormat;
        }
    }

    public TimeFormatLocalizationCluster(BigInteger nodeId, int endpointId) {
        super(nodeId, endpointId, 44, "TimeFormatLocalization");
    }

    @Override
    public @NonNull String toString() {
        String str = "";
        str += "clusterRevision : " + clusterRevision + "\n";
        str += "featureMap : " + featureMap + "\n";
        str += "hourFormat : " + hourFormat + "\n";
        str += "activeCalendarType : " + activeCalendarType + "\n";
        str += "supportedCalendarTypes : " + supportedCalendarTypes + "\n";
        return str;
    }
}
