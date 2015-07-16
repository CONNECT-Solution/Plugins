/**
 * 
 */
package org.connectopensource.interopgui.services;

import java.util.List;

import org.connectopensource.interopgui.view.Endpoint;
import org.connectopensource.interopgui.view.Organization;

/**
 * @author msw
 *
 */
public interface EndpointService {
    
    public void saveEndpoint(Organization organization, Endpoint endpoint);
    public void saveEndpoints(Organization organization);
    public List<Endpoint> getEndpoints(String homeCommunityId);
 
}
