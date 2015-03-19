/*
 * Copyright (c) 2009-2015, United States Government, as represented by the Secretary of Health and Human Services.
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
package gov.hhs.fhs.nhinc.fhir.transform;

import com.sun.istack.logging.Logger;
import gov.hhs.fha.nhinc.docrepository.adapter.model.Document;
import gov.hhs.fha.nhinc.docrepository.adapter.model.EventCode;
import gov.hhs.fha.nhinc.mpi.adapter.component.hl7parsers.HL7Parser201306;
import gov.hhs.fha.nhinc.mpilib.Identifier;
import gov.hhs.fha.nhinc.mpilib.Identifiers;
import gov.hhs.fha.nhinc.mpilib.PersonName;
import gov.hhs.fha.nhinc.mpilib.PersonNames;
import gov.hhs.fha.nhinc.nhinclib.NullChecker;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.hl7.fhir.instance.model.AtomEntry;
import org.hl7.fhir.instance.model.AtomFeed;
import org.hl7.fhir.instance.model.CodeableConcept;
import org.hl7.fhir.instance.model.DateAndTime;
import org.hl7.fhir.instance.model.DocumentReference;
import org.hl7.fhir.instance.model.Practitioner;
import org.hl7.fhir.instance.model.Resource;
import static org.hl7.fhir.instance.model.ResourceType.Patient;
import org.hl7.v3.PRPAIN201305UV02;
import org.hl7.v3.PRPAIN201306UV02;

/**
 *
 * @author jassmit
 */
public class ResourceTransformer {

