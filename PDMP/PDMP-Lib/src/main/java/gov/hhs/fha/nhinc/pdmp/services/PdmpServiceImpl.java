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

import gov.hhs.fha.nhinc.pdmp.model.PdmpPatient;
import gov.hhs.fha.nhinc.pdmp.DateRangeType;
import gov.hhs.fha.nhinc.pdmp.LocationType;
import gov.hhs.fha.nhinc.pdmp.LocationType.Address;
import gov.hhs.fha.nhinc.pdmp.PatientRequestType;
import gov.hhs.fha.nhinc.pdmp.PatientResponseType;
import gov.hhs.fha.nhinc.pdmp.PatientType;
import gov.hhs.fha.nhinc.pdmp.ProviderType;
import gov.hhs.fha.nhinc.pdmp.ReportRequestType;
import gov.hhs.fha.nhinc.pdmp.ReportResponseType;
import gov.hhs.fha.nhinc.pdmp.RequesterType;
import gov.hhs.fha.nhinc.pdmp.RoleType;
import gov.hhs.fha.nhinc.pdmp.SexCodeType;
import gov.hhs.fha.nhinc.pdmp.USStateCodeType;
import gov.hhs.fha.nhinc.pdmp.model.PrescriptionInfo;
import gov.hhs.fha.nhinc.pdmp.util.HtmlParserUtil;
import gov.hhs.fha.nhinc.pdmp.util.PropertyAccessorUtil;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.ws.rs.core.MediaType;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.apache.commons.lang.StringUtils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.common.util.Base64Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation for PDMP Service
 * {@inheritDoc}
 * @author jassmit
 */
public class PdmpServiceImpl implements PdmpService {

    private static final String URL_PROP_NAME = "pdmpUrl";
    private static final String USER_PROP_NAME = "pdmpUser";
    private static final String PASS_PROP_NAME = "pdmpPass";

    gov.hhs.fha.nhinc.pdmp.ObjectFactory of = new gov.hhs.fha.nhinc.pdmp.ObjectFactory();

    private static final Logger LOG = LoggerFactory.getLogger(PdmpServiceImpl.class);

    /**
     * @{inheritDoc}
     */
    @Override
    public PdmpPatient searchForPdmpInfo(PatientType patient, DateRangeType dateRange) {

        PdmpPatient resultPatient = null;

        PatientRequestType request = buildPatientRequest(patient, dateRange);

        String serviceUrl = getUrl();
        String userAndPass = getUserNameAndPassword();

        String authorizationHeader = "Basic " + Base64Utility.encode(userAndPass.getBytes());
        PatientResponseType response = null;

        try {
            response = getWebClient(authorizationHeader, serviceUrl)
                    .post(of.createPatientRequest(request), PatientResponseType.class);
        } catch (Exception ex) {
            LOG.error("Error with PDMP client: {}", ex.getLocalizedMessage(), ex);
        }

        if (allowed(response) && response.getReport() != null) {
            resultPatient = new PdmpPatient();
            if (response.getReport().getReportRequestURLs() != null && response.getReport().getReportRequestURLs().getViewableReport() != null) {
                ReportRequestType reportRequest = buildReportRequest(response.getReport().getReportRequestURLs().getViewableReport().getValue());
                ReportResponseType reportResponse
                        = getWebClient(authorizationHeader, response.getReport().getReportRequestURLs().getViewableReport().getValue())
                                .post(of.createReportRequest(reportRequest), ReportResponseType.class);
                if (reportResponse != null && !StringUtils.isBlank(reportResponse.getReportLink())) {
                    resultPatient.setReportUrl(reportResponse.getReportLink());
                }
            }
        }

        return resultPatient;
    }
    
    /**
     * @{inheritDoc}
     */
    @Override
    public List<PrescriptionInfo> getAllPrescriptions(PdmpPatient patient) throws IOException {
        return HtmlParserUtil.getAllPrescriptions(patient.getReportUrl());
    }

