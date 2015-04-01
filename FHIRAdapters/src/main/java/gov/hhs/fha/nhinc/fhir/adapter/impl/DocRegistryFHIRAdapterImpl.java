/*
 * Copyright (c) 2009-2015, United States Government, as represented by the Secretary of Health and Human Services.
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
package gov.hhs.fha.nhinc.fhir.adapter.impl;

import gov.hhs.fha.nhinc.connectmgr.ConnectionManagerException;
import gov.hhs.fha.nhinc.docregistry.adapter.AdapterComponentDocRegistryOrchImpl;
import gov.hhs.fha.nhinc.docrepository.adapter.model.Document;
import gov.hhs.fha.nhinc.fhir.client.AdapterFHIRClient;
import gov.hhs.fha.nhinc.fhir.util.FHIRConstants;
import gov.hhs.fha.nhinc.nhinclib.NullChecker;
import gov.hhs.fha.nhinc.util.format.PatientIdFormatUtil;
import gov.hhs.fha.nhinc.util.format.UTCDateUtil;
import gov.hhs.fhs.nhinc.fhir.transform.ResourceTransformer;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryRequest;
import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryResponse;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.AdhocQueryType;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.SlotType1;
import org.apache.log4j.Logger;
import org.hl7.fhir.instance.model.AtomEntry;
import org.hl7.fhir.instance.model.AtomFeed;
import org.hl7.fhir.instance.model.DocumentReference;
import org.hl7.fhir.instance.model.Resource;

/**
 *
 * @author jassmit
 */
public class DocRegistryFHIRAdapterImpl extends FhirAdapter {

    private final AdapterFHIRClient client = new AdapterFHIRClient();
    private final ResourceTransformer transformer = new ResourceTransformer();
    private final AdapterComponentDocRegistryOrchImpl registryHelper = new AdapterComponentDocRegistryOrchImpl();
    private static final Logger LOG = Logger.getLogger(DocRegistryFHIRAdapterImpl.class);

    //This class UTCDateUtil has to be a singleton, should be changed in the future
    private UTCDateUtil utcDateUtil = new UTCDateUtil();

    private static final String EBXML_DOCENTRY_PATIENT_ID = "$XDSDocumentEntryPatientId";
    private static final String EBXML_DOCENTRY_CLASS_CODE = "$XDSDocumentEntryClassCode";
    private static final String EBXML_DOCENTRY_CREATION_TIME_FROM = "$XDSDocumentEntryCreationTimeFrom";
    private static final String EBXML_DOCENTRY_CREATION_TIME_TO = "$XDSDocumentEntryCreationTimeTo";

    public AdhocQueryResponse queryRegistry(AdhocQueryRequest request) {
        List<SlotType1> querySlots = getSlotsFromAdhocQueryRequest(request);
        String patientId = extractPatientIdentifier(querySlots);
        List<String> docCodes = extractClassCodes(querySlots);
        Date creationDateFrom = extractCreationTimeFrom(querySlots);
        Date creationDateTo = extractCreationTimeTo(querySlots);

        patientId = buildIdentifier(patientId);
        List<AtomEntry<? extends Resource>> referenceAtoms = getDocumentReferenceAtoms(patientId, docCodes);

        if (NullChecker.isNotNullish(referenceAtoms)) {
            AdhocQueryResponse response = new AdhocQueryResponse();
            //Filter the retrieved documents based on the request creationDate from and to
            //This is a temporary fix for the Demo, eventually the parameter will be added as part of the FHIR request
            List<Document> retrievedDocuments = filterDocumentsByCreationDateCriteria(transformer.buildDocumentsFromResource(referenceAtoms), creationDateFrom, creationDateTo);

            registryHelper.loadResponseMessage(response, retrievedDocuments);
            return response;
        }
        return null;
    }

    private List<SlotType1> getSlotsFromAdhocQueryRequest(AdhocQueryRequest request) {
        AdhocQueryType adhocQuery = request.getAdhocQuery();
        if (adhocQuery != null) {
            return adhocQuery.getSlot();
        }

        return null;
    }

    private String extractPatientIdentifier(List<SlotType1> slots) {
        String patientId = null;
        List<String> slotValues = extractSlotValues(slots, EBXML_DOCENTRY_PATIENT_ID);
        if ((slotValues != null) && (!slotValues.isEmpty())) {
            String formattedPatientId = slotValues.get(0);
            patientId = PatientIdFormatUtil.stripQuotesFromPatientId(formattedPatientId);
        }
        return patientId;
    }

