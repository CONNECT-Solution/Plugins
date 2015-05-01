/*
 * Copyright (c) 2009-2015, United States Government, as represented by the Secretary of Health and Human Services.  * All rights reserved. * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *   * Redistributions of source code must retain the above
 *     copyright notice, this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *   * Neither the name of the United States Government nor the
 *     names of its contributors may be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *DISCLAIMED. IN NO EVENT SHALL THE UNITED STATES GOVERNMENT BE LIABLE FOR ANY
 *DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package gov.hhs.fha.nhinc.fhir.adapter.impl;

import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.model.dstu.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu.resource.Binary;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.model.dstu.resource.DocumentReference;
import ca.uhn.fhir.model.dstu.valueset.DocumentReferenceStatusEnum;
import ca.uhn.fhir.model.dstu.valueset.IdentifierUseEnum;
import ca.uhn.fhir.model.primitive.InstantDt;
import gov.hhs.fha.nhinc.common.nhinccommonadapter.AdapterProvideAndRegisterDocumentSetRequestType;
import gov.hhs.fha.nhinc.connectmgr.ConnectionManagerException;
import gov.hhs.fha.nhinc.docsubmission.adapter.component.XDRHelper;
import static gov.hhs.fha.nhinc.docsubmission.adapter.component.XDRHelper.XDS_RETRIEVE_RESPONSE_STATUS_SUCCESS;
import gov.hhs.fha.nhinc.fhir.client.AdapterFHIRClient;
import gov.hhs.fha.nhinc.fhir.exception.DocSubmissionException;
import gov.hhs.fha.nhinc.fhir.parser.ds.DocSubmissionParser;
import gov.hhs.fha.nhinc.fhir.util.DocSubmissionConstants;
import gov.hhs.fha.nhinc.fhir.util.FHIRConstants;
import gov.hhs.fha.nhinc.nhinclib.NhincConstants;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

import ihe.iti.xds_b._2007.ProvideAndRegisterDocumentSetRequestType;
import ihe.iti.xds_b._2007.ProvideAndRegisterDocumentSetRequestType.Document;
import java.text.ParseException;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.RegistryObjectListType;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryError;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryErrorList;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryResponseType;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author tjafri
 */
public class DocSubmissionFHIRAdapterImpl {

    private final AdapterFHIRClient client = new AdapterFHIRClient();
    private DocSubmissionParser parser;
    private static Logger LOG = Logger.getLogger(DocSubmissionFHIRAdapterImpl.class);

    public DocSubmissionFHIRAdapterImpl() {
        parser = new DocSubmissionParser();
    }

    /**
     *
     * @param body
     * @return
     */
    public RegistryResponseType provideAndRegisterDocumentSetB(AdapterProvideAndRegisterDocumentSetRequestType body) {
        LOG.info("inside DocSubmissionFHIRAdapterImpl-->provideandResgisterDocumentSetB");

        RegistryResponseType response = null;
        XDRHelper helper = new XDRHelper();
        RegistryErrorList errorList = helper.validateDocumentMetaData(body.getProvideAndRegisterDocumentSetRequest());

        if (errorList.getHighestSeverity().equals(NhincConstants.XDS_REGISTRY_ERROR_SEVERITY_ERROR)) {
            response = helper.createErrorResponse(errorList);
        } else {
            LOG.info(" Request contained " + body.getProvideAndRegisterDocumentSetRequest().getDocument().size() + " documents.");
            LOG.info(" Request Id: " + body.getProvideAndRegisterDocumentSetRequest().getSubmitObjectsRequest().getId());
            try {
                response = processDocSubmissionRequest(body);
            } catch (DocSubmissionException docEx) {
                //TODO return error response
            }
        }
        return response;
    }

    private RegistryResponseType processDocSubmissionRequest(AdapterProvideAndRegisterDocumentSetRequestType body) throws DocSubmissionException {
        LOG.info("processDocSubmissionRequest");
        RegistryResponseType response = new RegistryResponseType();
        try {
            if (body != null) {
                ProvideAndRegisterDocumentSetRequestType request = body.getProvideAndRegisterDocumentSetRequest();

                if (request != null && request.getSubmitObjectsRequest() != null) {

                    RegistryObjectListType registry = request.getSubmitObjectsRequest().getRegistryObjectList();
                    String patientId = parser.extractPatientId(registry);
                    //LOG.info("patientId: " + queryP(registry));
                    String hapiPatientId = queryPatient(registry);
                    if (hapiPatientId == null) {
                        return createErrorResponse("Patient Not Found", "BinaryServerError");
                    }
                    //Document Reference Subject Information
                    ResourceReferenceDt patientRef = new ResourceReferenceDt("Patient/" + hapiPatientId);
                    patientRef.setDisplay(parser.getPatientName(registry));
                    Map<String, Binary> docMap = readDocumentsFromRequest(request);
                    for (Map.Entry<String, Binary> entry : docMap.entrySet()) {

                        String docId = entry.getKey();
                        Binary theDocument = docMap.get(docId);

                        MethodOutcome outcome = createBinaryResource(theDocument);

                        if (outcome.getOperationOutcome().isEmpty()) {
                            //Create a DocumentReference using DocSubmission request. Also
                            //Update the location property in the DocumentReference resource to the local URI
                            DocumentReference docRef = createDocumentReference(docId, registry, patientId);
                            docRef.setId("cid:" + outcome.getId().getIdPart());
                            docRef.setLocation(this.getBinaryResourceUri(outcome));
                            docRef.setSubject(patientRef);
                            docRef.setContext(parser.extractConextInformation(docId, registry));
                            // Set the size of document from request
                            docRef.setSize(parser.extractDocumentSize(docId, registry));
                            // set the language
                            docRef.setPrimaryLanguage(parser.extractLanguage(docId, registry));
                            //set the document name as DocRef Description
                            docRef.setDescription(parser.extractDocumentName(docId, registry));
                            //Adding Author information
                            docRef.setAuthor(parser.extractDocumentAuthorMetaData(docId, registry));
                            //Add the DocumentReference and Binary to a list of IResource
                            List<IResource> resources = new ArrayList<IResource>();
                            resources.add(docRef);
                            resources.add(theDocument);
                            List<IResource> result = client.postResources(resources);
                            if (result != null && result.size() > 0) {
                                response = createPositiveAck();
                            }
                        } else {
                            response = createErrorResponse(outcome.getOperationOutcome().getIssue().get(0).toString(),
                                "BinaryServerError");
                        }
                    }
                    //
                } else { //TODO return error response
                }

            } else {
                //TODO return error response
            }
        } catch (Exception anException) {
            anException.printStackTrace();
            response = createErrorResponse(
                DocSubmissionConstants.DS_RESPONSE_BASIC_ERROR_MESSAGE,
                DocSubmissionConstants.XDR_EC_XDSRegistryMetadataError);
        }
        return response;
    }

