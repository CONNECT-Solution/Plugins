/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.hhs.fha.nhinc.documentquery.loadtest;

import gov.hhs.fha.nhinc.common.nhinccommon.AssertionType;
import gov.hhs.fha.nhinc.docquery.adapter.proxy.AdapterDocQueryProxy;
import gov.hhs.fha.nhinc.loadtest.DataManager;
import gov.hhs.fha.nhinc.loadtest.DocumentMetaData;
import gov.hhs.fha.nhinc.loadtest.ILoadTestData;
import gov.hhs.fha.nhinc.loadtest.LoadTestData;
import javax.xml.bind.JAXBElement;
import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryRequest;
import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryResponse;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ExternalIdentifierType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ExtrinsicObjectType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.IdentifiableType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.SlotType1;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ObjectFactory;


/**
 *
 * @author mweaver
 */
public class DocumentQueryBO implements AdapterDocQueryProxy {

    public AdhocQueryResponse respondingGatewayCrossGatewayQuery(AdhocQueryRequest request, AssertionType assertion) {

        AdhocQueryResponse response = null;

        try {
            DataManager dm = DataManager.getInstance();
            LoadTestData ltd = dm.getLoadTestData();
            ILoadTestData data = null;
            
            for (SlotType1 slot : request.getAdhocQuery().getSlot()) {
                if (slot.getName().equalsIgnoreCase("$XDSDocumentEntryPatientId")) {
                    for (String s : slot.getValueList().getValue()) {
                        data = ltd.getLoadTestData().get(s);
                    }
                }
            }            
            if (data instanceof DocumentMetaData) {
                DocumentMetaData dmd = (DocumentMetaData) data;
                response = replaceCannedData(dm.getCannedDocumentQueryResponse(), dmd);
            }
            else
            {
                response = new AdhocQueryResponse();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    public AdhocQueryResponse replaceCannedData(AdhocQueryResponse response, DocumentMetaData dmd) throws Exception {
        for (String s : dmd.getDocumentIds()) {

            if (response == null ||
                    response.getRegistryObjectList() == null ||
                    response.getRegistryObjectList().getIdentifiable() == null) {
                throw new Exception("Canned response was improperly formed.");
            }

            DataManager dm = DataManager.getInstance();
            ExtrinsicObjectType eot = dm.getCannedExtrinsicObject();
            for (ExternalIdentifierType eit : eot.getExternalIdentifier()) {
                //1 replace add patient ids
                if ("urn:uuid:58a6f841-87b3-4a3e-92fd-a8ffeff98427".equalsIgnoreCase(eit.getIdentificationScheme())) {
                    eit.setValue(dmd.getPatientId());
                }
                //2 replace document id
                else if ("urn:uuid:2e82c1f6-a085-4c72-9da3-8640a32e42ab".equalsIgnoreCase(eit.getIdentificationScheme())) {
                    eit.setValue(s);
                }
            }
            ObjectFactory of = new ObjectFactory();
            response.getRegistryObjectList().getIdentifiable().add(of.createExtrinsicObject(eot));
        }

        for (JAXBElement<? extends IdentifiableType> JAXBEle : response.getRegistryObjectList().getIdentifiable()) {

            if (JAXBEle.getValue() instanceof oasis.names.tc.ebxml_regrep.xsd.rim._3.RegistryPackageType) {
                oasis.names.tc.ebxml_regrep.xsd.rim._3.RegistryPackageType registryPackage = (oasis.names.tc.ebxml_regrep.xsd.rim._3.RegistryPackageType) JAXBEle.getValue();

                for (ExternalIdentifierType eit : registryPackage.getExternalIdentifier()) {
                    //1 replace add patient ids
                    if ("urn:uuid:6b5aea1a-874d-4603-a4bc-96a0a7b38446".equalsIgnoreCase(eit.getIdentificationScheme())) {
                        eit.setValue(dmd.getPatientId());
                    }
                }
            }
        }

        return response;
    }
}
