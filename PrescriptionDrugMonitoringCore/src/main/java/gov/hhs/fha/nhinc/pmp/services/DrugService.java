/**
 *
 */
package gov.hhs.fha.nhinc.pmp.services;

import gov.hhs.fha.nhinc.pmp.dto.Rxclassdata;
import gov.hhs.fha.nhinc.pmp.dto.Rxclassdata.RxclassDrugInfoList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import javax.ws.rs.client.AsyncInvoker;
import javax.ws.rs.core.MediaType;
import org.apache.cxf.jaxrs.client.WebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mpnguyen
 *
 */
public class DrugService {
    private final static Logger logger = LoggerFactory.getLogger(DrugService.class);

    /**
     * @param drugName
     * @return
     */
    public boolean isOpioidDrug(String drugName) {

        logger.debug("Trying to retrieve drugclassName {}", drugName);
        try {

            WebClient restClient = WebClient.create(serviceUrl + drugName)
                    .accept(
                            MediaType.APPLICATION_XML);
            Rxclassdata response = restClient.get(Rxclassdata.class);
            String className = getDrugClassName(response);
            logger.debug("Input Drug {} ,ClassName: {}", drugName, className);
            return className.contains("OPIOID");
        } catch (Exception ex) {
            logger.error(ex.getLocalizedMessage(), ex);
            return false;
        }

    }


    private static final String serviceUrl = "https://rxnav.nlm.nih.gov/REST/rxclass/class/byDrugName.xml?drugName=";

    /**
     * @param drugList
     * @return
     */
    public Map<String, Boolean> getOpioidDrugStatus(List<String> drugList) {
        Map<String, Boolean> drugStatusMap = new HashMap<String, Boolean>();
        Map<String, Future<Rxclassdata>> serviceCallHistory = new HashMap<>();
        for (String drug : drugList) {
            StringBuilder strBuildEndpoint = new StringBuilder();
            strBuildEndpoint.append(serviceUrl);
            strBuildEndpoint.append(drug);
            logger.debug("Calling {}", strBuildEndpoint.toString());
            AsyncInvoker restClientInvoker = WebClient.create(strBuildEndpoint.toString())
                    .accept(MediaType.APPLICATION_XML)
                    .async();
            Future<Rxclassdata> responseFuture = restClientInvoker.get(Rxclassdata.class);
            serviceCallHistory.put(drug, responseFuture);
        }
        // retrieve result now

        for (Entry<String, Future<Rxclassdata>> cursor : serviceCallHistory.entrySet()) {
            //Try to retrieve result in 2 min. otherwise, it will throw exception.
            try{
                Rxclassdata drugResult = cursor.getValue().get(2, TimeUnit.MINUTES);
                String className = getDrugClassName(drugResult);
                logger.debug("Drug Name: {}, class {}", drugResult.getUserInput().getDrugName(), className);
                drugStatusMap.put(drugResult.getUserInput().getDrugName(), className.contains("OPIOID"));
            }catch(Exception ex){
                logger.error("Unable to retrieve result for {} due to {}",cursor.getKey(),ex);
            }
        }
        return drugStatusMap;
    }

    private String getDrugClassName(final Rxclassdata drugInfor) {
        String className = ""; // default value;
        // Retrieve first in the list
        RxclassDrugInfoList rxclassDrugInfoList = drugInfor.getRxclassDrugInfoList();
        if (rxclassDrugInfoList != null
                && rxclassDrugInfoList.getRxclassDrugInfo().get(0).getRxclassMinConceptItem() != null) {
            className = drugInfor.getRxclassDrugInfoList().getRxclassDrugInfo().get(0).getRxclassMinConceptItem()
                    .getClassName();
        }

        return className.toUpperCase();
    }
}


