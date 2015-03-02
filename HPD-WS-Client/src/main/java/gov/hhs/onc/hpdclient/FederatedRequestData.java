/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.hhs.onc.hpdclient;

import java.io.Serializable;

/**
 *
 * @author Wilmertech
 */
public class FederatedRequestData implements Serializable {
    protected String federatedRequestId;
    protected String directoryId;

    public String getFederatedRequestId() {
        return federatedRequestId;
    }

    public void setFederatedRequestId(String federatedRequestId) {
        this.federatedRequestId = federatedRequestId;
    }

    public String getDirectoryId() {
        return directoryId;
    }

    public void setDirectoryId(String directoryId) {
        this.directoryId = directoryId;
    }
    
    
}
