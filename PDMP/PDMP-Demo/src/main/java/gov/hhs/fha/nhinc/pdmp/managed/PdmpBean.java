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
package gov.hhs.fha.nhinc.pdmp.managed;

import gov.hhs.fha.nhinc.pdmp.model.PdmpPatient;
import gov.hhs.fha.nhinc.pdmp.model.PrescriptionInfo;
import gov.hhs.fha.nhinc.pdmp.services.PdmpService;
import gov.hhs.fha.nhinc.nhinclib.NullChecker;
import gov.hhs.fha.nhinc.pdmp.AddressRequiredZipType;
import gov.hhs.fha.nhinc.pdmp.PatientType;
import gov.hhs.fha.nhinc.pdmp.services.PdmpServiceImpl;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author jassmit
 */
@ManagedBean(name = "pdmpBean")
@ViewScoped
@Component
public class PdmpBean {

    private PdmpService pdmpService = new PdmpServiceImpl();
    
    private static final Logger LOG = LoggerFactory.getLogger(PdmpBean.class);

    private String patientFirstName;
    private String patientLastName;
    private Date birthDate;
    private String gender;
    private String phone;
    private String zip;
    
    private Date beginRange;
    private Date endRange;
    
    private List<String> genderList;
    
    private List<PdmpPatient> resultPatients = new ArrayList<>();
    private List<PrescriptionInfo> prescriptionList = new ArrayList<>();
    private String resultMessage;
    private boolean patientFound;
    private boolean prescriptionsFound;
    
    private int activeIndex = 0;
    
    public PdmpBean() {
        populateGenderList();
    }

    public void searchForPatient() {
        PatientType patient = new PatientType();
        PatientType.Name name = new PatientType.Name();
        name.setFirst(patientFirstName);
        name.setLast(patientLastName);

        gov.hhs.fha.nhinc.pdmp.ObjectFactory of = new gov.hhs.fha.nhinc.pdmp.ObjectFactory();
        patient.getContent().add(of.createPatientTypeName(name));
        patient.getContent().add(of.createPatientTypeBirthdate(pdmpService.getGregorianCalendar(birthDate)));
        patient.getContent().add(of.createPatientTypeSexCode(pdmpService.getSexCodeType(gender)));
        AddressRequiredZipType address = new AddressRequiredZipType();
        address.setZipCode(zip);
        patient.getContent().add(of.createPatientTypeAddress(address));
        patient.getContent().add(of.createPatientTypePhone(new BigInteger(phone)));
        
        PdmpPatient patientResult = pdmpService.searchForPdmpInfo(patient, pdmpService.buildDateRange(beginRange, endRange));
        
        if(patientResult != null) {
            patientFound = true;
            patientResult.setFirstName(patientFirstName);
            patientResult.setLastName(patientLastName);
            patientResult.setDateOfBirth(birthDate);
            patientResult.setGender(gender);
            patientResult.setPhone(phone);
            patientResult.setZip(zip);
            resultPatients.add(patientResult);
            
            if(NullChecker.isNotNullish(patientResult.getReportUrl())) {
                try {
                    prescriptionList = pdmpService.getAllPrescriptions(patientResult);
                    prescriptionsFound = true;
                } catch (IOException ex) {
                    LOG.warn("Unable to get prescriptions from report {}", patientResult.getReportUrl(), ex);
                }
            }
        }
    }
    
    public void clearValues() {
        patientLastName = patientFirstName = gender = null;
        beginRange = endRange = birthDate = null;
        resultPatients.clear();
        patientFound = prescriptionsFound = false;
        prescriptionList.clear();
    }

    public String getPatientFirstName() {
        return patientFirstName;
    }

    public void setPatientFirstName(String patientFirstName) {
        this.patientFirstName = patientFirstName;
    }

    public String getPatientLastName() {
        return patientLastName;
    }

    public void setPatientLastName(String patientLastName) {
        this.patientLastName = patientLastName;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getBeginRange() {
        return beginRange;
    }

    public void setBeginRange(Date beginRange) {
        this.beginRange = beginRange;
    }

    public Date getEndRange() {
        return endRange;
    }

    public void setEndRange(Date endRange) {
        this.endRange = endRange;
    }

    public List<String> getGenderList() {
        return genderList;
    }

    public void setGenderList(List<String> genderList) {
        this.genderList = genderList;
    }

    public List<PdmpPatient> getResultPatients() {
        return resultPatients;
    }

    public void setResultPatients(List<PdmpPatient> resultPatients) {
        this.resultPatients = resultPatients;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    public boolean isPatientFound() {
        return patientFound;
    }

    public void setPatientFound(boolean patientFound) {
        this.patientFound = patientFound;
    }

    public int getActiveIndex() {
        return activeIndex;
    }

    public void setActiveIndex(int activeIndex) {
        this.activeIndex = activeIndex;
    }

    public List<PrescriptionInfo> getPrescriptionList() {
        return prescriptionList;
    }

    public void setPrescriptionList(List<PrescriptionInfo> prescriptionList) {
        this.prescriptionList = prescriptionList;
    }

    public boolean isPrescriptionsFound() {
        return prescriptionsFound;
    }

    public void setPrescriptionsFound(boolean prescriptionsFound) {
        this.prescriptionsFound = prescriptionsFound;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }
    
    private void populateGenderList() {
        genderList = new ArrayList<>();
        genderList.add("M");
        genderList.add("F");
        genderList.add("U");
    }

}
