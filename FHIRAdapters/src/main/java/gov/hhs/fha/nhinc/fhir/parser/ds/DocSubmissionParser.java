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
import ca.uhn.fhir.model.dstu.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import gov.hhs.fha.nhinc.common.nhinccommon.PersonNameType;
import gov.hhs.fha.nhinc.fhir.exception.DocSubmissionException;
import gov.hhs.fha.nhinc.fhir.util.DocSubmissionConstants;
import gov.hhs.fha.nhinc.nhinclib.NullChecker;
import ihe.iti.xds_b._2007.ProvideAndRegisterDocumentSetRequestType;
import ihe.iti.xds_b._2007.ProvideAndRegisterDocumentSetRequestType.Document;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.activation.DataHandler;
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
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryError;

import org.apache.commons.lang.StringUtils;
import org.apache.cxf.helpers.IOUtils;
import org.apache.log4j.Logger;
import org.hl7.v3.ADExplicit;
import org.hl7.v3.AdxpExplicitCity;
import org.hl7.v3.AdxpExplicitCountry;
import org.hl7.v3.AdxpExplicitPostalCode;
import org.hl7.v3.AdxpExplicitState;
import org.hl7.v3.AdxpExplicitStreetAddressLine;
import org.hl7.v3.BinaryDataEncoding;
import org.hl7.v3.CE;
import org.hl7.v3.ENExplicit;
import org.hl7.v3.EnExplicitFamily;
import org.hl7.v3.EnExplicitGiven;
import org.hl7.v3.IVLTSExplicit;
import org.hl7.v3.PRPAMT201306UV02LivingSubjectAdministrativeGender;
import org.hl7.v3.PRPAMT201306UV02LivingSubjectBirthTime;
import org.hl7.v3.PRPAMT201306UV02LivingSubjectName;
import org.hl7.v3.PRPAMT201306UV02PatientAddress;
import org.hl7.v3.STExplicit;

public class DocSubmissionParser {

    private static final Logger LOG = Logger.getLogger(DocSubmissionParser.class);

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

    public DateTimeDt extractDocumentCreationTime(String documentId, RegistryObjectListType registryList) throws ParseException {
        LOG.info("extractDocumentCreationTime()");
        ExtrinsicObjectType extrinsicObj = extractExtrinsicObject(documentId, registryList);
        if (extrinsicObj != null) {
            List<SlotType1> slotList = extrinsicObj.getSlot();
            for (SlotType1 slot : slotList) {
                if (slot.getName().equalsIgnoreCase(DocSubmissionConstants.XDS_DOCUMENT_CREATION_TIME)
                    && slot.getValueList() != null
                    && NullChecker.isNotNullish(slot.getValueList().getValue())) {
                    SimpleDateFormat sdf = new SimpleDateFormat(DocSubmissionConstants.CREATION_TIME_FORMAT);
                    return new DateTimeDt(sdf.parse(slot.getValueList().getValue().get(0)));
                }
            }
        }
        return null;
    }

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

    public String extractDocumentType(String documentId, RegistryObjectListType registryList) {
        LOG.info("extractDocumentType()");
        ExtrinsicObjectType extrinsicObj = extractExtrinsicObject(documentId, registryList);
        if (extrinsicObj != null) {
            return extrinsicObj.getMimeType();
        }
        return null;
    }

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

    public String extractDocumentIdentificationValue(RegistryObjectListType registryList) {
        LOG.info("extractDocumentIdentificationValue");
        ExternalIdentifierType extDocId = extractExternalIdentifier(registryList, DocSubmissionConstants.XDS_EXTERNAL_DOCUMENT_IDENTIFICATION_SCHEME);
        if (extDocId != null) {
            return extDocId.getValue();
        }
        return null;
    }

