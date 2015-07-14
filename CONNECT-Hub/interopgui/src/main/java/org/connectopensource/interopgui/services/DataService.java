/**
 * 
 */
package org.connectopensource.interopgui.services;

import java.util.List;

import org.connectopensource.interopgui.dataobject.DocumentInfo;
import org.connectopensource.interopgui.dataobject.OrganizationInfo;
import org.connectopensource.interopgui.dataobject.PatientInfo;
import org.connectopensource.interopgui.view.impl.DirectEndpointImpl;
import org.connectopensource.interopgui.view.impl.EndpointImpl;

/**
 * @author msw
 *
 */
public interface DataService {
    
    /**
     * @param org
     * @return the id of the newly saved record.
     */
    public Long saveData(OrganizationInfo org);
    
    /**
     * @return list of all Organization Info
     */
    public List<OrganizationInfo> getData();
    
    /**
     * @param homeCommunityId
     * @return list of organization info matching a home community.
     */
    public List<OrganizationInfo> getDataByHCID(String homeCommunityId);
    
    /**
     * @param Id
     * @return list of organization info matching an id.
     */
    public OrganizationInfo getData(String id);
    
    /**
     * @param patient patient info
     * @param orgId organization id
     * @return the id of the newly saved record.
     */
    public PatientInfo addPatient(PatientInfo patient, String orgId);

    /**
     * @param document document info
     * @param orgId organization id
     * @return the id of the newly saved record.
     */
    public DocumentInfo addDocument(DocumentInfo document, String orgId);

    /**
     * @param directEndpoint
     * @param orgId
     */
    public DirectEndpointImpl addDirectEndpoint(DirectEndpointImpl directEndpoint, String orgId);

    /**
     * @param endpoint implementation
     * @param orgId org id
     * @return endpoint impl
     */
    public EndpointImpl addEndpoint(EndpointImpl endpoint, String orgId);    
    
}
