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
package gov.hhs.fha.nhinc.pdmp.services;

import gov.hhs.fha.nhinc.pdmp.util.PropertyAccessorUtil;
import gov.hhs.fha.nhinc.rx.Rxclassdata;
import gov.hhs.fha.nhinc.rx.Rxclassdata.RxclassDrugInfoList;
import javax.ws.rs.core.MediaType;
import org.apache.cxf.jaxrs.client.WebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jassmit
 */
public class PrescriptionClassSearchImpl implements PrescriptionClassSearch {

    private final static Logger LOGGER = LoggerFactory.getLogger(PrescriptionClassSearch.class);
    //TODO Change Service URL to property
    private static final String SERVICE_URL_KEY = "drugNameServiceUrl";

    @Override
    public String searchForDrugClass(String drugName) {
        WebClient restClient = WebClient.create(getServiceUrl() + drugName)
                .accept(MediaType.APPLICATION_XML);
        Rxclassdata response = restClient.get(Rxclassdata.class);

        String className = getDrugClassName(response);
        LOGGER.debug("Input Drug {} ,ClassName: {}", drugName, className);

        return className;
    }

    private String getDrugClassName(final Rxclassdata drugInfor) {
        String className = ""; // default value;
        // Retrieve first in the list
        RxclassDrugInfoList rxclassDrugInfoList = drugInfor.getRxclassDrugInfoList();
        if (rxclassDrugInfoList != null
                && rxclassDrugInfoList.getRxclassDrugInfo().get(0).getRxclassMinConceptItem() != null) {
            className = drugInfor.getRxclassDrugInfoList().getRxclassDrugInfo().get(0).getRxclassMinConceptItem()
                    .getClassName();
        }

        return className;
    }
    
    private String getServiceUrl() {
        return PropertyAccessorUtil.getInstance().getProperty(SERVICE_URL_KEY);
    }

}
