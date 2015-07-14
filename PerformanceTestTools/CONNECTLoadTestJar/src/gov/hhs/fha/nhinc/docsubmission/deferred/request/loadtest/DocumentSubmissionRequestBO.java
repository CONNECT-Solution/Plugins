package gov.hhs.fha.nhinc.docsubmission.deferred.request.loadtest;

import gov.hhs.fha.nhinc.docsubmission.adapter.component.deferred.request.proxy.AdapterComponentDocSubmissionRequestProxy;
import gov.hhs.fha.nhinc.loadtest.DataManager;
import gov.hhs.fha.nhinc.common.nhinccommon.AssertionType;
import gov.hhs.healthit.nhin.XDRAcknowledgementType;
import ihe.iti.xds_b._2007.ProvideAndRegisterDocumentSetRequestType;

public class DocumentSubmissionRequestBO implements
		AdapterComponentDocSubmissionRequestProxy {

	@Override
	public XDRAcknowledgementType provideAndRegisterDocumentSetBRequest(
			ProvideAndRegisterDocumentSetRequestType body,
			AssertionType assertion) {
		DataManager dm = DataManager.getInstance();
		XDRAcknowledgementType response = null;
		
		try {
		    response = dm.getCannedDocumentSubmissionDeferredRequest();
		} catch (Exception e) {
		    e.printStackTrace();
		}
		
		return response;
	}

}
