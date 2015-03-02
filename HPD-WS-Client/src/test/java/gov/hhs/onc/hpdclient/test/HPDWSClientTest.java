/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.hhs.onc.hpdclient.test;

import gov.hhs.onc.hpdclient.FederatedRequestData;
import ihe.iti.hpd._2010.ProviderInformationDirectoryPortType;
import ihe.iti.hpd._2010.ProviderInformationDirectoryService;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import javax.mail.internet.MimeUtility;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.ws.soap.AddressingFeature;
import oasis.names.tc.dsml._2._0.core.AttributeValueAssertion;
import oasis.names.tc.dsml._2._0.core.BatchRequest;
import oasis.names.tc.dsml._2._0.core.BatchResponse;
import oasis.names.tc.dsml._2._0.core.Control;
import oasis.names.tc.dsml._2._0.core.DsmlAttr;
import oasis.names.tc.dsml._2._0.core.ErrorResponse;
import oasis.names.tc.dsml._2._0.core.Filter;
import oasis.names.tc.dsml._2._0.core.ObjectFactory;
import oasis.names.tc.dsml._2._0.core.SearchRequest;
import oasis.names.tc.dsml._2._0.core.SearchResponse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author tjafri
 */
public class HPDWSClientTest {

    private Properties prop = null;
    private ObjectFactory dsmlBasedObjectFactory = null;
    private ProviderInformationDirectoryService pdService = null;
    private ProviderInformationDirectoryPortType pdPortType = null;

    private final String DN_HC_PROFESSIONAL = "pdti.server.dnHcProfessional";
    private final String DN_HC_REG_ORG = "pdti.server.dnHcRegulatedOrganization";
    private final String DN_HPD_MEMEBERSHIP = "pdti.server.dnHPDProviderMembership";
    private final String DN_HPD_CREDENTIAL = "pdti.server.dnHPDCredential";
    private final String DN_SERVICES = "pdti.server.dnServices";
    private final String DN_RELATIONSHIP = "pdti.server.dnRelationShip";
    private final String SINGLE_LEVEL = "singleLevel";
    private final String DEREF_FINDING_BASE_OBJ = "derefFindingBaseObj";
    private final String WSDL_URL = "pdti.server.wdslurl";

