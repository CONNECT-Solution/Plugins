package gov.hhs.fha.nhinc.loadtest;

import gov.hhs.fha.nhinc.properties.PropertyAccessor;
import gov.hhs.fha.nhinc.util.jaxb.JAXBXMLUtils;
import gov.hhs.healthit.nhin.XDRAcknowledgementType;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.JAXBException;

import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryResponse;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ExtrinsicObjectType;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryResponseType;

import org.apache.log4j.Logger;
import org.hl7.v3.MCCIIN000002UV01;
import org.hl7.v3.PRPAIN201306UV02;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.basic.DateConverter;
import javax.xml.bind.JAXBElement;
import org.caqh.soap.wsdl.corerule2_2_0.COREEnvelopeBatchSubmissionResponse;
import org.caqh.soap.wsdl.corerule2_2_0.COREEnvelopeRealTimeResponse;
import org.hl7.v3.RetrievePatientCorrelationsResponseType;

public class DataManager {

    private static DataManager instance = null;
    private String propertyFileLocation = null;
    private Map<String, Date> PropertyDateModifiedMap = null;
    private Map<String, Object> PropertyConfigMap = null;
    private Map<String, String> PropertyFileMap = null;
    public static String LOAD_TEST_DATA = "LoadTestData";
    public static String CANNED_PD_RESPONSE = "PatientDiscoveryResponse";
    public static String CANNED_PD_DEFERRED_RESPONSE = "PatientDiscoveryDeferredResponse";
    public static String CANNED_PD_DEFERRED_REQUEST = "PatientDiscoveryDeferredRequest";
    public static String CANNED_DS_RESPONSE = "DocumentSubmissionResponse";
    public static String CANNED_DS_DEFERRED_RESPONSE = "DocumentSubmissionDeferredResponse";
    public static String CANNED_DS_DEFERRED_REQUEST = "DocumentSubmissionDeferredRequest";
    public static String CANNED_PC_RESPONSE = "PatientCorrelationResponse";
    public static String CANNED_QD_RESPONSE = "DocumentQueryResponse";
    public static String CANNED_EXTRINSIC_OBJECT = "ExtrinsicObject";
    public static String CANNED_CORE_X12DSGenericBatchRequest = "CORE_X12DSGenericBatchRequest";
    public static String CANNED_CORE_X12DSGenericBatchResponse = "CORE_X12DSGenericBatchResponse";
    public static String CANNED_CORE_X12DSRealTime = "CORE_X12DSRealTime";
    /*public static String QUALIFIED_SUBMITTERS = "QualifiedSubmitters";
     public static String INTERNAL_ENDPOINTS = "InternalEndpoints";
     public static String SCHEMA_VALIDATOR_MAPPINGS = "SchemaValidatorMappings";*/
    private static final Logger LOG = Logger.getLogger(DataManager.class);

    public static synchronized DataManager getInstance() {

        try {

            if (instance == null) {
                instance = new DataManager();
            }
        } catch (Exception exc) {
            LOG.error("exception:", exc);
        }

        return instance;
    }

