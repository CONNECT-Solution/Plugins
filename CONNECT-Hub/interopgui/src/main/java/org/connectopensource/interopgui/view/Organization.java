/**
 * 
 */
package org.connectopensource.interopgui.view;

import java.util.List;

import org.connectopensource.interopgui.dataobject.DocumentInfo;
import org.connectopensource.interopgui.dataobject.PatientInfo;

/**
 * @author msw
 *
 */
public interface Organization {

    /**
     * @return endpoints
     */
    public List<Endpoint> getEndpoints();

    /**
     * @return
     */
    public List<PatientInfo> getPatients();

    /**
     * @return
     */
    public List<DocumentInfo> getDocuments();

    /**
     * @return
     */
    public Certificate getCertificate();
    
    /**
     * @param endpoints to set
     */
    public void setEndPoints(List<Endpoint> endpoints);

    /**
     * @return
     */
    public void setCertificate(Certificate cert);

    /**
     * @return
     */
    public void setDocuments(List<DocumentInfo> doc);

    /**
     * @return
     */
    public void setPatients(List<PatientInfo> patient);

    /**
     * @param homeCommunityId
     */
    public String getHCID();
    
    /**
     * @param homeCommunityId
     */
    public void setHCID(String homeCommunityId);

    /**
     * @param orgName
     */
    public String getOrgName();
    
    /**
     * @param orgName
     */
    public void setOrgName(String orgName);
    
    /**
     * @param orgName
     */
    public String getOrgId();
    
    /**
     * @param orgName
     */
    public void setOrgId(String orgId);

    /**
     * @return
     */
    public List<DirectEndpoint> getDirectEndpoints();

    /**
     * @return
     */
    public void setDirectEndpoints(List<DirectEndpoint> directEndpoints);

    /**
     * @return
     */
    public DirectCertificate getDirectCertificate();
    
    /**
     * @param directCert
     */
    public void setDirectCertificate(DirectCertificate directCert);
}
