/**
 *
 */
package gov.hhs.fha.nhinc.pmp.services;

import java.util.ArrayList;
import gov.hhs.fha.nhinc.pmp.dto.PrescriptionReport;
import java.util.List;

/**
 * @author mpnguyen
 *
 */
public class CCDADoc {

    private List<PrescriptionReport> medications;

    /**
     * @return the medications
     */
    public List<PrescriptionReport> getMedications() {
        if (medications == null) {
            medications = new ArrayList<PrescriptionReport>();
        }
        return medications;
    }

    /**
     * @param medications the medications to set
     */
    public void setMedications(List<PrescriptionReport> medications) {
        this.medications = medications;
    }

}
