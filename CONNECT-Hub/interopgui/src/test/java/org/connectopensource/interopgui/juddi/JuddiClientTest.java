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
package org.connectopensource.interopgui.juddi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.connectopensource.test.IntegrationTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.uddi.api_v3.BusinessDetail;
import org.uddi.api_v3.BusinessEntity;
import org.uddi.api_v3.BusinessList;
import org.uddi.api_v3.FindBusiness;
import org.uddi.api_v3.IdentifierBag;
import org.uddi.api_v3.KeyedReference;
import org.uddi.api_v3.Name;
import org.uddi.api_v3.SaveBusiness;

/**
 * @author bhumphrey
 * 
 */
@Category(IntegrationTest.class)
public class JuddiClientTest {


    JuddiClient client;
    
    @Before
    public void setup() {
        client = new JuddiClient();
    }
    
    @Test
    public void constructor() {
        assertNotNull(client.juddiApi);
        assertNotNull(client.inquiry);
    }
    
    
    @Test
    public void publish() {
        
        BusinessEntity businessEntity = new BusinessEntity();
        
        
        businessEntity.setBusinessKey("uddi:nhincnode:Test");
        
        Name organizationName = new Name();
        organizationName.setValue("Test");
        businessEntity.getName().add(organizationName);

              IdentifierBag identifierBag = new IdentifierBag();
        KeyedReference homeCommunityIdentifier = new KeyedReference();
        homeCommunityIdentifier.setTModelKey("uddi:nhin:nhie:homecommunityid");
        homeCommunityIdentifier.setKeyValue("Test");

        identifierBag.getKeyedReference().add(homeCommunityIdentifier);
        businessEntity.setIdentifierBag(identifierBag);

        SaveBusiness sb = new SaveBusiness();
        sb.getBusinessEntity().add(businessEntity);
        
        client.saveBusinessDetail(sb);
        
    }

    
    @Test
    public void search() {
        FindBusiness fb = new FindBusiness();
        KeyedReference hcid = new KeyedReference();
        hcid.setTModelKey("uddi:nhin:nhie:homecommunityid");
        hcid.setKeyValue("Test");
        fb.setIdentifierBag(new IdentifierBag());
        fb.getIdentifierBag().getKeyedReference().add(hcid);
        BusinessList results = client.findBusiness(fb);
        assertNotNull(results.getBusinessInfos());
        assertFalse(results.getBusinessInfos().getBusinessInfo().isEmpty());
        
        
        BusinessDetail bd = client.getBusinessDetail(results);
        assertNotNull(bd);
        
        assertEquals("Test", bd.getBusinessEntity().get(0).getName().get(0).getValue());
        
    }
    

}
