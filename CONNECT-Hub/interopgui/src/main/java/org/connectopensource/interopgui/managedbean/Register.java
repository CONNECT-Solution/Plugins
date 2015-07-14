package org.connectopensource.interopgui.managedbean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;

import org.apache.commons.lang.StringUtils;
import org.connectopensource.interopgui.controller.RegisterController;
import org.connectopensource.interopgui.dataobject.DocumentInfo;
import org.connectopensource.interopgui.dataobject.PatientInfo;
import org.connectopensource.interopgui.view.Certificate;
import org.connectopensource.interopgui.view.DirectCertificate;
import org.connectopensource.interopgui.view.DirectEndpoint;
import org.connectopensource.interopgui.view.Endpoint;
import org.connectopensource.interopgui.view.Organization;
import org.connectopensource.interopgui.view.impl.CertificateImpl;
import org.connectopensource.interopgui.view.impl.DirectCertificateImpl;
import org.connectopensource.interopgui.view.impl.DirectEndpointImpl;
import org.connectopensource.interopgui.view.impl.EndpointImpl;

/**
 * @author msw
 * 
 */
@ManagedBean
public class Register {

    public enum CertificateType {
        CERT_REQ, CERT_TO_TRUST
    }

    private String alert = StringUtils.EMPTY;
    private String orgId = StringUtils.EMPTY;
    private String hcid = StringUtils.EMPTY;
    private String orgName = null;

    private List<Endpoint> endpoints = null;
    private List<PatientInfo> patients = null;
    private List<DocumentInfo> documents = null;
    private List<DirectEndpoint> directEndpoints = null;

    private Certificate certificate = null;
    private DirectCertificate directCertificate = null;
    private PatientInfo patient = null;
    private DocumentInfo document = null;
    private EndpointImpl endpoint = null;
    private DirectEndpoint currentDirectEndpoint = null;
    
    public Register() {        
        endpoints = new ArrayList<Endpoint>();        
        certificate = new CertificateImpl();
        directCertificate = new DirectCertificateImpl();
        patients = new ArrayList<PatientInfo>();
        documents = new ArrayList<DocumentInfo>();
        directEndpoints = new ArrayList<DirectEndpoint>();        
        patient = new PatientInfo();
        document = new DocumentInfo();
        endpoint = new EndpointImpl(null,null,"https://");
        currentDirectEndpoint = new DirectEndpointImpl();
    }
    
    /**
     * This method needs to be kicked off in a pre-render view event 
     */
    public void loadDetail() {
        Map<String, Object> sessionMap = null;
        
        try {
            sessionMap = getSessionMap();
            orgId = (String) sessionMap.get("organizationId");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!StringUtils.isBlank(orgId)) {
            System.out.println("OrgId:" + orgId + ":");
            loadOrganization(orgId);
            sessionMap.put("organizationId", StringUtils.EMPTY);
        } else {
            orgId = StringUtils.EMPTY;
        }
    }

    /**
     * @param orgId
     */
    private void loadOrganization(String orgId) {

        RegisterController controller = new RegisterController();
        Organization org = controller.retrieveOrganization(orgId);
        
        orgId = org.getOrgId();
        hcid = org.getHCID();
        orgName = org.getOrgName();
        
        endpoints = org.getEndpoints();       
        certificate = org.getCertificate();
        directCertificate = org.getDirectCertificate();
        System.out.println("trust bundle:" + directCertificate.getTrustBundleUrl());
        patients = org.getPatients();
        documents = org.getDocuments();
        directEndpoints = org.getDirectEndpoints();
    }

    /**
     * @return the hcid
     */
    public String getHcid() {
        return hcid;
    }

    /**
     * @param hcid the hcid to set
     */
    public void setHcid(String hcid) {
        System.out.println("hcid: " + hcid);
        this.hcid = hcid;
    }

    /**
     * @return the orgName
     */
    public String getOrgName() {
        return orgName;
    }

    /**
     * @param orgName the orgName to set
     */
    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    /**
     * @return endpoint
     */
    public Endpoint getEndpoint() {
        return endpoint;
    }

    /**
     * @param endpoint to be set
     */
    public void setEndpoint(EndpointImpl endpoint) {
        this.endpoint = endpoint ;
    }

    /**
     * @return the certificate
     */
    public Certificate getCertificate() {
        return certificate;
    }
    
    /**
     * @return the certificate
     */
    public Certificate getDisplayCertificate() {
        return certificate;
    }
    
    /**
     * @return the certificate
     */
    public void setDisplayCertificate(Certificate cert) {
        
    }

    /**
     * @param certificate the certificate to set
     */
    public void setCertificate(Certificate certificate) {
        this.certificate = certificate;
    }
    
    /**
     * @return the direct certificate
     */
    public DirectCertificate getDirectCertificate() {
        return directCertificate;
    }

