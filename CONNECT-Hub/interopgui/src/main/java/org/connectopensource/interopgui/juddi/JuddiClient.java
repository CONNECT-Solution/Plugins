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

import java.rmi.RemoteException;

import org.apache.juddi.v3.client.ClassUtil;
import org.apache.juddi.v3.client.config.UDDIClientContainer;
import org.apache.juddi.v3.client.transport.Transport;
import org.apache.juddi.v3_service.JUDDIApiPortType;
import org.uddi.api_v3.AuthToken;
import org.uddi.api_v3.BusinessDetail;
import org.uddi.api_v3.BusinessInfo;
import org.uddi.api_v3.BusinessInfos;
import org.uddi.api_v3.BusinessList;
import org.uddi.api_v3.BusinessService;
import org.uddi.api_v3.FindBusiness;
import org.uddi.api_v3.GetAuthToken;
import org.uddi.api_v3.GetBusinessDetail;
import org.uddi.api_v3.SaveBusiness;
import org.uddi.api_v3.SaveService;
import org.uddi.api_v3.ServiceDetail;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;

/**
 * @author bhumphrey
 * 
 */
public class JuddiClient implements UddiClient {

    static UDDISecurityPortType security;
    static JUDDIApiPortType juddiApi;
    static UDDIPublicationPortType publish;
    static UDDIInquiryPortType inquiry;

    GetAuthToken getAuthTokenMyPub;

    static {
        try {

            String clazz = UDDIClientContainer.getUDDIClerkManager(null).getClientConfig().getUDDINode("default")
                    .getProxyTransport();
            Class<?> transportClass = ClassUtil.forName(clazz, Transport.class);
            if (transportClass != null) {
                Transport transport = (Transport) transportClass.getConstructor(String.class).newInstance("default");

                security = transport.getUDDISecurityService();
                juddiApi = transport.getJUDDIApiService();
                publish = transport.getUDDIPublishService();
                inquiry = transport.getUDDIInquiryService();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JuddiClient() {
        getAuthTokenMyPub = new GetAuthToken();
        getAuthTokenMyPub.setUserID("hub");
        getAuthTokenMyPub.setCred("");
    }

    /**
     * @param myService
     * @return
     */
    public ServiceDetail saveService(BusinessService myService) {
        AuthToken myPubAuthToken;
        ServiceDetail sd = null;
        try {
            myPubAuthToken = security.getAuthToken(getAuthTokenMyPub);

            SaveService ss = new SaveService();
            ss.getBusinessService().add(myService);
            ss.setAuthInfo(myPubAuthToken.getAuthInfo());

            sd = publish.saveService(ss);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return sd;
    }

    /**
     * @param sb
     */
    public BusinessDetail saveBusinessDetail(SaveBusiness sb) {
        AuthToken myPubAuthToken;
        BusinessDetail bd = null;
        try {
            myPubAuthToken = security.getAuthToken(getAuthTokenMyPub);
            sb.setAuthInfo(myPubAuthToken.getAuthInfo());
            bd = publish.saveBusiness(sb);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bd;
    }

  

    /**
     * @return BusinessList
     */
    public BusinessList findBusiness(FindBusiness fb) {
        BusinessList businessList = null;
        try {
            businessList = inquiry.findBusiness(fb);
            
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return businessList;
    }
    
    
    /**
     * 
     * @param BusinessDetail
     * @return
     */
    public BusinessDetail getBusinessDetail(BusinessList bl) {
        BusinessDetail bd = null;
        try {
            bd = inquiry.getBusinessDetail(createSearchParamsFromBusinessKeys(bl.getBusinessInfos()));
        } catch ( RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bd;
    }
    
    private GetBusinessDetail createSearchParamsFromBusinessKeys(BusinessInfos businessInfos) {
        GetBusinessDetail searchParams = new GetBusinessDetail();
        for (BusinessInfo businessInfo : businessInfos.getBusinessInfo()) {
            if ((businessInfo.getBusinessKey() != null) && (businessInfo.getBusinessKey().length() > 0)) {
                searchParams.getBusinessKey().add(businessInfo.getBusinessKey());
            }
        }

        return searchParams;
    }
}