    private DataManager() throws Exception {
        LOG.debug("entering DataManager");

        PropertyFileMap = new HashMap<String, String>();
        PropertyFileMap.put(LOAD_TEST_DATA, "loadTestData.xml");
        PropertyFileMap.put(CANNED_PD_RESPONSE, "CannedPatientDiscoveryResponse.xml");
        PropertyFileMap.put(CANNED_PD_DEFERRED_RESPONSE, "CannedPatientDiscoveryDeferredResponse.xml");
        PropertyFileMap.put(CANNED_PD_DEFERRED_REQUEST, "CannedPatientDiscoveryDeferredRequest.xml");

        PropertyFileMap.put(CANNED_DS_RESPONSE, "CannedDocumentSubmissionResponse.xml");
        PropertyFileMap.put(CANNED_DS_DEFERRED_RESPONSE, "CannedDocumentSubmissionDeferredResponse.xml");
        PropertyFileMap.put(CANNED_DS_DEFERRED_REQUEST, "CannedDocumentSubmissionDeferredRequest.xml");

        PropertyFileMap.put(CANNED_QD_RESPONSE, "CannedDocumentQueryResponse.xml");
        PropertyFileMap.put(CANNED_EXTRINSIC_OBJECT, "CannedExtrinsicObject.xml");
        PropertyFileMap.put(CANNED_PC_RESPONSE, "CannedPatientCorrelationResponse.xml");
        PropertyFileMap.put(CANNED_CORE_X12DSGenericBatchRequest, "CannedCORE_X12DSGenericBatch.xml");
        PropertyFileMap.put(CANNED_CORE_X12DSGenericBatchResponse, "CannedCORE_X12DSGenericBatch.xml");

        PropertyFileMap.put(CANNED_CORE_X12DSRealTime, "CannedCORE_X12DSRealTime.xml");

        propertyFileLocation = PropertyAccessor.getInstance().getPropertyFileLocation();
        if (propertyFileLocation == null) {
            throw new Exception("unable to attain property file directory location.");
        }

        if (!propertyFileLocation.endsWith(File.separator)) {
            propertyFileLocation += File.separator;
        }

        setLoadTestData(((LoadTestData) getLoadTestData()));

        LOG.debug("returning from DataManager");
    }

    /* ****************** Load Test Data *************************/
    private void setLoadTestData(LoadTestData ltd) {
        LOG.debug("entering setLoadTestData");
        updatePropertyConfigMap(LOAD_TEST_DATA, ltd);
        LOG.debug("returning from setLoadTestData");
    }

    private void readLoadTestDataFromString(String xml) {
        LOG.debug("entering readLoadTestDataFromString");
        XStream xstream = new XStream();
        xstream.autodetectAnnotations(true);
        xstream.alias("LoadTestData", LoadTestData.class);
        xstream.alias("Patient", Patient.class);
        xstream.alias("DocumentMetaData", DocumentMetaData.class);
        xstream.alias("Document", Document.class);
        xstream.registerConverter(new DateConverter());
        //xstream.registerConverter(new MapConverter());
        LoadTestData ltd = (LoadTestData) xstream.fromXML(xml);
        setLoadTestData(ltd);
        LOG.debug("returning from readLoadTestDataFromString");
    }

    public LoadTestData getLoadTestData() throws Exception {
        LOG.debug("entering getLoadTestData");
        if (checkRefresh(LOAD_TEST_DATA)) {
            String xml = getXMLProperties(LOAD_TEST_DATA);
            readLoadTestDataFromString(xml);
            updateLastModified(LOAD_TEST_DATA);
        }

        LOG.debug("returning from getLoadTestData");
        return (LoadTestData) getPropertyConfig(LOAD_TEST_DATA);
    }

    /* ****** Patient Discovery ***** */
    private PRPAIN201306UV02 readPatientDiscoveryResponseFromString(String xml) {
        PRPAIN201306UV02 prpa = null;
        try {
            JAXBXMLUtils jaxb = new JAXBXMLUtils();
            prpa = ((PRPAIN201306UV02) jaxb.parseXML(xml, "org.hl7.v3"));
            LOG.debug("returning from readPatientDiscoveryResponseFromString");
        } catch (JAXBException ex) {
            LOG.error("Exception parsing XML file: " + ex.getLocalizedMessage(), ex);
        }
        return prpa;
    }

    public PRPAIN201306UV02 getCannedPatientDiscoveryResponse() throws Exception {
        LOG.debug("entering getCannedPatientDiscoveryResponse");
        if (checkRefresh(CANNED_PD_RESPONSE)) {
            String xml = getXMLProperties(CANNED_PD_RESPONSE);
            updatePropertyConfigMap(CANNED_PD_RESPONSE, xml);
            updateLastModified(CANNED_PD_RESPONSE);
        }

        LOG.debug("returning from getCannedPatientDiscoveryResponse");
        return readPatientDiscoveryResponseFromString((String) getPropertyConfig(CANNED_PD_RESPONSE));
    }

