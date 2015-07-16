/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.hhs.fha.nhinc.documentretrieve.loadtest;

import gov.hhs.fha.nhinc.common.nhinccommon.AssertionType;
import gov.hhs.fha.nhinc.docretrieve.adapter.proxy.AdapterDocRetrieveProxy;
import gov.hhs.fha.nhinc.largefile.LargeFileUtils;
import gov.hhs.fha.nhinc.loadtest.DataManager;
import gov.hhs.fha.nhinc.loadtest.Document;
import gov.hhs.fha.nhinc.loadtest.ILoadTestData;
import gov.hhs.fha.nhinc.loadtest.LoadTestData;
import ihe.iti.xds_b._2007.ObjectFactory;
import ihe.iti.xds_b._2007.RetrieveDocumentSetRequestType;
import ihe.iti.xds_b._2007.RetrieveDocumentSetRequestType.DocumentRequest;
import ihe.iti.xds_b._2007.RetrieveDocumentSetResponseType;
import ihe.iti.xds_b._2007.RetrieveDocumentSetResponseType.DocumentResponse;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryResponseType;

/**
 *
 * @author mweaver
 */
public class DocumentRetrieveBO implements AdapterDocRetrieveProxy {

    public RetrieveDocumentSetResponseType retrieveDocumentSet(RetrieveDocumentSetRequestType request, AssertionType assertion) {

        ObjectFactory of = new ObjectFactory();
        RetrieveDocumentSetResponseType response = of.createRetrieveDocumentSetResponseType();

        try {
            DocumentResponse dr = of.createRetrieveDocumentSetResponseTypeDocumentResponse();
            DataManager dm = DataManager.getInstance();
            LoadTestData ltd = dm.getLoadTestData();

            RegistryResponseType responseType = new RegistryResponseType();
            responseType.setStatus("urn:oasis:names:tc:ebxml-regrep:ResponseStatusType:Success");
            response.setRegistryResponse(responseType);

            for (DocumentRequest req : request.getDocumentRequest()) {
                ILoadTestData data = ltd.getLoadTestData().get(req.getDocumentUniqueId());
                if (data instanceof Document)
                {
                    Document d = (Document)data;
                    dr.setDocument(LargeFileUtils.getInstance().convertToDataHandler(d.getDocument()));
                }
                else
                {
                    byte[] bytes = {0xa,0xa};
                    dr.setDocument(LargeFileUtils.getInstance().convertToDataHandler(bytes));
                }
                dr.setHomeCommunityId(ltd.getEnvironment());
                dr.setDocumentUniqueId(req.getDocumentUniqueId());
                dr.setRepositoryUniqueId(req.getRepositoryUniqueId());
                dr.setMimeType("text/xml");

                response.getDocumentResponse().add(dr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
}
