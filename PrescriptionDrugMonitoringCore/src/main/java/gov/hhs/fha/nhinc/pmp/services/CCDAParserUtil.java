/**
 *
 */
package gov.hhs.fha.nhinc.pmp.services;

import org.hl7.v3.ObjectFactory;

import java.io.InputStream;
import java.io.StringWriter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.hl7.v3.POCDMT000040ClinicalDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mpnguyen
 *
 */
public class CCDAParserUtil {
    private final static Logger logger = LoggerFactory.getLogger(CCDAParserUtil.class);

    /**
     * Convert xml into object
     *
     * @param ccDAInputStream
     * @return
     */
    public static POCDMT000040ClinicalDocument convertXMLToCCDA(InputStream ccDAInputStream) {
        POCDMT000040ClinicalDocument document;
        try {
            logger.debug("Convert CCDA XML into CCDA Java Obj");
            JAXBContext jContext = JAXBContext.newInstance(POCDMT000040ClinicalDocument.class);
            Unmarshaller unmarshaller = jContext.createUnmarshaller();
            JAXBElement<POCDMT000040ClinicalDocument> result = (JAXBElement<POCDMT000040ClinicalDocument>) unmarshaller
                    .unmarshal(ccDAInputStream);
            document = result.getValue();
        } catch (JAXBException e) {
            logger.error("Unable to parsing xml into object due to {}", e);
            document = new POCDMT000040ClinicalDocument();
        }
        return document;

    }
    /**
     * @param ccDAInputStream
     * @return
     */
    public static String convertCCDAToXML(POCDMT000040ClinicalDocument ccDADoc) {
        try {
            logger.debug("Convert CCDA Java Obj to XML");
            ObjectFactory factory = new ObjectFactory();
            JAXBContext jContext = JAXBContext.newInstance(POCDMT000040ClinicalDocument.class);
            Marshaller marshaller = jContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

            StringWriter strWrite = new StringWriter();
            marshaller.marshal(factory.createClinicalDocument(ccDADoc), strWrite);
            return strWrite.toString();

        } catch (JAXBException e) {
            logger.error("Unable to convert ccdaObj to xml due to {}", e);
        }
        return null;
    }

}
