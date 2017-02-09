/**
 *
 */
package gov.hhs.fha.nhinc.pmp.services;

import gov.hhs.fha.nhinc.pmp.dto.Rxclassdata;

import java.io.InputStream;
import java.io.File;
import java.net.URL;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mpnguyen
 *
 */
public class CCDAParserUtilTest {
    private final static Logger logger = LoggerFactory.getLogger(CCDAParserUtilTest.class);
    private static InputStream xmlInputStream;
    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        URL url = CCDAParserTest.class.getClassLoader().getResource("RXDrug.xml");
        final String htmlSourcePath = url.getPath();
        logger.debug("File exist {}", htmlSourcePath);
        File ccDASourceFile = new File(url.getPath());
        Assert.assertTrue("File should exist before running", ccDASourceFile.exists());
        xmlInputStream = FileUtils.openInputStream(ccDASourceFile);
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        if (xmlInputStream != null) {
            logger.debug("Preparing closing the input Stream");
            xmlInputStream.close();
        }
    }

    /**
     * Test method for {@link gov.hhs.fha.nhinc.pmp.services.CCDAParserUtil#convertXMLToCCDA(java.io.InputStream, java.lang.Class)}.
     */
    @Test
    public final void testConvertXMLToCCDA() {
        Rxclassdata drugInfor = CCDAParserUtil.convertXMLToCCDA(xmlInputStream, Rxclassdata.class);
        Assert.assertNotNull(drugInfor);
        Assert.assertEquals("OPIOID ANALGESICS", drugInfor.getRxclassDrugInfoList().getRxclassDrugInfo().get(0)
                .getRxclassMinConceptItem().getClassName());
    }

}
