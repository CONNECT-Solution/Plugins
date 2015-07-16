package gov.hhs.fha.nhinc.patientdiscovery.deferred.response.loadtest;

import org.hl7.v3.MCCIIN000002UV01;
import org.hl7.v3.PRPAIN201306UV02;

import gov.hhs.fha.nhinc.common.nhinccommon.AssertionType;
import gov.hhs.fha.nhinc.loadtest.DataManager;
import gov.hhs.fha.nhinc.patientdiscovery.adapter.deferred.response.proxy.AdapterPatientDiscoveryDeferredRespProxy;

public class PatientDiscoveryResponseBO implements
		AdapterPatientDiscoveryDeferredRespProxy {

	@Override
	public MCCIIN000002UV01 processPatientDiscoveryAsyncResp(
			PRPAIN201306UV02 request, AssertionType assertion) {
		DataManager dm = DataManager.getInstance();
		MCCIIN000002UV01 response = null;
		
		try {
		    response = dm.getCannedPatientDiscoveryDeferredResponse();
		} catch (Exception e) {
		    e.printStackTrace();
		}
		
		return response;
	}

}
