/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.hhs.fha.nhinc.fhir.adapter.impl;

import gov.hhs.fha.nhinc.connectmgr.ConnectionManagerException;
import gov.hhs.fha.nhinc.fhir.client.AdapterFHIRClient;
import gov.hhs.fha.nhinc.mpi.adapter.component.hl7parsers.HL7Parser201305;
import gov.hhs.fha.nhinc.mpi.adapter.component.hl7parsers.HL7Parser201306;
import gov.hhs.fha.nhinc.mpilib.Patient;
import gov.hhs.fha.nhinc.nhinclib.NullChecker;
import gov.hhs.fhs.nhinc.fhir.transform.ResourceTransformer;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import org.hl7.fhir.instance.model.AtomFeed;
import org.hl7.v3.PRPAIN201305UV02;
import org.hl7.v3.PRPAIN201306UV02;
import org.hl7.v3.PRPAMT201306UV02ParameterList;

/**
 *
 * @author jassmit
 */
public class MpiFHIRAdapterImpl {
    
    private final AdapterFHIRClient client = new AdapterFHIRClient();
    private final ResourceTransformer transformer = new ResourceTransformer();
    
    private static final Logger LOG = Logger.getLogger(MpiFHIRAdapterImpl.class);
    
    public PRPAIN201306UV02 query(PRPAIN201305UV02 findCandidatesRequest) {
        
        PRPAMT201306UV02ParameterList queryParams = HL7Parser201305.extractHL7QueryParamsFromMessage(findCandidatesRequest);
        if (queryParams != null) {
            try {
                Patient sourcePatient = HL7Parser201305.extractMpiPatientFromQueryParams(queryParams);               
                Map<String, String> fhirParams = buildFhirParams(sourcePatient);
                
                if(fhirParams != null) {
                    AtomFeed patientFeed = client.getFhirResource("FHIRPatientResource", fhirParams, Patient.class);
                    return transformer.getPatientResponse(patientFeed, findCandidatesRequest);
                }
            } catch (URISyntaxException | ConnectionManagerException ex) {
                LOG.error("Unable to get Patient Resource due to: " + ex.getLocalizedMessage(), ex);
            }            
        }
        
        return HL7Parser201306.buildMessageFromMpiPatient(null, findCandidatesRequest);
    }
    
    private Map<String, String> buildFhirParams(Patient sourcePatient) {
        Map<String, String> params = null;
        
        if (sourcePatient != null && NullChecker.isNotNullish(sourcePatient.getDateOfBirth())
            && NullChecker.isNotNullish(sourcePatient.getGender())
            && NullChecker.isNotNullish(sourcePatient.getNames())
            && sourcePatient.getNames().get(0) != null
            && NullChecker.isNotNullish(sourcePatient.getNames().get(0).getFirstName())
            && NullChecker.isNotNullish(sourcePatient.getNames().get(0).getLastName())) {
            
            params = new HashMap<>();
            
            params.put("gender", sourcePatient.getGender());
            params.put("birthdate", sourcePatient.getDateOfBirth());
            params.put("given", sourcePatient.getNames().get(0).getFirstName());
            params.put("family", sourcePatient.getNames().get(0).getLastName());            
        }
        
        return params;
    }
    
}
