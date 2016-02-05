/*
 * Copyright (c) 2009-2016, United States Government, as represented by the Secretary of Health and Human Services.
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
package gov.hhs.fha.nhinc.loadtest;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.basic.DateConverter;
import gov.hhs.fha.nhinc.properties.PropertyAccessor;
import gov.hhs.fha.nhinc.util.jaxb.JAXBXMLUtils;
import gov.hhs.healthit.nhin.XDRAcknowledgementType;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryResponse;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ExtrinsicObjectType;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryResponseType;
import org.apache.commons.io.FileUtils;
import org.caqh.soap.wsdl.corerule2_2_0.COREEnvelopeBatchSubmissionResponse;
import org.caqh.soap.wsdl.corerule2_2_0.COREEnvelopeRealTimeResponse;
import org.hl7.v3.MCCIIN000002UV01;
import org.hl7.v3.PRPAIN201306UV02;
import org.hl7.v3.RetrievePatientCorrelationsResponseType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO: Class description
 *
 * Note: Getters for canned responses must return a new object for every call, therefore the xml will be stored (only
 * file io on the first call), and the xml will be parsed every time.
 */
public final class DataManager {

    private static DataManager instance = null;
    private String propFileLocation = null;

    private boolean refreshFiles;

    /**
     * Keeps track of when a property file was updated, to determine if a reload is needed.
     *
     * TODO: Can this be handled by commons-io?
     */
    private Map<String, Date> propertyDateModifiedMap = null;
    /**
     * Maps a service name to a property file.
     */
    private Map<String, String> propFileMap = null;

    /**
     * Maps a service name to the contents of the related property file.
     */
    private Map<String, Object> propertyConfigMap = null;

    public static final String LOAD_TEST_DATA = "LoadTestData";
    public static final String CANNED_PD_RESPONSE = "PatientDiscoveryResponse";
    public static final String CANNED_PD_DEFERRED_RESPONSE = "PatientDiscoveryDeferredResponse";
    public static final String CANNED_PD_DEFERRED_REQUEST = "PatientDiscoveryDeferredRequest";
    public static final String CANNED_DS_RESPONSE = "DocumentSubmissionResponse";
    public static final String CANNED_DS_DEFERRED_RESPONSE = "DocumentSubmissionDeferredResponse";
    public static final String CANNED_DS_DEFERRED_REQUEST = "DocumentSubmissionDeferredRequest";
    public static final String CANNED_PC_RESPONSE = "PatientCorrelationResponse";
    public static final String CANNED_QD_RESPONSE = "DocumentQueryResponse";
    public static final String CANNED_EXTRINSIC_OBJECT = "ExtrinsicObject";
    public static final String CANNED_CORE_X12DS_GENERIC_BATCH_REQUEST = "CORE_X12DSGenericBatchRequest";
    public static final String CANNED_CORE_X12DS_GENERIC_BATCH_RESPONSE = "CORE_X12DSGenericBatchResponse";
    public static final String CANNED_CORE_X12DS_REALTIME = "CORE_X12DSRealTime";

    private static final Logger LOG = LoggerFactory.getLogger(DataManager.class);

    // TODO: If coded correctly, does this need to be synchronized, or just the map?
    public static synchronized DataManager getInstance() {
        try {
            if (instance == null) {
                instance = new DataManager();
            }
        } catch (Exception e) {
            LOG.error("Error initializing DataManager: {}", e.getLocalizedMessage(), e);
        }

        return instance;
    }

