/**
 * 
 */
package org.connectopensource.interopgui.view;

import org.connectopensource.interopgui.dataobject.OrganizationInfo;

/**
 * @author msw
 *
 */
public interface DirectEndpoint {
    
    public Long getId();
    public void setId(Long id);
    
    public String getEndpoint();
    public void setEndpoint(String endpoint);
    
    public boolean getDnsAddressBound();
    public void setDnsAddressBound(boolean dnsAddressBound);
    
    public boolean getDnsDomainBound();
    public void setDnsDomainBound(boolean dnsDomainBound);
    
    public boolean getLdapAddressBound();
    public void setLdapAddressBound(boolean ldapAddressBound);
    
    public boolean getLdapDomainBound();
    public void setLdapDomainBound(boolean ldapDomainBound);
    
    public OrganizationInfo getOrganizationInfo();
    public void setOrganizationInfo(OrganizationInfo orgInfo);

}