    public String getCannedPatientDiscoveryResponseString() throws Exception {
        LOG.debug("entering getCannedPatientDiscoveryResponseString");
        if (checkRefresh(CANNED_PD_RESPONSE)) {
            String xml = getXMLProperties(CANNED_PD_RESPONSE);
            updatePropertyConfigMap(CANNED_PD_RESPONSE, xml);
            updateLastModified(CANNED_PD_RESPONSE);
        }

        LOG.debug("returning from getCannedPatientDiscoveryResponseString");
        return (String) getPropertyConfig(CANNED_PD_RESPONSE);
    }

    /* ****** Patient Discovery Deferred Response ***** */
    private MCCIIN000002UV01 readPatientDiscoveryDeferredResponseFromString(String xml) {
        MCCIIN000002UV01 prpa = null;
        try {
            JAXBXMLUtils jaxb = new JAXBXMLUtils();
            prpa = ((MCCIIN000002UV01) jaxb.parseXML(xml, "org.hl7.v3"));
            LOG.debug("returning from readPatientDiscoveryResponseFromString");
        } catch (JAXBException ex) {
            LOG.error("Exception parsing XML file: " + ex.getLocalizedMessage(), ex);

        }
        return prpa;
    }

    public MCCIIN000002UV01 getCannedPatientDiscoveryDeferredResponse() throws Exception {
        LOG.debug("entering getCannedPatientDiscoveryResponse");
        if (checkRefresh(CANNED_PD_DEFERRED_RESPONSE)) {
            String xml = getXMLProperties(CANNED_PD_DEFERRED_RESPONSE);
            updatePropertyConfigMap(CANNED_PD_DEFERRED_RESPONSE, xml);
            updateLastModified(CANNED_PD_DEFERRED_RESPONSE);
        }

        LOG.debug("returning from getCannedPatientDiscoveryResponse");
        return readPatientDiscoveryDeferredResponseFromString((String) getPropertyConfig(CANNED_PD_DEFERRED_RESPONSE));
    }

    /* ****** Patient Discovery Deferred Request ***** */
    private MCCIIN000002UV01 readPatientDiscoveryDeferredRequestFromString(String xml) {
        MCCIIN000002UV01 prpa = null;
        try {
            JAXBXMLUtils jaxb = new JAXBXMLUtils();
            prpa = ((MCCIIN000002UV01) jaxb.parseXML(xml, "org.hl7.v3"));
            LOG.debug("returning from readPatientDiscoveryDeferredRequestFromString");
        } catch (JAXBException ex) {
            LOG.error("Exception parsing XML file: " + ex.getLocalizedMessage(), ex);

        }
        return prpa;
    }

    public MCCIIN000002UV01 getCannedPatientDiscoveryDeferredRequest() throws Exception {
        LOG.debug("entering getCannedPatientDiscoveryResponse");
        if (checkRefresh(CANNED_PD_DEFERRED_REQUEST)) {
            String xml = getXMLProperties(CANNED_PD_DEFERRED_REQUEST);
            updatePropertyConfigMap(CANNED_PD_DEFERRED_REQUEST, xml);
            updateLastModified(CANNED_PD_DEFERRED_REQUEST);
        }

        LOG.debug("returning from getCannedPatientDiscoveryResponse");
        return readPatientDiscoveryDeferredRequestFromString((String) getPropertyConfig(CANNED_PD_DEFERRED_REQUEST));
    }

    /* ****** Core X12 Batch Response  ***** */
    private COREEnvelopeBatchSubmissionResponse readCoreX12BatchResponseFromString(String xml) {
        COREEnvelopeBatchSubmissionResponse ahqr = null;
        try {
            JAXBXMLUtils jaxb = new JAXBXMLUtils();
            ahqr = ((COREEnvelopeBatchSubmissionResponse) jaxb.parseXML(xml, "org.caqh.soap.wsdl.corerule2_2_0"));
            LOG.debug("returning from readCOREEnvelopeRealTimeResponseFromString");
        } catch (JAXBException ex) {
            LOG.error("Exception parsing XML file: " + ex.getLocalizedMessage(), ex);

        }
        return ahqr;
    }