    private DataManager() throws Exception {
        // Don't reload files by default
        this(false);

        // Seed map: Service to Prop File Name
        propFileMap = new ConcurrentHashMap<>();

        propFileMap.put(LOAD_TEST_DATA, "loadTestData.xml");

        propFileMap.put(CANNED_PD_RESPONSE, "CannedPatientDiscoveryResponse.xml");
        propFileMap.put(CANNED_PD_DEFERRED_RESPONSE, "CannedPatientDiscoveryDeferredResponse.xml");
        propFileMap.put(CANNED_PD_DEFERRED_REQUEST, "CannedPatientDiscoveryDeferredRequest.xml");

        propFileMap.put(CANNED_DS_RESPONSE, "CannedDocumentSubmissionResponse.xml");
        propFileMap.put(CANNED_DS_DEFERRED_RESPONSE, "CannedDocumentSubmissionDeferredResponse.xml");
        propFileMap.put(CANNED_DS_DEFERRED_REQUEST, "CannedDocumentSubmissionDeferredRequest.xml");

        propFileMap.put(CANNED_QD_RESPONSE, "CannedDocumentQueryResponse.xml");
        propFileMap.put(CANNED_EXTRINSIC_OBJECT, "CannedExtrinsicObject.xml");
        propFileMap.put(CANNED_PC_RESPONSE, "CannedPatientCorrelationResponse.xml");

        propFileMap.put(CANNED_CORE_X12DS_GENERIC_BATCH_REQUEST, "CannedCORE_X12DSGenericBatch.xml");
        propFileMap.put(CANNED_CORE_X12DS_GENERIC_BATCH_RESPONSE, "CannedCORE_X12DSGenericBatch.xml");

        propFileMap.put(CANNED_CORE_X12DS_REALTIME, "CannedCORE_X12DSRealTime.xml");

        // Set prop file directory
        propFileLocation = PropertyAccessor.getInstance().getPropertyFileLocation();
        if (propFileLocation == null) {
            throw new Exception("Unable to attain property file directory location.");
        }

        if (!propFileLocation.endsWith(File.separator)) {
            propFileLocation += File.separator;
        }
        if (propertyConfigMap == null){
            propertyConfigMap = new ConcurrentHashMap<>();
        }

        updatePropertyConfigMap(LOAD_TEST_DATA, (LoadTestData) getLoadTestData());
    }

    private DataManager(boolean refreshFiles) {
        this.refreshFiles = refreshFiles;
    }

    public LoadTestData getLoadTestData() throws Exception {
        if (checkRefresh(LOAD_TEST_DATA)) {
            String xml = readXmlPropsFromFile(LOAD_TEST_DATA);
            readLoadTestDataFromString(xml);
            updateLastModified(LOAD_TEST_DATA);
        }

        return (LoadTestData) getLoadTestPropertyConfig(LOAD_TEST_DATA);
    }

    private void readLoadTestDataFromString(String xml) {
        XStream xstream = new XStream();
        xstream.autodetectAnnotations(true);
        xstream.alias("LoadTestData", LoadTestData.class);
        xstream.alias("Patient", Patient.class);
        xstream.alias("DocumentMetaData", DocumentMetaData.class);
        xstream.alias("Document", Document.class);
        xstream.registerConverter(new DateConverter());

        LoadTestData ltd = (LoadTestData) xstream.fromXML(xml);
        updatePropertyConfigMap(LOAD_TEST_DATA, ltd);
    }

    ///////// Patient Discovery ////////
    private PRPAIN201306UV02 readPatientDiscoveryResponseFromString(String xml) {
        return (PRPAIN201306UV02) parseXml(xml, "org.hl7.v3");
    }

    public PRPAIN201306UV02 getCannedPatientDiscoveryResponse() throws Exception {
        refresh(CANNED_PD_RESPONSE);
        return readPatientDiscoveryResponseFromString((String) getStringPropertyConfig(CANNED_PD_RESPONSE));
    }

    public String getCannedPatientDiscoveryResponseString() throws Exception {
        refresh(CANNED_PD_RESPONSE);
        return (String) getStringPropertyConfig(CANNED_PD_RESPONSE);
    }

    //////// Patient Discovery Deferred Response ////////
    private MCCIIN000002UV01 readPatientDiscoveryDeferredResponseFromString(String xml) {
        return (MCCIIN000002UV01) parseXml(xml, "org.hl7.v3");
    }

    public MCCIIN000002UV01 getCannedPatientDiscoveryDeferredResponse() throws Exception {
        refresh(CANNED_PD_DEFERRED_RESPONSE);
        return readPatientDiscoveryDeferredResponseFromString((String) getStringPropertyConfig(CANNED_PD_DEFERRED_RESPONSE));
    }

    //////// Patient Discovery Deferred Request ////////
    private MCCIIN000002UV01 readPatientDiscoveryDeferredRequestFromString(String xml) {
        return (MCCIIN000002UV01) parseXml(xml, "org.hl7.v3");
    }

    public MCCIIN000002UV01 getCannedPatientDiscoveryDeferredRequest() throws Exception {
        refresh(CANNED_PD_DEFERRED_REQUEST);
        return readPatientDiscoveryDeferredRequestFromString((String) getStringPropertyConfig(CANNED_PD_DEFERRED_REQUEST));
    }

