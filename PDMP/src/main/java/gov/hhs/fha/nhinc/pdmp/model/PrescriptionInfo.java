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
     * @return the drugName
     */
    public String getDrugName() {
        return drugName;
    }

    /**
     * @param drugName the drugName to set
     */
    public void setDrugName(String drugName) {
        this.drugName = drugName;
    }

    /**
     * @return the drugCount
     */
    public int getDrugCount() {
        return drugCount;
    }

    /**
     * @param drugCount the drugCount to set
     */
    public void setDrugCount(int drugCount) {
        this.drugCount = drugCount;
    }

    /**
     * @return the drugDuration
     */
    public int getDrugDuration() {
        return drugDuration;
    }

    /**
     * @param drugDuration the drugDuration to set
     */
    public void setDrugDuration(int drugDuration) {
        this.drugDuration = drugDuration;
    }

    /**
     * @return the prescriber
     */
    public String getPrescriber() {
        return prescriber;
    }

    /**
     * @param prescriber the prescriber to set
     */
    public void setPrescriber(String prescriber) {
        this.prescriber = prescriber;
    }

    /**
     * @return the pharmacyName
     */
    public String getPharmacyName() {
        return pharmacyName;
    }

    /**
     * @param pharmacyName the pharmacyName to set
     */
    public void setPharmacyName(String pharmacyName) {
        this.pharmacyName = pharmacyName;
    }

    /**
     * @return the refill
     */
    public String getRefill() {
        return refill;
    }

    /**
     * @param refill the refill to set
     */
    public void setRefill(String refill) {
        this.refill = refill;
    }

    /**
     * @return the mgEq
     */
    public double getMgEq() {
        return mgEq;
    }

    /**
     * @param mgEq the mgEq to set
     */
    public void setMgEq(double mgEq) {
        this.mgEq = mgEq;
    }

    /**
     * @return the mgEdPerDay
     */
    public String getMgEdPerDay() {
        return mgEdPerDay;
    }

    /**
     * @param mgEdPerDay the mgEdPerDay to set
     */
    public void setMgEdPerDay(String mgEdPerDay) {
        this.mgEdPerDay = mgEdPerDay;
    }

    /**
     * @return the paymentType
     */
    public String getPaymentType() {
        return paymentType;
    }

    /**
     * @param paymentType the paymentType to set
     */
    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    /**
     * @return the pmpState
     */
    public String getPmpState() {
        return pmpState;
    }

    /**
     * @param pmpState the pmpState to set
     */
    public void setPmpState(String pmpState) {
        this.pmpState = pmpState;
    }

    public String getDrugClass() {
        return drugClass;
    }

    public void setDrugClass(String drugClass) {
        this.drugClass = drugClass;
    }

    public boolean isIsOpioid() {
        return isOpioid;
    }

    public void setIsOpioid(boolean isOpioid) {
        this.isOpioid = isOpioid;
    }

}