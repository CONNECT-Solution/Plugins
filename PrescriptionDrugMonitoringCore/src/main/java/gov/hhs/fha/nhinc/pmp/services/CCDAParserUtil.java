/**
 *
 */
package gov.hhs.fha.nhinc.pmp.services;

import javax.xml.bind.JAXBIntrospector;

import java.io.InputStream;
import java.io.StringWriter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.hl7.v3.ObjectFactory;
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
     * @param <T>
     *
     * @param <T>
     *
     * @param ccDAInputStream
     * @return
     * @return
     */
    public static <T> T convertXMLToCCDA(InputStream ccDAInputStream, Class<T> responseClass) {
        try {
            logger.debug("Convert CCDA XML into Java Obj");
            JAXBContext jContext = JAXBContext.newInstance(responseClass);
            Unmarshaller unmarshaller = jContext.createUnmarshaller();
            return (T) JAXBIntrospector.getValue(unmarshaller.unmarshal(ccDAInputStream));
        } catch (JAXBException e) {
            logger.error("Unable to parsing xml into object due to {}", e);
            return null;
        }

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