    //////// Core X12 Batch Response ////////
    private COREEnvelopeBatchSubmissionResponse readCoreX12BatchResponseFromString(String xml) {
        return (COREEnvelopeBatchSubmissionResponse) parseXml(xml, "org.caqh.soap.wsdl.corerule2_2_0");
    }

    public COREEnvelopeBatchSubmissionResponse getCannedCoreX12BatchResponse() throws Exception {
        refresh(CANNED_CORE_X12DS_GENERIC_BATCH_RESPONSE);
        return readCoreX12BatchResponseFromString((String) getStringPropertyConfig(CANNED_CORE_X12DS_GENERIC_BATCH_RESPONSE));
    }

    //////// Core X12 Batch Request ////////
    private COREEnvelopeBatchSubmissionResponse readCoreX12BatchRequestFromString(String xml) {
        return (COREEnvelopeBatchSubmissionResponse) parseXml(xml, "org.caqh.soap.wsdl.corerule2_2_0");
    }

    public COREEnvelopeBatchSubmissionResponse getCannedCoreX12BatchRequest() throws Exception {
        refresh(CANNED_CORE_X12DS_GENERIC_BATCH_REQUEST);
        return readCoreX12BatchRequestFromString((String) getStringPropertyConfig(CANNED_CORE_X12DS_GENERIC_BATCH_REQUEST));
    }

    //////// Core X12 Real Time  ////////
    private COREEnvelopeRealTimeResponse readCoreX12RealTimeFromString(String xml) {
        return (COREEnvelopeRealTimeResponse) parseXml(xml, "org.caqh.soap.wsdl.corerule2_2_0");
    }

    // this method must return a new object for every call, therefore the xml will be stored
    //(only file io on the first call), and the xml will be parsed everytime.
    public COREEnvelopeRealTimeResponse getCannedCoreX12RealTime() throws Exception {
        refresh(CANNED_CORE_X12DS_REALTIME);
        return readCoreX12RealTimeFromString((String) getStringPropertyConfig(CANNED_CORE_X12DS_REALTIME));
    }

    //////// Document Query ////////
    private AdhocQueryResponse readDocumentQueryResponseFromString(String xml) {
        return (AdhocQueryResponse) parseXml(xml, "oasis.names.tc.ebxml_regrep.xsd.query._3");
    }

    public AdhocQueryResponse getCannedDocumentQueryResponse() throws Exception {
        refresh(CANNED_QD_RESPONSE);
        return readDocumentQueryResponseFromString((String) getStringPropertyConfig(CANNED_QD_RESPONSE));
    }

    //////// Document Retrieve ////////
    private ExtrinsicObjectType readExtrinsicObjectFromString(String xml) {
        return ((JAXBElement<ExtrinsicObjectType>) parseXml(xml, "oasis.names.tc.ebxml_regrep.xsd.rim._3")).getValue();
    }

    public ExtrinsicObjectType getCannedExtrinsicObject() throws Exception {
        refresh(CANNED_EXTRINSIC_OBJECT);
        return readExtrinsicObjectFromString((String) getStringPropertyConfig(CANNED_EXTRINSIC_OBJECT));
    }

    //////// Document Submission ////////
    private RegistryResponseType readDocumentSubmissionResponseFromString(String xml) {
        return ((JAXBElement<RegistryResponseType>) parseXml(xml, "oasis.names.tc.ebxml_regrep.xsd.rs._3"))
            .getValue();
    }

    public RegistryResponseType getCannedDocumentSubmissionResponse() throws Exception {
        refresh(CANNED_DS_RESPONSE);
        return readDocumentSubmissionResponseFromString((String) getStringPropertyConfig(CANNED_DS_RESPONSE));
    }

    //////// Document Submission Deferred Response ////////
    private XDRAcknowledgementType readDocumentSubmissionDeferredResponseFromString(String xml) {
        return ((JAXBElement<XDRAcknowledgementType>) parseXml(xml, "gov.hhs.healthit.nhin")).getValue();
    }

    public XDRAcknowledgementType getCannedDocumentSubmissionDeferredResponse() throws Exception {
        refresh(CANNED_DS_DEFERRED_RESPONSE);
        return readDocumentSubmissionDeferredResponseFromString((String) getStringPropertyConfig(CANNED_DS_DEFERRED_RESPONSE));
    }

    //////// Document Submission Deferred Request ////////
    private XDRAcknowledgementType readDocumentSubmissionDeferredRequestFromString(String xml) {
        return ((JAXBElement<XDRAcknowledgementType>) parseXml(xml, "gov.hhs.healthit.nhin")).getValue();
    }

