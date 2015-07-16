/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.hhs.fha.nhinc.patientcorrelation.loadtest;

import gov.hhs.fha.nhinc.common.nhinccommon.AssertionType;
import gov.hhs.fha.nhinc.loadtest.DataManager;
import gov.hhs.fha.nhinc.nhinclib.NhincConstants;
import gov.hhs.fha.nhinc.patientcorrelation.nhinc.proxy.PatientCorrelationProxy;
import gov.hhs.fha.nhinc.patientcorrelation.nhinc.proxy.PatientCorrelationProxyJavaImpl;
import gov.hhs.fha.nhinc.properties.PropertyAccessor;
import org.hl7.v3.AddPatientCorrelationResponseType;
import org.hl7.v3.PRPAIN201301UV02;
import org.hl7.v3.PRPAIN201309UV02;
import org.hl7.v3.PRPAIN201310UV02;
import org.hl7.v3.RetrievePatientCorrelationsResponseType;

/**
 *
 * @author rhalfert
 */
public class PatientCorrelationBO implements PatientCorrelationProxy{
    public static String CANNED_CORRELATION_COMMUNITY = "CANNED_CORRELATION_COMMUNITY";
    public static String CANNED_CORRELATION_PATIENT_ID = "CANNED_CORRELATION_PATIENT_ID";
    
    @Override
    public RetrievePatientCorrelationsResponseType retrievePatientCorrelations(PRPAIN201309UV02 prpnv, AssertionType at) {
        DataManager dm = DataManager.getInstance();
        RetrievePatientCorrelationsResponseType response = null;

        try {
            response = dm.getCannedPatientCorrelationResponse();
            
            String targetCommunity = PropertyAccessor.getInstance().getProperty(NhincConstants.GATEWAY_PROPERTY_FILE,
                    CANNED_CORRELATION_COMMUNITY);
            String patientID = PropertyAccessor.getInstance().getProperty(NhincConstants.GATEWAY_PROPERTY_FILE,
                    CANNED_CORRELATION_PATIENT_ID);
            
            response.getPRPAIN201310UV02().getControlActProcess().getSubject().get(0)
                    .getRegistrationEvent().getSubject1().getPatient().getId().get(0).setExtension(patientID);
            response.getPRPAIN201310UV02().getControlActProcess().getSubject().get(0)
                    .getRegistrationEvent().getSubject1().getPatient().getId().get(0).setRoot(targetCommunity);
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
		
        
        return response;
    }

    @Override
    public AddPatientCorrelationResponseType addPatientCorrelation(PRPAIN201301UV02 prpnv, AssertionType at) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
