/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013, United States Government, as represented by the Secretary of Health and Human Services.
 * All rights reserved.
 *  
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *   * Redistributions of source code must retain the above
 *     copyright notice, this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *   * Neither the name of the United States Government nor the
 *     names of its contributors may be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *DISCLAIMED. IN NO EVENT SHALL THE UNITED STATES GOVERNMENT BE LIABLE FOR ANY
 *DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.connectopensource.interopgui.services;

import java.util.ArrayList;
import java.util.List;

import org.connectopensource.interopgui.juddi.JuddiClient;
import org.connectopensource.interopgui.juddi.UddiClient;
import org.connectopensource.interopgui.view.Endpoint;
import org.connectopensource.interopgui.view.Organization;
import org.connectopensource.interopgui.view.impl.EndpointImpl;
import org.uddi.api_v3.AccessPoint;
import org.uddi.api_v3.BindingTemplate;
import org.uddi.api_v3.BindingTemplates;
import org.uddi.api_v3.BusinessDetail;
import org.uddi.api_v3.BusinessEntity;
import org.uddi.api_v3.BusinessList;
import org.uddi.api_v3.BusinessService;
import org.uddi.api_v3.CategoryBag;
import org.uddi.api_v3.FindBusiness;
import org.uddi.api_v3.IdentifierBag;
import org.uddi.api_v3.KeyedReference;
import org.uddi.api_v3.Name;
import org.uddi.api_v3.SaveBusiness;

/**
 * @author bhumphrey
 * 
 */
public class UddiEndpointService implements EndpointService {

    private static final String UDDI_NHIN_NHIE_HOMECOMMUNITYID_TMODEL = "uddi:nhin:nhie:homecommunityid";

    private static final String UDDI_NHIN_VERSIONOFSERVICE = "uddi:nhin:versionofservice";
   
    private static final String UDDI_NHIN_STANDARD_SERVICENAMES = "uddi:nhin:standard-servicenames";
    private UddiClient uddiClient;
    private EndpointHelper helper;

    public UddiEndpointService() {
        this(new JuddiClient());
    }

    UddiEndpointService(UddiClient publisher) {
        this.uddiClient = publisher;
        this.helper = new EndpointHelper();
    }

    @Override
    public void saveEndpoint(Organization organization, Endpoint endpoint) {
        publishBusinessService(organization, endpoint);

    }

    @Override
    public void saveEndpoints(Organization organization) {
        publishBusinessEntity(organization);
        
        for (Endpoint endpoint : organization.getEndpoints()) {
            saveEndpoint(organization, endpoint);
        }

    }

    @Override
    public List<Endpoint> getEndpoints(String homeCommunityId) {
        List<Endpoint> endpoints = new ArrayList<Endpoint>();
        FindBusiness fb = new FindBusiness();
        fb.setMaxRows(100);
        KeyedReference hcidReference = new KeyedReference();
        hcidReference.setKeyValue(homeCommunityId);
        hcidReference.setTModelKey(UDDI_NHIN_NHIE_HOMECOMMUNITYID_TMODEL);
        IdentifierBag identifierBag = new IdentifierBag();
        fb.setIdentifierBag(identifierBag);
        identifierBag.getKeyedReference().add(hcidReference);
        BusinessList bl = uddiClient.findBusiness(fb);

        BusinessDetail bd = uddiClient.getBusinessDetail(bl);

        for (BusinessEntity be : bd.getBusinessEntity()) {
            for (BusinessService bs : be.getBusinessServices().getBusinessService()) {
                Endpoint.Specification specification = null;
                String url = null;
                Endpoint.SpecVersion version = null;
                for (KeyedReference kr : bs.getCategoryBag().getKeyedReference()) {
                    if (UDDI_NHIN_STANDARD_SERVICENAMES.equals(kr.getTModelKey())) {
                        specification = helper.getSpecification(kr.getKeyValue());
                        break;
                    }
                }

                for (BindingTemplate bt : bs.getBindingTemplates().getBindingTemplate()) {
                    // url
                    url = bt.getAccessPoint().getValue();
                     
                    // version
                    for(KeyedReference kr : bt.getCategoryBag().getKeyedReference()) {
                        if(UDDI_NHIN_VERSIONOFSERVICE.equals(kr.getTModelKey())) {
                            version = helper.getSpecVersion(specification, kr.getKeyValue());
                            endpoints.add(new EndpointImpl(specification, version, url));
                            
                        }
                    }
                }
            }
        }

        return endpoints;
    }

