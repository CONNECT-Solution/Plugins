package gov.hhs.onc.hpdclient;

import java.io.Serializable;

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
