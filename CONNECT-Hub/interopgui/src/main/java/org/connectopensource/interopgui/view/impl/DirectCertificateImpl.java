/**
 * 
 */
package org.connectopensource.interopgui.view.impl;

import java.util.Arrays;

import org.connectopensource.interopgui.dataobject.CertificateInfo;
import org.connectopensource.interopgui.view.DirectCertificate;

/**
 * @author msw
 *
 */
public class DirectCertificateImpl extends CertificateImpl implements DirectCertificate {

    String trustBundleUrl = null;
    
    /**
     * @param directCertInfo
     */
    public DirectCertificateImpl(CertificateInfo directCertInfo) {
        super((CertificateInfo) directCertInfo);
        trustBundleUrl = directCertInfo.getTrustBundleUrl();
    }

    /**
     * 
     */
    public DirectCertificateImpl() {
        super();
    }

    /* (non-Javadoc)
     * @see org.connectopensource.interopgui.view.DirectCertificate#getTrustBundleUrl()
     */
    @Override
    public String getTrustBundleUrl() {
        return trustBundleUrl;
    }

    /* (non-Javadoc)
     * @see org.connectopensource.interopgui.view.DirectCertificate#setTrustBundleUrl(java.lang.String)
     */
    @Override
    public void setTrustBundleUrl(String url) {
        trustBundleUrl = url;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "DirectCertificateImpl [trustBundleUrl=" + trustBundleUrl + ", getCertificateTypes()="
                + Arrays.toString(getCertificateTypes()) + ", getAlias()=" + getAlias() + ", getFile()=" + getFile()
                + ", getCertType()=" + getCertType() + ", getPemString()=" + getPemString() + "]";
    }

}
