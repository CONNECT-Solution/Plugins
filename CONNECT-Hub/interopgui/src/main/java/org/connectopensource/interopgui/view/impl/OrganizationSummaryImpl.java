/**
 * 
 */
package org.connectopensource.interopgui.view.impl;

import org.apache.commons.lang.StringUtils;
import org.connectopensource.interopgui.view.OrganizationSummary;

/**
 * @author msw
 *
 */
public class OrganizationSummaryImpl implements OrganizationSummary {
    
    private Long id = 0L;
    private String hcid = StringUtils.EMPTY;
    private String organizationName = StringUtils.EMPTY;
    private boolean hasSignedCert = false;
    private String countExchangeEndpoints = StringUtils.EMPTY;
    private String countDirectEndpoints = StringUtils.EMPTY;
    private String countPatients = StringUtils.EMPTY;
    private String countDocuments = StringUtils.EMPTY;
    
    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }
    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }
    /**
     * @return the hcid
     */
    public String getHcid() {
        return hcid;
    }
    /**
     * @param hcid the hcid to set
     */
    public void setHcid(String hcid) {
        this.hcid = hcid;
    }
    /**
     * @return the organizationName
     */
    public String getOrganizationName() {
        return organizationName;
    }
    /**
     * @param organizationName the organizationName to set
     */
    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }
    /**
     * @return the hasSignedCert
     */
    public boolean isHasSignedCert() {
        return hasSignedCert;
    }
    /**
     * @param hasSignedCert the hasSignedCert to set
     */
    public void setHasSignedCert(boolean hasSignedCert) {
        this.hasSignedCert = hasSignedCert;
    }
    /**
     * @return the countExchangeEndpoints
     */
    public String getCountExchangeEndpoints() {
        return countExchangeEndpoints;
    }
    /**
     * @param countExchangeEndpoints the countExchangeEndpoints to set
     */
    public void setCountExchangeEndpoints(String countExchangeEndpoints) {
        this.countExchangeEndpoints = countExchangeEndpoints;
    }
    /**
     * @return the countDirectEndpoints
     */
    public String getCountDirectEndpoints() {
        return countDirectEndpoints;
    }
    /**
     * @param countDirectEndpoints the countDirectEndpoints to set
     */
    public void setCountDirectEndpoints(String countDirectEndpoints) {
        this.countDirectEndpoints = countDirectEndpoints;
    }
    /**
     * @return the countPatients
     */
    public String getCountPatients() {
        return countPatients;
    }
    /**
     * @param countPatients the countPatients to set
     */
    public void setCountPatients(String countPatients) {
        this.countPatients = countPatients;
    }
    /**
     * @return the countDocuments
     */
    public String getCountDocuments() {
        return countDocuments;
    }
    /**
     * @param countDocuments the countDocuments to set
     */
    public void setCountDocuments(String countDocuments) {
        this.countDocuments = countDocuments;
    }

}
