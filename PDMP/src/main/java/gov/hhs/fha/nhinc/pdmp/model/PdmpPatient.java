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
 *
 * @author jassmit
 */
public class PdmpPatient extends Patient {
    
    private String narcoticsScore;
    private String stimulantsScore;
    private String sedativesScore;
    
    private String reportUrl;
    private List<String> disallowedPmps;

    public String getNarcoticsScore() {
        return narcoticsScore;
    }

    public void setNarcoticsScore(String narcoticsScore) {
        this.narcoticsScore = narcoticsScore;
    }

    public String getStimulantsScore() {
        return stimulantsScore;
    }

    public void setStimulantsScore(String stimulantsScore) {
        this.stimulantsScore = stimulantsScore;
    }

    public String getSedativesScore() {
        return sedativesScore;
    }

    public void setSedativesScore(String sedativesScore) {
        this.sedativesScore = sedativesScore;
    }

    public String getReportUrl() {
        return reportUrl;
    }

    public void setReportUrl(String reportUrl) {
        this.reportUrl = reportUrl;
    }

    public List<String> getDisallowedPmps() {
        return disallowedPmps;
    }

    public void setDisallowedPmps(List<String> disallowedPmps) {
        this.disallowedPmps = disallowedPmps;
    }
    
}
