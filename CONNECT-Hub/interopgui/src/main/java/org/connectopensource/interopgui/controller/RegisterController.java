/**
 * 
 */
package org.connectopensource.interopgui.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.connectopensource.interopgui.dataobject.CertificateInfo;
import org.connectopensource.interopgui.dataobject.DocumentInfo;
import org.connectopensource.interopgui.dataobject.OrganizationInfo;
import org.connectopensource.interopgui.dataobject.PatientInfo;
import org.connectopensource.interopgui.services.CertificateService;
import org.connectopensource.interopgui.services.DataService;
import org.connectopensource.interopgui.services.EndpointService;
import org.connectopensource.interopgui.services.JceCertificateService;
import org.connectopensource.interopgui.services.JpaDataService;
import org.connectopensource.interopgui.services.UddiEndpointService;
import org.connectopensource.interopgui.view.Certificate;
import org.connectopensource.interopgui.view.Certificate.CertificateType;
import org.connectopensource.interopgui.view.DirectCertificate;
import org.connectopensource.interopgui.view.DirectEndpoint;
import org.connectopensource.interopgui.view.Endpoint;
import org.connectopensource.interopgui.view.Organization;
import org.connectopensource.interopgui.view.impl.CertificateImpl;
import org.connectopensource.interopgui.view.impl.DirectCertificateImpl;
import org.connectopensource.interopgui.view.impl.DirectEndpointImpl;
import org.connectopensource.interopgui.view.impl.EndpointImpl;
import org.connectopensource.interopgui.view.impl.OrganizationImpl;

/**
 * @author msw
 * 
 */
public class RegisterController {

    /**
     * @param hcid home community id
     * @param orgName organization name
     * @param cert certificate
     * @return id of the persisted org
     */
    public Long saveInfo(String id, String hcid, String orgName, Certificate cert, DirectCertificate directCert) {
        OrganizationInfo org = null;

        // if id is populated we need to retrieve that OrganizaitonInfo object
        if (!StringUtils.isBlank(id)) {
            org = retrieveOrgInfo(id);
        }

        if (org == null) {
            org = new OrganizationInfo(hcid, orgName);
        }

        CertificateInfo certInfo = new CertificateInfo(cert);
        CertificateInfo directCertInfo = new CertificateInfo(directCert);

        try {
            if (certInfo.getCertBytes() != null) {
                processCertificate(certInfo);
            }

            // processDirectCertificate(directCertInfo);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        org.updateExchangeCert(certInfo);
        org.updateDirectCert(directCertInfo);
        
        System.out.println("cert after processed: " + org.getExchangeCert());
        return saveOrganization(org);
    }

    /**
     * Persist a new patient with an organization.
     * 
     * @param patient information to be persisted
     * @param orgId organization parent for this patient
     */
    public void savePatient(PatientInfo patient, String orgId) {
        DataService service = new JpaDataService();
        service.addPatient(patient, orgId);
    }

    /**
     * Persist a new patient with an organization.
     * 
     * @param document information to be persisted
     * @param orgId organization parent for this patient
     */
    public void saveDocument(DocumentInfo document, String orgId) {
        DataService service = new JpaDataService();
        service.addDocument(document, orgId);
    }

    /**
     * Persist a new endpoint with an organization.
     * 
     * @param document information to be persisted
     * @param orgId organization parent for this patient
     */
    public void saveEndpoint(EndpointImpl endpoint, String orgId) {

        try {
            EndpointService endpointService = new UddiEndpointService();

            OrganizationInfo orgInfo = retrieveOrgInfo(orgId);
            Organization orgView = new OrganizationImpl();
            orgView.setHCID(orgInfo.getHomeCommunityId());
            orgView.setOrgName(orgInfo.getOrgName());
            orgView.setOrgId(orgInfo.getId().toString());

            endpointService.saveEndpoint(orgView, endpoint);
        } catch (Exception e) {
            System.out.println("Could not save endpoint to uddi." + e.getMessage());
            e.printStackTrace();
        }

        DataService service = new JpaDataService();
        service.addEndpoint(endpoint, orgId);
    }

    /**
     * @param cert
     * @throws IOException
     */
    private void processCertificate(CertificateInfo certInfo) throws IOException {

        CertificateService service = new JceCertificateService();
        if (certInfo.getCertType() == CertificateType.CERT) {
            service.trustCertificate(certInfo);
        } else if (certInfo.getCertType() == CertificateType.CERT_REQ) {
            CertificateInfo signedCert = service.signCertificate(certInfo);
            certInfo.setCertBytes(signedCert.getCertBytes());
        }
    }

    /**
     * @param org to be saved.
     * @return id of newly persisted record.
     */
    private Long saveOrganization(OrganizationInfo org) {
        DataService service = new JpaDataService();
        return service.saveData(org);
    }

    /**
     * @param orgId
     * @return
     */
    public Organization retrieveOrganization(String orgId) {

        OrganizationInfo orgInfo = retrieveOrgInfo(orgId);
        Organization orgView = new OrganizationImpl();

        System.out.println("hcid: " + orgInfo.getHomeCommunityId());
        System.out.println("orgname: " + orgInfo.getOrgName());
        orgView.setHCID(orgInfo.getHomeCommunityId());
        orgView.setOrgName(orgInfo.getOrgName());
        orgView.setOrgId(orgInfo.getId().toString());

        Certificate cert = new CertificateImpl(orgInfo.getExchangeCert());
        orgView.setCertificate(cert);
        DirectCertificate directCert = new DirectCertificateImpl(orgInfo.getDirectCert());
        orgView.setDirectCertificate(directCert);

        List<PatientInfo> patients = new ArrayList<PatientInfo>(orgInfo.getPatients().size());
        patients.addAll(orgInfo.getPatients());
        orgView.setPatients(patients);

        try {
            EndpointService endpointService = new UddiEndpointService();
            List<Endpoint> list = endpointService.getEndpoints(orgInfo.getHomeCommunityId());
            if (list != null) {
                orgView.setEndPoints(list);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            System.out.println("Defaulting uddi data from db");
            List<Endpoint> endpoints = new ArrayList<Endpoint>(orgInfo.getEndpoints().size());
            endpoints.addAll(orgInfo.getEndpoints());
            orgView.setEndPoints(endpoints);
        }

        List<DirectEndpoint> directEndpoints = new ArrayList<DirectEndpoint>(orgInfo.getDirectEndpoints().size());
        directEndpoints.addAll(orgInfo.getDirectEndpoints());
        orgView.setDirectEndpoints(directEndpoints);

        List<DocumentInfo> documents = new ArrayList<DocumentInfo>(orgInfo.getDocuments().size());
        documents.addAll(orgInfo.getDocuments());
        orgView.setDocuments(documents);

        System.out.println("org view: " + orgView);
        return orgView;
    }

    /**
     * @param orgId
     * @return
     */
    private OrganizationInfo retrieveOrgInfo(String orgId) {
        DataService service = new JpaDataService();
        OrganizationInfo info = service.getData(orgId);
        System.out.println("Retrieved org info: " + info);
        return info;
    }

    /**
     * @param currentDirectEndpoint
     * @param orgId
     */
    public void saveDirectEndpoint(DirectEndpoint directEndpoint, String orgId) {
        DataService service = new JpaDataService();
        service.addDirectEndpoint((DirectEndpointImpl) directEndpoint, orgId);
    }
}
