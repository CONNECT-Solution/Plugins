/*
 * Copyright (c) 2009-2018, United States Government, as represented by the Secretary of Health and Human Services.
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
package gov.hhs.fha.nhinc.pdmp.services;

import gov.hhs.fha.nhinc.pdmp.model.PrescriptionInfo;
import gov.hhs.fha.nhinc.pdmp.util.CDAParserUtil;
import org.w3c.dom.Node;
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
 * Implementation for CDA Parser Service
 * {@inheritDoc}
 * @author mpnguyen
 *
 */
public class CDAParserServiceImpl implements CDAParserService {

    private final static Logger logger = LoggerFactory.getLogger(CDAParserServiceImpl.class);
    private final static String MEDICATION_SECTION_ID = "2.16.840.1.113883.10.20.1.8";

    /**
     * {@inheritDoc}
     * 
     */
    @Override
    public String addMedicationSection(InputStream stream, List<PrescriptionInfo> prescriptions) {
        POCDMT000040ClinicalDocument cdaDocument = CDAParserUtil.convertXMLToCDA(stream, POCDMT000040ClinicalDocument.class);

        for (PrescriptionInfo drug : prescriptions) {
            String quantity = String.valueOf(drug.getDrugCount());
            IVLPQ doseQuantity = new IVLPQ();
            doseQuantity.setValue(quantity);

            POCDMT000040Entry medicationSection = new POCDMT000040Entry();
            POCDMT000040SubstanceAdministration substanceAdmin = new POCDMT000040SubstanceAdministration();
            substanceAdmin.getClassCode().add("SBADM");
            substanceAdmin.setMoodCode(XDocumentSubstanceMood.EVN);

            substanceAdmin.getTemplateId().add(buildTemplateId());
            substanceAdmin.setStatusCode(buildCs());

            substanceAdmin.setDoseQuantity(doseQuantity);
            // Set consumable section
            POCDMT000040Consumable consumable = new POCDMT000040Consumable();
            // set consumable->manufacturedProduct

            consumable.setManufacturedProduct(buildDrugProduct(drug.getDrugName()));
            substanceAdmin.setConsumable(consumable);
            medicationSection.setSubstanceAdministration(substanceAdmin);
            // Add to ccda

            addToCda(cdaDocument, medicationSection, drug);
        }

        return displayContent(cdaDocument);
    }

    private CS buildCs() {
        CS drugStatus = new CS();
        drugStatus.setCode("active");
        return drugStatus;
    }

    private II buildTemplateId() {
        II templateId = new II();
        templateId.setAssigningAuthorityName("CCD");
        templateId.setRoot("2.16.840.1.113883.10.20.1.24");
        return templateId;
    }

    private CEExplicit buildDrugCode(String drugName) {
        CEExplicit drugCode = new CEExplicit();
        drugCode.setCode("111111");// random drug code
        drugCode.setDisplayName(drugName);
        drugCode.setCodeSystemName("RxNorm");
        drugCode.setCodeSystem("2.16.840.1.113883.6.88"); // code for RxNorm

        EDExplicit drugOriginalText = new EDExplicit();
        drugOriginalText.getContent().add(drugName);

        drugCode.setOriginalText(drugOriginalText);
        return drugCode;
    }

    private POCDMT000040Material buildDrugMaterial(String drugName) {
        POCDMT000040Material drugMaterial = new POCDMT000040Material();
        drugMaterial.setClassCode("MMAT");
        drugMaterial.setDeterminerCode(EntityDeterminerDetermined.KIND);

        drugMaterial.setCode(buildDrugCode(drugName));
        return drugMaterial;
    }

    private POCDMT000040ManufacturedProduct buildDrugProduct(String drugName) {
        POCDMT000040ManufacturedProduct drugProduct = new POCDMT000040ManufacturedProduct();
        drugProduct.setClassCode(RoleClassManufacturedProduct.MANU);
        drugProduct.setManufacturedMaterial(buildDrugMaterial(drugName));
        return drugProduct;
    }

    private Element populateTable(Document document) {
        Element tableElement = document.createElement("table");
        tableElement.setAttribute("border", "1");
        tableElement.setAttribute("width", "100%");
        Element tHeadElement = (Element) createElement(document, "thead", null);
        Element tHeadRowElement = (Element) createElement(document, "tr", null);
        tHeadRowElement.appendChild(createElement(document, "th", "Product Display Name"));
        tHeadRowElement.appendChild(createElement(document, "th", "Drug Class"));
        tHeadRowElement.appendChild(createElement(document, "th", "Quantities"));
        tHeadRowElement.appendChild(createElement(document, "th", "Fill Date"));
        tHeadElement.appendChild(tHeadRowElement);
        tableElement.appendChild(tHeadElement);
        tableElement.appendChild(createElement(document, "tbody", null));
        return tableElement;
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


    private void addToCda(POCDMT000040ClinicalDocument cdaDocument, POCDMT000040Entry medicationSection,
            PrescriptionInfo drugInfo) {
        List<POCDMT000040Component3> components = cdaDocument.getComponent().getStructuredBody().getComponent();
        for (POCDMT000040Component3 component : components) {
            String templateDrugId = component.getSection().getTemplateId().get(0).getRoot();
            String title = component.getSection().getTitle().getContent().toString();
            logger.debug("TemplateDrugId: {} and title {} ", templateDrugId, title);
            // flag if medication section is found
            if (MEDICATION_SECTION_ID.equalsIgnoreCase(templateDrugId)) {
                logger.debug("Preparing to add medication history ");

                final String drugName = drugInfo.getDrugName();
                final String drugClass = drugInfo.getDrugClass();
                final String drugCount = String.valueOf(drugInfo.getDrugCount());
                final String drugFillDate = drugInfo.getFileStrDate();

                component.getSection().getEntry().add(medicationSection);
                Element drugElementText = component.getSection().getText();
                Document document = null;

                // This means there is no Medical history in CCDA document. Prepare to add one
                if (drugElementText == null) {
                    document = createCDATextDocument();
                    drugElementText = document.createElement("text");
                    drugElementText.appendChild(populateTable(document));
                }

                // Add new drug into table
                document = drugElementText.getOwnerDocument();
                Element tr = document.createElement("tr");
                NodeList tableBodyNodeList = drugElementText.getElementsByTagName("tbody");
                Element tableBody = (Element) tableBodyNodeList.item(0);

                // create new td
                tr.appendChild(createTDElement(document, drugName));

                tr.appendChild(createTDElement(document, drugClass));
                tr.appendChild(createTDElement(document, drugCount));
                tr.appendChild(createTDElement(document, String.valueOf(drugFillDate)));

                tableBody.appendChild(tr);
                component.getSection().setText(drugElementText);
            }
        }
    }

    /**
     * @return
     */
    private static Document createCDATextDocument() {

        DocumentBuilderFactory factory;
        DocumentBuilder builder;
        Document ret = null;
        try {
            factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            builder = factory.newDocumentBuilder();
            ret = builder.newDocument();

        } catch (ParserConfigurationException e) {
            logger.error(e.getLocalizedMessage(), e);
        }

        return ret;
    }

    private String displayContent(POCDMT000040ClinicalDocument cdaDocument) {
        return CDAParserUtil.convertCDAToXML(cdaDocument);

    }

    private static Element createTDElement(Document doc, String value) {
        Element element = doc.createElement("td");
        element.setTextContent(value);
        return element;
    }

}