    public XDRAcknowledgementType getCannedDocumentSubmissionDeferredRequest() throws Exception {
        refresh(CANNED_DS_DEFERRED_REQUEST);
        return readDocumentSubmissionDeferredRequestFromString((String) getStringPropertyConfig(CANNED_DS_DEFERRED_REQUEST));
    }

    //////// Patient Correlation ///////
    private RetrievePatientCorrelationsResponseType readPatientCorrelationResponseFromString(String xml) {
        return ((JAXBElement<RetrievePatientCorrelationsResponseType>) parseXml(xml, "org.hl7.v3")).getValue();
    }

    public RetrievePatientCorrelationsResponseType getCannedPatientCorrelationResponse() throws Exception {
        refresh(CANNED_PC_RESPONSE);
        return readPatientCorrelationResponseFromString((String) getStringPropertyConfig(CANNED_PC_RESPONSE));
    }

    
    private String readXmlPropsFromFile(String m_sPropertyFile) throws IOException {
        return FileUtils.readFileToString(new File(propFileLocation + propFileMap.get(m_sPropertyFile)), "UTF-8");
    }

    /**
     *
     * @param propFileName
     * @throws Exception
     */
    private void refresh(String propFileName) throws Exception {
        if (checkRefresh(propFileName)) {
            updatePropertyConfigMap(propFileName, readXmlPropsFromFile(propFileName));
            updateLastModified(propFileName);
        }
    }

    /**
     *
     * @param xml
     * @param namespace
     * @throws Exception
     */
    private Object parseXml(String xml, String namespace) {
        Object response = null;
        try {
            response = new JAXBXMLUtils().parseXML(xml, namespace);
        } catch (JAXBException ex) {
            LOG.error("Exception parsing XML file: {}", ex.getLocalizedMessage(), ex);
        }
        return response;
    }

    /**
     * Checks against the property map if the file has been modified since last read
     *
     * @param m_sPropertyFile property file name
     * @return true if file has not been read or file has been updated
     * @throws Exception
     */
    private boolean checkRefresh(String m_sPropertyFile) throws Exception {
        LOG.debug("entering checkRefresh");

        // TODO: Verify this logic.  Always return true if the config hasn't been loaded yet.
        // TODO: Pre-seed the data so we can never get this far??
        if (!propertyConfigMap.containsKey(m_sPropertyFile)) {
            return true;
        }

        // TODO: Verify this logic.  If config has been loaded but refresh is off, return false.
        if (!refreshFiles) {
            return false;
        }

        // If refresh is on, reload:
        File file = new File(propFileLocation + propFileMap.get(m_sPropertyFile));
        if (file.exists()) {
            // Get the last modification information.
            Long lLastModified = file.lastModified();

            // Create a new date object and pass last modified information to the date object.
            Date dLastModified = new Date(lLastModified);
            Date recLastModified = getLastModified(m_sPropertyFile);

            //Needs to create/refresh property Map if not done yet or file has changed
            LOG.debug("returning from checkRefresh");
            return (recLastModified == null || recLastModified.before(dLastModified));
        } else {
            throw new Exception(propFileMap.get(m_sPropertyFile) + " does not exist at location: "
                + propFileLocation + propFileMap.get(m_sPropertyFile));
        }
    }

    private Date getLastModified(String propertyFile) {
        if (propertyDateModifiedMap == null) {
            return null;
        }
        return propertyDateModifiedMap.get(propertyFile);
    }

    private void updateLastModified(String propertyFile) {
        if (propertyDateModifiedMap == null) {
            propertyDateModifiedMap = new ConcurrentHashMap<>();
        }

        propertyDateModifiedMap.put(propertyFile, new Date());
    }

    private String getStringPropertyConfig(String property) {
        if (propertyConfigMap == null) {
            return null;
        }
        return (String) propertyConfigMap.get(property);
    }

    private LoadTestData getLoadTestPropertyConfig(String property) {
        if (propertyConfigMap == null) {
            return null;
        }
        return (LoadTestData) propertyConfigMap.get(property);
    }

    private void updatePropertyConfigMap(String property, Object propertyConfig) {
        if (propertyConfigMap == null) {
            propertyConfigMap = new ConcurrentHashMap<>();
        }

        propertyConfigMap.put(property, propertyConfig);
    }
}