    // this method must return a new object for every call, therefore the xml will be stored
    //(only file io on the first call), and the xml will be parsed everytime.
    public COREEnvelopeBatchSubmissionResponse getCannedCoreX12BatchResponse() throws Exception {
        LOG.debug("entering getCannedCoreX12BatchResponse");
        if (checkRefresh(CANNED_CORE_X12DSGenericBatchResponse)) {
            String xml = getXMLProperties(CANNED_CORE_X12DSGenericBatchResponse);
            updatePropertyConfigMap(CANNED_CORE_X12DSGenericBatchResponse, xml);
            updateLastModified(CANNED_CORE_X12DSGenericBatchResponse);
        }

        LOG.debug("returning from getCannedCoreX12BatchResponse");
        return readCoreX12BatchResponseFromString((String) getPropertyConfig(CANNED_CORE_X12DSGenericBatchResponse));
    }

    /* ****** Core X12 Batch Request  ***** */
    private COREEnvelopeBatchSubmissionResponse readCoreX12BatchRequestFromString(String xml) {
        COREEnvelopeBatchSubmissionResponse ahqr = null;
        try {
            JAXBXMLUtils jaxb = new JAXBXMLUtils();
            ahqr = ((COREEnvelopeBatchSubmissionResponse) jaxb.parseXML(xml, "org.caqh.soap.wsdl.corerule2_2_0"));
            LOG.debug("returning from readCOREEnvelopeRealTimeResponseFromString");
        } catch (JAXBException ex) {
            LOG.error("Exception parsing XML file: " + ex.getLocalizedMessage(), ex);

        }
        return ahqr;
    }

    // this method must return a new object for every call, therefore the xml will be stored
    //(only file io on the first call), and the xml will be parsed everytime.
    public COREEnvelopeBatchSubmissionResponse getCannedCoreX12BatchRequest() throws Exception {
        LOG.debug("entering getCannedCoreX12BatchResponse");
        if (checkRefresh(CANNED_CORE_X12DSGenericBatchRequest)) {
            String xml = getXMLProperties(CANNED_CORE_X12DSGenericBatchRequest);
            updatePropertyConfigMap(CANNED_CORE_X12DSGenericBatchRequest, xml);
            updateLastModified(CANNED_CORE_X12DSGenericBatchRequest);
        }

        LOG.debug("returning from getCannedCoreX12BatchResponse");
        return readCoreX12BatchRequestFromString((String) getPropertyConfig(CANNED_CORE_X12DSGenericBatchRequest));
    }


    /* ****** Core X12 Real Time  ***** */
    private COREEnvelopeRealTimeResponse readCoreX12RealTimeFromString(String xml) {
        COREEnvelopeRealTimeResponse ahqr = null;
        try {
            JAXBXMLUtils jaxb = new JAXBXMLUtils();
            ahqr = ((COREEnvelopeRealTimeResponse) jaxb.parseXML(xml, "org.caqh.soap.wsdl.corerule2_2_0"));
            LOG.debug("returning from readCOREEnvelopeRealTimeResponseFromString");
        } catch (JAXBException ex) {
            LOG.error("Exception parsing XML file: " + ex.getLocalizedMessage(), ex);

        }
        return ahqr;
    }

    // this method must return a new object for every call, therefore the xml will be stored
    //(only file io on the first call), and the xml will be parsed everytime.
    public COREEnvelopeRealTimeResponse getCannedCoreX12RealTime() throws Exception {
        LOG.debug("entering getCannedCoreX12RealTime");
        if (checkRefresh(CANNED_CORE_X12DSRealTime)) {
            String xml = getXMLProperties(CANNED_CORE_X12DSRealTime);
            updatePropertyConfigMap(CANNED_CORE_X12DSRealTime, xml);
            updateLastModified(CANNED_CORE_X12DSRealTime);
        }

        LOG.debug("returning from getCannedCoreX12RealTime");
        return readCoreX12RealTimeFromString((String) getPropertyConfig(CANNED_CORE_X12DSRealTime));
    }

