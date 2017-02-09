/**
 *
 */
package gov.hhs.fha.nhinc.pmp.services;

import org.w3c.dom.Node;
import gov.hhs.fha.nhinc.pmp.dto.PrescriptionReport;
import java.io.InputStream;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.hl7.v3.CEExplicit;
import org.hl7.v3.CS;
import org.hl7.v3.EDExplicit;
import org.hl7.v3.EntityDeterminerDetermined;
import org.hl7.v3.II;
import org.hl7.v3.IVLPQ;
import org.hl7.v3.POCDMT000040ClinicalDocument;
import org.hl7.v3.POCDMT000040Component3;
import org.hl7.v3.POCDMT000040Consumable;
import org.hl7.v3.POCDMT000040Entry;
import org.hl7.v3.POCDMT000040ManufacturedProduct;
import org.hl7.v3.POCDMT000040Material;
import org.hl7.v3.POCDMT000040SubstanceAdministration;
import org.hl7.v3.RoleClassManufacturedProduct;
import org.hl7.v3.XDocumentSubstanceMood;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


/**
 * @author mpnguyen
 *
 */
public class CCDAParser {
    private POCDMT000040ClinicalDocument CCDADocument;
    private final static Logger logger = LoggerFactory.getLogger(CCDAParser.class);
    private final static String MEDICATION_SECTION_ID = "2.16.840.1.113883.10.20.1.8";
    /**
     * @param ccDAInputStream
     */
    public CCDAParser(InputStream ccDAInputStream) {
        CCDADocument = CCDAParserUtil.convertXMLToCCDA(ccDAInputStream, POCDMT000040ClinicalDocument.class);
    }

    /**
     * @return
     */
    public CCDADoc getMedicationHistory() {
        CCDADoc doc = new CCDADoc();
        List<POCDMT000040Component3> components = CCDADocument.getComponent().getStructuredBody().getComponent();
        for (POCDMT000040Component3 component : components) {
            String templateId = component.getSection().getTemplateId().get(0).getRoot();
            String title = component.getSection().getTitle().getContent().toString();
            logger.debug("TemplateId: {} and title {} ", templateId, title);
            // flag if medication section is found
            if (MEDICATION_SECTION_ID.equalsIgnoreCase(templateId)) {
                List<POCDMT000040Entry> medicationEntries = component.getSection().getEntry();

                for (POCDMT000040Entry medication : medicationEntries) {

                    String drugName = medication.getSubstanceAdministration().getConsumable().getManufacturedProduct()
                            .getManufacturedMaterial().getName().getContent().get(0).toString();
                    String drugIngredient = medication.getSubstanceAdministration().getConsumable()
                            .getManufacturedProduct().getManufacturedMaterial().getCode().getDisplayName();
                    logger.debug("---->Drug Name: {}-->Drug Ingredients {}", drugName, drugIngredient);

                    PrescriptionReport prescriptionReport = new PrescriptionReport();
                    prescriptionReport.setDrugName(drugName);
                    doc.getMedications().add(prescriptionReport);
                }
            }

        }
        return doc;

    }

