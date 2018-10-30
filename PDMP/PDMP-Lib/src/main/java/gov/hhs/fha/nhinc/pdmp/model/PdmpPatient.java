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

import java.util.List;

/**
 * Extends patient POJO with additional prescription information.
 * 
 * {@inheritDoc}
 * 
 * @author jassmit
 */
public class PdmpPatient extends Patient {
    
    private String narcoticsScore;
    private String stimulantsScore;
    private String sedativesScore;
    
    private String reportUrl;
    private List<String> disallowedPmps;

    /**
     * 
     * @return narcotics score for determining narcotics risk for patient 
     */
    public String getNarcoticsScore() {
        return narcoticsScore;
    }

    /**
     * 
     * @param narcoticsScore the narcotics score set for patient 
     */
    public void setNarcoticsScore(String narcoticsScore) {
        this.narcoticsScore = narcoticsScore;
    }

    /**
     * 
     * @return stimulant score for determining stimulant risk for patient
     */
    public String getStimulantsScore() {
        return stimulantsScore;
    }

    /**
     * 
     * @param stimulantsScore the stimulant score set for patient 
     */
    public void setStimulantsScore(String stimulantsScore) {
        this.stimulantsScore = stimulantsScore;
    }

    /**
     * 
     * @return sedatives score for determining sedatives risk for patient
     */
    public String getSedativesScore() {
        return sedativesScore;
    }

    /**
     * 
     * @param sedativesScore the sedatives score set for patient
     */
    public void setSedativesScore(String sedativesScore) {
        this.sedativesScore = sedativesScore;
    }

    /**
     * 
     * @return report URL for patient prescription results
     */
    public String getReportUrl() {
        return reportUrl;
    }

    /**
     * 
     * @param reportUrl the report URL set for the patient prescription results
     */
    public void setReportUrl(String reportUrl) {
        this.reportUrl = reportUrl;
    }

    /**
     * 
     * @return the PMP registries the patient is not allowed to access 
     */
    public List<String> getDisallowedPmps() {
        return disallowedPmps;
    }

    /**
     * 
     * @param disallowedPmps the registries set that the patient can not access
     */
    public void setDisallowedPmps(List<String> disallowedPmps) {
        this.disallowedPmps = disallowedPmps;
    }
    
}
