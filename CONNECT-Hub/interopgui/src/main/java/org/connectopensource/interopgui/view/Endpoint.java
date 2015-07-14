/**
 * 
 */
package org.connectopensource.interopgui.view;

/**
 * @author msw
 *
 */
public interface Endpoint {
    
    public enum SpecVersion { 
        
        JAN_2010("Jan 2010"), SUMMER_2011("Summer 2011"); 
        
        private final String label;
        
        private SpecVersion(String label) {
            this.label = label;
        }

        /**
         * @return the label
         */
        public String getLabel() {
            return label;
        }

    }

    public enum Specification { 
        
        PATIENT_DISCOVERY("PatientDiscovery"), 
        PATIENT_DISCOVERY_DEF_REQ("PatientDiscoveryDeferredReq"),
        PATIENT_DISCOVERY_DEF_RESP("PatientDiscoveryDeferredResp"),
        DOCUMENT_QUERY("QueryForDocuments"), 
        DOCUMENT_RETRIEVE("RetrieveDocuments"), 
        DOCUMENT_SUBMISSION("DocSubmission"), 
        DOCUMENT_SUBMISSION_DEF_REQ("DocSubmissionDeferredReq"), 
        DOCUMENT_SUBMISSION_DEF_RESP("DocSubmissionDeferredResp"), 
        ADMINISTRATIVE_DISTRIBUTION("AdminDistribution");
        
        private final String label;
        
        private Specification(String label) {
            this.label = label;
        }

        /**
         * @return the label
         */
        public String getLabel() {
            return label;
        }

    }
    
    public SpecVersion[] getSpecVersions();
    public Specification[] getSpecifications();
    
    public Specification getSpecification();
    public void setSpecification(Specification spec);
    
    public SpecVersion getSpecVersion();
    public void setSpecVersion(SpecVersion version);
    
    public String getEndpoint();
    public void setEndpoint(String endpoint);
}