    /* ****** Document Query ***** */
    private AdhocQueryResponse readDocumentQueryResponseFromString(String xml) {
        AdhocQueryResponse ahqr = null;
        try {
            JAXBXMLUtils jaxb = new JAXBXMLUtils();
            ahqr = ((AdhocQueryResponse) jaxb.parseXML(xml, "oasis.names.tc.ebxml_regrep.xsd.query._3"));
            LOG.debug("returning from readPatientDiscoveryResponseFromString");
        } catch (JAXBException ex) {
            LOG.error("Exception parsing XML file: " + ex.getLocalizedMessage(), ex);

        }
        return ahqr;
    }

    // this method must return a new object for every call, therefore the xml will be stored (only file io on the first call), and the xml will be parsed everytime.
    public AdhocQueryResponse getCannedDocumentQueryResponse() throws Exception {
        LOG.debug("entering getCannedDocumentQueryResponse");
        if (checkRefresh(CANNED_QD_RESPONSE)) {
            String xml = getXMLProperties(CANNED_QD_RESPONSE);
            updatePropertyConfigMap(CANNED_QD_RESPONSE, xml);
            updateLastModified(CANNED_QD_RESPONSE);
        }

        LOG.debug("returning from getCannedDocumentQueryResponse");
        return readDocumentQueryResponseFromString((String) getPropertyConfig(CANNED_QD_RESPONSE));
    }

    /* ****** Document Retrieve ***** */
    private ExtrinsicObjectType readExtrinsicObjectFromString(String xml) {
        ExtrinsicObjectType eo = null;
        try {
            JAXBXMLUtils jaxb = new JAXBXMLUtils();
            eo = ((JAXBElement<ExtrinsicObjectType>) jaxb.parseXML(xml, "oasis.names.tc.ebxml_regrep.xsd.rim._3")).getValue();
            LOG.debug("returning from readExtrinsicObjectFromString");
        } catch (JAXBException ex) {
            LOG.error("Exception parsing XML file: " + ex.getLocalizedMessage(), ex);

        }
        return eo;
    }

    // this method must return a new object for every call, therefore the xml will be stored (only file io on the first call), and the xml will be parsed everytime.
    public ExtrinsicObjectType getCannedExtrinsicObject() throws Exception {
        LOG.debug("entering getCannedExtrinsicObject");
        if (checkRefresh(CANNED_EXTRINSIC_OBJECT)) {
            String xml = getXMLProperties(CANNED_EXTRINSIC_OBJECT);
            updatePropertyConfigMap(CANNED_EXTRINSIC_OBJECT, xml);
            updateLastModified(CANNED_EXTRINSIC_OBJECT);
        }

        LOG.debug("returning from getCannedExtrinsicObject");
        return readExtrinsicObjectFromString((String) getPropertyConfig(CANNED_EXTRINSIC_OBJECT));
    }

    /* ****** Document Submission ***** */
    private RegistryResponseType readDocumentSubmissionResponseFromString(String xml) {
        RegistryResponseType response = null;
        try {
            JAXBXMLUtils jaxb = new JAXBXMLUtils();
            response = ((JAXBElement<RegistryResponseType>) jaxb.parseXML(xml, "oasis.names.tc.ebxml_regrep.xsd.rs._3")).getValue();
            LOG.debug("returning from readPatientDiscoveryResponseFromString");
        } catch (JAXBException ex) {
            LOG.error("Exception parsing XML file: " + ex.getLocalizedMessage(), ex);

        }
        return response;
    }