    /**
     * @param certificate the direct certificate to set
     */
    public void setCertificate(DirectCertificate directCertificate) {
        this.directCertificate = directCertificate;
    }
    
    /**
     * @return the direct certificate
     */
    public DirectCertificate getDisplayDirectCertificate() {
        return directCertificate;
    }

    /**
     * @param certificate the direct certificate to set
     */
    public void setDisplayCertificate(DirectCertificate directCertificate) {
        
    }

    /**
     * @return the patients
     */
    public List<PatientInfo> getPatients() {
        return patients;
    }

    /**
     * @return the patients
     */
    public List<DocumentInfo> getDocuments() {
        return documents;
    }

    /**
     * @return the directEndpoints
     */
    public List<DirectEndpoint> getDirectEndpoints() {
        return directEndpoints;
    }

    public DirectEndpoint getDirectEndpoint() {
        return currentDirectEndpoint;
    }

    public void setDirectEndpoint(DirectEndpoint endpoint) {
        currentDirectEndpoint = endpoint;
    }

    /**
     * @param directEndpoints the directEndpoints to set
     */
    public void setDirectEndpoints(List<DirectEndpoint> directEndpoints) {
        this.directEndpoints = directEndpoints;
    }

    /**
     * @return the endpoints
     */
    public List<Endpoint> getEndpoints() {
        return endpoints;
    }

    public String saveInfo() {

        Long id = saveOrgInfo();

        getSessionMap().put("organizationId", String.valueOf(id));

        // redirect back so we can gather more data from the registration form (endpoints, patients, etc...)
        alert = "Organization information saved. Now add patients, documents, endpoints and direct endpoints.";
        return "RegisterInformation?faces-direct=true";
    }
    
    private Long saveOrgInfo() {
        RegisterController impl = new RegisterController();
        return impl.saveInfo(orgId, hcid, orgName, certificate, directCertificate);
    }

    /**
     * Add a patient.
     * 
     * @return route for screen flow destination
     */
    public String addPatient() {
        if (StringUtils.isBlank(orgId)) {
            orgId = saveOrgInfo().toString();
        }
          
        RegisterController registerController = new RegisterController();
        registerController.savePatient(patient, orgId);
        
        try {
            System.out.println("saving patient: " + patient);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        getSessionMap().put("organizationId", String.valueOf(orgId));

        alert = "Patient added.";
        return "RegisterInformation?faces-direct=true";
        
    }
    
    /**
     * Add a document.
     * 
     * @return route for screen flow destination
     */
    public String addEndpoint() {

        RegisterController registerController = new RegisterController();
        registerController.saveEndpoint(endpoint, orgId);
        try {
            System.out.println("saving endpoint: " + endpoint);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        getSessionMap().put("organizationId", String.valueOf(orgId));

        alert = "Endpoint added.";
        return "RegisterInformation?faces-direct=true";        
    }

    /**
     * Add a document.
     * 
     * @return route for screen flow destination
     */
    public String addDocument() {
        if (StringUtils.isBlank(orgId)) {
            orgId = saveOrgInfo().toString();
        }
        
        RegisterController registerController = new RegisterController();
        registerController.saveDocument(document, orgId);
        
        try {
            System.out.println("saving document: " + document);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        getSessionMap().put("organizationId", String.valueOf(orgId));

        alert = "Document added.";
        return "RegisterInformation?faces-direct=true";
        
    }
    /*
     * Add a patient.
     * 
     * @return route for screen flow destination
     */
    public String addDirectEndpoint() {
        //if (StringUtils.isBlank(orgId)) {
            orgId = saveOrgInfo().toString();
        //}
        
        RegisterController registerController = new RegisterController();
        registerController.saveDirectEndpoint(currentDirectEndpoint, orgId);

        try {
            System.out.println("saving direct endpoint: " + currentDirectEndpoint);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getSessionMap().put("organizationId", String.valueOf(orgId));

        alert = "Direct Endpoint added.";
        return "RegisterInformation?faces-direct=true";

    }

    public String back() {
        return "ListInformation?faces-direct=true";
    }

    /**
     * @return the orgId
     */
    public String getOrgId() {
        return orgId;
    }

    /**
     * @param orgId the orgId to set
     */
    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    /**
     * @return the alert
     */
    public String getAlert() {
        return alert;
    }

    /**
     * @return the patient
     */
    public PatientInfo getPatient() {
        return patient;
    }

    /**
     * @param patient the patient to set
     */
    public void setPatient(PatientInfo patient) {
        this.patient = patient;
    }

    /**
     * @return the document
     */
    public DocumentInfo getDocument() {
        return document;
    }

    /**
     * @param document the document to set
     */
    public void setDocument(DocumentInfo document) {
        this.document = document;
    }    
        
    private Map<String, Object> getSessionMap() {
        FacesContext context = FacesContext.getCurrentInstance();
        return context.getExternalContext().getSessionMap();
    }
    
}
