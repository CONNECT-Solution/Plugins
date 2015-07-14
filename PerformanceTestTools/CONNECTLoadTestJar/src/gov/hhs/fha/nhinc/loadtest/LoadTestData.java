/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.hhs.fha.nhinc.loadtest;

import java.util.HashMap;

/**
 *
 * @author mweaver
 */
public class LoadTestData {

    String environment = null;
    HashMap<String, ILoadTestData> loadTestData = null;

    /*
    HashMap<String, Patient> patientData = null;
    HashMap<String, DocumentMetaData> documentMetaData = null;
    HashMap<String, Document> documentData = null;
    */

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public HashMap<String, ILoadTestData> getLoadTestData() {
        return loadTestData;
    }

    public void setLoadTestData(HashMap<String, ILoadTestData> loadTestData) {
        this.loadTestData = loadTestData;
    }
}
