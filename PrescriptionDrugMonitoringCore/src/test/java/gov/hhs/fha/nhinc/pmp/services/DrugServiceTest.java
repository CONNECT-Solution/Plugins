/**
 *
 */
package gov.hhs.fha.nhinc.pmp.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author mpnguyen
 *
 */
public class DrugServiceTest {



    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    @Test
    public final void testVerifyOpioidDrug() {
        String drugName = "ENDOCET";
        DrugService drugService = new DrugService();
        Assert.assertTrue("Endocet should be belong to OPIOID drug", drugService.isOpioidDrug(drugName));
    }

    @Test
    public final void testVerifyListOpioidDrug() {
        List<String> drugList = new ArrayList<String>();
        drugList.add("ENDOCET");
        drugList.add("DummyDrug");
        drugList.add("DummyDrug333");
        DrugService drugService = new DrugService();
        Map<String, Boolean> drugStatusMap = drugService.getOpioidDrugStatus(drugList);
        Assert.assertTrue("Endocet should be belong to OPIOID drug", drugStatusMap.get("ENDOCET"));
        Assert.assertFalse("Endocet should not be belong to OPIOID drug", drugStatusMap.get("DummyDrug"));
        Assert.assertFalse("Not OPIOID drug", drugStatusMap.get("DummyDrug333"));
    }

    @Test
    public final void testVerifyNONOpioidDrug() {
        String drugName = "TestDrug";
        DrugService drugService = new DrugService();
        Assert.assertFalse("Endocet should not belong to OPIOID drug", drugService.isOpioidDrug(drugName));
    }

}
