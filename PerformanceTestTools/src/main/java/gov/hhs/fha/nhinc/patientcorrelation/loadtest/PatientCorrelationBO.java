/*
 * Copyright (c) 2009-2016, United States Government, as represented by the Secretary of Health and Human Services.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above
 *       copyright notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the documentation
 *       and/or other materials provided with the distribution.
 *     * Neither the name of the United States Government nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE UNITED STATES GOVERNMENT BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package gov.hhs.fha.nhinc.patientcorrelation.loadtest;

import gov.hhs.fha.nhinc.common.nhinccommon.AssertionType;
import gov.hhs.fha.nhinc.loadtest.DataManager;
import gov.hhs.fha.nhinc.nhinclib.NhincConstants;
import gov.hhs.fha.nhinc.patientcorrelation.nhinc.proxy.PatientCorrelationProxy;
import gov.hhs.fha.nhinc.properties.PropertyAccessor;
import org.hl7.v3.AddPatientCorrelationResponseType;
import org.hl7.v3.PRPAIN201301UV02;
import org.hl7.v3.PRPAIN201309UV02;
import org.hl7.v3.RetrievePatientCorrelationsResponseType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author rhalfert
 */
public class PatientCorrelationBO implements PatientCorrelationProxy {

    public static String CANNED_CORRELATION_COMMUNITY = "CANNED_CORRELATION_COMMUNITY";
    public static String CANNED_CORRELATION_PATIENT_ID = "CANNED_CORRELATION_PATIENT_ID";

    private static final Logger LOG = LoggerFactory.getLogger(PatientCorrelationBO.class);

    @Override
    public RetrievePatientCorrelationsResponseType retrievePatientCorrelations(PRPAIN201309UV02 prpnv,
        AssertionType at) {

        RetrievePatientCorrelationsResponseType response = null;

        try {
            response = DataManager.getInstance().getCannedPatientCorrelationResponse();

            String targetCommunity = PropertyAccessor.getInstance().getProperty(NhincConstants.GATEWAY_PROPERTY_FILE,
                CANNED_CORRELATION_COMMUNITY);
            String patientID = PropertyAccessor.getInstance().getProperty(NhincConstants.GATEWAY_PROPERTY_FILE,
                CANNED_CORRELATION_PATIENT_ID);

            response.getPRPAIN201310UV02().getControlActProcess().getSubject().get(0)
                .getRegistrationEvent().getSubject1().getPatient().getId().get(0).setExtension(patientID);
            response.getPRPAIN201310UV02().getControlActProcess().getSubject().get(0)
                .getRegistrationEvent().getSubject1().getPatient().getId().get(0).setRoot(targetCommunity);

        } catch (Exception e) {
            LOG.error(e.getLocalizedMessage(), e);
        }

        return response;
    }

    @Override
    public AddPatientCorrelationResponseType addPatientCorrelation(PRPAIN201301UV02 prpnv, AssertionType at) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