    public void publishBusinessService(Organization organization, Endpoint endpoint) {
        BusinessService myService = new BusinessService();
        myService.setBusinessKey(getBusinessKey(organization, endpoint));

        CategoryBag categoryBag = new CategoryBag();
        KeyedReference serviceName = new KeyedReference();
        serviceName.setKeyName(helper.getSpecName(endpoint.getSpecification()));
        serviceName.setTModelKey(UDDI_NHIN_STANDARD_SERVICENAMES);
        categoryBag.getKeyedReference().add(serviceName);
        myService.setCategoryBag(categoryBag);

        Name name = new Name();
        name.setValue(helper.getSpecName(endpoint.getSpecification()));
        myService.getName().add(name);
        BindingTemplates bindingTemplates = createBindingTemplates(endpoint);
        myService.setBindingTemplates(bindingTemplates);

        uddiClient.saveService(myService);

    }

    /**
     * @param organization
     * @param endpoint
     * @return
     */
    public String getBusinessKey(Organization organization, Endpoint endpoint) {
        return "uddi:" + organization.getOrgName() + ":" + helper.getSpecName(endpoint.getSpecification());
    }

    /**
     * @param endpoint
     * @return
     */
    public BindingTemplates createBindingTemplates(Endpoint endpoint) {
        BindingTemplates bindingTemplates = new BindingTemplates();
        BindingTemplate bindingTemplate = new BindingTemplate();
        AccessPoint accessPoint = new AccessPoint();
        accessPoint.setUseType("endpoint");
        accessPoint.setValue(endpoint.getEndpoint());
        bindingTemplate.setAccessPoint(accessPoint);

        CategoryBag categoryBag = new CategoryBag();
        KeyedReference version = new KeyedReference();
        version.setTModelKey(UDDI_NHIN_VERSIONOFSERVICE);
        version.setKeyValue(helper.getVersion(endpoint.getSpecification(), endpoint.getSpecVersion()));
        categoryBag.getKeyedReference().add(version);
        bindingTemplate.setCategoryBag(categoryBag);
        bindingTemplates.getBindingTemplate().add(bindingTemplate);
        return bindingTemplates;
    }

   
    public void publishBusinessEntity(Organization organization) {

        BusinessEntity businessEntity = new BusinessEntity();
        Name organizationName = new Name();
        organizationName.setValue(organization.getOrgName());
        businessEntity.getName().add(organizationName);

        businessEntity.setBusinessKey(getBusinessKey(organization));

        IdentifierBag identifierBag = new IdentifierBag();
        KeyedReference homeCommunityIdentifier = new KeyedReference();
        homeCommunityIdentifier.setTModelKey(UDDI_NHIN_NHIE_HOMECOMMUNITYID_TMODEL);
        homeCommunityIdentifier.setKeyValue(organization.getHCID());

        identifierBag.getKeyedReference().add(homeCommunityIdentifier);
        businessEntity.setIdentifierBag(identifierBag);

        SaveBusiness sb = new SaveBusiness();
        sb.getBusinessEntity().add(businessEntity);

        uddiClient.saveBusinessDetail(sb);
    }

    
    /**
     * @param organization
     * @return
     */
    public String getBusinessKey(Organization organization) {
        return "uddi:" + organization.getOrgName() + ":" + organization.getHCID();
    }

}