    private static final String DATE_FORMAT = "yyyy-MM-dd'T'hh:mm:ss";
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT);

    private static final Logger LOG = Logger.getLogger(ResourceTransformer.class);

    public PRPAIN201306UV02 buildPatientResponse(AtomFeed feed, PRPAIN201305UV02 findCandidatesRequest) {
        gov.hhs.fha.nhinc.mpilib.Patients patients = new gov.hhs.fha.nhinc.mpilib.Patients();

        if (feed != null && feed.getEntryList() != null
            && !feed.getEntryList().isEmpty()
            && feed.getEntryList().get(0) != null
            && feed.getEntryList().get(0).getResource() != null
            && feed.getEntryList().get(0).getResource().getResourceType().equals(Patient)) {

            org.hl7.fhir.instance.model.Patient fhirPatient = (org.hl7.fhir.instance.model.Patient) feed.getEntryList().get(0).getResource();
            patients.add(transformPatient(fhirPatient));
        }
        return HL7Parser201306.buildMessageFromMpiPatient(patients, findCandidatesRequest);
    }

    private gov.hhs.fha.nhinc.mpilib.Patient transformPatient(org.hl7.fhir.instance.model.Patient fhirPatient) {
        gov.hhs.fha.nhinc.mpilib.Patient nhinPatient = new gov.hhs.fha.nhinc.mpilib.Patient();

        PersonName personName = new PersonName();

        if (NullChecker.isNotNullish(fhirPatient.getName())
            && fhirPatient.getName().get(0) != null) {
            if (NullChecker.isNotNullish(fhirPatient.getName().get(0).getFamily())) {
                personName.setLastName(fhirPatient.getName().get(0).getFamily().get(0).getValue());
            }
            if (NullChecker.isNotNullish(fhirPatient.getName().get(0).getGiven())) {
                personName.setFirstName(fhirPatient.getName().get(0).getGiven().get(0).getValue());
            }
        }

        PersonNames names = new PersonNames();
        names.add(personName);
        nhinPatient.setNames(names);

        if (fhirPatient.getGender() != null
            && NullChecker.isNotNullish(fhirPatient.getGender().getCoding())) {
            nhinPatient.setGender(fhirPatient.getGender().getCoding().get(0).getCode().getValue());
        }

        if (fhirPatient.getBirthDate() != null) {
            nhinPatient.setDateOfBirth(fhirPatient.getBirthDate().getValue().toString());
        }

        if (NullChecker.isNotNullish(fhirPatient.getIdentifier())) {

            String aa = fhirPatient.getIdentifier().get(0).getSystem().getValue();
            String patientId = fhirPatient.getIdentifier().get(0).getValue().getValue();

            Identifiers identifiers = new Identifiers();
            Identifier identifier = new Identifier();
            identifier.setId(patientId);
            identifier.setOrganizationId(aa);
            identifiers.add(identifier);
            nhinPatient.setIdentifiers(identifiers);
        }

        return nhinPatient;
    }

    public List<Document> buildDocumentsFromResource(List<AtomEntry<? extends Resource>> referenceAtoms) {
        List<Document> documents = new ArrayList<>();
        for (AtomEntry entry : referenceAtoms) {
            if (entry.getResource() != null && entry.getResource() instanceof DocumentReference) {
                DocumentReference reference = (DocumentReference) entry.getResource();
                Document document = new Document();

                document.setDocumentUri(reference.getMasterIdentifier().getValueSimple());
                document.setHash(reference.getHashSimple());
                document.setMimeType(reference.getMimeTypeSimple());

                if (reference.getStatus() != null) {
                    document.setStatus(reference.getStatusSimple().name());
                }

                
                document.setDocumentTitle(reference.getDescriptionSimple());
                
                if(reference.getSize() != null) {
                    document.setSize(reference.getSizeSimple());
                }
                
                try {
                    if(reference.getCreated() != null && reference.getCreated().getValue() != null) {
                        document.setCreationTime(convertDateAndTime(reference.getCreated().getValue()));
                    }
                } catch (ParseException ex) {
                    LOG.warning("Error parsing creation time for document: " + ex, ex);
                }

                buildAuthorInfo(document, reference);

                buildFromContext(document, reference.getContext());

                buildConfidentialityCode(document, reference.getConfidentiality());

                buildClassCode(document, reference.getType());

                documents.add(document);
            }
        }
        return documents;
    }

    private void buildAuthorInfo(Document document, DocumentReference reference) {
        if (NullChecker.isNotNullish(reference.getAuthor())) {
            String authorRef = reference.getAuthor().get(0).getReferenceSimple();

            Practitioner authorInfo = getPractitioner(authorRef, reference.getContained());
            if (authorInfo != null) {
                if (authorInfo.getOrganization() != null) {
                    document.setAuthorInstitution(authorInfo.getOrganization().getDisplaySimple());
                }
                if (authorInfo.getName() != null && NullChecker.isNotNullish(authorInfo.getName().getFamily())
                    && NullChecker.isNotNullish(authorInfo.getName().getGiven())) {
                    document.setAuthorPerson(authorInfo.getName().getGiven().get(0).getValue() + " "
                        + authorInfo.getName().getFamily().get(0).getValue());
                }

                if (NullChecker.isNotNullish(authorInfo.getRole())) {
                    document.setAuthorRole(authorInfo.getRole().get(0).getTextSimple());
                }

                if (NullChecker.isNotNullish(authorInfo.getSpecialty())) {
                    document.setAuthorSpecialty(authorInfo.getSpecialty().get(0).getTextSimple());
                }
            }
        }
    }

    private Practitioner getPractitioner(String ref, List<Resource> resources) {
        for (Resource resource : resources) {
            if (ref.equals("#" + resource.getXmlId()) && resource.getResourceType().equals(org.hl7.fhir.instance.model.ResourceType.Practitioner)) {
                return (Practitioner) resource;
            }
        }
        return null;
    }

    private void buildFromContext(Document document, DocumentReference.DocumentReferenceContextComponent context) {
        if (context != null) {

            if (NullChecker.isNotNullish(context.getEvent())) {
                Set<EventCode> events = new HashSet<>();
                for (CodeableConcept event : context.getEvent()) {

                    if (NullChecker.isNotNullish(event.getCoding())) {
                        EventCode eventCode = new EventCode();
                        eventCode.setEventCode(event.getCoding().get(0).getCodeSimple());
                        eventCode.setEventCodeScheme(event.getCoding().get(0).getSystemSimple());
                        eventCode.setEventCodeDisplayName(event.getCoding().get(0).getDisplaySimple());

                        events.add(eventCode);
                    }
                    document.setEventCodes(events);
                }
            }

            if (context.getPeriod() != null) {
                try {
                    document.setServiceStartTime(convertDateAndTime(context.getPeriod().getStart().getValue()));
                    document.setServiceStopTime(convertDateAndTime(context.getPeriod().getEnd().getValue()));
                } catch (ParseException ex) {
                    LOG.warning("Unable to format start/end times for document: " + ex, ex);
                }
            }

            if (context.getFacilityType() != null) {
                CodeableConcept facility = context.getFacilityType();
                document.setFacilityCode(facility.getCoding().get(0).getCodeSimple());
                document.setFacilityCodeDisplayName(facility.getCoding().get(0).getDisplaySimple());
                document.setFacilityCodeScheme(facility.getCoding().get(0).getSystemSimple());
            }
        }
    }

    private void buildConfidentialityCode(Document document, List<CodeableConcept> confCodes) {
        if (NullChecker.isNotNullish(confCodes)) {
            document.setConfidentialityCode(confCodes.get(0).getCoding().get(0).getCodeSimple());
            document.setConfidentialityCodeDisplayName(confCodes.get(0).getCoding().get(0).getDisplaySimple());
            document.setConfidentialityCodeScheme(confCodes.get(0).getCoding().get(0).getSystemSimple());
        }
    }

    private void buildClassCode(Document document, CodeableConcept type) {
        if (type != null) {
            document.setClassCode(type.getCoding().get(0).getCodeSimple());
            document.setClassCodeDisplayName(type.getCoding().get(0).getDisplaySimple());
            document.setClassCodeScheme(type.getCoding().get(0).getSystemSimple());
        }
    }

    private Date convertDateAndTime(DateAndTime dateTime) throws ParseException {
        StringBuilder dateBuilder = new StringBuilder();

        String year = Integer.toString(dateTime.getYear());
        String month = Integer.toString(dateTime.getMonth());
        month = appendZero(month, dateTime.getMonth());
        String day = Integer.toString(dateTime.getDay());
        day = appendZero(day, dateTime.getDay());

        String hours = Integer.toString(dateTime.getHour());
        hours = appendZero(hours, dateTime.getHour());
        String minutes = Integer.toString(dateTime.getMinute());
        minutes = appendZero(minutes, dateTime.getMinute());
        String seconds = Integer.toString(dateTime.getSecond());
        seconds = appendZero(seconds, dateTime.getSecond());

        dateBuilder.append(year).append("-").append(month).append("-").append(day).append("T").append(hours)
            .append(":").append(minutes).append(":").append(seconds);

        return dateFormatter.parse(dateBuilder.toString());
    }

    private String appendZero(String value, int number) {
        if (number < 10) {
            value = "0" + value;
        }
        return value;
    }

}