    // this method must return a new object for every call, therefore the xml will be stored (only file io on the first call), and the xml will be parsed everytime.
    public RegistryResponseType getCannedDocumentSubmissionResponse() throws Exception {
        LOG.debug("entering getCannedDocumentQueryResponse");
        if (checkRefresh(CANNED_DS_RESPONSE)) {
            String xml = getXMLProperties(CANNED_DS_RESPONSE);
            updatePropertyConfigMap(CANNED_DS_RESPONSE, xml);
            updateLastModified(CANNED_DS_RESPONSE);
        }

        LOG.debug("returning from getCannedDocumentQueryResponse");
        return readDocumentSubmissionResponseFromString((String) getPropertyConfig(CANNED_DS_RESPONSE));
    }

    /* ****** Document Submission Deferred Response ***** */
    private XDRAcknowledgementType readDocumentSubmissionDeferredResponseFromString(String xml) {
        XDRAcknowledgementType response = null;
        try {
            JAXBXMLUtils jaxb = new JAXBXMLUtils();
            response = ((JAXBElement<XDRAcknowledgementType>) jaxb.parseXML(xml, "gov.hhs.healthit.nhin")).getValue();
            LOG.debug("returning from readPatientDiscoveryResponseFromString");
        } catch (JAXBException ex) {
            LOG.error("Exception parsing XML file: " + ex.getLocalizedMessage(), ex);

        }
        return response;
    }

    // this method must return a new object for every call, therefore the xml will be stored (only file io on the first call), and the xml will be parsed everytime.
    public XDRAcknowledgementType getCannedDocumentSubmissionDeferredResponse() throws Exception {
        LOG.debug("entering getCannedDocumentQueryResponse");
        if (checkRefresh(CANNED_DS_DEFERRED_RESPONSE)) {
            String xml = getXMLProperties(CANNED_DS_DEFERRED_RESPONSE);
            updatePropertyConfigMap(CANNED_DS_DEFERRED_RESPONSE, xml);
            updateLastModified(CANNED_DS_DEFERRED_RESPONSE);
        }

        LOG.debug("returning from getCannedDocumentQueryResponse");
        return readDocumentSubmissionDeferredResponseFromString((String) getPropertyConfig(CANNED_DS_DEFERRED_RESPONSE));
    }

    /* ****** Document Submission Deferred Request ***** */
    private XDRAcknowledgementType readDocumentSubmissionDeferredRequestFromString(String xml) {
        XDRAcknowledgementType response = null;
        try {
            JAXBXMLUtils jaxb = new JAXBXMLUtils();
            response = ((JAXBElement<XDRAcknowledgementType>) jaxb.parseXML(xml, "gov.hhs.healthit.nhin")).getValue();
            LOG.debug("returning from readPatientDiscoveryResponseFromString");
        } catch (JAXBException ex) {
            LOG.error("Exception parsing XML file: " + ex.getLocalizedMessage(), ex);

        }
        return response;
    }

    // this method must return a new object for every call, therefore the xml will be stored (only file io on the first call), and the xml will be parsed everytime.
    public XDRAcknowledgementType getCannedDocumentSubmissionDeferredRequest() throws Exception {
        LOG.debug("entering getCannedDocumentQueryResponse");
        if (checkRefresh(CANNED_DS_DEFERRED_REQUEST)) {
            String xml = getXMLProperties(CANNED_DS_DEFERRED_REQUEST);
            updatePropertyConfigMap(CANNED_DS_DEFERRED_REQUEST, xml);
            updateLastModified(CANNED_DS_DEFERRED_REQUEST);
        }

        LOG.debug("returning from getCannedDocumentQueryResponse");
        return readDocumentSubmissionDeferredRequestFromString((String) getPropertyConfig(CANNED_DS_DEFERRED_REQUEST));
    }

    /**
     * ** Patient Correlation ***
     */
    private RetrievePatientCorrelationsResponseType readPatientCorrelationResponseFromString(String xml) {
        RetrievePatientCorrelationsResponseType response = null;
        try {
            JAXBXMLUtils jaxb = new JAXBXMLUtils();
            response = ((JAXBElement<RetrievePatientCorrelationsResponseType>) jaxb.parseXML(xml, "org.hl7.v3")).getValue();
            LOG.debug("returning from readPatientCorrelationResponseFromString");
        } catch (JAXBException ex) {
            LOG.error("Exception parsing XML file: " + ex.getLocalizedMessage(), ex);

        }
        return response;
    }

