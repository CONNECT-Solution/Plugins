package gov.hhs.fha.nhinc.docsubmission.loadtest;

import org.hl7.v3.PRPAIN201306UV02;

import ihe.iti.xds_b._2007.ProvideAndRegisterDocumentSetRequestType;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryResponseType;
import gov.hhs.fha.nhinc.common.nhinccommon.AssertionType;
import gov.hhs.fha.nhinc.docsubmission.adapter.component.proxy.AdapterComponentDocSubmissionProxy;
import gov.hhs.fha.nhinc.loadtest.DataManager;

public class DocumentSubmissionBO implements AdapterComponentDocSubmissionProxy {

	@Override
	public RegistryResponseType provideAndRegisterDocumentSetB(
			ProvideAndRegisterDocumentSetRequestType msg,
			AssertionType assertion) {
		DataManager dm = DataManager.getInstance();
		RegistryResponseType response = null;
		
		try {
		    response = dm.getCannedDocumentSubmissionResponse();
		} catch (Exception e) {
		    e.printStackTrace();
		}
		
		return response;
	}

}
