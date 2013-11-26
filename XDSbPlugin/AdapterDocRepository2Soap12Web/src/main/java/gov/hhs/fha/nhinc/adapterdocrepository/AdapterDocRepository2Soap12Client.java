/*
 * Copyright (c) 2012, United States Government, as represented by the Secretary of Health and Human Services. 
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
package gov.hhs.fha.nhinc.adapterdocrepository;

import gov.hhs.fha.nhinc.common.nhinccommon.AssertionType;
import gov.hhs.fha.nhinc.docrepository.adapter.proxy.service.AdapterComponentDocRepositoryServicePortDescriptor;
import gov.hhs.fha.nhinc.document.DocumentConstants;
import gov.hhs.fha.nhinc.messaging.client.CONNECTClient;
import gov.hhs.fha.nhinc.messaging.service.port.ServicePortDescriptor;
import gov.hhs.fha.nhinc.nhinclib.NhincConstants;
import gov.hhs.fha.nhinc.properties.PropertyAccessor;
import gov.hhs.fha.nhinc.webserviceproxy.WebServiceProxyHelper;
import ihe.iti.xds_b._2007.DocumentRepositoryPortType;
import ihe.iti.xds_b._2007.RetrieveDocumentSetResponseType;

import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryError;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryResponseType;

import org.apache.log4j.Logger;


/**
 * This class calls a soap 1.2 enabled document repository given a soap 1.1 retrieve document set or provide and
 * register document set request message. This class was initially created to connect to the Vangent/HIEOS document
 * repository.
 * 
 * @author shawc
 */
public class AdapterDocRepository2Soap12Client {
    private static final Logger LOG = Logger.getLogger(AdapterDocRepository2Soap12Client.class);
    public static final String ADAPTER_XDS_REP_SERVICE_NAME = "adapterxdsbdocrepositorysoap12";
    private WebServiceProxyHelper oProxyHelper = null;

    public AdapterDocRepository2Soap12Client() {
        oProxyHelper = createWebServiceProxyHelper();
    }

    protected WebServiceProxyHelper createWebServiceProxyHelper() {
        return new WebServiceProxyHelper();
    }


    
    /**
     * This method connects to a soap 1.2 enabled document repository and retrieves a document with the document id
     * found in the given RetrieveDocumentSetRequestType object.
     * 
     * @param retrieveRequest A RetrieveDocumentSetRequestType object containing a document id and repository id of the
     *            desired document.
     * @return Returns a RetrieveDocumentSetResponseType containing the desired document.
     */
    public ihe.iti.xds_b._2007.RetrieveDocumentSetResponseType retrieveDocument(
            ihe.iti.xds_b._2007.RetrieveDocumentSetRequestType retrieveRequest) {
        ihe.iti.xds_b._2007.RetrieveDocumentSetResponseType response = null;
        AssertionType assertion = null;

        LOG.debug("Entering AdapterDocRepository2Soap12Client.retrieveDocument() method");
        
        try {
            String xdsbHomeCommunityId = PropertyAccessor.getInstance().getProperty(
                NhincConstants.ADAPTER_PROPERTY_FILE_NAME, NhincConstants.XDS_HOME_COMMUNITY_ID_PROPERTY);
            String url = oProxyHelper.getAdapterEndPointFromConnectionManager(xdsbHomeCommunityId,
                    ADAPTER_XDS_REP_SERVICE_NAME);

            if (retrieveRequest == null) {
                LOG.error("Message was null");
            } else {
                ServicePortDescriptor<DocumentRepositoryPortType> portDescriptor = new AdapterComponentDocRepositoryServicePortDescriptor();

                CONNECTClient<DocumentRepositoryPortType> client = AdapterDocRepositoryClientFactory.getInstance().getCONNECTClientUnsecured(portDescriptor, url,
                    assertion);
                client.enableMtom();
                response = (RetrieveDocumentSetResponseType) client.invokePort(DocumentRepositoryPortType.class,
                    "documentRepositoryRetrieveDocumentSet", retrieveRequest);
            }
        } catch (Exception ex) {
            LOG.error("Error sending Adapter Component Doc Repository Unsecured message: " + ex.getMessage(), ex);
            response = new RetrieveDocumentSetResponseType();
            RegistryResponseType regResp = new RegistryResponseType();

            regResp.setStatus(DocumentConstants.XDS_QUERY_RESPONSE_STATUS_FAILURE);

            RegistryError registryError = new RegistryError();
            registryError.setCodeContext("Processing Adapter Doc Query document retrieve");
            registryError.setErrorCode("XDSRepostoryError");
            registryError.setSeverity(NhincConstants.XDS_REGISTRY_ERROR_SEVERITY_ERROR);
            regResp.getRegistryErrorList().getRegistryError().add(registryError);
            response.setRegistryResponse(regResp);
        }

        LOG.debug("End retrieveDocument");
        return response;
    }
}
