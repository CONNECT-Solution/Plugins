/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.hhc.onc.hpdclient.service;

import ihe.iti.hpd._2010.ProviderInformationDirectoryPortType;
import ihe.iti.hpd._2010.ProviderInformationDirectoryService;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
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
 */
public class HPDServiceImpl implements HPDService {

    private ObjectFactory dsmlBasedObjectFactory;
    private ProviderInformationDirectoryService pdService;
    private ProviderInformationDirectoryPortType pdPortType;
    private Properties prop;
    private static final String DN_HC_PROFESSIONAL = "pdti.server.dnHcProfessional";
    private static final String DN_HC_REG_ORG = "pdti.server.dnHcRegulatedOrganization";
    private static final String DN_HPD_MEMEBERSHIP = "pdti.server.dnHPDProviderMembership";
    private static final String DN_HPD_CREDENTIAL = "pdti.server.dnHPDCredential";
    private static final String DN_SERVICES = "pdti.server.dnServices";
    private static final String DN_RELATIONSHIP = "pdti.server.dnRelationShip";
    private static final String SCOPE = "pdti.server.scope";
    private static final String DEREF_ALIASES = "pdti.server.derefAliases";
    private static final String WSDL_URL = "pdti.server.wsdlurl";
    private static final String PROPERTY_FILENAME = "config.properties";

    public HPDServiceImpl() throws IOException {

        prop = new Properties();
        InputStream inputStream = null;
        try {
            //inputStream = this.getClass().getClassLoader().getResourceAsStream(PROPERTY_FILENAME);

            //if (inputStream != null) {
            prop.load(new FileReader(System.getProperty("propertyFile")));
            //}
            dsmlBasedObjectFactory = new ObjectFactory();
            pdService = new ProviderInformationDirectoryService(getServiceURL());
            pdPortType = pdService.getProviderInformationDirectoryPortSoap(new AddressingFeature(true, true));
        } catch (IOException ex) {
            throw new FileNotFoundException("Unable to load properties file" + ex.getMessage());
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
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
