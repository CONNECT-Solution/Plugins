package gov.hhs.fha.nhinc.docsubmission.deferred.response.loadtest;

import gov.hhs.fha.nhinc.common.nhinccommon.AssertionType;
import gov.hhs.fha.nhinc.docsubmission.adapter.component.deferred.response.proxy.AdapterComponentDocSubmissionResponseProxy;
import gov.hhs.fha.nhinc.loadtest.DataManager;
import gov.hhs.healthit.nhin.XDRAcknowledgementType;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryResponseType;


public class DocumentSubmissionResponseBO implements
		AdapterComponentDocSubmissionResponseProxy {

	@Override
	public XDRAcknowledgementType provideAndRegisterDocumentSetBResponse(
			RegistryResponseType body, AssertionType assertion) {
		DataManager dm = DataManager.getInstance();
		XDRAcknowledgementType response = null;
		
		try {
		    response = dm.getCannedDocumentSubmissionDeferredResponse();
		} catch (Exception e) {
		    e.printStackTrace();
		}
		
		return response;
	}

}
