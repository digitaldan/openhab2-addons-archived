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
import org.openhab.binding.matter.internal.client.model.cluster.gen.OperationalCredentialsClusterTypes.*;

/**
 * OperationalCredentials
 *
 * @author Dan Cunningham - Initial contribution
 */
public class OperationalCredentialsClusterCommands {

    public static ClusterCommand attestationRequest(String attestationNonce) {
        return new ClusterCommand("attestationRequest", new AttestationRequestCommandOptions(attestationNonce));
    }

    public static ClusterCommand attestationResponse(String attestationElements, String attestationSignature) {
        return new ClusterCommand("attestationResponse",
                new AttestationResponseCommandOptions(attestationElements, attestationSignature));
    }

    public static ClusterCommand certificateChainRequest(CertificateChainTypeEnum certificateType) {
        return new ClusterCommand("certificateChainRequest",
                new CertificateChainRequestCommandOptions(certificateType));
    }

    public static ClusterCommand certificateChainResponse(String certificate) {
        return new ClusterCommand("certificateChainResponse", new CertificateChainResponseCommandOptions(certificate));
    }

    public static ClusterCommand CSRRequest(String CSRNonce, Boolean isForUpdateNOC) {
        return new ClusterCommand("CSRRequest", new CSRRequestCommandOptions(CSRNonce, isForUpdateNOC));
    }

    public static ClusterCommand CSRResponse(String NOCSRElements, String attestationSignature) {
        return new ClusterCommand("CSRResponse", new CSRResponseCommandOptions(NOCSRElements, attestationSignature));
    }

    public static ClusterCommand addNOC(String NOCValue, String ICACValue, String IPKValue, Long caseAdminSubject,
            Integer adminVendorId) {
        return new ClusterCommand("addNOC",
                new AddNOCCommandOptions(NOCValue, ICACValue, IPKValue, caseAdminSubject, adminVendorId));
    }

    public static ClusterCommand updateNOC(String NOCValue, String ICACValue) {
        return new ClusterCommand("updateNOC", new UpdateNOCCommandOptions(NOCValue, ICACValue));
    }

    public static ClusterCommand NOCResponse(NodeOperationalCertStatusEnum statusCode, Integer fabricIndex,
            String debugText) {
        return new ClusterCommand("NOCResponse", new NOCResponseCommandOptions(statusCode, fabricIndex, debugText));
    }

    public static ClusterCommand updateFabricLabel(String label) {
        return new ClusterCommand("updateFabricLabel", new UpdateFabricLabelCommandOptions(label));
    }

    public static ClusterCommand removeFabric(Integer fabricIndex) {
        return new ClusterCommand("removeFabric", new RemoveFabricCommandOptions(fabricIndex));
    }

    public static ClusterCommand addTrustedRootCertificate(String rootCACertificate) {
        return new ClusterCommand("addTrustedRootCertificate",
                new AddTrustedRootCertificateCommandOptions(rootCACertificate));
    }
}
