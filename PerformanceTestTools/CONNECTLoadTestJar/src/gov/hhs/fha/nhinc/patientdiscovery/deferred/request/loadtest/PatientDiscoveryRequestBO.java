package gov.hhs.fha.nhinc.patientdiscovery.deferred.request.loadtest;

import org.hl7.v3.MCCIIN000002UV01;
import org.hl7.v3.PRPAIN201305UV02;

import gov.hhs.fha.nhinc.common.nhinccommon.AssertionType;
import gov.hhs.fha.nhinc.loadtest.DataManager;
import gov.hhs.fha.nhinc.patientdiscovery.adapter.deferred.request.proxy.AdapterPatientDiscoveryDeferredReqProxy;
import gov.hhs.healthit.nhin.XDRAcknowledgementType;

public class PatientDiscoveryRequestBO implements
		AdapterPatientDiscoveryDeferredReqProxy {

	@Override
	public MCCIIN000002UV01 processPatientDiscoveryAsyncReq(
			PRPAIN201305UV02 request, AssertionType assertion) {
		DataManager dm = DataManager.getInstance();
		MCCIIN000002UV01 response = null;
		
		try {
		    response = dm.getCannedPatientDiscoveryDeferredRequest();
		} catch (Exception e) {
		    e.printStackTrace();
		}
		
		return response;
	}

}
