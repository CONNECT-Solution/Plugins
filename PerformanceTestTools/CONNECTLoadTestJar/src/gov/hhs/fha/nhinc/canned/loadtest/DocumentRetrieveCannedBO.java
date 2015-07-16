package gov.hhs.fha.nhinc.canned.loadtest;

import ihe.iti.xds_b._2007.RetrieveDocumentSetRequestType;
import ihe.iti.xds_b._2007.RetrieveDocumentSetResponseType;

import java.util.logging.Level;
import java.util.logging.Logger;

import gov.hhs.fha.nhinc.common.nhinccommon.AssertionType;
import gov.hhs.fha.nhinc.docretrieve.adapter.proxy.AdapterDocRetrieveProxy;
import gov.hhs.fha.nhinc.documentretrieve.loadtest.DocumentRetrieveBO;

public class DocumentRetrieveCannedBO implements AdapterDocRetrieveProxy{
	
	public RetrieveDocumentSetResponseType retrieveDocumentSet(RetrieveDocumentSetRequestType request, AssertionType assertion) {

		RetrieveDocumentSetResponseType resp = null;
        try {
            DocumentRetrieveBO businessObject = new DocumentRetrieveBO();            
            request.getDocumentRequest().get(0).setDocumentUniqueId("V005a");            
            resp = businessObject.retrieveDocumentSet(request, assertion);
        } catch (Exception ex) {
            Logger.getLogger(DocumentRetrieveCannedBO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return resp;
    }
}