    private List<String> extractClassCodes(List<SlotType1> slots) {
        List<String> classCodes = null;
        List<String> slotValues = extractSlotValues(slots, EBXML_DOCENTRY_CLASS_CODE);
        if ((slotValues != null) && (!slotValues.isEmpty())) {
            classCodes = new ArrayList<>();
            for (String slotValue : slotValues) {
                registryHelper.parseParamFormattedString(slotValue, classCodes);
            }
        }
        return classCodes;
    }

    private List<String> extractSlotValues(List<SlotType1> slots, String slotName) {
        if (slots != null) {
            for (SlotType1 slot : slots) {
                if (NullChecker.isNotNullish(slot.getName()) && slot.getValueList() != null
                    && NullChecker.isNotNullish(slot.getValueList().getValue())
                    && slot.getName().equals(slotName)) {

                    return slot.getValueList().getValue();
                }
            }
        }
        return null;
    }

    private List<AtomEntry<? extends Resource>> getDocumentReferenceAtoms(String patientId, List<String> docTypes) {
        try {
            Map<String, String> fhirParams = new HashMap<>();
            //TODO: Need to pull out pid and AA for call (current example in server doesn't appear to have AA, might be org).
            fhirParams.put("subject.identifier", patientId);
            if (NullChecker.isNotNullish(docTypes)) {
                //TODO: Need to figure out how to query for multiple doc types.
                fhirParams.put("type", docTypes.get(0));
            }

            addFormatParam(fhirParams);

            //TODO: Plenty of other params that can be pulled from request, probably should pull out full query params
            // and then convert to map.
            AtomFeed docFeed = client.searchFhirResource(FHIRConstants.FHIR_DOC_REFERENCE_URL_KEY, fhirParams, DocumentReference.class);
            if (docFeed != null && NullChecker.isNotNullish(docFeed.getEntryList())) {
                return docFeed.getEntryList();
            }
        } catch (URISyntaxException | ConnectionManagerException ex) {
            LOG.error("Unable to get Document Reference resource: " + ex.getLocalizedMessage(), ex);
        }
        return null;
    }

    private String buildIdentifier(String patientId) {
        String regex = ".+\\^\\^\\^&.+&ISO";
        String returnValue;
        if (patientId.matches(regex)) {
            String aa = PatientIdFormatUtil.parseCommunityId(patientId);
            if (!aa.startsWith("urn:oid:")) {
                aa = "urn:oid:" + aa;
            }
            returnValue = aa + "|" + PatientIdFormatUtil.parsePatientId(patientId);
        } else {
            returnValue = patientId;
        }
        return returnValue;
    }

    /**
     * Filters the retrieved document by document creation date from and to range passed in the request. This is a
     * temporary workaround for the demo.
     *
     * @param documentList
     * @param creationDateFrom
     * @param creationDateTo
     * @return
     */
    private List<Document> filterDocumentsByCreationDateCriteria(List<Document> documentList, Date creationDateFrom, Date creationDateTo) {
        List<Document> filteredDocumentList = new ArrayList<Document>();
        if (creationDateFrom != null && creationDateTo != null && !documentList.isEmpty()) {
            for (Document document : documentList) {
                if (document.getCreationTime().before(creationDateTo) && document.getCreationTime().after(creationDateFrom)) {
                    filteredDocumentList.add(document);
                }
            }
            return filteredDocumentList;
        } else {
            return documentList;
        }
    }

    /**
     * Retrieves the CreationTimeFrom from the request
     *
     */
    private Date extractCreationTimeFrom(List<SlotType1> slots) {
        Date creationTimeFrom = null;
        List<String> slotValues = extractSlotValues(slots, EBXML_DOCENTRY_CREATION_TIME_FROM);
        if ((slotValues != null) && (!slotValues.isEmpty())) {
            creationTimeFrom = utcDateUtil.parseUTCDateOptionalTimeZone(slotValues.get(0));
        }
        return creationTimeFrom;
    }

    /**
     * Retrieves the CreationTimeTo from the request
     *
     */
    private Date extractCreationTimeTo(List<SlotType1> slots) {
        Date creationTimeTo = null;
        List<String> slotValues = extractSlotValues(slots, EBXML_DOCENTRY_CREATION_TIME_TO);
        if ((slotValues != null) && (!slotValues.isEmpty())) {
            creationTimeTo = utcDateUtil.parseUTCDateOptionalTimeZone(slotValues.get(0));
        }
        return creationTimeTo;
    }
}
