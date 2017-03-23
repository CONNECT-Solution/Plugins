/**
 *
 */
package gov.hhs.fha.nhinc.pmp.dto;

/**
 * @author mpnguyen
 *
 */
public class PatientDTO {
    private String firstName;
    private String lastName;
    private String patientRecordId;

    /**
     * @return the firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName the firstName to set
     */
    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return the lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName the lastName to set
     */
    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    /**
     * @return the patientRecordId
     */
    public String getPatientRecordId() {
        return patientRecordId;
    }

    /**
     * @param patientRecordId the patientRecordId to set
     */
    public void setPatientRecordId(final String patientRecordId) {
        this.patientRecordId = patientRecordId;
    }

}
