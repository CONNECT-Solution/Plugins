package gov.hhs.fha.nhinc.corex12.loadtest;

import gov.hhs.fha.nhinc.common.nhinccommon.AssertionType;
import gov.hhs.fha.nhinc.loadtest.DataManager;
import org.caqh.soap.wsdl.corerule2_2_0.COREEnvelopeRealTimeRequest;
import org.caqh.soap.wsdl.corerule2_2_0.COREEnvelopeRealTimeResponse;
import gov.hhs.fha.nhinc.corex12.docsubmission.realtime.adapter.proxy.AdapterCORE_X12DSRealTimeProxy;

public class CORE_X12DSRealTimeBO implements AdapterCORE_X12DSRealTimeProxy {

    @Override
    public COREEnvelopeRealTimeResponse realTimeTransaction(COREEnvelopeRealTimeRequest msg,
        AssertionType assertion) {
        DataManager dm = DataManager.getInstance();
        COREEnvelopeRealTimeResponse response = null;

        try {
            response = dm.getCannedCoreX12RealTime();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

}
