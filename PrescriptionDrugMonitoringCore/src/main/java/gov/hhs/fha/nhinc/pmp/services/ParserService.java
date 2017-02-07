/**
 *
 */
package gov.hhs.fha.nhinc.pmp.services;

import gov.hhs.fha.nhinc.pmp.dto.ProviderReport;

import gov.hhs.fha.nhinc.pmp.dto.PrescriptionReport;
import java.util.List;

/**
 * @author mpnguyen
 *
 */
public interface ParserService {

    public List<PrescriptionReport> getAllPrescriptions();

    public List<ProviderReport> getAllProviders();

    public List<ProviderReport> getAllPharmacies();
}
