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

import gov.hhs.fha.nhinc.docrepository.adapter.model.Document;
import gov.hhs.fha.nhinc.mpi.adapter.component.hl7parsers.HL7Parser201306;
import gov.hhs.fha.nhinc.mpilib.Identifier;
import gov.hhs.fha.nhinc.mpilib.Identifiers;
import gov.hhs.fha.nhinc.mpilib.PersonName;
import gov.hhs.fha.nhinc.mpilib.PersonNames;
import gov.hhs.fha.nhinc.nhinclib.NullChecker;
import java.util.ArrayList;
import java.util.List;
import org.hl7.fhir.instance.model.AtomEntry;
import org.hl7.fhir.instance.model.AtomFeed;
import org.hl7.fhir.instance.model.DocumentReference;
import org.hl7.fhir.instance.model.Resource;
import static org.hl7.fhir.instance.model.ResourceType.Patient;
import org.hl7.v3.PRPAIN201305UV02;
import org.hl7.v3.PRPAIN201306UV02;

/**
 *
 * @author jassmit
 */
public class ResourceTransformer {

    public PRPAIN201306UV02 getPatientResponse(AtomFeed feed, PRPAIN201305UV02 findCandidatesRequest) {
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
        
        if(fhirPatient.getBirthDate() != null) {
            nhinPatient.setDateOfBirth(fhirPatient.getBirthDate().getValue().toString());
        }
        
        if(NullChecker.isNotNullish(fhirPatient.getIdentifier())) {
            
            //TODO: confirm what the AA should be in a FHIR Patient Resource
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

    public List<Document> getDocumentsFromResource(List<AtomEntry<? extends Resource>> referenceAtoms) {
        List<Document> documents = new ArrayList<>();
        for(AtomEntry entry : referenceAtoms) {
            if(entry.getResource() != null && entry.getResource() instanceof DocumentReference) {
                DocumentReference reference = (DocumentReference) entry.getResource();
                Document document = new Document();
                
                document.setDocumentUri(reference.getLocationSimple());
                document.setHash(reference.getHashSimple());
                document.setMimeType(reference.getMimeTypeSimple());
                
                if(reference.getStatus() != null)
                    document.setStatus(reference.getStatus().asStringValue());
                
                document.setDocumentTitle(reference.getDescriptionSimple());
                document.setSize(reference.getSizeSimple());

                //TODO: finish conversions, need a lot more research.
                documents.add(document);
            }
        }
        return documents;
    }
}
