/*
 * Copyright (c) 2014, United States Government, as represented by the Secretary of Health and Human Services.
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

package gov.hhs.fha.nhinc.x12.test;

import gov.hhs.fha.nhinc.common.nhinccommon.AssertionType;
import gov.hhs.fha.nhinc.common.nhinccommon.HomeCommunityType;
import gov.hhs.fha.nhinc.common.nhinccommon.NhinTargetCommunitiesType;
import gov.hhs.fha.nhinc.common.nhinccommon.NhinTargetCommunityType;
import gov.hhs.fha.nhinc.common.nhinccommonentity.RespondingGatewayCrossGatewayRealTimeRequestType;
import gov.hhs.fha.nhinc.common.nhinccommonentity.RespondingGatewayCrossGatewayRealTimeResponseType;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import org.apache.commons.io.IOUtils;
import org.apache.ws.security.util.Base64;
import org.caqh.soap.wsdl.corerule2_2_0.COREEnvelopeRealTimeRequest;
import org.caqh.soap.wsdl.corerule2_2_0.COREEnvelopeRealTimeResponse;

/**
 *
 * @author jassmit
 */
public class X12Runner {

    private static final String LARGE_PAYLOAD_FILE = "C:\\Temp\\payload_test.txt";

    public void realTimePayloadTransactionTest() throws Exception {
        RespondingGatewayCrossGatewayRealTimeRequestType requestWrapper = new RespondingGatewayCrossGatewayRealTimeRequestType();
        AssertionType assertion = buildAssertion();
        requestWrapper.setAssertion(assertion);
        requestWrapper.setCOREEnvelopeRealTimeRequest(buildRequest());
        requestWrapper.setNhinTargetCommunities(buildTargets());

        X12RealTimeClient client = new X12RealTimeClient();

        long startTime = System.nanoTime();
        RespondingGatewayCrossGatewayRealTimeResponseType response = client.callEntity(requestWrapper, assertion);
        long stopTime = System.nanoTime();

        System.out.println("Start Time, " + startTime + ", stop time, " + stopTime);

        double timeElapsed = ((double) (stopTime - startTime)) / 1000000000.0;
        System.out.println("Time elapsed(s): " + timeElapsed);

        comparePayloads(requestWrapper.getCOREEnvelopeRealTimeRequest(), response.getCOREEnvelopeRealTimeResponse());
    }

    private COREEnvelopeRealTimeRequest buildRequest() throws IOException {
        COREEnvelopeRealTimeRequest request = new COREEnvelopeRealTimeRequest();
        request.setPayloadType("X12_270_Request_005010X279A1");
        request.setProcessingMode("RealTime");
        request.setPayloadID(UUID.randomUUID().toString());
        request.setSenderID("HospitalA");
        request.setReceiverID("PayerB");
        request.setCORERuleVersion("2.2.0");
        request.setPayload(buildPayload());

        return request;
    }

    private NhinTargetCommunitiesType buildTargets() {
        NhinTargetCommunitiesType targets = new NhinTargetCommunitiesType();
        NhinTargetCommunityType target = new NhinTargetCommunityType();
        HomeCommunityType hc = new HomeCommunityType();
        hc.setHomeCommunityId("urn:oid:2.2");
        hc.setName("urn:oid:2.2");
        hc.setDescription("Payload Test HCID");
        target.setHomeCommunity(hc);
        targets.getNhinTargetCommunity().add(target);

        return targets;
    }

    private AssertionType buildAssertion() {
        return new AssertionType();
    }

    private String buildPayload() throws IOException {

        File testFile = new File(LARGE_PAYLOAD_FILE);
        System.out.println("File Size: " + testFile.length());
        FileInputStream fStream = new FileInputStream(testFile);
        return new String(Base64.encode(IOUtils.toByteArray(fStream)));
    }

    private void comparePayloads(COREEnvelopeRealTimeRequest request, COREEnvelopeRealTimeResponse response) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String requestPayloadHash = convertToHex(md.digest(request.getPayload().getBytes()));
            System.out.println("Request Hash: " + requestPayloadHash);

            String responsePayloadHash = convertToHex(md.digest(response.getPayload().getBytes()));
            System.out.println("Response Hash: " + responsePayloadHash);

            String result = (requestPayloadHash.equals(responsePayloadHash)) ? "Document Match." : "Document mismatch.";
            System.out.println(result);
        } catch (NoSuchAlgorithmException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private String convertToHex(byte[] hash) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < hash.length; i++) {
            sb.append(Integer.toString((hash[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

    public static void main(String[] args) throws Exception {
        X12Runner runner = new X12Runner();
        runner.realTimePayloadTransactionTest();
    }

}
