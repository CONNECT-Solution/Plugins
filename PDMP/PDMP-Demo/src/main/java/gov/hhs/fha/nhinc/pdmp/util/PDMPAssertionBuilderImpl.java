/*
 * Copyright (c) 2009-2018, United States Government, as represented by the Secretary of Health and Human Services.
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
package gov.hhs.fha.nhinc.pdmp.util;

import gov.hhs.fha.nhinc.common.nhinccommon.CeType;
import gov.hhs.fha.nhinc.common.nhinccommon.HomeCommunityType;
import gov.hhs.fha.nhinc.messaging.builder.impl.AssertionBuilderImpl;
import gov.hhs.fha.nhinc.nhinclib.NhincConstants;
import gov.hhs.fha.nhinc.properties.PropertyAccessException;
import gov.hhs.fha.nhinc.properties.PropertyAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jassmit
 */
public class PDMPAssertionBuilderImpl extends AssertionBuilderImpl {

    private static final Logger LOG = LoggerFactory.getLogger(PDMPAssertionBuilderImpl.class);

    @Override
    public void build() {
        super.build();
        HomeCommunityType homeCommunity = new HomeCommunityType();
        homeCommunity.setName(userOrganization);
        homeCommunity.setHomeCommunityId(getHomeCommunityId());

        assertionType.setHomeCommunity(homeCommunity);
        assertionType.getUserInfo().setOrg(homeCommunity);

        if (assertionType.getUserInfo().getRoleCoded() == null) {
            CeType userRole = new CeType();
            userRole.setCode(userCode);
            userRole.setCodeSystem(userSystem);
            userRole.setCodeSystemName(userSystemName);
            userRole.setDisplayName(userDisplay);
            assertionType.getUserInfo().setRoleCoded(userRole);
        }
    }

    private String getHomeCommunityId() {
        try {
            PropertyAccessor propertyAccessor = PropertyAccessor.getInstance();
            return propertyAccessor.getProperty(NhincConstants.GATEWAY_PROPERTY_FILE, NhincConstants.HOME_COMMUNITY_ID_PROPERTY);
        } catch (PropertyAccessException ex) {
            LOG.warn("Unable to retrieve value for {)", NhincConstants.HOME_COMMUNITY_ID_PROPERTY, ex);
        }
        return "";
    }
}
