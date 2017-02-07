/**
 *
 */
package gov.hhs.fha.nhinc.pmp.services;

import org.hl7.v3.EDExplicit;
import gov.hhs.fha.nhinc.pmp.dto.PrescriptionReport;
import java.io.InputStream;
import java.util.List;
import org.hl7.v3.CEExplicit;
import org.hl7.v3.CS;
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
        CCDADocument = CCDAParserUtil.convertXMLToCCDA(ccDAInputStream);
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
                return true;
            }
        }
        return false;
    }

    /**
     * @return
     */
    public String displayContent() {
        return CCDAParserUtil.convertCCDAToXML(CCDADocument);

    }

}