    @Before
    public void setup() throws IOException {
        prop = new Properties();
        String propFilename = "config.properties";
        InputStream inputStream = null;
        try {
            inputStream = this.getClass().getClassLoader().getResourceAsStream(propFilename);
            if (inputStream != null) {
                prop.load(inputStream);
            }
        } catch (IOException ex) {
            throw new FileNotFoundException("Unable to load properties file" + ex.getMessage());
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        dsmlBasedObjectFactory = new ObjectFactory();
        pdService = new ProviderInformationDirectoryService(getServiceURL());
        pdPortType = pdService.getProviderInformationDirectoryPortSoap(new AddressingFeature(true, true));
    }

    @Test
    public void testHcProfessionalFederated() {
        String requestId = UUID.randomUUID().toString();
        SearchRequest sreq = createSearchRequest();
        sreq.setRequestID(requestId);
        AttributeValueAssertion attr = new AttributeValueAssertion();
        attr.setName("givenName");
        attr.setValue("Thomas");
        Control ctrl = new Control(); //only needed for Federated option
        ctrl.setType("1.2.3.4.5");
        ctrl.setCriticality(false);
        FederatedRequestData reqData = new FederatedRequestData();
        reqData.setFederatedRequestId("12345");
        ctrl.setControlValue(convertToBytes(reqData));
        sreq.getControl().clear();
        sreq.getControl().add(ctrl);

        Filter filter = dsmlBasedObjectFactory.createFilter();
        filter.setEqualityMatch(attr);
        sreq.setFilter(filter);
        sreq.setDn(prop.getProperty(DN_HC_PROFESSIONAL));

        BatchRequest batchRequest = createBatchRequestObject(requestId, sreq);
        BatchResponse response = pdPortType.providerInformationQueryRequest(batchRequest);

        assertTrue(response.getBatchResponses().get(0).getValue() instanceof SearchResponse);

        Map<String, String> attrMap = getResponseAttributeMap((SearchResponse) response.getBatchResponses().get(0).getValue());
        assertTrue(attrMap.containsKey("givenName"));
        assertTrue(attrMap.get("givenName").equals("Thomas"));

        printSearchResult((SearchResponse) response.getBatchResponses().get(0).getValue());
    }

    @Test
    public void testHcProfessional() {
        String requestId = UUID.randomUUID().toString();
        SearchRequest sreq = createSearchRequest();
        sreq.setRequestID(requestId);
        AttributeValueAssertion attr = new AttributeValueAssertion();
        attr.setName("givenName");
        attr.setValue("Thomas");
        Filter filter = dsmlBasedObjectFactory.createFilter();
        filter.setEqualityMatch(attr);
        sreq.setFilter(filter);
        sreq.setDn(prop.getProperty(DN_HC_PROFESSIONAL));

        BatchRequest batchRequest = createBatchRequestObject(requestId, sreq);
        BatchResponse response = pdPortType.providerInformationQueryRequest(batchRequest);

        assertTrue(response.getBatchResponses().get(0).getValue() instanceof SearchResponse);

        Map<String, String> attrMap = getResponseAttributeMap((SearchResponse) response.getBatchResponses().get(0).getValue());
        assertTrue(attrMap.containsKey("givenName"));
        assertTrue(attrMap.get("givenName").equals("Thomas"));

        printSearchResult((SearchResponse) response.getBatchResponses().get(0).getValue());
    }

    @Test
    public void testRegulatedOrganization() {

        String requestId = UUID.randomUUID().toString();
        SearchRequest sreq = createSearchRequest();
        sreq.setRequestID(requestId);

        AttributeValueAssertion attr = new AttributeValueAssertion();
        attr.setName("o");
        attr.setValue("Dr. Thomas Jones' Private Practice");
        Filter filter = dsmlBasedObjectFactory.createFilter();
        filter.setEqualityMatch(attr);
        sreq.setFilter(filter);
        sreq.setDn(prop.getProperty(DN_HC_REG_ORG));

        BatchRequest batchRequest = createBatchRequestObject(requestId, sreq);
        BatchResponse response = pdPortType.providerInformationQueryRequest(batchRequest);
        assertTrue(response.getBatchResponses().get(0).getValue() instanceof SearchResponse);
        Map<String, String> attrMap = getResponseAttributeMap((SearchResponse) response.getBatchResponses().get(0).getValue());
        assertTrue(attrMap.containsKey("o"));
        assertTrue(attrMap.get("o").equals("Dr. Thomas Jones' Private Practice"));
        printSearchResult((SearchResponse) response.getBatchResponses().get(0).getValue());
    }

    @Test
    public void testProviderMemebership() {
        String requestId = UUID.randomUUID().toString();
        SearchRequest sreq = createSearchRequest();
        sreq.setRequestID(requestId);

        AttributeValueAssertion attr = new AttributeValueAssertion();
        attr.setName("hpdHasAProvider");
        attr.setValue("uid=2.16.840.1.113883.3.4295:provider1,ou=HcProfessional,o=dev.provider-directories.com,dc=hpd");
        Filter filter = dsmlBasedObjectFactory.createFilter();
        filter.setEqualityMatch(attr);
        sreq.setFilter(filter);
        sreq.setDn(prop.getProperty(DN_HPD_MEMEBERSHIP));

        BatchRequest batchRequest = createBatchRequestObject(requestId, sreq);
        BatchResponse response = pdPortType.providerInformationQueryRequest(batchRequest);
        assertTrue(response.getBatchResponses().get(0).getValue() instanceof SearchResponse);
        Map<String, String> attrMap = getResponseAttributeMap((SearchResponse) response.getBatchResponses().get(0).getValue());
        assertTrue(attrMap.containsKey("hpdHasAProvider"));
        assertTrue(attrMap.get("hpdHasAProvider").equals("uid=2.16.840.1.113883.3.4295:provider1,ou=HcProfessional,o=dev.provider-directories.com,dc=hpd"));

        printSearchResult((SearchResponse) response.getBatchResponses().get(0).getValue());
    }

    @Test
    public void testCredential() {
        String requestId = UUID.randomUUID().toString();
        SearchRequest sreq = createSearchRequest();
        sreq.setRequestID(requestId);

        AttributeValueAssertion attr = new AttributeValueAssertion();
        attr.setName("credentialNumber");
        attr.setValue("1");
        Filter filter = dsmlBasedObjectFactory.createFilter();
        filter.setEqualityMatch(attr);
        sreq.setFilter(filter);
        sreq.setDn(prop.getProperty(DN_HPD_CREDENTIAL));

        BatchRequest batchRequest = createBatchRequestObject(requestId, sreq);
        BatchResponse response = pdPortType.providerInformationQueryRequest(batchRequest);
        assertTrue(response.getBatchResponses().get(0).getValue() instanceof SearchResponse);
        Map<String, String> attrMap = getResponseAttributeMap((SearchResponse) response.getBatchResponses().get(0).getValue());
        assertTrue(attrMap.containsKey("credentialNumber"));
        assertTrue(attrMap.get("credentialNumber").equals("1"));

        printSearchResult((SearchResponse) response.getBatchResponses().get(0).getValue());
    }

    @Test
    public void testServices() {
        String requestId = UUID.randomUUID().toString();
        SearchRequest sreq = createSearchRequest();
        sreq.setRequestID(requestId);

        AttributeValueAssertion attr = new AttributeValueAssertion();
        attr.setName("hpdServiceId");
        attr.setValue("6");
        Filter filter = dsmlBasedObjectFactory.createFilter();
        filter.setEqualityMatch(attr);
        sreq.setFilter(filter);
        sreq.setDn(prop.getProperty(DN_SERVICES));

        BatchRequest batchRequest = createBatchRequestObject(requestId, sreq);
        BatchResponse response = pdPortType.providerInformationQueryRequest(batchRequest);
        assertTrue(response.getBatchResponses().get(0).getValue() instanceof SearchResponse);
        Map<String, String> attrMap = getResponseAttributeMap((SearchResponse) response.getBatchResponses().get(0).getValue());
        assertTrue(attrMap.containsKey("hpdServiceId"));
        assertTrue(attrMap.get("hpdServiceId").equals("6"));

        printSearchResult((SearchResponse) response.getBatchResponses().get(0).getValue());
    }

    @Test
    public void testRelationship() {
        String requestId = UUID.randomUUID().toString();
        SearchRequest sreq = createSearchRequest();
        sreq.setRequestID(requestId);
        AttributeValueAssertion attr = new AttributeValueAssertion();

        attr.setName("cn");
        attr.setValue("JonesPracticeGroup");
        Filter filter = dsmlBasedObjectFactory.createFilter();
        filter.setEqualityMatch(attr);
        sreq.setFilter(filter);
        sreq.setDn(prop.getProperty(DN_RELATIONSHIP));
        BatchRequest batchRequest = createBatchRequestObject(requestId, sreq);
        BatchResponse response = pdPortType.providerInformationQueryRequest(batchRequest);
        assertTrue(response.getBatchResponses().get(0).getValue() instanceof SearchResponse);
        Map<String, String> attrMap = getResponseAttributeMap((SearchResponse) response.getBatchResponses().get(0).getValue());
        assertTrue(attrMap.containsKey("cn"));
        assertTrue(attrMap.get("cn").equals("JonesPracticeGroup"));

        printSearchResult((SearchResponse) response.getBatchResponses().get(0).getValue());
    }

    private URL getServiceURL() {
        URL serviceURL = null;
        try {
            serviceURL = new URL(prop.getProperty(WSDL_URL));
        } catch (MalformedURLException ex) {
            System.out.println("" + ex);
        }
        return serviceURL;
    }

    private SearchRequest createSearchRequest() {
        SearchRequest sreq = dsmlBasedObjectFactory.createSearchRequest();
        sreq.setScope(SINGLE_LEVEL);
        sreq.setDerefAliases(DEREF_FINDING_BASE_OBJ);
        return sreq;
    }

    private BatchRequest createBatchRequestObject(String requestId, SearchRequest sreq) {

        BatchRequest batchRequest = dsmlBasedObjectFactory.createBatchRequest();
        batchRequest.getBatchRequests().add(sreq);
        batchRequest.setRequestID(requestId);
        return batchRequest;
    }

    private void printSearchResult(SearchResponse searchResponse) {
        JAXBContext jbc;
        try {
            jbc = JAXBContext.newInstance(SearchResponse.class.getPackage().getName());
            Marshaller marshaller = jbc.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(new JAXBElement<SearchResponse>(new QName("uri", "local"), SearchResponse.class, searchResponse), System.out);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private Map getResponseAttributeMap(SearchResponse srep) {
        List<DsmlAttr> attrList = srep.getSearchResultEntry().get(0).getAttr();
        Map<String, String> attrMap = new HashMap<String, String>();
        for (DsmlAttr dsmlAttr : attrList) {
            attrMap.put(dsmlAttr.getName(), dsmlAttr.getValue().get(0));
        }
        return attrMap;
    }

    private byte[] convertToBytes(FederatedRequestData reqData) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        OutputStream mout;
        ObjectOutputStream out;
        try {
            mout = MimeUtility.encode(bos, "base64");
            out = new ObjectOutputStream(mout);
            out.writeObject(reqData);
            out.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        byte[] bytes = bos.toByteArray();
        return bytes;
    }

}
