/**
 * 
 */
package org.connectopensource.interopgui.view;

/**
 * @author msw
 *
 */
public interface OrganizationSummary {
    
    public Long getId();
    public void setId(Long id);

    public String getHcid();
    public void setHcid(String hcid);
    
    public String getOrganizationName();
    public void setOrganizationName(String organizationName);

    public boolean isHasSignedCert();
    public void setHasSignedCert(boolean hasSignedCert);

    public String getCountExchangeEndpoints();
    public void setCountExchangeEndpoints(String countExchangeEndpoints);
    
    public String getCountDirectEndpoints();
    public void setCountDirectEndpoints(String countDirectEndpoints);
    
    public String getCountPatients();
    public void setCountPatients(String countPatients);
    
    public String getCountDocuments();
    public void setCountDocuments(String countDocuments);

}
