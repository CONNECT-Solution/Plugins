package gov.hhc.onc.hpdclient.service;

import oasis.names.tc.dsml._2._0.core.BatchResponse;

/**
 *
 * @author tjafri
 */
public interface HPDService {

    public BatchResponse searchQuery(String dn, String filterBy, String filterValue);
}
