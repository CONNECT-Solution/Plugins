/*
 * Copyright (c) 2009-2015, United States Government, as represented by the Secretary of Health and Human Services.
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
package gov.hhs.fha.nhinc.fhir.adapter.impl;

import gov.hhs.fha.nhinc.connectmgr.ConnectionManagerException;
import gov.hhs.fha.nhinc.fhir.client.AdapterFHIRClient;
import gov.hhs.fha.nhinc.fhir.exception.UnknownResourceLocation;
import gov.hhs.fha.nhinc.fhir.util.FHIRConstants;
import gov.hhs.fha.nhinc.nhinclib.NhincConstants;
import gov.hhs.fha.nhinc.nhinclib.NullChecker;
import ihe.iti.xds_b._2007.RetrieveDocumentSetRequestType;
import ihe.iti.xds_b._2007.RetrieveDocumentSetRequestType.DocumentRequest;
import ihe.iti.xds_b._2007.RetrieveDocumentSetResponseType;
import ihe.iti.xds_b._2007.RetrieveDocumentSetResponseType.DocumentResponse;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryResponseType;
import org.apache.log4j.Logger;
import org.hl7.fhir.instance.model.AtomFeed;
import org.hl7.fhir.instance.model.Binary;
import org.hl7.fhir.instance.model.DocumentReference;

/**
 *
 * @author jassmit
 */
public class DocRepositoryFHIRAdapterImpl extends FhirAdapter {

    private final AdapterFHIRClient client = new AdapterFHIRClient();

    private static final Logger LOG = Logger.getLogger(DocRepositoryFHIRAdapterImpl.class);

    public RetrieveDocumentSetResponseType queryRepository(RetrieveDocumentSetRequestType request) {
        RetrieveDocumentSetResponseType response = new RetrieveDocumentSetResponseType();
        RegistryResponseType regResponse = new RegistryResponseType();
        regResponse.setStatus(NhincConstants.NHINC_ADHOC_QUERY_SUCCESS_RESPONSE);
        response.setRegistryResponse(regResponse);

        if (NullChecker.isNotNullish(request.getDocumentRequest())) {
            for (DocumentRequest docRequest : request.getDocumentRequest()) {
                try {
                    DocumentReference docReference = getDocReference(docRequest);
                    String id = filterId(docReference.getLocationSimple());

                    byte[] doc = client.readBinaryResource(FHIRConstants.FHIR_BINARY_URL_KEY, Binary.class, id);
                    if (doc != null && doc.length > 0) {
                        DocumentResponse docResponse = new DocumentResponse();
                        DataHandler dataHandler = getDataHandler(doc, docReference.getMimeTypeSimple());
                        docResponse.setDocumentUniqueId(docRequest.getDocumentUniqueId());
                        docResponse.setHomeCommunityId(docRequest.getHomeCommunityId());
                        docResponse.setRepositoryUniqueId(docRequest.getRepositoryUniqueId());
                        docResponse.setMimeType(docReference.getMimeTypeSimple());

                        docResponse.setDocument(dataHandler);
                        response.getDocumentResponse().add(docResponse);
                    }
                } catch (URISyntaxException | ConnectionManagerException | UnsupportedEncodingException | MalformedURLException |
                    UnknownResourceLocation ex) {
                    LOG.warn("Unable to add document from doc reference: " + ex.getMessage(), ex);
                }
            }
        }
        return response;
    }

    private DocumentReference getDocReference(DocumentRequest docRequest) throws URISyntaxException, ConnectionManagerException {
        String uri = docRequest.getDocumentUniqueId();
        uri = (uri.startsWith("urn:oid:")) ? uri : "urn:oid:" + uri;

        Map<String, String> fhirParams = new HashMap<>();
        fhirParams.put("identifier", uri);
        addFormatParam(fhirParams);

        AtomFeed docFeed = client.searchFhirResource(FHIRConstants.FHIR_DOC_REFERENCE_URL_KEY, fhirParams, DocumentReference.class);

        if (docFeed != null && NullChecker.isNotNullish(docFeed.getEntryList())) {
            return (DocumentReference) docFeed.getEntryList().get(0).getResource();
        }
        return null;
    }

    private String filterId(String location) throws UnknownResourceLocation, ConnectionManagerException {
        String url = client.getAdapterUrl(FHIRConstants.FHIR_BINARY_URL_KEY);
        url = url + "/" + org.hl7.fhir.instance.model.ResourceType.Binary.getPath();

        LOG.debug("Binary Resource url: " + url);
        LOG.debug("Document Resource Location: " + location);

        if (location.toLowerCase().startsWith(url.toLowerCase())) {
            return location.substring(url.length(), location.length());
        }

        throw new UnknownResourceLocation("Location value does not match system endpoint. ");
    }

    private DataHandler getDataHandler(byte[] doc, String content) throws UnsupportedEncodingException, URISyntaxException, MalformedURLException {
        DataSource dataSource = new ByteArrayDataSource(doc, content);
        return new DataHandler(dataSource);
    }
}
