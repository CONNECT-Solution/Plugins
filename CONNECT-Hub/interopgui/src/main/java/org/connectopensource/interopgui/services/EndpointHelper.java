/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013, United States Government, as represented by the Secretary of Health and Human Services.
 * All rights reserved.
 *  
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *   * Redistributions of source code must retain the above
 *     copyright notice, this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *   * Neither the name of the United States Government nor the
 *     names of its contributors may be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *DISCLAIMED. IN NO EVENT SHALL THE UNITED STATES GOVERNMENT BE LIABLE FOR ANY
 *DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.connectopensource.interopgui.services;

import org.connectopensource.interopgui.view.Endpoint.SpecVersion;
import org.connectopensource.interopgui.view.Endpoint.Specification;

/**
 * @author bhumphrey
 * 
 */
public class EndpointHelper {

    private static final String DOC_SUBMISSION_TMODEL = "DocSubmission";
    private static final String RETRIEVE_DOCUMENTS_TMODEL = "RetrieveDocuments";
    private static final String ADMIN_DISTRIBUTION_TMODEL = "AdminDistribution";
    private static final String QUERY_FOR_DOCUMENTS_TMODEL = "QueryForDocuments";
    private static final String PATIENT_DISCOVERY_TMODEL = "PatientDiscovery";

    public String getVersion(Specification specification, SpecVersion specVersion) {
        switch (specification) {
        case PATIENT_DISCOVERY:
            return getSpecVersion(specVersion, "1.0", "2.0");
        case DOCUMENT_QUERY:
            return getSpecVersion(specVersion, "2.0", "3.0");
        case ADMINISTRATIVE_DISTRIBUTION:
            return getSpecVersion(specVersion, "1.0", "2.0");
        case DOCUMENT_RETRIEVE:
            return getSpecVersion(specVersion, "2.0", "3.0");
        case DOCUMENT_SUBMISSION:
            return getSpecVersion(specVersion, "1.1", "2.0");
        default:
            break;
        
        }
        return "unk";
    }

    
    public SpecVersion getSpecVersion(Specification specification, String keyValue) {
        switch (specification) {
        case PATIENT_DISCOVERY:
            return getSpecVersion(keyValue, "1.0", "2.0");
        case DOCUMENT_QUERY:
            return getSpecVersion(keyValue, "2.0", "3.0");
        case ADMINISTRATIVE_DISTRIBUTION:
            return getSpecVersion(keyValue, "1.0", "2.0");
        case DOCUMENT_RETRIEVE:
            return getSpecVersion(keyValue, "2.0", "3.0");
        case DOCUMENT_SUBMISSION:
            return getSpecVersion(keyValue, "1.1", "2.0");
        default:
            break;
        
        }
        return null;
    }

    public SpecVersion getSpecVersion(String keyValue, String jan2010, String summer2011) {
        if(jan2010.equals(keyValue)) {
            return SpecVersion.JAN_2010;
        } else if (summer2011.equals(keyValue)) {
            return SpecVersion.SUMMER_2011;
        }
        return null;
    }


    /**
     * @param specVersion
     * @param jan2010 
     * @param summer2011 
     * @return
     */
    public String getSpecVersion(SpecVersion specVersion, String jan2010, String summer2011) {
        switch(specVersion) {
            case JAN_2010:
                return jan2010;
            case SUMMER_2011:
                return summer2011;
            default:
                break;
        }
        return "unk";
    }

    /**
     * @param specification
     * @return
     */
    public String getSpecName(Specification specification) {
        switch (specification) {
        case PATIENT_DISCOVERY:
            return PATIENT_DISCOVERY_TMODEL;
        case DOCUMENT_QUERY:
            return QUERY_FOR_DOCUMENTS_TMODEL;
        case ADMINISTRATIVE_DISTRIBUTION:
            return ADMIN_DISTRIBUTION_TMODEL;
        case DOCUMENT_RETRIEVE:
            return RETRIEVE_DOCUMENTS_TMODEL;
        case DOCUMENT_SUBMISSION:
            return DOC_SUBMISSION_TMODEL;
        }
        return "unknown";
    }

    public Specification getSpecification(String keyValue) {
        switch (keyValue) {
        case PATIENT_DISCOVERY_TMODEL:
            return Specification.PATIENT_DISCOVERY;
        case QUERY_FOR_DOCUMENTS_TMODEL:
            return Specification.DOCUMENT_QUERY;
        case ADMIN_DISTRIBUTION_TMODEL:
            return Specification.ADMINISTRATIVE_DISTRIBUTION;
        case RETRIEVE_DOCUMENTS_TMODEL:
            return Specification.DOCUMENT_RETRIEVE;
        case DOC_SUBMISSION_TMODEL:
            return Specification.DOCUMENT_SUBMISSION;
        default:
            break;
        }
        return null;
    }


 

}
