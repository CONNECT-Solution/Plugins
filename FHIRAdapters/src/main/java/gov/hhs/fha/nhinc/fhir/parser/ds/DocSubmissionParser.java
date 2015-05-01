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
package gov.hhs.fha.nhinc.fhir.parser.ds;

/**
 *
 * @author tjafri
 */
import ca.uhn.fhir.model.dstu.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu.composite.CodingDt;
import ca.uhn.fhir.model.dstu.composite.PeriodDt;
import ca.uhn.fhir.model.dstu.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu.resource.DocumentReference.Context;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import gov.hhs.fha.nhinc.common.nhinccommon.PersonNameType;
import gov.hhs.fha.nhinc.fhir.exception.DocSubmissionException;
import gov.hhs.fha.nhinc.fhir.util.DocSubmissionConstants;
import gov.hhs.fha.nhinc.nhinclib.NullChecker;
import gov.hhs.fha.nhinc.util.format.UTCDateUtil;
import ihe.iti.xds_b._2007.ProvideAndRegisterDocumentSetRequestType;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBElement;

import oasis.names.tc.ebxml_regrep.xsd.rim._3.ClassificationType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ExternalIdentifierType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ExtrinsicObjectType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.IdentifiableType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.LocalizedStringType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.RegistryObjectListType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.RegistryObjectType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.RegistryPackageType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.SlotType1;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ValueListType;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class DocSubmissionParser {

    private static final Logger LOG = Logger.getLogger(DocSubmissionParser.class);
    private static final UTCDateUtil utcDateUtil = new UTCDateUtil();

    /**
     *
     * @param registryList
     * @return
     * @throws DocSubmissionException
     */
    public String extractPatientId(RegistryObjectListType registryList) throws DocSubmissionException {
        String patientId = null;
        RegistryPackageType regPackage = extractRegistryPackage(registryList);

        if (regPackage != null && regPackage.getSlot() != null && regPackage.getSlot().size() > 0) {
            patientId = getPatientIdFromExternalIdentifiers(regPackage.getExternalIdentifier(),
                DocSubmissionConstants.XDS_SUBMISSIONSET_PATIENT_ID);
        }

        return patientId;
    }

    /**
     *
     * @param registryList
     * @return
     */
    public String extractSourcePatientId(RegistryObjectListType registryList) {
        String patientId = null;

        ExtrinsicObjectType extrinsicObj = extractFirstExtrinsicObject(registryList);

        if (extrinsicObj != null) {
            ValueListType valueList = getValueListFromSlot(extrinsicObj.getSlot(),
                DocSubmissionConstants.XDS_DOCUMENT_SOURCE_PID);
            if (valueList != null && valueList.getValue() != null && valueList.getValue().size() > 0) {
                patientId = valueList.getValue().get(0);
            }
        }
        return patientId;
    }

    /**
     * This method parses the DS request and get the Document codingScheme
     *
     * @param registryList
     * @return
     */
    public CodeableConceptDt extractDocumentTypeMetaData(String documentId, RegistryObjectListType registryList) {
        LOG.info("extractDocumentTypeMetaData()");
        CodeableConceptDt type = null;
        ExtrinsicObjectType extrinsicObj = extractExtrinsicObject(documentId, registryList);
        if (extrinsicObj != null) {
            ClassificationType classification = getClassificationFromSchema(
                extrinsicObj.getClassification(), DocSubmissionConstants.XDS_DOCUMENT_CLASSIFICATION_SCHEME_TYPE_CODE);
            type = new CodeableConceptDt();
            //adding LONIC
            type.addCoding().setCode(classification.getNodeRepresentation());
            List<LocalizedStringType> localizedString = classification.getName().getLocalizedString();
            if (localizedString != null && localizedString.size() > 0) {
                //Document Type
                type.setText(classification.getName().getLocalizedString().get(0).getValue());
            }
        }
        return type;
    }

    /**
     * This method parses the DS request and get the Document class codingScheme
     *
     * @param registryList
     * @return
     */
    public CodeableConceptDt extractDocumentClassMetaData(String documentId, RegistryObjectListType registryList) {
        LOG.info("extractDocumentClassMetaData()");
        CodeableConceptDt classCode = null;
        ExtrinsicObjectType extrinsicObj = extractExtrinsicObject(documentId, registryList);
        if (extrinsicObj != null) {
            ClassificationType classification = getClassificationFromSchema(
                extrinsicObj.getClassification(), DocSubmissionConstants.XDS_DOCUMENT_CLASSIFICATION_SCHEME_CLASS_CODE);
            classCode = new CodeableConceptDt();
            //TODO adding LONIC
            //classCode.addCoding().setCode(classification.);
            List<LocalizedStringType> localizedString = classification.getName().getLocalizedString();
            if (localizedString != null && localizedString.size() > 0) {
                classCode.setText(classification.getName().getLocalizedString().get(0).getValue());
            }
        }
        return classCode;
    }

    /**
     * This method parses the DS request and gets the creationTime
     *
     * @param documentId
     * @param registryList
     * @return
     */
    public DateTimeDt extractDocumentCreationTime(String documentId, RegistryObjectListType registryList) throws ParseException {
        LOG.info("extractDocumentCreationTime()");
        ExtrinsicObjectType extrinsicObj = extractExtrinsicObject(documentId, registryList);
        if (extrinsicObj != null) {
            List<SlotType1> slotList = extrinsicObj.getSlot();
            for (SlotType1 slot : slotList) {
                if (slot.getName().equalsIgnoreCase(DocSubmissionConstants.XDS_DOCUMENT_CREATION_TIME)
                    && slot.getValueList() != null
                    && NullChecker.isNotNullish(slot.getValueList().getValue())) {
                    return new DateTimeDt(utcDateUtil.parseUTCDateOptionalTimeZone(slot.getValueList().getValue().get(0)));
                }
            }
        }
        return null;
    }

    /**
     * This method parses the DS request and get the authorPerson data
     *
     * @param registryList
     * @return
     */
    public List<ResourceReferenceDt> extractDocumentAuthorMetaData(String documentId, RegistryObjectListType registryList) {
        LOG.info("extractDocumentAuthorMetaData()");
        List<ResourceReferenceDt> authors = new ArrayList<ResourceReferenceDt>();
        ExtrinsicObjectType extrinsicObj = extractExtrinsicObject(documentId, registryList);
        if (extrinsicObj != null) {
            List<ClassificationType> classifications = getAuthorClassificationsFromSchema(
                extrinsicObj.getClassification(), DocSubmissionConstants.XDS_DOCUMENT_CLASSIFICATION_SCHEME_AUTHOR);
            for (ClassificationType classification : classifications) {
                List<SlotType1> slotList = classification.getSlot();
                ResourceReferenceDt author = new ResourceReferenceDt();
                for (SlotType1 slot : slotList) {
                    if (slot.getName().equalsIgnoreCase(DocSubmissionConstants.XDS_DOCUMENT_AUTHOR_PERSON)
                        && slot.getValueList() != null
                        && NullChecker.isNotNullish(slot.getValueList().getValue())) {
                        author.setDisplay(slot.getValueList().getValue().get(0));
                    }
                }
                authors.add(author);
            }
        }
        return authors;
    }

    /**
     * This method parses the DS request and get the Document mimeType
     *
     * @param documentId
     * @param registryList
     * @return
     */
    public String extractDocumentType(String documentId, RegistryObjectListType registryList) {
        LOG.info("extractDocumentType()");
        ExtrinsicObjectType extrinsicObj = extractExtrinsicObject(documentId, registryList);
        if (extrinsicObj != null) {
            return extrinsicObj.getMimeType();
        }
        return null;
    }

    /**
     * This method parses the DS request and get all the document Ids present in the request
     *
     * @param request
     * @return
     */
    public List<String> extractDocumentIds(ProvideAndRegisterDocumentSetRequestType request) {
        LOG.info("extractDocumentIds()");
        List<String> documentIds = new ArrayList<String>();
        RegistryObjectListType registryList = request.getSubmitObjectsRequest().getRegistryObjectList();
        if (registryList != null && registryList.getIdentifiable() != null && registryList.getIdentifiable().size() > 0) {
            List<JAXBElement<? extends IdentifiableType>> identifiers = registryList.getIdentifiable();
            for (JAXBElement<? extends IdentifiableType> object : identifiers) {
                if (object.getValue() != null && object.getValue() instanceof ExtrinsicObjectType) {
                    ExtrinsicObjectType extrinsicObj = (ExtrinsicObjectType) object.getValue();
                    documentIds.add(extrinsicObj.getId());
                }
            }
        }
        return documentIds;
    }

    /**
     * This method parses the DS request and gets the external identifier of the document
     *
     * @param registryList
     * @return
     */
    public String extractDocumentIdentificationValue(RegistryObjectListType registryList) {
        LOG.info("extractDocumentIdentificationValue");
        ExternalIdentifierType extDocId = extractExternalIdentifier(registryList, DocSubmissionConstants.XDS_EXTERNAL_DOCUMENT_IDENTIFICATION_SCHEME);
        if (extDocId != null) {
            return extDocId.getValue();
        }
        return null;
    }

    public RegistryPackageType extractRegistryPackage(RegistryObjectListType registryList)
        throws DocSubmissionException {
        RegistryPackageType regPackage = null;
        if (registryList != null && registryList.getIdentifiable() != null && registryList.getIdentifiable().size() > 0) {
            List<JAXBElement<? extends IdentifiableType>> identifiers = registryList.getIdentifiable();
            for (JAXBElement<? extends IdentifiableType> object : identifiers) {
                if (object.getValue() != null && object.getValue() instanceof RegistryPackageType) {
                    regPackage = (RegistryPackageType) object.getValue();
                    break;
                }
            }
        } else {
            LOG.error("RegistryPackage is null.");
            throw new DocSubmissionException("Unable to read Registry Package in request.",
                DocSubmissionConstants.XDR_EC_XDSRegistryMetadataError);
        }

        return regPackage;
    }

    private PersonNameType extractName(String value) {
        PersonNameType name = null;

        String pattern = "([a-zA-Z]*)(\\^)([a-zA-Z]*)(\\^\\^\\^)";
        Pattern namePattern = Pattern.compile(pattern);

        Matcher matcher = namePattern.matcher(value);

        if (matcher.matches()) {
            name = new PersonNameType();
            name.setFamilyName(matcher.group(1));
            name.setGivenName(matcher.group(3));
        } else {
            pattern = "([a-zA-Z]*)(\\^)" + pattern;
            namePattern = Pattern.compile(pattern);
            matcher = namePattern.matcher(value);

            if (matcher.matches()) {
                name = new PersonNameType();
                name.setFamilyName(matcher.group(1));
                name.setGivenName(matcher.group(3));
                name.setSecondNameOrInitials(matcher.group(5));
            }
        }

        return name;
    }

    /**
     * Gets the classification value.
     *
     * @param classification the classification
     * @param extrinsicObject the extrinsic object
     * @return the classification value
     */
    public String getClassificationValue(String classification, ExtrinsicObjectType extrinsicObject) {
        RegistryObjectType registryObjectType = getClassification(classification, extrinsicObject);
        ClassificationType classType = null;
        if (registryObjectType instanceof ClassificationType) {
            classType = (ClassificationType) registryObjectType;
        }
        return classType.getNodeRepresentation();
    }

    /**
     * Gets the classification.
     *
     * @param classification the classification
     * @param extrinsicObject the extrinsic object
     * @return the classification
     */
    public RegistryObjectType getClassification(String classification, ExtrinsicObjectType extrinsicObject) {
        RegistryObjectType registryObject = null;
        if (extrinsicObject != null && classification != null) {
            List<ClassificationType> classifications = extrinsicObject.getClassification();
            for (ClassificationType c : classifications) {
                if (StringUtils.equalsIgnoreCase(c.getClassificationScheme(), classification)) {
                    registryObject = c;
                }
            }
        }
        return registryObject;
    }

    private ExtrinsicObjectType extractExtrinsicObject(String documentId, RegistryObjectListType registryList) {
        LOG.info("extractExtrinsicObject()");
        CodeableConceptDt type = null;
        List<JAXBElement<? extends IdentifiableType>> identifiers = registryList.getIdentifiable();
        for (JAXBElement<? extends IdentifiableType> object : identifiers) {
            if (object.getValue() != null && object.getValue() instanceof ExtrinsicObjectType) {
                ExtrinsicObjectType extrinsicObj = (ExtrinsicObjectType) object.getValue();
                if (extrinsicObj.getId().equals(documentId)) {
                    return extrinsicObj;
                }
            }
        }
        return null;
    }

    /**
     * This method parses the DS request and returns Patient name as a String(first name, middle initial if any and last
     * name)
     *
     * @param registryList
     * @return
     */
    public String getPatientName(RegistryObjectListType registryList) throws DocSubmissionException {

        ValueListType valueList = extractPatientInfoValueListFromRegistryList(registryList);
        StringBuilder builder = new StringBuilder();
        for (String value : valueList.getValue()) {
            if (value.startsWith(DocSubmissionConstants.XDS_PATIENT_INFO_NAME_PREFIX)) {
                String nameValue = value.substring(DocSubmissionConstants.XDS_PATIENT_INFO_NAME_PREFIX.length());
                PersonNameType name = extractName(nameValue);
                if (name != null) {
                    builder.append(name.getGivenName());
                    if (name.getSecondNameOrInitials() != null && !name.getSecondNameOrInitials().equals("")) {
                        builder.append(DocSubmissionConstants.SEPARATOR);
                        builder.append(name.getSecondNameOrInitials());
                    }
                    builder.append(DocSubmissionConstants.SEPARATOR);
                    builder.append(name.getFamilyName());
                }
                break;
            }
        }
        return builder.toString();
    }

    /**
     * This method parses the DS request and returns a map containing given and family name
     *
     * @param registryList
     * @return
     */
    public Map<String, String> getPatientQueryParam(RegistryObjectListType registryList) throws DocSubmissionException {
        ValueListType valueList = extractPatientInfoValueListFromRegistryList(registryList);
        for (String value : valueList.getValue()) {
            if (value.startsWith(DocSubmissionConstants.XDS_PATIENT_INFO_NAME_PREFIX)) {
                String nameValue = value.substring(DocSubmissionConstants.XDS_PATIENT_INFO_NAME_PREFIX.length());
                PersonNameType name = extractName(nameValue);
                HashMap<String, String> param = null;
                if (name != null) {
                    param = new HashMap<String, String>();
                    param.put("given", name.getGivenName());
                    param.put("family", name.getFamilyName());
                    return param;
                }
            }
        }
        return null;
    }

    private ValueListType extractPatientInfoValueListFromRegistryList(RegistryObjectListType registryList)
        throws DocSubmissionException {
        ValueListType valueList = null;
        ExtrinsicObjectType extrinsicObj = extractFirstExtrinsicObject(registryList);

        if (extrinsicObj != null) {
            valueList = getValueListFromSlot(extrinsicObj.getSlot(),
                DocSubmissionConstants.XDS_DOCUMENT_SOURCE_PATIENT_INFO);
        } else {
            throw new DocSubmissionException("No extrinsic objects included in request.",
                DocSubmissionConstants.XDR_EC_XDSRegistryMetadataError);
        }
        return valueList;
    }

    private List<ClassificationType> getAuthorClassificationsFromSchema(List<ClassificationType> classificationList, String scheme) {
        List<ClassificationType> foundClassification = new ArrayList<ClassificationType>();
        for (ClassificationType classification : classificationList) {
            if (classification.getClassificationScheme().equals(scheme)) {
                foundClassification.add(classification);
            }
        }
        return foundClassification;
    }

    private ClassificationType getClassificationFromSchema(List<ClassificationType> classificationList, String scheme) {
        ClassificationType foundClassification = null;
        for (ClassificationType classification : classificationList) {
            if (classification.getClassificationScheme().equals(scheme)) {
                foundClassification = classification;
                break;
            }
        }
        return foundClassification;
    }

    private ExtrinsicObjectType extractFirstExtrinsicObject(RegistryObjectListType registryList) {
        ExtrinsicObjectType extrinsicObj = null;
        if (registryList != null && registryList.getIdentifiable() != null && registryList.getIdentifiable().size() > 0) {
            List<JAXBElement<? extends IdentifiableType>> identifiers = registryList.getIdentifiable();
            for (JAXBElement<? extends IdentifiableType> object : identifiers) {
                if (object.getValue() != null && object.getValue() instanceof ExtrinsicObjectType) {
                    extrinsicObj = (ExtrinsicObjectType) object.getValue();
                }
            }
        }
        return extrinsicObj;
    }

    private ValueListType getValueListFromSlot(List<SlotType1> slots, String name) {
        ValueListType valueList = null;
        for (SlotType1 slot : slots) {
            if (slot.getName() != null && slot.getName().equals(name)) {
                if (slot.getValueList() != null && slot.getValueList().getValue() != null
                    && slot.getValueList().getValue().size() > 0) {
                    valueList = slot.getValueList();
                    break;
                }
            }
        }
        return valueList;
    }

    String getPatientIdFromExternalIdentifiers(List<ExternalIdentifierType> externalIdentifiers, String name) {
        String patientId = null;
        for (ExternalIdentifierType identifier : externalIdentifiers) {
            if (identifier.getName() != null
                && identifier.getName().getLocalizedString() != null
                && identifier.getName().getLocalizedString().size() > 0
                && identifier.getName().getLocalizedString().get(0) != null
                && identifier.getName().getLocalizedString().get(0).getValue()
                .equals(DocSubmissionConstants.XDS_SUBMISSIONSET_PATIENT_ID)) {
                patientId = identifier.getValue();
                break;
            }

        }
        return patientId;
    }

    private ExternalIdentifierType extractExternalIdentifier(RegistryObjectListType registryList, String externalID) {
        LOG.info("extractExternalIdentifier");
        List<JAXBElement<? extends IdentifiableType>> identifiers = registryList.getIdentifiable();
        for (JAXBElement<? extends IdentifiableType> object : identifiers) {
            if (object.getValue() != null && object.getValue() instanceof ExtrinsicObjectType) {
                ExtrinsicObjectType extrinsicObj = (ExtrinsicObjectType) object.getValue();
                if (extrinsicObj != null) {
                    List<ExternalIdentifierType> extIdentifierList = extrinsicObj.getExternalIdentifier();
                    for (ExternalIdentifierType extIdType : extIdentifierList) {
                        if (extIdType.getIdentificationScheme().equals(externalID)) {
                            return extIdType;
                        }
                    }
                }
            }
        }
        return null;
    }

    public String extractDocumentName(String documentId, RegistryObjectListType registryList) {
        LOG.info("extractDocumentName()");
        ExtrinsicObjectType extrinsicObj = extractExtrinsicObject(documentId, registryList);
        if (extrinsicObj != null) {
            if (extrinsicObj.getName() != null
                && NullChecker.isNotNullish(extrinsicObj.getName().getLocalizedString())) {
                return extrinsicObj.getName().getLocalizedString().get(0).getValue();
            }
        }
        return null;
    }

    /**
     * This method parses the DS request and returns Document size name)
     *
     * @param documentId
     * @param registryList
     * @return
     */
    public int extractDocumentSize(String documentId, RegistryObjectListType registryList) {
        LOG.info("extractDocumentSize()");
        ExtrinsicObjectType extrinsicObj = extractExtrinsicObject(documentId, registryList);
        if (extrinsicObj != null) {
            List<SlotType1> slotList = extrinsicObj.getSlot();
            for (SlotType1 slot : slotList) {
                if (slot.getName().equalsIgnoreCase(DocSubmissionConstants.XDS_DOCUMENT_SIZE)
                    && slot.getValueList() != null
                    && NullChecker.isNotNullish(slot.getValueList().getValue())) {
                    return (new Integer(slot.getValueList().getValue().get(0)));
                }
            }
        }
        return 0;
    }

    /**
     * This method parses the DS request and returns Document Language
     *
     * @param documentId
     * @param registryList
     * @return
     */
    public String extractLanguage(String documentId, RegistryObjectListType registryList) {
        LOG.info("extractLanguage()");
        ExtrinsicObjectType extrinsicObj = extractExtrinsicObject(documentId, registryList);
        if (extrinsicObj != null) {
            List<SlotType1> slotList = extrinsicObj.getSlot();
            for (SlotType1 slot : slotList) {
                if (slot.getName().equalsIgnoreCase(DocSubmissionConstants.XDS_DOCUMENT_LANGUAGE)
                    && slot.getValueList() != null
                    && NullChecker.isNotNullish(slot.getValueList().getValue())) {
                    return slot.getValueList().getValue().get(0);
                }
            }
        }
        return null;
    }

    /**
     * This method parses the DS request and returns Context Information including serviceStartTime, ServiceEndTine,
     * FacilityType and eventCode name)
     *
     * @param documentId
     * @param registryList
     * @return
     * @throws java.text.ParseException
     */
    public Context extractConextInformation(String documentId, RegistryObjectListType registryList) throws ParseException {
        LOG.info("extractConextInformation");
        Context context = null;
        ExtrinsicObjectType extrinsicObj = extractExtrinsicObject(documentId, registryList);
        if (extrinsicObj != null) {
            context = new Context();
            //extracting the eventcode
            CodeableConceptDt eventCode = getCodeClassification(
                extrinsicObj.getClassification(), DocSubmissionConstants.XDS_CLASSIFICATION_EVENT_CODE);
            //setting the event code
            context.getEvent().add(eventCode);

            //setting the Facility TYpe
            context.setFacilityType(getCodeClassification(extrinsicObj.getClassification(),
                DocSubmissionConstants.XDS_CLASSIFICATION_FACILITY_TYPE));

            //setting the service start time and service stop time
            PeriodDt period = new PeriodDt();
            period.setStart(getServiceTime(extrinsicObj, DocSubmissionConstants.XDS_DOCUMENT_SERVICE_START_TIME));
            period.setEnd(getServiceTime(extrinsicObj, DocSubmissionConstants.XDS_DOCUMENT_SERVICE_STOP_TIME));
            context.setPeriod(period);
        }
        return context;
    }

    private DateTimeDt getServiceTime(ExtrinsicObjectType extrinsicObj, String serviceTimeCode) throws ParseException {
        if (extrinsicObj != null) {
            List<SlotType1> slotList = extrinsicObj.getSlot();
            for (SlotType1 slot : slotList) {
                if (slot.getName().equalsIgnoreCase(serviceTimeCode)
                    && slot.getValueList() != null
                    && NullChecker.isNotNullish(slot.getValueList().getValue())) {
                    return new DateTimeDt(utcDateUtil.parseUTCDateOptionalTimeZone(slot.getValueList().getValue().get(0)));
                }
            }
        }
        return null;
    }

    private CodeableConceptDt getCodeClassification(List<ClassificationType> list, String scheme) {
        ClassificationType classification = getClassificationFromSchema(list, scheme);
        if (classification != null
            && classification.getName() != null
            && classification.getName().getLocalizedString() != null
            && classification.getName().getLocalizedString().size() > 0) {

            CodingDt code = new CodingDt();
            code.setCode(classification.getNodeRepresentation());
            CodeableConceptDt facilityConcept = new CodeableConceptDt();
            code.setDisplay(classification.getName().getLocalizedString().get(0).getValue());
            List<CodingDt> codingList = new ArrayList<CodingDt>();
            codingList.add(code);
            facilityConcept.setCoding(codingList);
            return facilityConcept;
        }
        return null;
    }
}
