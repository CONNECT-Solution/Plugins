/**
 * 
 */
package org.connectopensource.interopgui.view.impl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.connectopensource.interopgui.dataobject.OrganizationInfo;
import org.connectopensource.interopgui.view.DirectEndpoint;

/**
 * @author msw
 * 
 */
@Entity
@Table(name = "directEndpoint")
public class DirectEndpointImpl implements DirectEndpoint {

    private String endpoint = null;
    private Long id;
    private boolean dnsDomainBound = false;
    private boolean dnsAddressBound = false;
    private boolean ldapDomainBound = false;
    private boolean ldapAddressBound = false;
    private OrganizationInfo orgInfo = null;

    /*
     * (non-Javadoc)
     * 
     * @see org.connectopensource.interopgui.view.DirectEndpoint#getEndpoint()
     */
    @Column(name = "endpoint")
    public String getEndpoint() {
        return endpoint;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.connectopensource.interopgui.view.DirectEndpoint#setEndpoint(java.lang.String)
     */
    @Override
    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.connectopensource.interopgui.view.DirectEndpoint#getDnsAddressBound()
     */
    @Column(name = "dnsAddressBound")
    public boolean getDnsAddressBound() {
        return dnsAddressBound;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.connectopensource.interopgui.view.DirectEndpoint#setDnsAddressBound(boolean)
     */
    @Override
    public void setDnsAddressBound(boolean dnsAddressBound) {
        this.dnsAddressBound = dnsAddressBound;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.connectopensource.interopgui.view.DirectEndpoint#getDnsDomainBound()
     */
    @Column(name = "dnsDomainBound")
    public boolean getDnsDomainBound() {
        return dnsDomainBound;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.connectopensource.interopgui.view.DirectEndpoint#setDnsDomainBound(boolean)
     */
    @Override
    public void setDnsDomainBound(boolean dnsDomainBound) {
        this.dnsDomainBound = dnsDomainBound;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.connectopensource.interopgui.view.DirectEndpoint#getLdapAddressBound()
     */
    @Column(name = "ldapAddressBound")
    public boolean getLdapAddressBound() {
        return ldapAddressBound;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.connectopensource.interopgui.view.DirectEndpoint#setLdapAddressBound(boolean)
     */
    @Override
    public void setLdapAddressBound(boolean ldapAddressBound) {
        this.ldapAddressBound = ldapAddressBound;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.connectopensource.interopgui.view.DirectEndpoint#getLdapDomainBound()
     */
    @Column(name = "ldapDomainBound")
    public boolean getLdapDomainBound() {
        return ldapDomainBound;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.connectopensource.interopgui.view.DirectEndpoint#setLdapDomainBound(boolean)
     */
    @Override
    public void setLdapDomainBound(boolean ldapDomainBound) {
        this.ldapDomainBound = ldapDomainBound;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.connectopensource.interopgui.view.DirectEndpoint#getId()
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.connectopensource.interopgui.view.DirectEndpoint#setId(java.lang.String)
     */
    @Override
    public void setId(Long id) {
        this.id = id;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.connectopensource.interopgui.view.DirectEndpoint#getOrganizationInfo()
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "orginfo_id", nullable = false)
    public OrganizationInfo getOrganizationInfo() {
        return orgInfo;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.connectopensource.interopgui.view.DirectEndpoint#setOrganizationInfo(org.connectopensource.interopgui.dataobject
     * .OrganizationInfo)
     */
    @Override
    public void setOrganizationInfo(OrganizationInfo orgInfo) {
        this.orgInfo = orgInfo;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "DirectEndpointImpl [endpoint=" + endpoint + ", id=" + id + ", dnsDomainBound=" + dnsDomainBound
                + ", dnsAddressBound=" + dnsAddressBound + ", ldapDomainBound=" + ldapDomainBound
                + ", ldapAddressBound=" + ldapAddressBound + ", orgInfo=" + orgInfo.getId() + "]";
    }
}
