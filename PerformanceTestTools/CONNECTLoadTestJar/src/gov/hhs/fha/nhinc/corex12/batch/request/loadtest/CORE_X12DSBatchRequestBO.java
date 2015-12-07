package gov.hhs.fha.nhinc.corex12.batch.request.loadtest;

import gov.hhs.fha.nhinc.common.nhinccommon.AssertionType;
import gov.hhs.fha.nhinc.corex12.docsubmission.genericbatch.request.adapter.proxy.AdapterCORE_X12DGenericBatchRequestProxy;
import gov.hhs.fha.nhinc.loadtest.DataManager;
import org.caqh.soap.wsdl.corerule2_2_0.COREEnvelopeBatchSubmission;
import org.caqh.soap.wsdl.corerule2_2_0.COREEnvelopeBatchSubmissionResponse;
import org.apache.log4j.Logger;

public class CORE_X12DSBatchRequestBO implements AdapterCORE_X12DGenericBatchRequestProxy {

    private static final Logger LOG = Logger.getLogger(CORE_X12DSBatchRequestBO.class);

    @Override
    public COREEnvelopeBatchSubmissionResponse batchSubmitTransaction(COREEnvelopeBatchSubmission msg,
        AssertionType assertion) {
        DataManager dm = DataManager.getInstance();
        COREEnvelopeBatchSubmissionResponse response = null;

        try {
            response = dm.getCannedCoreX12BatchRequest();
        } catch (Exception e) {
            LOG.error("Exception occurred retriving response: " + e.getLocalizedMessage(), e);
        }

        return response;
    }

}
