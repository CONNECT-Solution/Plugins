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
package gov.hhs.fha.nhinc.adapterdocregistry;

import gov.hhs.fha.nhinc.common.nhinccommon.AssertionType;
import gov.hhs.fha.nhinc.docregistry.adapter.proxy.description.AdapterComponentDocRegistryServicePortDescriptor;
import gov.hhs.fha.nhinc.document.DocumentConstants;
import gov.hhs.fha.nhinc.messaging.client.CONNECTClient;
import gov.hhs.fha.nhinc.messaging.service.port.ServicePortDescriptor;
import gov.hhs.fha.nhinc.nhinclib.NhincConstants;
import gov.hhs.fha.nhinc.nhinclib.NullChecker;
import gov.hhs.fha.nhinc.webserviceproxy.WebServiceProxyHelper;
import ihe.iti.xds_b._2007.DocumentRegistryPortType;
import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryRequest;
import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryResponse;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.RegistryObjectListType;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryError;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryErrorList;

import org.apache.log4j.Logger;

/**
 * This class calls a SOAP 1.2 enabled document registry given a SOAP 1.1 registry stored query request message.
 * 
 * @author Anand Sastry, msweaver, achidamb
 */
public class AdapterDocRegistry2Soap12Client {
    private static final Logger LOG = Logger.getLogger(AdapterDocRegistry2Soap12Client.class);
    private WebServiceProxyHelper oProxyHelper = null;

    // CONSTANTS - Were not created in NhincConstants to simplify provisioning of this component
    // as an adapter between CONNECT and SOAP 1.2 Registry.
    public static final String WS_REGISTRY_STORED_QUERY_ACTION = "urn:ihe:iti:2007:RegistryStoredQuery";
    public static final String ADAPTER_XDS_REG_SERVICE_NAME = "adapterxdsbdocregistrysoap12";

    /**
     * Default constructor.
     */
    public AdapterDocRegistry2Soap12Client() {
        oProxyHelper = createWebServiceProxyHelper();
    }

    protected WebServiceProxyHelper createWebServiceProxyHelper() {
        return new WebServiceProxyHelper();
    }

    public ServicePortDescriptor<DocumentRegistryPortType> getServicePortDescriptor(
            NhincConstants.ADAPTER_API_LEVEL apiLevel) {
        switch (apiLevel) {
        case LEVEL_a0:
            return new AdapterComponentDocRegistryServicePortDescriptor();
        default:
            return new AdapterComponentDocRegistryServicePortDescriptor();
        }
    }

    /**
     * This method connects to a soap 1.2 enabled document registry and retrieves metadata.
     * 
     * @param body A AdhocQueryRequest object containing key criteria to query for registry metadata.
     * 
     * @return Returns a AdhocQueryResponse containing the desired metadata.
     * 
     */

    public AdhocQueryResponse documentRegistryRegistryStoredQuery(AdhocQueryRequest body) {

        AdhocQueryResponse response = null;
        AssertionType assertion = null;
        LOG.debug("Entering AdapterDocRegistry2Soap12Client.documentRegistryRegistryStoredQuery() method");

        try {
            String url = oProxyHelper.getAdapterEndPointFromConnectionManager(ADAPTER_XDS_REG_SERVICE_NAME);
            if (NullChecker.isNotNullish(url)) {

                if (body == null) {
                    LOG.error("Message was null");
                } else {
                    ServicePortDescriptor<DocumentRegistryPortType> portDescriptor = getServicePortDescriptor(NhincConstants.ADAPTER_API_LEVEL.LEVEL_a0);

                    CONNECTClient<DocumentRegistryPortType> client = AdapterDocRegistrySoapClientFactory.getInstance()
                            .getCONNECTClientUnsecured(portDescriptor, url, assertion);

                    response = (AdhocQueryResponse) client.invokePort(DocumentRegistryPortType.class,
                            "documentRegistryRegistryStoredQuery", body);
                }
            } else {
                LOG.error("Failed to call the web service (" + NhincConstants.ADAPTER_DOC_REGISTRY_SERVICE_NAME
                        + ").  The URL is null.");
            }
        } catch (Exception ex) {
            LOG.error("Error sending Adapter Component Doc Registry Unsecured message: " + ex.getMessage(), ex);
            response = new AdhocQueryResponse();
            response.setStatus(DocumentConstants.XDS_QUERY_RESPONSE_STATUS_FAILURE);
            response.setRegistryObjectList(new RegistryObjectListType());

            RegistryError registryError = new RegistryError();
            registryError.setCodeContext("Processing Adapter Doc Query document query");
            registryError.setErrorCode("XDSRegistryError");
            registryError.setSeverity(NhincConstants.XDS_REGISTRY_ERROR_SEVERITY_ERROR);
            response.setRegistryErrorList(new RegistryErrorList());
            response.getRegistryErrorList().getRegistryError().add(registryError);
        }

        LOG.debug("End registryStoredQuery");
        return response;
    }

}