    private RegistryResponseType createErrorResponse(String message,
        String errorCode) {
        RegistryResponseType response = new RegistryResponseType();
        response.setStatus(DocSubmissionConstants.DS_RESPONSE_STATUS_FAILURE);
        RegistryErrorList errorList = new RegistryErrorList();
        RegistryError error = new RegistryError();
        error.setErrorCode(errorCode);
        error.setCodeContext(message);
        errorList.getRegistryError().add(error);
        response.setRegistryErrorList(errorList);
        return response;
    }

    private Map<String, Binary> readDocumentsFromRequest(ProvideAndRegisterDocumentSetRequestType request)
        throws DocSubmissionException, IOException {
        HashMap<String, Binary> docMap = new HashMap<String, Binary>();
        for (Document doc : request.getDocument()) {
            //Read the binary document into a byte array;
            //String docValue = parser.documentToString(doc);
            java.io.InputStream is = doc.getValue().getInputStream();
            byte[] inputDoc = IOUtils.toByteArray(is);
            if (inputDoc != null && inputDoc.length > 0) {
                String docType = parser.extractDocumentType(doc.getId(), request.getSubmitObjectsRequest().getRegistryObjectList());
                docMap.put(doc.getId(), createHAPIBinaryDoc(inputDoc, docType));
            }
        }
        return docMap;
    }

    private Binary createHAPIBinaryDoc(byte[] document, String docType) {
        LOG.info("createHAPIBinaryDoc()");
        Binary encodedDoc = new Binary();
        encodedDoc.setContent(document);
        encodedDoc.setContentType(docType);

        return encodedDoc;
    }

    private MethodOutcome createBinaryResource(Binary theDocument) throws IOException, URISyntaxException, ConnectionManagerException {
        LOG.info("createBinaryResource()");
        return client.createBinaryDocument(FHIRConstants.FHIR_BINARY_URL_KEY, theDocument);
    }

    private DocumentReference createDocumentReference(String docId, RegistryObjectListType registry, String patientId) throws ParseException {
        LOG.info("createDocumentReference()");
        DocumentReference docRef = new DocumentReference();
        //Set Document Reference type: Type defines What kind of document this is (LOINC if possible) -
        docRef.setType(parser.extractDocumentTypeMetaData(docId, registry));
        //Class is Categorization of Document  .. Setting the Document Refernce Class
        docRef.setClassElement(parser.extractDocumentClassMetaData(docId, registry));
        //Document creation time
        docRef.setCreated(parser.extractDocumentCreationTime(docId, registry));
        //Indexed holds DateTime when this document reference is created
        InstantDt Indexed = new InstantDt(Calendar.getInstance().getTime());
        docRef.setIndexed(Indexed);
        //Setting the MIME Type
        docRef.setMimeType(parser.extractDocumentType(docId, registry));
        docRef.setStatus(DocumentReferenceStatusEnum.CURRENT);
        String extDocId = parser.extractDocumentIdentificationValue(registry);
        docRef.setMasterIdentifier(IdentifierUseEnum.OFFICIAL, "urn:oid:" + extDocId, "urn:oid:" + extDocId, "uniqueId");
        docRef.addIdentifier()
            .setLabel("pid")
            .setUse(IdentifierUseEnum.USUAL)
            .setSystem("urn:oid:1.2.36.146.595.217.0.1.2");
        return docRef;
    }

    private String getBinaryResourceUri(MethodOutcome outcome) {
        StringBuilder builder = new StringBuilder();
        builder.append(outcome.getId().getBaseUrl());
        builder.append("/Binary/docBinary");
        return builder.toString();
    }

    private RegistryResponseType createPositiveAck() {
        RegistryResponseType result = new RegistryResponseType();
        result.setStatus(XDS_RETRIEVE_RESPONSE_STATUS_SUCCESS);
        return result;
    }

    private String queryPatient(RegistryObjectListType registry) throws URISyntaxException, DocSubmissionException, ConnectionManagerException, Exception {
        Map<String, String> fhirparam = parser.getPatientQueryParam(registry);
        List<IResource> resources = client.queryPatient(fhirparam);
        if (resources != null && resources.size() > 0) {
            return resources.get(0).getId().getIdPart();
        }
        return null;
    }
}
