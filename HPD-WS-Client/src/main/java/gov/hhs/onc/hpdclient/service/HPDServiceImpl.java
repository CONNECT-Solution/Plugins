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
package gov.hhs.onc.hpdclient.service;

import ihe.iti.hpd._2010.ProviderInformationDirectoryPortType;
import ihe.iti.hpd._2010.ProviderInformationDirectoryService;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.UUID;
import javax.xml.ws.soap.AddressingFeature;
import oasis.names.tc.dsml._2._0.core.AttributeValueAssertion;
import oasis.names.tc.dsml._2._0.core.BatchRequest;
import oasis.names.tc.dsml._2._0.core.BatchResponse;
import oasis.names.tc.dsml._2._0.core.Filter;
import oasis.names.tc.dsml._2._0.core.ObjectFactory;
import oasis.names.tc.dsml._2._0.core.SearchRequest;

/**
 *
 * @author tjafri
 *
 * This class is used to query PDTI server which is a test implementation of Provider Directory Specification.
 *
 */
public class HPDServiceImpl implements HPDService {

    private ObjectFactory dsmlBasedObjectFactory;
    private ProviderInformationDirectoryService pdService;
    private ProviderInformationDirectoryPortType pdPortType;
    private Properties prop;
    private static final String DN_HC_PROFESSIONAL = "hpd.dnHcProfessional";
    private static final String DN_HC_REG_ORG = "hpd.dnHcRegulatedOrganization";
    private static final String DN_HPD_MEMEBERSHIP = "hpd.dnHPDProviderMembership";
    private static final String DN_HPD_CREDENTIAL = "hpd.dnHPDCredential";
    private static final String DN_SERVICES = "hpd.dnServices";
    private static final String DN_RELATIONSHIP = "hpd.dnRelationShip";
    private static final String SCOPE = "hpd.scope";
    private static final String DEREF_ALIASES = "hpd.derefAliases";
    private static final String WSDL_URL = "hpd.wsdlurl";
    private static final String PROPERTY_FILE = "propertyFile";

    public HPDServiceImpl() throws IOException {

        prop = new Properties();
        try {
            prop.load(new FileReader(System.getProperty(PROPERTY_FILE)));
            dsmlBasedObjectFactory = new ObjectFactory();
            pdService = new ProviderInformationDirectoryService(getServiceURL());
            pdPortType = pdService.getProviderInformationDirectoryPortSoap(new AddressingFeature(true, true));
        } catch (IOException ex) {
            throw new FileNotFoundException("Unable to load properties file" + ex.getMessage());
        }
    }

    public BatchResponse searchQuery(String dn, String filterBy, String filterValue) {
        String requestId = UUID.randomUUID().toString();
        SearchRequest sreq = createSearchRequest();
        sreq.setRequestID(requestId);
        AttributeValueAssertion attr = new AttributeValueAssertion();
        attr.setName(filterBy);
        attr.setValue(filterValue);
        Filter filter = dsmlBasedObjectFactory.createFilter();
        filter.setEqualityMatch(attr);
        sreq.setFilter(filter);
        sreq.setDn(prop.getProperty(getDN(dn)));

        BatchRequest batchRequest = createBatchRequestObject(requestId, sreq);
        return pdPortType.providerInformationQueryRequest(batchRequest);
    }

    private SearchRequest createSearchRequest() {
        SearchRequest sreq = dsmlBasedObjectFactory.createSearchRequest();
        sreq.setScope(prop.getProperty(SCOPE));
        sreq.setDerefAliases(prop.getProperty(DEREF_ALIASES));
        return sreq;
    }

    private BatchRequest createBatchRequestObject(String requestId, SearchRequest sreq) {
        BatchRequest batchRequest = dsmlBasedObjectFactory.createBatchRequest();
        batchRequest.getBatchRequests().add(sreq);
        batchRequest.setRequestID(requestId);
        return batchRequest;
    }

    private URL getServiceURL() throws MalformedURLException {
        URL serviceURL = new URL(prop.getProperty(WSDL_URL));
        return serviceURL;
    }

    private String getDN(String dn) {
        if (dn.equalsIgnoreCase("professional")) {
            return DN_HC_PROFESSIONAL;
        } else if (dn.equalsIgnoreCase("organization")) {
            return DN_HC_REG_ORG;
        } else if (dn.equalsIgnoreCase("membership")) {
            return DN_HPD_MEMEBERSHIP;
        } else if (dn.equalsIgnoreCase("services")) {
            return DN_SERVICES;
        } else if (dn.equalsIgnoreCase("credential")) {
            return DN_HPD_CREDENTIAL;
        } else if (dn.equalsIgnoreCase("relationship")) {
            return DN_RELATIONSHIP;
        } else {
            return DN_HC_PROFESSIONAL;
        }
    }
}
