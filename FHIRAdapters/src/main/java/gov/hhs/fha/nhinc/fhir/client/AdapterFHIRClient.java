/*
 * Copyright (c) 2009-2015, United States Government, as represented by the Secretary of Health and Human Services.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above
 *       copyright notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the documentation
 *       and/or other materials provided with the distribution.
 *     * Neither the name of the United States Government nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE UNITED STATES GOVERNMENT BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package gov.hhs.fha.nhinc.fhir.client;

import gov.hhs.fha.nhinc.connectmgr.ConnectionManagerException;
import gov.hhs.fha.nhinc.webserviceproxy.WebServiceProxyHelper;
import java.net.URISyntaxException;
import java.util.Map;
import org.hl7.fhir.client.FHIRSimpleClient;
import org.hl7.fhir.instance.model.AtomEntry;
import org.hl7.fhir.instance.model.AtomFeed;

/**
 *
 * @author jassmit
 */
public class AdapterFHIRClient {
    
    private final WebServiceProxyHelper proxyHelper;
    private final FHIRSimpleClient fhirClient = new FHIRSimpleClient();
    
    public AdapterFHIRClient() {
        proxyHelper = new WebServiceProxyHelper();
    }
    
    public AdapterFHIRClient(WebServiceProxyHelper proxyHelper) {
        this.proxyHelper = proxyHelper;
    }
    
    public AtomFeed searchFhirResource(String serviceName, Map<String,String> params, Class resourceType) throws URISyntaxException, ConnectionManagerException {
        String url = getAdapterUrl(serviceName);
        fhirClient.initialize(url);
        
        return fhirClient.search(resourceType, params);
    }
    
    public AtomEntry readFhirResource(String serviceName, Class resourceType, String id) throws ConnectionManagerException, URISyntaxException {
        String url = getAdapterUrl(serviceName);
        fhirClient.initialize(url);
        
        return fhirClient.read(resourceType, id);
    }
    
    public byte[] readBinaryResource(String serviceName, Class resourceType, String id) throws ConnectionManagerException, URISyntaxException {
        String url = getAdapterUrl(serviceName);
        fhirClient.initialize(url);
        
        return fhirClient.readBinary(resourceType, id);
    }

    public String getAdapterUrl(String serviceName) throws ConnectionManagerException {
        return proxyHelper.getAdapterEndPointFromConnectionManager(serviceName);
    }
}
