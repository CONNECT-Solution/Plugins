/*
 * Copyright (c) 2013, United States Government, as represented by the Secretary of Health and Human Services.
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
package org.connectopensource.interopgui.services;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyStoreException;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.connectopensource.interopgui.PropertiesHolder;
import org.connectopensource.interopgui.dataobject.CertificateInfo;
import org.connectopensource.interopgui.view.Certificate.CertificateType;
import org.junit.Before;
import org.junit.Test;

/**
 * Test {@link JceCertificateService}.
 */
public class JceCertificateServiceTest {

    /**
     * Set up before each test.
     * @throws URISyntaxException uri syntax exception
     */
    @Before
    public void setUp() throws URISyntaxException {
        Properties props = new Properties();
        props.setProperty("truststore.path", getClassPath() + "/truststore.jks");
        props.setProperty("truststore.pass", "changeit");
        props.setProperty("truststore.type", "JKS");
        props.setProperty("privkeypem.path", getClassPath() + "/cakey-nopass.pem");
        props.setProperty("cacertpem.path", getClassPath() + "/cacert.pem");
        PropertiesHolder.setProps(props);
    }
    
    /**
     * Test {@link JceCertificateService#trustCertificate(CertificateInfo)}.
     * @throws URISyntaxException uri syntax exception
     * @throws KeyStoreException key store exception
     * @throws IOException 
     */
    @Test
    public void canAddCertToTrustStore() throws URISyntaxException, KeyStoreException, IOException {
        CertificateService certService = new JceCertificateService();
        certService.trustCertificate(getTrustedCertInfo());
        assertTrue(JceTrustStoreManager.getInstance().loadTrustStore()
                .containsAlias(JceCertificateServiceTest.class.getName()));
    }

    /**
     * Test {@link JceCertificateService#signCertificate(CertificateInfo)}.
     * @throws URISyntaxException uri syntax exception
     * @throws IOException 
     */
    @Test
    public void canSignCsr() throws URISyntaxException, IOException {
        CertificateService certService = new JceCertificateService();
        CertificateInfo signedCertInfo = certService.signCertificate(getCsrCertInfo());
        assertNotNull(signedCertInfo.getCertBytes());
    }
    
    private CertificateInfo getTrustedCertInfo() throws URISyntaxException, IOException {
        
        CertificateInfo certInfo = new CertificateInfo();
        certInfo.setAlias(JceCertificateServiceTest.class.getName());
        certInfo.setCertType(CertificateType.CERT);
        certInfo.setCertBytes(getCertFromFile(getClassPath() + "/provider-cert.pem"));
        
        return certInfo;
    }
    
    private CertificateInfo getCsrCertInfo() throws URISyntaxException, IOException {
        
        CertificateInfo certInfo = new CertificateInfo();
        certInfo.setAlias(JceCertificateServiceTest.class.getName());
        certInfo.setCertType(CertificateType.CERT_REQ);
        certInfo.setCertBytes(getCertFromFile(getClassPath() + "/provider-req.pem"));
        
        return certInfo;
    }

    /**
     * Used when calling code requires absolute paths to test resources.
     * @return absolute classpath.
     */
    private File getClassPath() throws URISyntaxException {
        return new File(JceCertificateServiceTest.class.getProtectionDomain().getCodeSource().getLocation().toURI());
    }  
    
    private byte[] getCertFromFile(String path) throws IOException {
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(new File(path));
            byte[] value = new byte[inputStream.available()];
            inputStream.read(value);

            return value;
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

}