    /**
     * @param prescription
     * @return
     */
    public boolean addMedicationSection(PrescriptionReport prescription) {
        String drugName = prescription.getDrugName();
        String drugQuality = String.valueOf(prescription.getDrugCount());
        POCDMT000040Entry medicationSection = new POCDMT000040Entry();
        POCDMT000040SubstanceAdministration substanceAdmin = new POCDMT000040SubstanceAdministration();
        substanceAdmin.getClassCode().add("SBADM");
        substanceAdmin.setMoodCode(XDocumentSubstanceMood.EVN);
        II templateId = new II();
        templateId.setAssigningAuthorityName("CCD");
        templateId.setRoot("2.16.840.1.113883.10.20.1.24");
        substanceAdmin.getTemplateId().add(templateId);
        CS drugStatus = new CS();
        drugStatus.setCode("active");
        substanceAdmin.setStatusCode(drugStatus);
        // ignore EffectiveTime.routeCode
        IVLPQ dosequality = new IVLPQ();
        dosequality.setValue(drugQuality);
        substanceAdmin.setDoseQuantity(dosequality);
        // Set consumable section
        POCDMT000040Consumable consumable = new POCDMT000040Consumable();
        // set consumable->manufacturedProduct
        POCDMT000040ManufacturedProduct drugProduct = new POCDMT000040ManufacturedProduct();
        drugProduct.setClassCode(RoleClassManufacturedProduct.MANU);
        POCDMT000040Material drugMaterial = new POCDMT000040Material();
        drugMaterial.setClassCode("MMAT");
        drugMaterial.setDeterminerCode(EntityDeterminerDetermined.KIND);
        CEExplicit drugCode = new CEExplicit();
        drugCode.setCode("111111");// random drug code
        drugCode.setDisplayName(drugName);
        drugCode.setCodeSystemName("RxNorm");
        drugCode.setCodeSystem("2.16.840.1.113883.6.88"); // code for RxNorm
        EDExplicit drugOriginalText = new EDExplicit();
        drugOriginalText.getContent().add(drugName);
        drugCode.setOriginalText(drugOriginalText);
        drugMaterial.setCode(drugCode);
        drugProduct.setManufacturedMaterial(drugMaterial);
        consumable.setManufacturedProduct(drugProduct);
        substanceAdmin.setConsumable(consumable);
        medicationSection.setSubstanceAdministration(substanceAdmin);
        // Add to ccda
        List<POCDMT000040Component3> components = CCDADocument.getComponent().getStructuredBody().getComponent();
        for (POCDMT000040Component3 component : components) {
            String templateDrugId = component.getSection().getTemplateId().get(0).getRoot();
            String title = component.getSection().getTitle().getContent().toString();
            logger.debug("TemplateId: {} and title {} ", templateId, title);
            // flag if medication section is found
            if (MEDICATION_SECTION_ID.equalsIgnoreCase(templateDrugId)) {
                logger.debug("Preparing to add medication history ");
                component.getSection().getEntry().add(medicationSection);
                logger.debug("Add Text elements");
                Element drugElementText = component.getSection().getText();
                Document document = null;
                if (drugElementText == null) {
                    // This means there is no Medical history in CCDA document. Prepare to add one
                    document = createCCDATextDocument();
                    // need to find how to get rid of namespace in text element
                    drugElementText = document.createElement("text");

                    // Create Table element
                    Element tableElement = document.createElement("table");
                    tableElement.setAttribute("border", "1");
                    tableElement.setAttribute("width", "100%");
                    Element tHeadElement = (Element) createElement(document, "thead", null);
                    Element tHeadRowElement = (Element) createElement(document, "tr", null);
                    tHeadRowElement.appendChild(createElement(document, "th", "Product Display Name"));
                    tHeadRowElement.appendChild(createElement(document, "th", "Free Text Brand Name"));
                    tHeadRowElement.appendChild(createElement(document, "th", "Ordered Value"));
                    tHeadRowElement.appendChild(createElement(document, "th", "Ordered Unit"));
                    tHeadRowElement.appendChild(createElement(document, "th", "Expiration Time"));
                    tHeadElement.appendChild(tHeadRowElement);
                    tableElement.appendChild(tHeadElement);
                    tableElement.appendChild(createElement(document, "tbody", null));
                    drugElementText.appendChild(tableElement);

                }
                // Add new drug into table
                document = drugElementText.getOwnerDocument();
                Element tr = document.createElement("tr");
                NodeList tableBodyNodeList = drugElementText.getElementsByTagName("tbody");
                Element tableBody = (Element) tableBodyNodeList.item(0);

                // create new td
                tr.appendChild(createTDElement(document, prescription.getDrugName()));
                tr.appendChild(createTDElement(document, prescription.getDrugBrandName()));
                tr.appendChild(createTDElement(document, String.valueOf(prescription.getDrugCount())));
                tr.appendChild(createTDElement(document, prescription.getOrderUnit()));
                tr.appendChild(createTDElement(document, prescription.getDrugExpiration()));
                tableBody.appendChild(tr);
                component.getSection().setText(drugElementText);
                return true;
            }
        }
        return false;
    }

    /**
     * @param document
     * @param string
     * @param object
     * @return
     */
    private static Node createElement(Document document, String tagName, String tagValue) {
        Element element = document.createElement(tagName);
        element.setTextContent(tagValue);
        return element;
    }

    /**
     * @return
     */
    private static Document createCCDATextDocument() {

        DocumentBuilderFactory factory = null;
        DocumentBuilder builder = null;
        Document ret;
        try {
            factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        ret = builder.newDocument();

        return ret;
    }

    /**
     * @return
     */
    public String displayContent() {
        return CCDAParserUtil.convertCCDAToXML(CCDADocument);

    }

    private static Element createTDElement(Document doc, String value) {
        Element element = doc.createElement("td");
        element.setTextContent(value);
        return element;
    }


}
