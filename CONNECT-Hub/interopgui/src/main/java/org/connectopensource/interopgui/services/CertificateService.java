package org.connectopensource.interopgui.services;

import org.connectopensource.interopgui.dataobject.CertificateInfo;

/**
 * @author msw
 *
 */
public interface CertificateService {
    
    /**
     * Add certInfo to the configured truststore for the connect gateway.
     * @param certInfo containing cert to be trusted.
     */
    void trustCertificate(CertificateInfo certInfo);

    /**
     * Sign a certificate programmatically.
     * @param certInfo to be signed by the CONNECT Gateway CA 
     * @return signed certificate
     */
    CertificateInfo signCertificate(CertificateInfo certInfo);
}