    /**
     * @{inheritDoc}
     */
    @Override
    public XMLGregorianCalendar getGregorianCalendar(Date date) {
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(df.format(date));
        } catch (DatatypeConfigurationException ex) {
            LOG.warn("Unable to convert Date to XMLGregorianCalendar.", ex);
        }
        return null;
    }
    
    /**
     * @{inheritDoc}
     */
    @Override
    public SexCodeType getSexCodeType(String gender) {
        if ("M".equalsIgnoreCase(gender)) {
            return SexCodeType.M;
        } else if ("F".equalsIgnoreCase(gender)) {
            return SexCodeType.F;
        } else {
            return SexCodeType.U;
        }
    }

    /**
     * @{inheritDoc}
     */
    @Override
    public DateRangeType buildDateRange(Date beginRange, Date endRange) {
        DateRangeType dateRange = null;
        Date now = new Date();
        if (beginRange != null && endRange != null
                && beginRange.before(now) && beginRange.before(endRange)) {
            dateRange = new DateRangeType();
            dateRange.setBegin(getGregorianCalendar(beginRange));
            dateRange.setEnd(getGregorianCalendar(endRange));
        }
        return dateRange;
    }

    private String getUrl() {
        return getPropertyAccessor().getProperty(URL_PROP_NAME);
    }

    private String getUserNameAndPassword() {
        StringBuilder builder = new StringBuilder();
        builder.append(getPropertyAccessor().getProperty(USER_PROP_NAME));
        builder.append(":");
        builder.append(getPropertyAccessor().getProperty(PASS_PROP_NAME));

        return builder.toString();
    }

    protected PropertyAccessorUtil getPropertyAccessor() {
        return PropertyAccessorUtil.getInstance();
    }

    protected PatientRequestType buildPatientRequest(PatientType patient, DateRangeType dRange) {
        PatientRequestType request = new PatientRequestType();

        PatientRequestType.PrescriptionRequest presRequest = new PatientRequestType.PrescriptionRequest();
        presRequest.setPatient(patient);

        if (dRange != null) {
            presRequest.setDateRange(dRange);
        }
        request.setPrescriptionRequest(presRequest);

        RequesterType requester = buildRequester();
        request.setRequester(requester);

        return request;
    }

    private RequesterType buildRequester() {
        RequesterType requester = new RequesterType();

        requester.setProvider(buildProvider());
        requester.setLocation(buildLocation());

        return requester;
    }

    private LocationType buildLocation() {
        LocationType location = new LocationType();
        Address address = new Address();
        address.setStateCode(USStateCodeType.KS);
        location.getContent().add(of.createLocationTypeName("Federal Agency"));
        location.getContent().add(of.createLocationTypeDEANumber("AB1234579"));
        location.getContent().add(of.createLocationTypeAddress(address));
        return location;
    }

    private ProviderType buildProvider() {
        ProviderType provider = new ProviderType();
        provider.getContent().add(of.createProviderTypeRole(RoleType.PHYSICIAN));
        provider.getContent().add(of.createProviderTypeFirstName("Jason"));
        provider.getContent().add(of.createProviderTypeLastName("Smith"));
        provider.getContent().add(of.createProviderTypeDEANumber("AB1234579"));
        return provider;
    }

    protected InputStream getViewableForm(String formSite, String userPass) {
        try {
            URL url = new URL(formSite);
            String encoding = Base64Utility.encode(userPass.getBytes());

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Authorization", "Basic " + encoding);
            return (InputStream) connection.getInputStream();
        } catch (IOException ex) {
            LOG.warn("Unable to read viewable report from: {}", formSite, ex);
        }
        return null;
    }

    private boolean allowed(PatientResponseType response) {
        if (response == null) {
            return false;
        }

        return (response.getError() == null || !(response.getResponse() != null && !response.getResponse().isEmpty()
                && response.getResponse().get(0) != null && response.getResponse().get(0).getDisallowed() != null));
    }

    private WebClient getWebClient(String authorizationHeader, String serviceUrl) {
        return WebClient.create(serviceUrl)
                .accept(MediaType.APPLICATION_XML)
                .type(MediaType.APPLICATION_XML)
                .header("Authorization", authorizationHeader);
    }

    private ReportRequestType buildReportRequest(String reportLink) {
        ReportRequestType reportRequest = new ReportRequestType();
        ReportRequestType.Requester requester = new ReportRequestType.Requester();
        requester.setReportLink(reportLink);
        requester.setLocation(buildLocation());
        requester.setProvider(buildProvider());

        reportRequest.setRequester(requester);

        return reportRequest;
    }

}
