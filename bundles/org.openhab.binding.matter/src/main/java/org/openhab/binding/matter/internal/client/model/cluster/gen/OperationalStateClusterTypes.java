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
 * OperationalState
 *
 * @author Dan Cunningham - Initial contribution
 */
public class OperationalStateClusterTypes {

    public static final String CLUSTER_NAME = "OPERATIONAL_STATE_CLUSTER";
    public static final int CLUSTER_ID = 0x0060;

    public class ErrorStateStruct {
        public Integer errorStateID; // enum8
        public String errorStateLabel; // char_string
        public String errorStateDetails; // char_string

        public ErrorStateStruct(Integer errorStateID, String errorStateLabel, String errorStateDetails) {
            this.errorStateID = errorStateID;
            this.errorStateLabel = errorStateLabel;
            this.errorStateDetails = errorStateDetails;
        }
    }

    public class OperationalStateStruct {
        public Integer operationalStateID; // enum8
        public String operationalStateLabel; // char_string

        public OperationalStateStruct(Integer operationalStateID, String operationalStateLabel) {
            this.operationalStateID = operationalStateID;
            this.operationalStateLabel = operationalStateLabel;
        }
    }

    // ZCL Enums
    public enum ErrorStateEnum {
        NOERROR(0, "NoError"),
        UNABLETOSTARTORRESUME(1, "UnableToStartOrResume"),
        UNABLETOCOMPLETEOPERATION(2, "UnableToCompleteOperation"),
        COMMANDINVALIDINSTATE(3, "CommandInvalidInState"),
        UNKNOWN_VALUE(4, "UnknownValue");

        public final int value;
        public final String label;

        private ErrorStateEnum(int value, String label) {
            this.value = value;
            this.label = label;
        }
    };

    public enum OperationalStateEnum {
        STOPPED(0, "Stopped"),
        RUNNING(1, "Running"),
        PAUSED(2, "Paused"),
        ERROR(3, "Error"),
        UNKNOWN_VALUE(4, "UnknownValue");

        public final int value;
        public final String label;

        private OperationalStateEnum(int value, String label) {
            this.value = value;
            this.label = label;
        }
    };

    public static class PauseCommandOptions {
        public PauseCommandOptions() {
        }
    }

    public static class StopCommandOptions {
        public StopCommandOptions() {
        }
    }

    public static class StartCommandOptions {
        public StartCommandOptions() {
        }
    }

    public static class ResumeCommandOptions {
        public ResumeCommandOptions() {
        }
    }

    public static class OperationalCommandResponseCommandOptions {
        public ErrorStateStruct[] commandResponseState;

        public OperationalCommandResponseCommandOptions(ErrorStateStruct[] commandResponseState) {
            this.commandResponseState = commandResponseState;
        }
    }
}
