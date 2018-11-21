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
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
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
    private static final String FILL_AT = "filled_at";
    private static final String DRUG = "drug";
    private static final String PRODUCT_NAME = "product_name";
    private static final String QUANTITY = "quantity";
    private static final String DAYS_SUPPLY = "days_supply";
    private static final String SOURCE_STATE_CD = "source_state_code";
    private static final String PRESCRIPTIONS = "prescriptions";
    private static final String RX = "rx";
    private static final String SCRIPT_TAG = "script";
    private static final String JS_REPORT_METHOD = "window.DataReportPageSetup()(";

    /**
     * Retrieves patient prescription report from the given URL and adds extracts the prescriptions from the report HTML
     * using the JSoup library
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

    /**
     * Use for junit test
     *
     * @param fileName
     * @return
     * @throws MalformedURLException
     * @throws IOException
     */
    public static List<PrescriptionInfo> getAllPrescriptionsFromFile(String fileName)
        throws MalformedURLException, IOException {
        File file = new File(fileName);
        logger.debug("File Location {} ", file.toPath());
        Document htmlDocument = Jsoup.parse(file, "UTF-8");
        return populatePrescriptionList(htmlDocument);
    }

    private static List<PrescriptionInfo> populatePrescriptionList(Document htmlDocument) {
        Elements elements = htmlDocument.select(SCRIPT_TAG);
        Element data = elements.get(1);
        List<Node> childNodes = data.childNodes();
        Node node = childNodes.get(0);
        List<PrescriptionInfo> prescriptionList = new ArrayList<>();
        if (node instanceof DataNode) {
            DataNode childDataNode = (DataNode) node;
            String content = childDataNode.getWholeData();
            content = StringUtils.trim(content);
            content = StringUtils.removeStart(content, JS_REPORT_METHOD);
            content = StringUtils.removeEnd(content, ");");
            JSONObject jsonObject = new JSONObject(content);
            JSONArray prescriptions = jsonObject.getJSONObject(RX).getJSONArray(PRESCRIPTIONS);
            prescriptions.forEach(prescription -> {
                JSONObject obj = (JSONObject) prescription;
                prescriptionList.add(populatePrescriptionInfo(obj));
            });
        }
        return prescriptionList;
    }

    private static PrescriptionInfo populatePrescriptionInfo(JSONObject row) {
        PrescriptionInfo prescriptionInfo = new PrescriptionInfo();
        prescriptionInfo.setFileStrDate(row.getString(FILL_AT));
        prescriptionInfo.setDrugName(row.getJSONObject(DRUG).getString(PRODUCT_NAME));
        prescriptionInfo.setDrugCount(row.getJSONObject(DRUG).getInt(QUANTITY));
        prescriptionInfo.setDrugDuration(row.getInt(DAYS_SUPPLY));
        prescriptionInfo.setPmpState(row.getString(SOURCE_STATE_CD));
        return prescriptionInfo;
    }
}
