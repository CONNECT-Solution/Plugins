package org.connectopensource.interopgui.view.impl;

import java.nio.charset.Charset;

import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.connectopensource.interopgui.dataobject.CertificateInfo;
import org.connectopensource.interopgui.view.Certificate;

/**
 * @author msw
 *
 */
public class CertificateImpl implements Certificate {
    
    private CertificateType certType;
    private UploadedFile file;
    private String alias;
    private String pemString;
    
    public CertificateType[] getCertificateTypes()
    {
        return CertificateType.values();
    }
    
    /**
     * Default constructor specifies a timestamp for an alias.
     */
    public CertificateImpl() {
        super();
        this.alias = String.valueOf(System.currentTimeMillis());
    }

    /**
     * @param certInfo
     */
    public CertificateImpl(CertificateInfo certInfo) {
        alias = certInfo.getAlias();
        if (certInfo.getCertBytes() != null) {
            pemString = new String(certInfo.getCertBytes(), Charset.forName("US-ASCII"));
        }
        certType = certInfo.getCertType();
    }

    /**
     * @return the alias
     */
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
    @Override
    public UploadedFile getFile() {
        return file;
    }

    /* (non-Javadoc)
     * @see org.connectopensource.interopgui.view.Certificate#setFile(org.apache.myfaces.custom.fileupload.UploadedFile)
     */
    @Override
    public void setFile(UploadedFile file) {
        this.file = file;
    }

    /* (non-Javadoc)
     * @see org.connectopensource.interopgui.view.Certificate#getCertType()
     */
    @Override
    public CertificateType getCertType() {
        return certType;
    }

    /* (non-Javadoc)
     * @see org.connectopensource.interopgui.view.Certificate#setCertType(org.connectopensource.interopgui.view.Certificate.CertificateType)
     */
    @Override
    public void setCertType(CertificateType certType) {
        this.certType = certType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPemString() {
        return pemString;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPemString(String pemString) {
        this.pemString = pemString;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "CertificateImpl [certType=" + certType + ", file=" + file + ", alias=" + alias + ", pemString="
                + pemString + "]";
    }   
}
