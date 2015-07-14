/**
 * 
 */
package org.connectopensource.interopgui.controller;

import java.util.ArrayList;
import java.util.List;

import org.connectopensource.interopgui.dataobject.OrganizationInfo;
import org.connectopensource.interopgui.services.DataService;
import org.connectopensource.interopgui.services.JpaDataService;
import org.connectopensource.interopgui.view.OrganizationSummary;
import org.connectopensource.interopgui.view.impl.OrganizationSummaryImpl;

/**
 * @author msw
 * 
 */
public class ListController {

    /**
     * @return
     */
    public List<OrganizationSummary> getSummaries() {
        return processInformationForDisplay();
    }

    private List<OrganizationSummary> processInformationForDisplay() {

        return getOrganizationSummaries();
    }

    private List<OrganizationSummary> getOrganizationSummaries() {

        DataService service = new JpaDataService();
        List<OrganizationInfo> orgs = service.getData();
        List<OrganizationSummary> summaries = new ArrayList<OrganizationSummary>(orgs.size());

        for (OrganizationInfo orgInfo : orgs) {
            OrganizationSummary summary = new OrganizationSummaryImpl();
            summary.setCountDirectEndpoints(String.valueOf(orgInfo.getDirectEndpoints().size()));
            summary.setCountDocuments(String.valueOf(orgInfo.getDocuments().size()));
            summary.setCountPatients(String.valueOf(orgInfo.getPatients().size()));
            summary.setHcid(orgInfo.getHomeCommunityId());
            summary.setId(orgInfo.getId());
            summary.setOrganizationName(orgInfo.getOrgName());
            summaries.add(summary);
        }

        return summaries;
    }

}
