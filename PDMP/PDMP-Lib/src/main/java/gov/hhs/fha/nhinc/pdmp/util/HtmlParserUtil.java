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

import gov.hhs.fha.nhinc.pdmp.model.PrescriptionInfo;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Searches for patient prescription results and parses the found HTML for the prescription list.
 * 
 * @author mpnguyen
 *
 */
public class HtmlParserUtil {

    private static final Logger logger = LoggerFactory.getLogger(HtmlParserUtil.class);

    /**
     * Retrieves patient prescription report from the given URL and adds extracts the prescriptions from the report HTML using the JSoup library
     * 
     * @param htmlUrl URL provided for retrieving prescription data
     * @return list of prescriptions for the searched patient
     * @throws MalformedURLException
     * @throws IOException 
     */
    public static List<PrescriptionInfo> getAllPrescriptions(String htmlUrl) throws MalformedURLException, IOException {
        Document htmlDocument = Jsoup.parse(new URL(htmlUrl), 10000);
        return populatePrescriptionList(htmlDocument);
    }

    private static List<PrescriptionInfo> populatePrescriptionList(Document htmlDocument) throws NumberFormatException {
        Element prescriptionTable = htmlDocument.getElementById("prescriptions-table");
        Elements prescriptionRows = prescriptionTable.select("tbody > tr");
        List<PrescriptionInfo> prescriptionList = new ArrayList<>();
        for (Element row : prescriptionRows) {

            prescriptionList.add(populatePrescriptionInfo(row));
        }
        return prescriptionList;
    }

    private static PrescriptionInfo populatePrescriptionInfo(Element row) throws NumberFormatException {
        PrescriptionInfo prescriptionInfo = new PrescriptionInfo();
        // retrieve TD tag Cell as lists
        Elements tDCells = row.select("td");
        prescriptionInfo.setFileStrDate(tDCells.get(0).ownText());
        prescriptionInfo.setDrugName(tDCells.get(3).ownText());
        prescriptionInfo.setDrugCount(Integer.parseInt(tDCells.get(4).ownText()));
        prescriptionInfo.setDrugDuration(Integer.parseInt(tDCells.get(5).ownText()));
        prescriptionInfo.setPrescriber(tDCells.get(6).ownText());
        prescriptionInfo.setPharmacyName(tDCells.get(7).ownText());
        prescriptionInfo.setPmpState(tDCells.get(12).ownText());
        return prescriptionInfo;
    }

}