    public RegistryError createRegistryError(String error, String code) {
        RegistryError regError = new RegistryError();
        regError.setErrorCode(code);
        regError.setCodeContext(error);
        regError.setSeverity(DocSubmissionConstants.DS_RESPONSE_ERROR_SEVERITY);

        return regError;
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

    public String documentToString(Document document) throws IOException {
        InputStream is = null;
        try {
            is = document.getValue().getInputStream();
            return IOUtils.toString(is);
        } finally {
            is.close();
        }
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

    private PRPAMT201306UV02PatientAddress extractAddress(String addressValue) {
        PRPAMT201306UV02PatientAddress address = null;
        org.hl7.v3.ObjectFactory objectFactory = new org.hl7.v3.ObjectFactory();

        String pattern = "([^\\^]*)(\\^\\^)([^\\^]*)(\\^)([^\\^]*)(\\^)([^\\^]*)(\\^)([^\\^]*)";
        Pattern addressPattern = Pattern.compile(pattern);

        Matcher matcher = addressPattern.matcher(addressValue);

        if (matcher.matches()) {
            address = new PRPAMT201306UV02PatientAddress();

            ADExplicit explicit = createBasicAddress(matcher, objectFactory);

            AdxpExplicitCountry countryAddress = new AdxpExplicitCountry();
            countryAddress.setContent(matcher.group(9));
            JAXBElement<AdxpExplicitCountry> countryJAX = objectFactory.createADExplicitCountry(countryAddress);

            explicit.getContent().add(countryJAX);

            address.getValue().add(explicit);
        } else {
            pattern = "([^\\^]*)(\\^\\^)([^\\^]*)(\\^)([^\\^]*)(\\^)([^\\^]*)";
            addressPattern = Pattern.compile(pattern);
            matcher = addressPattern.matcher(addressValue);

            if (matcher.matches()) {
                address = new PRPAMT201306UV02PatientAddress();

                ADExplicit explicit = createBasicAddress(matcher, objectFactory);

                address.getValue().add(explicit);
            }
        }

        return address;
    }

    private ADExplicit createBasicAddress(Matcher matcher, org.hl7.v3.ObjectFactory objectFactory) {
        ADExplicit explicit = new ADExplicit();

        AdxpExplicitStreetAddressLine streetAddress = new AdxpExplicitStreetAddressLine();
        streetAddress.setContent(matcher.group(1));
        JAXBElement<AdxpExplicitStreetAddressLine> streetAddressJAX = objectFactory
            .createADExplicitStreetAddressLine(streetAddress);

        AdxpExplicitCity cityAddress = new AdxpExplicitCity();
        cityAddress.setContent(matcher.group(3));
        JAXBElement<AdxpExplicitCity> cityJAX = objectFactory.createADExplicitCity(cityAddress);

        AdxpExplicitState stateAddress = new AdxpExplicitState();
        stateAddress.setContent(matcher.group(5));
        JAXBElement<AdxpExplicitState> stateJAX = objectFactory.createADExplicitState(stateAddress);

        AdxpExplicitPostalCode postalAddress = new AdxpExplicitPostalCode();
        postalAddress.setContent(matcher.group(7));
        JAXBElement<AdxpExplicitPostalCode> postalJAX = objectFactory.createADExplicitPostalCode(postalAddress);

        explicit.getContent().add(streetAddressJAX);
        explicit.getContent().add(cityJAX);
        explicit.getContent().add(stateJAX);
        explicit.getContent().add(postalJAX);

        return explicit;
    }

    private STExplicit createST(String mediaType) {
        STExplicit semantics = new STExplicit();
        semantics.setMediaType(mediaType);
        semantics.setRepresentation(BinaryDataEncoding.TXT);
        return semantics;
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

    /**
     * @param e
     * @return
     */
    public String getDocumentId(ExtrinsicObjectType e) {
        return e.getId();
    }

    /**
     * @param documentId
     * @param request
     * @return
     * @throws IOException
     */
    public byte[] getDocumentById(String documentId, ProvideAndRegisterDocumentSetRequestType request)
        throws IOException {
        byte[] document = null;
        for (Document d : request.getDocument()) {
            if (StringUtils.equals(documentId, d.getId())) {
                document = convertToBytes(d.getValue());
            }
        }
        return document;
    }

    /**
     * Saves the data handler as a byte array. The data handler will be empty at the end of this call.
     *
     * @param dh - the data handler to convert
     * @return a byte array containing the data from the data handler
     * @throws IOException
     */
    public byte[] convertToBytes(DataHandler dh) throws IOException {
        InputStream is = dh.getInputStream();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            int read = 0;
            byte[] bytes = new byte[1024];
            while ((read = is.read(bytes)) != -1) {
                baos.write(bytes, 0, read);
            }
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                    LOG.error("Could not close input stream : " + e.getMessage());
                }
            }
        }
        return baos.toByteArray();
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

    ExtrinsicObjectType extractFirstExtrinsicObject(RegistryObjectListType registryList) {
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

    ValueListType getValueListFromSlot(List<SlotType1> slots, String name) {
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

    PRPAMT201306UV02LivingSubjectName createSubjectName(PersonNameType name) {

        PRPAMT201306UV02LivingSubjectName subjectName = new PRPAMT201306UV02LivingSubjectName();
        ENExplicit explicitName = new ENExplicit();

        org.hl7.v3.ObjectFactory objFactory = new org.hl7.v3.ObjectFactory();

        EnExplicitFamily explicitFamilyValue = new EnExplicitFamily();
        explicitFamilyValue.setContent(name.getFamilyName());
        explicitFamilyValue.setPartType(DocSubmissionConstants.PARAMS_FAMILY_PART_TYPE);
        JAXBElement<EnExplicitFamily> explicitFamily = objFactory.createENExplicitFamily(explicitFamilyValue);

        EnExplicitGiven explicitGivenValue = new EnExplicitGiven();
        explicitGivenValue.setContent(name.getGivenName());
        explicitGivenValue.setPartType(DocSubmissionConstants.PARAMS_GIVEN_PART_TYPE);
        JAXBElement<EnExplicitGiven> explicitGiven = objFactory.createENExplicitGiven(explicitGivenValue);

        explicitName.getContent().add(explicitFamily);
        explicitName.getContent().add(explicitGiven);

        if (name.getSecondNameOrInitials() != null && !name.getSecondNameOrInitials().equals("")) {
            EnExplicitGiven explicitMiddleValue = new EnExplicitGiven();
            explicitMiddleValue.setContent(name.getSecondNameOrInitials());
            explicitMiddleValue.setPartType(DocSubmissionConstants.PARAMS_GIVEN_PART_TYPE);
            JAXBElement<EnExplicitGiven> explicitMiddle = objFactory.createENExplicitGiven(explicitMiddleValue);
            explicitName.getContent().add(explicitMiddle);
        }

        subjectName.setSemanticsText(createST(DocSubmissionConstants.SUBJECT_NAME_SEMANTICS));

        subjectName.getValue().add(explicitName);

        return subjectName;
    }

    PRPAMT201306UV02LivingSubjectAdministrativeGender createAdminGender(String gender) {
        PRPAMT201306UV02LivingSubjectAdministrativeGender adminGender = new PRPAMT201306UV02LivingSubjectAdministrativeGender();
        CE code = new CE();
        code.setCode(gender);
        adminGender.getValue().add(code);

        adminGender.setSemanticsText(createST(DocSubmissionConstants.SUBJECT_GENDER_SEMANTICS));
        return adminGender;
    }

    PRPAMT201306UV02LivingSubjectBirthTime createBirthTime(String time) {
        PRPAMT201306UV02LivingSubjectBirthTime birthTime = new PRPAMT201306UV02LivingSubjectBirthTime();

        IVLTSExplicit explicitDate = new IVLTSExplicit();
        explicitDate.setValue(time);
        birthTime.getValue().add(explicitDate);

        birthTime.setSemanticsText(createST(DocSubmissionConstants.SUBJECT_DOB_SEMANTICS));
        return birthTime;
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
}
