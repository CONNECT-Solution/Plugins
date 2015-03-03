/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.hhs.fha.nhinc.fhir.client;

import gov.hhs.fha.nhinc.connectmgr.ConnectionManagerException;
import gov.hhs.fha.nhinc.webserviceproxy.WebServiceProxyHelper;
import java.net.URISyntaxException;
import java.util.Map;
import org.hl7.fhir.client.FHIRSimpleClient;
import org.hl7.fhir.instance.model.AtomFeed;

/**
 *
 * @author jassmit
 */
public class AdapterFHIRClient {
    
    private WebServiceProxyHelper proxyHelper;
    private FHIRSimpleClient fhirClient = new FHIRSimpleClient();
    
    public AdapterFHIRClient() {
        proxyHelper = new WebServiceProxyHelper();
    }
    
    public AdapterFHIRClient(WebServiceProxyHelper proxyHelper) {
        this.proxyHelper = proxyHelper;
    }
    
    public AtomFeed getFhirResource(String serviceName, Map<String,String> params, Class resourceType) throws URISyntaxException, ConnectionManagerException {
        String url = getAdapterUrl(serviceName);
        fhirClient.initialize(url);
        
        return fhirClient.search(resourceType, params);
    }

    private String getAdapterUrl(String serviceName) throws ConnectionManagerException {
        return proxyHelper.getAdapterEndPointFromConnectionManager(serviceName);
    }
}
