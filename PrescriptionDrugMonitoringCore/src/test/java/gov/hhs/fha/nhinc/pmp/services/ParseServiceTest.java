/**
 *
 */
package gov.hhs.fha.nhinc.pmp.services;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import gov.hhs.fha.nhinc.pmp.dto.PrescriptionReport;
import gov.hhs.fha.nhinc.pmp.dto.ProviderReport;
import java.net.URL;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mpnguyen
 *
 */
public class ParseServiceTest {
    private String htmlSourceFile;
    private final static Logger logger = LoggerFactory.getLogger(ParseServiceTest.class);
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        htmlSourceFile = "sample_report.html";

    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    @Test
    public final void testGetAllPrescriptions() {

        URL url = getClass().getClassLoader().getResource(htmlSourceFile);
        final String htmlSourcePath = url.getPath();
        ParserService htmlParser = ParserServiceFactory.getInstance().getHTMLParser(htmlSourcePath);
        try {
            List<PrescriptionReport> prescriptionList = htmlParser.getAllPrescriptions();
            assertTrue(prescriptionList.size() == 5);
            for (PrescriptionReport report : prescriptionList) {
                logger.debug("Fill Date-->{}", report.getFileStrDate());
                logger.debug("Drug-->{}", report.getDrugName());
                logger.debug("Drug Quality--> {}", report.getDrugCount());
                logger.debug("Drug Days-->{}", report.getDrugDuration());
                logger.debug("Prescriber--> {}", report.getPrescriber());
                logger.debug("Pharmacy--> {}", report.getPharmacyName());
                logger.debug("Refill--> {}", report.getRefill());
                logger.debug("MgEq--> {}", report.getMgEq());
                logger.debug("MgEq/Day--> {}", report.getMgEdPerDay());
                logger.debug("Payment Type--> {}", report.getPaymentType());
                logger.debug("PMP State--> {}", report.getPmpState());
                logger.debug("--------------------");
            }
        } catch (Exception e) {
            fail(e.getLocalizedMessage());
        }


    }

    @Test
    public final void testGetAllProviders() {
        // https://dailymed.nlm.nih.gov/dailymed/app-support-web-services.cfm
        URL url = getClass().getClassLoader().getResource(htmlSourceFile);
        final String htmlSourcePath = url.getPath();
        ParserService htmlParser = ParserServiceFactory.getInstance().getHTMLParser(htmlSourcePath);
        try {
            List<ProviderReport> providerList = htmlParser.getAllProviders();
            assertTrue(providerList.size() == 3);
            logger.debug("--------Providers---------");
            for (ProviderReport report : providerList) {
                logger.debug("Name:{}, Address: {}, City: {},State: {}, ZipCode: {}, DEA: {}",
                        report.getProviderName(), report.getAddress(), report.getCity(), report.getState(),
                        report.getZipCode(), report.getDea());
                logger.debug("--------------------");
            }
        } catch (Exception e) {
            fail(e.getLocalizedMessage());
        }

    }

    @Test
    public final void testGetAllPharmacy() {

        URL url = getClass().getClassLoader().getResource(htmlSourceFile);
        final String htmlSourcePath = url.getPath();
        ParserService htmlParser = ParserServiceFactory.getInstance().getHTMLParser(htmlSourcePath);
        try {
            List<ProviderReport> pharmacies = htmlParser.getAllPharmacies();
            assertTrue(pharmacies.size() == 2);
            logger.debug("--------pharmacies---------");
            for (ProviderReport report : pharmacies) {
                logger.debug("Name:{}, Address: {}, City: {},State: {}, ZipCode: {}, DEA: {}",
                        report.getProviderName(), report.getAddress(), report.getCity(), report.getState(),
                        report.getZipCode(), report.getDea());
                logger.debug("--------------------");
            }
        } catch (Exception e) {
            fail(e.getLocalizedMessage());
        }

    }


}
