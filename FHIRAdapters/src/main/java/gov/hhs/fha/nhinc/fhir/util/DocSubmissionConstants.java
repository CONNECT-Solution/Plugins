/*
 * Copyright (c) 2009-2015, United States Government, as represented by the Secretary of Health and Human Services.  * All rights reserved. * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *   * Redistributions of source code must retain the above
 *     copyright notice, this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *   * Neither the name of the United States Government nor the
 *     names of its contributors may be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *DISCLAIMED. IN NO EVENT SHALL THE UNITED STATES GOVERNMENT BE LIABLE FOR ANY
 *DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package gov.hhs.fha.nhinc.fhir.util;

/**
 *
 * @author tjafri
 */
public class DocSubmissionConstants {

    public static final String DS_RESPONSE_BASIC_ERROR_MESSAGE = "Unable to update server with DocumentReference";

    public static final String XDS_SUBMISSIONSET_PATIENT_ID = "XDSSubmissionSet.patientId";
    public static final String XDS_DOCUMENT_SOURCE_PID = "sourcePatientId";
    public static final String XDS_DOCUMENT_SOURCE_PATIENT_INFO = "sourcePatientInfo";
    public static final String XDS_DOCUMENT_PATIENT_ID = "XDSDocumentEntry.patientId";
    public static final String XDS_PATIENT_INFO_NAME_PREFIX = "PID-5|";
    public static final String XDS_PATIENT_INFO_DOB_PREFIX = "PID-7|";
    public static final String XDS_PATIENT_INFO_GENDER_PREFIX = "PID-8|";
    public static final String XDS_PATIENT_INFO_ADDRESS_PREFIX = "PID-11|";

    public static final String PARAMS_FAMILY_PART_TYPE = "FAM";
    public static final String PARAMS_GIVEN_PART_TYPE = "GIV";
    public static final String SUBJECT_NAME_SEMANTICS = "LivingSubject.name";
    public static final String SUBJECT_GENDER_SEMANTICS = "LivingSubject.administrativeGender";
    public static final String SUBJECT_DOB_SEMANTICS = "LivingSubject.birthTime";

    public static final String DS_RESPONSE_STATUS_SUCCESS = "urn:oasis:names:tc:ebxml-regrep:ResponseStatusType:Success";
    public static final String DS_RESPONSE_STATUS_FAILURE = "urn:oasis:names:tc:ebxml-regrep:ResponseStatusType:Failure";
    public static final String DS_RESPONSE_ERROR_SEVERITY = "urn:oasis:names:tc:ebxml-regrep:ErrorSeverityType:Error";

    public static final String XDR_EC_XDSMissingDocument = "XDSMissingDocument";
    public static final String XDR_EC_XDSMissingDocumentMetadata = "XDSMissingDocumentMetadata";
    public static final String XDR_EC_XDSNonIdenticalHash = "XDSNonIdenticalHash";
    public static final String XDR_EC_XDSRegistryDuplicateUniqueIdInMessage = "XDSRegistryDuplicateUniqueIdInMessage";
    public static final String XDR_EC_XDSRegistryBusy = "XDSRegistryBusy";
    public static final String XDR_EC_XDSRegistryMetadataError = "XDSRegistryMetadataError";
    public static final String XDR_EC_XDSUnknownPatientId = "XDSUnknownPatientId";
    public static final String XDR_EC_XDSPatientIdDoesNotMatch = "XDSPatientIdDoesNotMatch";
    public static final String XDR_EC_XDSRegistryError = "XDSRegistryError";

    public static final String XDS_DOCUMENT_CLASSIFICATION_SCHEME_TYPE_CODE = "urn:uuid:f0306f51-975f-434e-a61c-c59651d33983";
    public static final String XDS_DOCUMENT_CLASSIFICATION_SCHEME_CLASS_CODE = "urn:uuid:41a5887f-8865-4c09-adf7-e362475b143a";
    public static String XDS_EXTERNAL_DOCUMENT_IDENTIFICATION_SCHEME = "urn:uuid:2e82c1f6-a085-4c72-9da3-8640a32e42ab";
    public static final String XDS_DOCUMENT_CLASSIFICATION_SCHEME_AUTHOR = "urn:uuid:93606bcf-9494-43ec-9b4e-a7748d1a838d";
    public static final String CACCD_LOINC_TYPE_CODE = "";
    public static final String XDS_DOCUMENT_CREATION_TIME = "creationTime";
    public static final String XDS_DOCUMENT_AUTHOR_PERSON = "authorPerson";

    public static final String SEPARATOR = " ";

    public static final String XDS_CLASSIFICATION_FACILITY_TYPE = "urn:uuid:f33fb8ac-18af-42cc-ae0e-ed0b0bdb91e1";
    public static final String XDS_CLASSIFICATION_EVENT_CODE = "urn:uuid:2c6b8cb7-8b2a-4051-b291-b1ae6a575ef4";
    public static final String XDS_DOCUMENT_SERVICE_START_TIME = "serviceStartTime";
    public static final String XDS_DOCUMENT_SERVICE_STOP_TIME = "serviceStopTime";
    public static final String XDS_DOCUMENT_SIZE = "size";
    public static final String XDS_DOCUMENT_LANGUAGE = "languageCode";
}
