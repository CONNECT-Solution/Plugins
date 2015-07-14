/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.hhs.fha.nhinc.loadtest;

import java.util.List;

/**
 *
 * @author mweaver
 */
public class DocumentMetaData implements ILoadTestData {

    private String patientId = null;
    private List<String> documentIds = null;

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public List<String> getDocumentIds() {
        return documentIds;
    }
}