    // this method must return a new object for every call, therefore the xml will be stored (only file io on the first call), and the xml will be parsed everytime.
    public RetrievePatientCorrelationsResponseType getCannedPatientCorrelationResponse() throws Exception {
        LOG.debug("entering getCannedPatientCorrelationResponse");
        if (checkRefresh(CANNED_PC_RESPONSE)) {
            String xml = getXMLProperties(CANNED_PC_RESPONSE);
            updatePropertyConfigMap(CANNED_PC_RESPONSE, xml);
            updateLastModified(CANNED_PC_RESPONSE);
        }

        LOG.debug("returning from getCannedPatientCorrelationResponse");
        return readPatientCorrelationResponseFromString((String) getPropertyConfig(CANNED_PC_RESPONSE));
    }

    //Reads the specified property file as a String
    //Input
    //		String: The Property key (ie QUALIFIED_SUBMITTERS)
    //Output
    //		String: The file's XML read as a string
    //
    private String getXMLProperties(String m_sPropertyFile) {

        LOG.debug("entering getXMLProperties");
        String xml = null;
        try {
            File file = new File(propertyFileLocation + PropertyFileMap.get(m_sPropertyFile));
            if (file.exists()) {
                // Open the file
                FileInputStream fstream = new FileInputStream(file.getAbsolutePath());

                // Get the object of DataInputStream
                DataInputStream in = new DataInputStream(fstream);
                BufferedReader br = new BufferedReader(new InputStreamReader(in));

                //Read File Line By Line
                String line = null;
                while ((line = br.readLine()) != null) {
                    if (xml == null) {
                        xml = "";
                    }
                    xml += line;
                }
                //Close the input stream
                in.close();
            }
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }

        LOG.debug("returning from getXMLProperties");
        return xml;
    }

    //Checks against the property map if the file has been modified since last read
    // Returns boolean
    //		true: file has not been read or file has been updated
    //		false: Properties stored in the mapping are the latest
    //
    private boolean checkRefresh(String m_sPropertyFile) throws Exception {

        LOG.debug("entering checkRefresh");
        // Create an instance of file object.
        File file = new File(propertyFileLocation + PropertyFileMap.get(m_sPropertyFile));
        if (file.exists()) {
            // Get the last modification information.
            Long lLastModified = file.lastModified();
            // Create a new date object and pass last modified information
            // to the date object.
            Date dLastModified = new Date(lLastModified);
            Date recLastModified = getLastModified(m_sPropertyFile);

            //Needs to create/refresh property Map if not done yet or file has changed
            LOG.debug("returning from checkRefresh");
            return (recLastModified == null || recLastModified.before(dLastModified));

        } else {
            throw new Exception(PropertyFileMap.get(m_sPropertyFile) + " does not exist at location: " + propertyFileLocation + PropertyFileMap.get(m_sPropertyFile));
        }
    }

    private Date getLastModified(String propertyFile) {
        if (PropertyDateModifiedMap == null) {
            return null;
        }
        return PropertyDateModifiedMap.get(propertyFile);
    }

    private void updateLastModified(String propertyFile) {
        if (PropertyDateModifiedMap == null) {
            PropertyDateModifiedMap = new HashMap<String, Date>();
        }

        Date now = new Date();
        PropertyDateModifiedMap.put(propertyFile, now);
    }

    private Object getPropertyConfig(String Property) {
        if (PropertyConfigMap == null) {
            return null;
        }
        return PropertyConfigMap.get(Property);
    }

    private void updatePropertyConfigMap(String Property, Object PropertyConfig) {
        if (PropertyConfigMap == null) {
            PropertyConfigMap = new HashMap<String, Object>();
        }

        PropertyConfigMap.put(Property, PropertyConfig);
    }
}
