package gov.hhs.fha.nhinc.canned.loadtest;

import java.util.logging.Level;
import java.util.logging.Logger;

import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryRequest;
import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryResponse;
import gov.hhs.fha.nhinc.common.nhinccommon.AssertionType;
import gov.hhs.fha.nhinc.docquery.adapter.proxy.AdapterDocQueryProxy;
import gov.hhs.fha.nhinc.documentquery.loadtest.DocumentQueryBO;
import gov.hhs.fha.nhinc.loadtest.DataManager;
import gov.hhs.fha.nhinc.loadtest.DocumentMetaData;
import gov.hhs.fha.nhinc.loadtest.ILoadTestData;
import gov.hhs.fha.nhinc.loadtest.LoadTestData;

public class DocumentQueryCannedBO implements AdapterDocQueryProxy{
	
	public AdhocQueryResponse respondingGatewayCrossGatewayQuery(AdhocQueryRequest request, AssertionType assertion) {
	       
        AdhocQueryResponse response = null;
        try {
            DataManager dm = DataManager.getInstance();
            LoadTestData ltd = dm.getLoadTestData();
            ILoadTestData data = ltd.getLoadTestData().get("V005^^^&19.1.2.2&ISO");
            
            if (data instanceof DocumentMetaData) {
            	DocumentMetaData dmd = (DocumentMetaData) data;
            	DocumentQueryBO bo = new DocumentQueryBO();
                response = bo.replaceCannedData(dm.getCannedDocumentQueryResponse(), dmd);
            }
            else
            {
            	response = new AdhocQueryResponse();
            }
                                                  

        } catch (Exception e) {
            Logger.getLogger(DocumentRetrieveCannedBO.class.getName()).log(Level.SEVERE, null, e);
        }
        
        
        return response;
	}
}
