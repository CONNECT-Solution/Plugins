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
import gov.hhs.fha.nhinc.connectmgr.ConnectionManagerCache;
import gov.hhs.fha.nhinc.document.DocumentConstants;
import gov.hhs.fha.nhinc.messaging.client.CONNECTClient;
import gov.hhs.fha.nhinc.messaging.client.CONNECTClientFactory;
import gov.hhs.fha.nhinc.messaging.service.port.ServicePortDescriptor;
import gov.hhs.fha.nhinc.nhinclib.NhincConstants;
import gov.hhs.fha.nhinc.nhinclib.NullChecker;
import gov.hhs.fha.nhinc.webserviceproxy.WebServiceProxyHelper;
import ihe.iti.xds_b._2007.DocumentRegistryPortType;
import gov.hhs.fha.nhinc.docregistry.adapter.proxy.*;
import gov.hhs.fha.nhinc.docregistry.adapter.proxy.description.AdapterComponentDocRegistryServicePortDescriptor;

import java.util.ArrayList;
import java.util.List;

import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryRequest;
import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryResponse;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.RegistryObjectListType;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryError;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryErrorList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.jaxws.context.WebServiceContextImpl;
import javax.xml.ws.handler.MessageContext;
import org.apache.log4j.Logger;

import java.util.UUID;
import javax.xml.namespace.QName;
//import com.sun.xml.ws.api.message.Headers;
//import com.sun.xml.ws.api.message.Header;
//import com.sun.xml.ws.developer.WSBindingProvider;
import javax.xml.ws.WebServiceContext;


/**
 * This class calls a SOAP 1.2 enabled document registry given a SOAP 1.1 registry stored query request message.
 *
 * @author Anand Sastry
 */
public class AdapterDocRegistry2Soap12Client {
    private static final Logger LOG = Logger.getLogger(AdapterDocRegistry2Soap12Client.class);
    private WebServiceProxyHelper oProxyHelper = null;
    private static String ADAPTER_XDS_REG_DEFAULT_SERVICE_URL = "http://localhost:8080/axis2/services/xdsregistryb";
    private static ihe.iti.xds_b._2007.DocumentRegistryService service = null;

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

    public AdhocQueryResponse documentRegistryRegistryStoredQuery(
            AdhocQueryRequest body) {
        
        AdhocQueryResponse response = null;
        AssertionType assertion = null;
        LOG.debug("Entering AdapterDocRegistry2Soap12Client.documentRegistryRegistryStoredQuery() method");

        try {
            String url = oProxyHelper
                    .getAdapterEndPointFromConnectionManager(ADAPTER_XDS_REG_SERVICE_NAME);
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

        

      /*  try {
            // get a connection to the soap 1.2 registryStoreQuery document web service
            ihe.iti.xds_b._2007.DocumentRegistryPortType port = getSoap12Port(WS_REGISTRY_STORED_QUERY_ACTION);

            // call the soap 1.2 retrieve document web service
            response = port.documentRegistryRegistryStoredQuery(body);
            LOG.debug("RetrieveDocumentSetRequest Response = " + ((response != null) ? response.getStatus() : "null"));
        } catch (Exception e) {
            String sErrorMessage = "Failed to execute registry stored query from the soap 1.2 web service.  Error: "
                    + e.getMessage();
            LOG.error(sErrorMessage, e);
            throw new RuntimeException(sErrorMessage, e);
        }

        log.debug("Leaving AdapterDocRegistry2Soap12Client.documentRegistryRegistryStoredQuery() method");
        return response;
    }*/

    /**
     * This method connects to a SOAP 1.2 enabled document registry based on the configuration found in the
     * internalConnectionInfo.xml file, creates the appropriate SOAP 1.2 header and returns a DocumentRegistryPortType
     * object so that a registry stored query request can be made on a SOAP 1.2 enabled document registry.
     *
     * @param action A string representing the soap header action needed to perform a registry stored query.
     * @return Returns a DocumentRegistryPortType object which will enable the registry stored query txn.
     */
   /* private ihe.iti.xds_b._2007.DocumentRegistryPortType getSoap12Port(String action) {
        log.debug("Entering AdapterDocRegistry2Soap12Client.getSoap12Port() method");

        ihe.iti.xds_b._2007.DocumentRegistryPortType port = null;

        try {
            // Call Web Service Operation
            service = new ihe.iti.xds_b._2007.DocumentRegistryService();
            port = service.getDocumentRegistryPortSoap();

            // Get the real endpoint URL for this service.
            // --------------------------------------------
            // Note, set the sEndpointURL to null and comment out the ConnectionMangerCache logic if running outside of
            // GF.
            String sEndpointURL = ConnectionManagerCache.getInstance().getInternalEndpointURLByServiceName(
                    ADAPTER_XDS_REG_SERVICE_NAME);

            if ((sEndpointURL == null) || (sEndpointURL.length() <= 0)) {
                sEndpointURL = ADAPTER_XDS_REG_DEFAULT_SERVICE_URL;
                String sErrorMessage = "Failed to retrieve the Endpoint URL for service: '"
                        + ADAPTER_XDS_REG_SERVICE_NAME + "'.  " + "Setting this to: '" + sEndpointURL + "'";
                LOG.warn(sErrorMessage);
            }

            ((javax.xml.ws.BindingProvider) port).getRequestContext().put(
                    javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY, sEndpointURL);

            // add the soap header
            List<Header> headers = new ArrayList<Header>();
            QName qname = new QName(NhincConstants.WS_ADDRESSING_URL, NhincConstants.WS_SOAP_HEADER_ACTION);
            Header tmpHeader = Headers.create(qname, action);
            headers.add(tmpHeader);
            qname = new QName(NhincConstants.WS_ADDRESSING_URL, NhincConstants.WS_SOAP_HEADER_TO);
            tmpHeader = Headers.create(qname, sEndpointURL);
            headers.add(tmpHeader);
            qname = new QName(NhincConstants.WS_ADDRESSING_URL, NhincConstants.WS_SOAP_HEADER_MESSAGE_ID);
            UUID oMessageId = UUID.randomUUID();
            String sMessageId = oMessageId.toString();
            tmpHeader = Headers.create(qname, NhincConstants.WS_SOAP_HEADER_MESSAGE_ID_PREFIX + sMessageId);
            headers.add(tmpHeader);

            ((WSBindingProvider) port).setOutboundHeaders(headers);
        } catch (Exception ex) {
            String sErrorMessage = "Failed to retrieve a handle to the soap 1.2 web service.  Error: "
                    + ex.getMessage();
            log.error(sErrorMessage, ex);
            throw new RuntimeException(sErrorMessage, ex);

        }

        log.debug("Leaving AdapterDocRegistry2Soap12Client.getSoap12Port() method");
        return port;
    }*/
}
