/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.hhs.fhs.nhinc.fhir.transform;

import gov.hhs.fha.nhinc.mpi.adapter.component.hl7parsers.HL7Parser201306;
import gov.hhs.fha.nhinc.mpilib.Identifier;
import gov.hhs.fha.nhinc.mpilib.Identifiers;
import gov.hhs.fha.nhinc.mpilib.PersonName;
import gov.hhs.fha.nhinc.mpilib.PersonNames;
import gov.hhs.fha.nhinc.nhinclib.NullChecker;
import org.hl7.fhir.instance.model.AtomFeed;
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
            String aa = "1.2.3.FHIR";
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
}
