/*
 * Copyright (c) 2009-2017, United States Government, as represented by the Secretary of Health and Human Services.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above
 *       copyright notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the documentation
 *       and/or other materials provided with the distribution.
 *     * Neither the name of the United States Government nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE UNITED STATES GOVERNMENT BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ObjectFactory;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.RegistryPackageType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.SlotType1;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author mweaver
 */
public class DocumentQueryBO implements AdapterDocQueryProxy {

    private static final Logger LOG = LoggerFactory.getLogger(DocumentQueryBO.class);

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
            } else {
                response = new AdhocQueryResponse();
            }
        } catch (Exception e) {
            LOG.error(e.getLocalizedMessage(), e);
        }

        return response;
    }

    public AdhocQueryResponse replaceCannedData(AdhocQueryResponse response, DocumentMetaData dmd) throws Exception {
        for (String s : dmd.getDocumentIds()) {
            if (response == null || response.getRegistryObjectList() == null
                || response.getRegistryObjectList().getIdentifiable() == null) {

                throw new Exception("Canned response was improperly formed.");
            }

            ExtrinsicObjectType eot = DataManager.getInstance().getCannedExtrinsicObject();
            for (ExternalIdentifierType eit : eot.getExternalIdentifier()) {
                if ("urn:uuid:58a6f841-87b3-4a3e-92fd-a8ffeff98427".equalsIgnoreCase(eit.getIdentificationScheme())) {
                    // 1 replace add patient ids
                    eit.setValue(dmd.getPatientId());
                } else if ("urn:uuid:2e82c1f6-a085-4c72-9da3-8640a32e42ab".equalsIgnoreCase(eit.getIdentificationScheme())) {
                    // 2 replace document id
                    eit.setValue(s);
                }
            }
            ObjectFactory of = new ObjectFactory();
            response.getRegistryObjectList().getIdentifiable().add(of.createExtrinsicObject(eot));
        }

        for (JAXBElement<? extends IdentifiableType> JAXBEle : response.getRegistryObjectList().getIdentifiable()) {
            if (JAXBEle.getValue() instanceof RegistryPackageType) {
                RegistryPackageType registryPackage = (RegistryPackageType) JAXBEle.getValue();

                for (ExternalIdentifierType eit : registryPackage.getExternalIdentifier()) {
                    if ("urn:uuid:6b5aea1a-874d-4603-a4bc-96a0a7b38446".equalsIgnoreCase(eit.getIdentificationScheme())) {
                        // 1 replace add patient ids
                        eit.setValue(dmd.getPatientId());
                    }
                }
            }
        }

        return response;
    }
}
