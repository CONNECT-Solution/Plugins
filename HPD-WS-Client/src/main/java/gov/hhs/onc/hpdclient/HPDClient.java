package gov.hhs.onc.hpdclient;

import ihe.iti.hpd._2010.ProviderInformationDirectoryPortType;
import ihe.iti.hpd._2010.ProviderInformationDirectoryService;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.UUID;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.ws.soap.AddressingFeature;
import oasis.names.tc.dsml._2._0.core.AttributeValueAssertion;
import oasis.names.tc.dsml._2._0.core.BatchRequest;
import oasis.names.tc.dsml._2._0.core.BatchResponse;
import oasis.names.tc.dsml._2._0.core.ErrorResponse;
import oasis.names.tc.dsml._2._0.core.Filter;
import oasis.names.tc.dsml._2._0.core.ObjectFactory;
import oasis.names.tc.dsml._2._0.core.SearchRequest;
import oasis.names.tc.dsml._2._0.core.SearchResponse;

/**
 *
 * @author tjafri
 */
public class HPDClient {

    private static ObjectFactory dsmlBasedObjectFactory = null;
    private static ProviderInformationDirectoryService pdService = null;
    private static ProviderInformationDirectoryPortType pdPortType = null;

    private static final String DN_HC_PROFESSIONAL = "ou=HcProfessional,o=dev.provider-directories.com,dc=hpd";
    private static final String DN_HC_REG_ORG = "ou=HcRegulatedOrganization,o=dev.provider-directories.com,dc=hpd";
    private static final String DN_HPD_MEMEBERSHIP = "ou=HPDProviderMembership,o=dev.provider-directories.com,dc=hpd";
    private static final String DN_HPD_CREDENTIAL = "ou=HPDCredential,o=dev.provider-directories.com,dc=hpd";
    private static final String DN_SERVICES = "ou=Services,o=dev.provider-directories.com,dc=hpd";
    private static final String DN_RELATIONSHIP = "ou=Relationship,o=dev.provider-directories.com,dc=hpd";
    private static final String SINGLE_LEVEL = "singleLevel";
    private static final String DEREF_FINDING_BASE_OBJ = "derefFindingBaseObj";
    private static final String WSDL_URL = "http://54.201.181.21/pdti-server/ProviderInformationDirectoryService?wsdl";

    public static void main(String[] args) {
        try {
            setup();
        } catch (Exception anException) {
            anException.printStackTrace();
            System.exit(-1);
        }
        if (args.length == 0) {
            printUsage();
        } else {
            if ("professional".equalsIgnoreCase(args[0])) {
                searchQuery(args, DN_HC_PROFESSIONAL);
            } else if ("organization".equalsIgnoreCase(args[0])) {
                searchQuery(args, DN_HC_REG_ORG);
            } else if ("membership".equalsIgnoreCase(args[0])) {
                searchQuery(args, DN_HPD_MEMEBERSHIP);
            } else if ("services".equalsIgnoreCase(args[0])) {
                searchQuery(args, DN_SERVICES);
            } else if ("credential".equalsIgnoreCase(args[0])) {
                searchQuery(args, DN_HPD_CREDENTIAL);
            } else if ("relationship".equalsIgnoreCase(args[0])) {
                searchQuery(args, DN_RELATIONSHIP);
            } else {
                printUsage();
            }
        }
    }

    private static void printUsage() {
        System.out.println("USAGE");
        System.out.println("professional givenName Thomas");
        System.out.println("organization businessCategory MRI");
        System.out.println("membership hpdHasAProvider uid=2.16.840.1.113883.3.4295:provider1,ou=HcProfessional,o=dev.provider-directories.com,dc=hpd");
        System.out.println("services hpdServiceId 6");
        System.out.println("credential credentialNumber 1");
        System.out.println("relationship cn JonesPracticeGroup");
    }

    private static void searchQuery(String[] arg, String dn) {
        String requestId = UUID.randomUUID().toString();
        SearchRequest sreq = createSearchRequest();
        sreq.setRequestID(requestId);
        AttributeValueAssertion attr = new AttributeValueAssertion();
        attr.setName(arg[1]);
        attr.setValue(arg[2]);
        Filter filter = dsmlBasedObjectFactory.createFilter();
        filter.setEqualityMatch(attr);
        sreq.setFilter(filter);
        sreq.setDn(dn);

        BatchRequest batchRequest = createBatchRequestObject(requestId, sreq);
        BatchResponse response = pdPortType.providerInformationQueryRequest(batchRequest);

        printSearchResult(response.getBatchResponses().get(0).getValue());
    }

    private static URL getServiceURL() {
        URL serviceURL = null;
        try {
            serviceURL = new URL(WSDL_URL);
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
            System.exit(-1);
        }
        return serviceURL;
    }

    private static SearchRequest createSearchRequest() {
        SearchRequest sreq = dsmlBasedObjectFactory.createSearchRequest();
        sreq.setScope(SINGLE_LEVEL);
        sreq.setDerefAliases(DEREF_FINDING_BASE_OBJ);
        return sreq;
    }

    private static BatchRequest createBatchRequestObject(String requestId, SearchRequest sreq) {
        BatchRequest batchRequest = dsmlBasedObjectFactory.createBatchRequest();
        batchRequest.getBatchRequests().add(sreq);
        batchRequest.setRequestID(requestId);
        return batchRequest;
    }

    private static void printSearchResult(Object response) {
        JAXBContext jbc;
        try {
            jbc = JAXBContext.newInstance(SearchResponse.class.getPackage().getName());
            Marshaller marshaller = jbc.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            if (response instanceof SearchResponse) {
                marshaller.marshal(new JAXBElement<SearchResponse>(new QName("uri", "local"), SearchResponse.class, (SearchResponse) response), System.out);
            } else {
                marshaller.marshal(new JAXBElement<ErrorResponse>(new QName("uri", "local"), ErrorResponse.class, (ErrorResponse) response), System.out);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void setup() throws IOException {
        dsmlBasedObjectFactory = new ObjectFactory();
        pdService = new ProviderInformationDirectoryService(getServiceURL());
        pdPortType = pdService.getProviderInformationDirectoryPortSoap(new AddressingFeature(true, true));
    }

}
