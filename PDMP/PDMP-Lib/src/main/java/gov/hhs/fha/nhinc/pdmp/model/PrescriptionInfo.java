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
package gov.hhs.fha.nhinc.pdmp.model;

/**
 * POJO data for a specific prescription provided to a patient
 * 
 * @author mpnguyen
 *
 */
public class PrescriptionInfo {
    private String fileStrDate;
    private String drugName;
    private int drugCount;
    private int drugDuration;
    private String prescriber;
    private String pharmacyName;
    private String refill;
    private double mgEq;
    private String mgEdPerDay;
    private String paymentType;
    private String pmpState;   
    private String drugClass;
    private boolean isOpioid;


    /**
     * @return the fileStrDate
     */
    public String getFileStrDate() {
        return fileStrDate;
    }

    /**
     * @param fileStrDate the fileStrDate to set
     */
    public void setFileStrDate(String fileStrDate) {
        this.fileStrDate = fileStrDate;
    }

    /**
     * @return the name of the drug prescribed
     */
    public String getDrugName() {
        return drugName;
    }

    /**
     * @param drugName name of the prescribed drug to set
     */
    public void setDrugName(String drugName) {
        this.drugName = drugName;
    }

    /**
     * @return the count of drug prescribed
     */
    public int getDrugCount() {
        return drugCount;
    }

    /**
     * @param drugCount the prescribed drug count to set
     */
    public void setDrugCount(int drugCount) {
        this.drugCount = drugCount;
    }

    /**
     * @return the duration of the drug prescribed
     */
    public int getDrugDuration() {
        return drugDuration;
    }

    /**
     * @param drugDuration the duration for the drug to set
     */
    public void setDrugDuration(int drugDuration) {
        this.drugDuration = drugDuration;
    }

    /**
     * @return the name of the prescriber
     */
    public String getPrescriber() {
        return prescriber;
    }

    /**
     * @param prescriber the name of the prescriber to set
     */
    public void setPrescriber(String prescriber) {
        this.prescriber = prescriber;
    }

    /**
     * @return the pharmacy name
     */
    public String getPharmacyName() {
        return pharmacyName;
    }

    /**
     * @param pharmacyName the pharmacy name to set
     */
    public void setPharmacyName(String pharmacyName) {
        this.pharmacyName = pharmacyName;
    }

    /**
     * @return the number of refills for the prescription
     */
    public String getRefill() {
        return refill;
    }

    /**
     * @param refill the prescription refills to set
     */
    public void setRefill(String refill) {
        this.refill = refill;
    }

    /**
     * @return the amount in milligrams of prescribed drug
     */
    public double getMgEq() {
        return mgEq;
    }

    /**
     * @param mgEq amount in milligrams of drug to set
     */
    public void setMgEq(double mgEq) {
        this.mgEq = mgEq;
    }

    /**
     * @return the amount in milligrams of the drug to be taken daily
     */
    public String getMgEdPerDay() {
        return mgEdPerDay;
    }

    /**
     * @param mgEdPerDay the amount in milligrams of the drug to be taken daily to set
     */
    public void setMgEdPerDay(String mgEdPerDay) {
        this.mgEdPerDay = mgEdPerDay;
    }

    /**
     * @return the payment type used to purchase the prescription
     */
    public String getPaymentType() {
        return paymentType;
    }

    /**
     * @param paymentType the payment type used to purchase the prescription to set
     */
    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    /**
     * @return the US state the prescription was registered
     */
    public String getPmpState() {
        return pmpState;
    }

    /**
     * @param pmpState the US State the prescription was registered to set
     */
    public void setPmpState(String pmpState) {
        this.pmpState = pmpState;
    }

    /**
     * 
     * @return the classification of the drug prescribed 
     */
    public String getDrugClass() {
        return drugClass;
    }

    /**
     * 
     * @param drug classification to set 
     */
    public void setDrugClass(String drugClass) {
        this.drugClass = drugClass;
    }

    /**
     * 
     * @return if the drug prescribed is classified as an opioid 
     */
    public boolean isIsOpioid() {
        return isOpioid;
    }

    /**
     * 
     * @param is Opioid to set if drug is categorized as an opioid
     */
    public void setIsOpioid(boolean isOpioid) {
        this.isOpioid = isOpioid;
    }

}