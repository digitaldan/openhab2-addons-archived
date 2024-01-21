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

import org.openhab.binding.matter.internal.client.model.cluster.ClusterCommand;
import org.openhab.binding.matter.internal.client.model.cluster.gen.GeneralCommissioningClusterTypes.*;

/**
 * GeneralCommissioning
 *
 * @author Dan Cunningham - Initial contribution
 */
public class GeneralCommissioningClusterCommands {

    public static ClusterCommand armFailSafe(Integer expiryLengthSeconds, Long breadcrumb) {
        return new ClusterCommand("armFailSafe", new ArmFailSafeCommandOptions(expiryLengthSeconds, breadcrumb));
    }

    public static ClusterCommand armFailSafeResponse(CommissioningErrorEnum errorCode, String debugText) {
        return new ClusterCommand("armFailSafeResponse", new ArmFailSafeResponseCommandOptions(errorCode, debugText));
    }

    public static ClusterCommand setRegulatoryConfig(RegulatoryLocationTypeEnum newRegulatoryConfig, String countryCode,
            Long breadcrumb) {
        return new ClusterCommand("setRegulatoryConfig",
                new SetRegulatoryConfigCommandOptions(newRegulatoryConfig, countryCode, breadcrumb));
    }

    public static ClusterCommand setRegulatoryConfigResponse(CommissioningErrorEnum errorCode, String debugText) {
        return new ClusterCommand("setRegulatoryConfigResponse",
                new SetRegulatoryConfigResponseCommandOptions(errorCode, debugText));
    }

    public static ClusterCommand commissioningComplete() {
        return new ClusterCommand("commissioningComplete", new CommissioningCompleteCommandOptions());
    }

    public static ClusterCommand commissioningCompleteResponse(CommissioningErrorEnum errorCode, String debugText) {
        return new ClusterCommand("commissioningCompleteResponse",
                new CommissioningCompleteResponseCommandOptions(errorCode, debugText));
    }
}
