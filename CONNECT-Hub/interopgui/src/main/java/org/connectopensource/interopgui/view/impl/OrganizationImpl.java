package org.connectopensource.interopgui.view.impl;

import java.util.List;

import org.connectopensource.interopgui.dataobject.DocumentInfo;
import org.connectopensource.interopgui.dataobject.PatientInfo;
import org.connectopensource.interopgui.view.Certificate;
import org.connectopensource.interopgui.view.DirectCertificate;
import org.connectopensource.interopgui.view.DirectEndpoint;
import org.connectopensource.interopgui.view.Endpoint;
import org.connectopensource.interopgui.view.Organization;

/**
 * @author msw
 *
 */
public class OrganizationImpl implements Organization {

    private List<Endpoint> endpoints = null;
    private List<PatientInfo> patients = null;
    private List<DocumentInfo> documents = null;
    private List<DirectEndpoint> directEndpoints = null;
    private Certificate cert = null;
    private DirectCertificate directCert = null;
    private String orgName = null;
    private String hcid = null;
    private String orgId = null;
    
    /* (non-Javadoc)
     * @see org.connectopensource.interopgui.view.Organization#getEndpoints()
     */
    @Override
    public List<Endpoint> getEndpoints() {
        return endpoints;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEndPoints(List<Endpoint> endpoints) {
        this.endpoints = endpoints;
    }

    /* (non-Javadoc)
     * @see org.connectopensource.interopgui.view.Organization#getCertificate()
     */
    @Override
    public Certificate getCertificate() {
        return cert;
    }

    /* (non-Javadoc)
     * @see org.connectopensource.interopgui.view.Organization#setCertificate(org.connectopensource.interopgui.view.Certificate)
     */
    @Override
    public void setCertificate(Certificate cert) {
        this.cert = cert;
    }

    /* (non-Javadoc)
     * @see org.connectopensource.interopgui.view.Organization#getHCID()
     */
    @Override
    public String getHCID() {
        return hcid;
    }

    /* (non-Javadoc)
     * @see org.connectopensource.interopgui.view.Organization#setHCID(java.lang.String)
     */
    @Override
    public void setHCID(String homeCommunityId) {
        this.hcid = homeCommunityId;
    }

    /* (non-Javadoc)
     * @see org.connectopensource.interopgui.view.Organization#getOrgName()
     */
    @Override
    public String getOrgName() {
        return orgName;
    }

    /* (non-Javadoc)
     * @see org.connectopensource.interopgui.view.Organization#setOrgName(java.lang.String)
     */
    @Override
    public void setOrgName(String orgName) {
        this.orgName = orgName;       
    }

    /* (non-Javadoc)
     * @see org.connectopensource.interopgui.view.Organization#getOrgId()
     */
    @Override
    public String getOrgId() {
        return orgId;
    }

    /* (non-Javadoc)
     * @see org.connectopensource.interopgui.view.Organization#setOrgId(java.lang.String)
     */
    @Override
    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<PatientInfo> getPatients() {
        return patients;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<DocumentInfo> getDocuments() {
        return documents;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDocuments(List<DocumentInfo> documents) {
        this.documents = documents;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPatients(List<PatientInfo> patients) {
        this.patients = patients;
    }

    /* (non-Javadoc)
     * @see org.connectopensource.interopgui.view.Organization#getDirectEndpoints()
     */
    @Override
    public List<DirectEndpoint> getDirectEndpoints() {
        return directEndpoints;
    }

    /* (non-Javadoc)
     * @see org.connectopensource.interopgui.view.Organization#setDirectEndpoints(java.util.List)
     */
    @Override
    public void setDirectEndpoints(List<DirectEndpoint> directEndpoints) {
        this.directEndpoints = directEndpoints;
    }

    /* (non-Javadoc)
     * @see org.connectopensource.interopgui.view.Organization#getDirectCertificate()
     */
    @Override
    public DirectCertificate getDirectCertificate() {
        return directCert;
    }

    /* (non-Javadoc)
     * @see org.connectopensource.interopgui.view.Organization#setDirectCertificate(org.connectopensource.interopgui.view.Certificate)
     */
    @Override
    public void setDirectCertificate(DirectCertificate directCert) {
        this.directCert = directCert;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "OrganizationImpl [endpoints=" + endpoints + ", patients=" + patients + ", documents=" + documents
                + ", directEndpoints=" + directEndpoints + ", cert=" + cert + ", directCert=" + directCert
                + ", orgName=" + orgName + ", hcid=" + hcid + ", orgId=" + orgId + "]";
    }

    
    
}
