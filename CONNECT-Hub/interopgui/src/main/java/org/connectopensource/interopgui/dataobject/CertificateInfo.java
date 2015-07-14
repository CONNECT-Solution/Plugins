package org.connectopensource.interopgui.dataobject;

import java.io.IOException;
import java.util.Arrays;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.connectopensource.interopgui.services.CertificateServiceException;
import org.connectopensource.interopgui.view.Certificate;
import org.connectopensource.interopgui.view.DirectCertificate;

/**
 * @author msw
 *
 */
@Entity
@Table(name="certs")
public class CertificateInfo {

    private Long id;
    private Certificate.CertificateType certType;
    private byte[] certBytes;
    private String alias;
    private String trustBundleUrl;
    private OrganizationInfo orgInfo;
    private String specification = "exchange";
    
    /**
     * @return the orgInfo
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "orginfo_id", nullable = false)
    public OrganizationInfo getOrganizationInfo() {
        return orgInfo;
    }

    /**
     * @param orgInfo the orgInfo to set
     */
    public void setOrganizationInfo(OrganizationInfo orgInfo) {
        this.orgInfo = orgInfo;
    }

    /**
     * @return the id
     */
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Default constructor specifies a timestamp for an alias.
     */
    public CertificateInfo() {
        this.alias = String.valueOf(System.currentTimeMillis());
    }
    
    public CertificateInfo(Certificate cert) {
        this.alias = String.valueOf(System.currentTimeMillis());
        this.certType = cert.getCertType();
        if (cert.getFile() != null) {
            try {
                this.certBytes = cert.getFile().getBytes();
            } catch (IOException e) {
                throw new CertificateServiceException("Error getting bytes from UploadedFile.", e);
            }
        }
    }
    
    /**
     * @param cert
     */
    public CertificateInfo(DirectCertificate cert) {
        this.trustBundleUrl = cert.getTrustBundleUrl();
        
        this.alias = String.valueOf(System.currentTimeMillis());
        this.certType = cert.getCertType();
        if (cert.getFile() != null) {
            try {
                this.certBytes = cert.getFile().getBytes();
            } catch (IOException e) {
                throw new CertificateServiceException("Error getting bytes from UploadedFile.", e);
            }
        }
    }

    /**
     * @return the trustBundleUrl
     */
    @Column(name = "trustBundle")
    public String getTrustBundleUrl() {
        return trustBundleUrl;
    }

    /**
     * @param trustBundleUrl the trustBundleUrl to set
     */
    public void setTrustBundleUrl(String trustBundleUrl) {
        this.trustBundleUrl = trustBundleUrl;
    }

    /**
     * @return the alias
     */
    @Column(name = "alias")
    public String getAlias() {
        return alias;
    }

    /**
     * @param alias the alias to set
     */
    public void setAlias(String alias) {
        this.alias = alias;
    }

    /* (non-Javadoc)
     * @see org.connectopensource.interopgui.view.Certificate#getFile()
     */
    @Lob
    @Column(name = "cert")
    public byte[] getCertBytes() {
        return certBytes;
    }

    /* (non-Javadoc)
     * @see org.connectopensource.interopgui.view.Certificate#setFile(org.apache.myfaces.custom.fileupload.UploadedFile)
     */
    public void setCertBytes(byte[] uploadedCert) {
        this.certBytes = uploadedCert;
    }

    /* (non-Javadoc)
     * @see org.connectopensource.interopgui.view.Certificate#getCertType()
     */
    @Column(name="certtype")
    @Enumerated(EnumType.STRING)
    public Certificate.CertificateType getCertType() {
        return certType;
    }

    /* (non-Javadoc)
     * @see org.connectopensource.interopgui.view.Certificate#setCertType(org.connectopensource.interopgui.view.Certificate.CertificateType)
     */
    public void setCertType(Certificate.CertificateType certType) {
        this.certType = certType;
    }

    /**
     * @return the specification
     */
    @Column(name="specification")
    public String getSpecification() {
        return specification;
    }

    /**
     * @param specification the specification to set
     */
    public void setSpecification(String specification) {
        this.specification = specification;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        String orgId = null;
        if (orgInfo != null && orgInfo.getId() != null) {
            orgId = orgInfo.getId().toString();
        }
        return "CertificateInfo [id=" + id + ", certType=" + certType + ", certBytes=" + Arrays.toString(certBytes)
                + ", alias=" + alias + ", trustBundleUrl=" + trustBundleUrl + ", orgInfo=" + orgId + "]";
    }
}
