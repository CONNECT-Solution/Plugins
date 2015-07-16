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

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyStoreException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.connectopensource.interopgui.PropertiesHolder;
import org.junit.Before;
import org.junit.Test;

/**
 * Test {@link JceTrustStoreManager}.
 */
public class JceTrustStoreManagerTest {
    
    /**
     * Set up before each test.
     * @throws URISyntaxException uri syntax exception.
     */
    @Before
    public void setUp() throws URISyntaxException {
        Properties props = new Properties();
        props.setProperty("truststore.path", getClassPath() + "/truststore.jks");
        props.setProperty("truststore.pass", "changeit");
        props.setProperty("truststore.type", "JKS");
        props.setProperty("truststore.ca.alias", "thecacert");
        PropertiesHolder.setProps(props);
    }
    
    /**
     * Test {@link JceTrustStoreManager#addTrustedCert(X509Certificate, String)}.
     * @throws CertificateException cert exception
     * @throws URISyntaxException uri syntax exception
     * @throws IOException io exception
     * @throws KeyStoreException key store exception
     */
    @Test
    public void canAdd() throws CertificateException, URISyntaxException, IOException, KeyStoreException {
        JceTrustStoreManager trustStoreManager = JceTrustStoreManager.getInstance();  
        X509Certificate cert = getTestCert();
        trustStoreManager.addTrustedCert(cert, JceTrustStoreManagerTest.class.getName());
        assertTrue(JceTrustStoreManager.getInstance().loadTrustStore()
                .containsAlias(JceTrustStoreManagerTest.class.getName()));
    }
    
    private X509Certificate getTestCert() throws URISyntaxException, IOException, CertificateException {

        FileInputStream inputStream = null;
        ByteArrayInputStream bais = null;
        try {
            // use FileInputStream to read the file
            inputStream = new FileInputStream(getClassPath() + "/provider-cert.pem");

            // read the bytes
            byte[] value = new byte[inputStream.available()];
            inputStream.read(value);
            bais = new ByteArrayInputStream(value);

            // get X509 certificate factory
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");

            // certificate factory can now create the certificate
            return (X509Certificate) certFactory.generateCertificate(bais);
        } finally {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(bais);
        }
    }

    /**
     * Used when calling code requires absolute paths to test resources.
     * @return absolute classpath.
     */
    private File getClassPath() throws URISyntaxException {
        return new File(JceTrustStoreManagerTest.class.getProtectionDomain().getCodeSource().getLocation().toURI());
    }   

}
