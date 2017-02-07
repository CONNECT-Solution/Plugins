/**
 *
 */
package gov.hhs.fha.nhinc.pmp.services;

import org.junit.BeforeClass;

import org.junit.AfterClass;
import java.io.IOException;
import gov.hhs.fha.nhinc.pmp.dto.PrescriptionReport;
import org.junit.Assert;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mpnguyen
 *
 */
public class CCDAParserTest {
    private final static Logger logger = LoggerFactory.getLogger(CCDAParserTest.class);
    private static String CCDASourceFileName;
    private static InputStream ccDAInputStream;
    private static CCDAParser ccdaParser;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUp() throws Exception {
        CCDASourceFileName = "sample_ccda.xml";
        ccdaParser = retrieveCCDAParser(CCDASourceFileName);

    }

    /**
     * @throws IOException
     */
    private static CCDAParser retrieveCCDAParser(final String ccdaFileName) throws IOException {

        URL url = CCDAParserTest.class.getClassLoader().getResource(ccdaFileName);
        final String htmlSourcePath = url.getPath();
        logger.debug("File exist {}", htmlSourcePath);
        File ccDASourceFile = new File(url.getPath());
        Assert.assertTrue("File should exist before running", ccDASourceFile.exists());
        ccDAInputStream = FileUtils.openInputStream(ccDASourceFile);
        return ParserServiceFactory.getInstance().getCCDAParser(ccDAInputStream);

    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDown() throws Exception {
        if (ccDAInputStream != null) {
            logger.debug("Preparing closing the input Stream");
            ccDAInputStream.close();
        }
    }

    @Test
    public void testRetrieveCCDAMedication() {
        CCDADoc ccDA = ccdaParser.getMedicationHistory();
        // CCDADoc ccDA = CCDAParserUtil.convertXMLToCCDA(ccDAInputStream);
        Assert.assertNotNull(ccDA);
        Assert.assertEquals(3, ccDA.getMedications().size());
    }

    @Test
    public void testAddMedicationSection() {
        PrescriptionReport prescription = new PrescriptionReport();
        prescription.setDrugName("Prednisone YYYYYYY");
        prescription.setDrugCount(3);
        boolean status = ccdaParser.addMedicationSection(prescription);
        Assert.assertTrue("It should be true after adding medication successful ", status);
    }

    @Test
    public void testDisplayCCDA() {
        String xmlContent = ccdaParser.displayContent();
        logger.debug("CCDA \n {}", xmlContent);
        Assert.assertNotNull(xmlContent);
    }



}
