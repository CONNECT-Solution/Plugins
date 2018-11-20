/**
 * Copyright 2018 Michigan Health Information Network.  All rights reserved.
 * MiHIN Confidential  Proprietary  Restricted
 */
package gov.hhs.fha.nhinc.pdmp.util;

import static org.junit.Assert.fail;

import gov.hhs.fha.nhinc.pdmp.model.PrescriptionInfo;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author mpnguyen
 *
 */
public class HtmlParserUtilTest {
    @Test
    public void testGetAllPrescriptions() {
        String sampleHtml = "PMP_Gateway_Data_Report.html";
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            URL url = classLoader.getResource(sampleHtml);
            List<PrescriptionInfo> drugList = HtmlParserUtil.getAllPrescriptionsFromFile(url.getFile());
            Assert.assertNotNull(drugList);
            Assert.assertEquals(drugList.size(), 26);
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

}
