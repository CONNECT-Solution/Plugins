package gov.hhs.onc.hpdclient;

import gov.hhc.onc.hpdclient.service.HPDService;
import gov.hhc.onc.hpdclient.service.HPDServiceImpl;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import oasis.names.tc.dsml._2._0.core.BatchResponse;
import oasis.names.tc.dsml._2._0.core.ErrorResponse;
import oasis.names.tc.dsml._2._0.core.SearchResponse;

/**
 *
 * @author tjafri
 */
public class HPDClient {

    public static void main(String[] args) {
        try {
            HPDService service = new HPDServiceImpl();
            if (args.length == 0 || args.length != 3) {
                printUsage();
            } else {
                if (service != null) {
                    printSearchResult(service.searchQuery(args[0], args[1], args[2]));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
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

    private static void printSearchResult(BatchResponse response) {
        JAXBContext jbc;
        try {
            jbc = JAXBContext.newInstance(SearchResponse.class.getPackage().getName());
            Marshaller marshaller = jbc.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            Object result = response.getBatchResponses().get(0).getValue();
            if (result instanceof SearchResponse) {
                marshaller.marshal(new JAXBElement<SearchResponse>(new QName("uri", "local"), SearchResponse.class, (SearchResponse) result), System.out);
            } else {
                marshaller.marshal(new JAXBElement<ErrorResponse>(new QName("uri", "local"), ErrorResponse.class, (ErrorResponse) result), System.out);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
