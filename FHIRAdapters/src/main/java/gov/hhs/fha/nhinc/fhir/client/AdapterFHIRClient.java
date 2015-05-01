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

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.Bundle;
import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.model.dstu.resource.Binary;
import ca.uhn.fhir.model.dstu.resource.Patient;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.IGenericClient;
import gov.hhs.fha.nhinc.connectmgr.ConnectionManagerException;
import gov.hhs.fha.nhinc.webserviceproxy.WebServiceProxyHelper;
import gov.hhs.fha.nhinc.fhir.util.FHIRConstants;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.hl7.fhir.client.EFhirClientException;
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
    private static Logger LOG = Logger.getLogger(AdapterFHIRClient.class);
    private FhirContext fhirContext = null;

    public AdapterFHIRClient() {
        proxyHelper = new WebServiceProxyHelper();
    }

    public AdapterFHIRClient(WebServiceProxyHelper proxyHelper) {
        this.proxyHelper = proxyHelper;
    }

    public AtomFeed searchFhirResource(String serviceName, Map<String, String> params, Class resourceType) throws URISyntaxException, ConnectionManagerException {
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

    /* Creates a Bianry resource in local store using FHIRRestBinary application
     *
     */
    public MethodOutcome createBinaryDocument(String serviceName, Binary binary) {
        MethodOutcome outcome = null;
        try {
            IGenericClient client = getHAPIClient(getAdapterUrl(serviceName));
            outcome = client.create().resource(binary).execute();
            LOG.info("outcome.getId().getIdPart(): " + outcome.getId().getIdPart());
            LOG.info("outcome.getId().getBaseUrl(): " + outcome.getId().getBaseUrl());
            LOG.info("outcome.getId().getVersionIdPart(): " + outcome.getId().getVersionIdPart());
            if (!outcome.getOperationOutcome().isEmpty()) {
                throw new EFhirClientException("Unable to save the binary resource");
            }
        } catch (ConnectionManagerException anEx) {
            throw new EFhirClientException("Unable to save the binary resource");
        }
        return outcome;
    }

    /*
     * Post a DocumentReference and Binary resource to FHIR server
     */
    public List<IResource> postResources(List<IResource> resources) throws ConnectionManagerException {
        IGenericClient client = getHAPIClient(getAdapterUrl(FHIRConstants.FHIR_DOC_REFERENCE_URL_KEY));
        Bundle b = Bundle.withResources(resources, fhirContext, "");
        Bundle result = client.transaction().withBundle(b).encodedXml().execute();
        LOG.info("result.size: " + result.size());
        List<IResource> resultList = result.toListOfResources();
        for (IResource res : resultList) {
            LOG.info("Resource Name: " + res.getResourceName() + ", Resource IdDt: " + res.getId());
        }
        return resources;
    }

    /*
     * Query FHIR server for patient with given and family name
     */
    public List<IResource> queryPatient(Map<String, String> param) throws ConnectionManagerException {
        IGenericClient client = getHAPIClient(getAdapterUrl(FHIRConstants.FHIR_DOC_REFERENCE_URL_KEY));
        Bundle b = client.search()
            .forResource(Patient.class)
            .encodedJson()
            .where(Patient.GIVEN.matches().value(param.get("given")))
            .and(Patient.FAMILY.matches().value(param.get("family")))
            .execute();
        LOG.info("result.size: " + b.size());
        return b.toListOfResources();
    }
    /*
     * returns servcie url based on serviceName from internalConnectionInfo.xml
     */

    public String getAdapterUrl(String serviceName) throws ConnectionManagerException {
        return proxyHelper.getAdapterEndPointFromConnectionManager(serviceName);
    }

    /* Creates a HAPI FHIR Context
     * The FHIR context is the central starting point for the use of the HAPI FHIR API.
     * FhirContext is an expensive object to create, so you should try to keep an instance around for
     * the lifetime of your application. It is thread-safe so it can be passed as needed.
     */
    private FhirContext getHAPI_FHIRContext() {
        if (fhirContext == null) {
            fhirContext = new FhirContext();
        }
        return fhirContext;
    }

    private IGenericClient getHAPIClient(String serverBase) {
        return getHAPI_FHIRContext().newRestfulGenericClient(serverBase);
    }
}
