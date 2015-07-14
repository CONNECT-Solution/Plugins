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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.ssl.PEMItem;
import org.apache.commons.ssl.PEMUtil;
import org.bouncycastle.jce.PKCS10CertificationRequest;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMWriter;
import org.connectopensource.interopgui.PropertiesHolder;
import org.connectopensource.interopgui.dataobject.CertificateInfo;
import org.connectopensource.interopgui.view.Certificate.CertificateType;

/**
 * Implementation of {@link CertificateService} that relies on JCE libraries.
 */
public class JceCertificateService implements CertificateService {

    private static final String PRIVKEY_PEM_PATH = PropertiesHolder.getProps().getProperty("privkeypem.path");
    private static final String CACERT_PEM_PATH = PropertiesHolder.getProps().getProperty("cacertpem.path");
    
    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void trustCertificate(CertificateInfo certInfo) {
        try {
            X509Certificate x509Cert = createX509Cert(certInfo.getCertBytes());
            JceTrustStoreManager.getInstance().addTrustedCert(x509Cert, certInfo.getAlias());
        } catch (Exception e) {
            throw new CertificateServiceException("Error while trusting cert, alias = " + certInfo.getAlias()
                    + ", path uri = " + certInfo.getCertBytes(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CertificateInfo signCertificate(CertificateInfo certInfo) {

        CertificateInfo signedCertInfo = null;
        try {
            signedCertInfo = new CertificateInfo();

            PKCS10CertificationRequest csr = createCsr(certInfo.getCertBytes());
            X509Certificate signedCert = JceCsrSignedCertGenerator.sign(csr, createX509Cert(CACERT_PEM_PATH),
                    getCaPrivateKey());

            // drop in the new signed certificate bytes from PEM format.
            signedCertInfo.setCertBytes(getSignedCertBytes(signedCert));

            // We need to store that this is signed... even though this is not a CSR anymore, type is still CERT_REQ
            signedCertInfo.setCertType(CertificateType.CERT_REQ);            
        } catch (Exception e) {
            throw new CertificateServiceException("Error while creating signed cert from CSR", e);
        }

        return signedCertInfo;
    }

    private X509Certificate createX509Cert(String path) throws URISyntaxException, IOException, CertificateException {

        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(new File(path));
            byte[] value = new byte[inputStream.available()];
            inputStream.read(value);

            return (X509Certificate) createX509Cert(value);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    private X509Certificate createX509Cert(byte[] cert) throws URISyntaxException, IOException, CertificateException {

        ByteArrayInputStream bais = null;
        try {
            bais = new ByteArrayInputStream(cert);

            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");

            return (X509Certificate) certFactory.generateCertificate(bais);
        } finally {
            IOUtils.closeQuietly(bais);
        }
    }

    private PKCS10CertificationRequest createCsr(byte[] certRequest) {

        final PEMItem csrPemFormat = getPemItem(certRequest);

        // Verify the type.
        System.out.println("csrPemFormat.pemType:" + csrPemFormat.pemType);
        if (!StringUtils.contains(csrPemFormat.pemType, "CERTIFICATE REQUEST")) {
            throw new CertificateServiceException("pem does not appear to contain a CSR.");
        }

        return new PKCS10CertificationRequest(csrPemFormat.getDerBytes());
    }

    private PrivateKey getCaPrivateKey() throws NoSuchAlgorithmException, InvalidKeySpecException {

        final PEMItem privKeyPem = getPemItem(PRIVKEY_PEM_PATH);

        // PKCS8 decode the encoded RSA private key
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privKeyPem.getDerBytes());
        KeyFactory kf = KeyFactory.getInstance("RSA");

        return kf.generatePrivate(keySpec);
    }

    private PEMItem getPemItem(String filepath) {

        FileInputStream inputStream = null;
        byte[] value = null;
        try {
            inputStream = new FileInputStream(new File(filepath));
            value = new byte[inputStream.available()];
            inputStream.read(value);
        } catch (Exception e) {
            throw new CertificateServiceException("pem is empty.", e);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }

        return getPemItem(value);
    }
    
    private PEMItem getPemItem(byte[] certRequest) {

        @SuppressWarnings("rawtypes")
        final List pemItems = PEMUtil.decode(certRequest);

        // Verify list isn't empty - uses Apache Commons Lang.
        if (pemItems.isEmpty()) {
            throw new CertificateServiceException("privkey pem is empty!");
        }

        return (PEMItem) pemItems.get(0);
    }

  
    private byte[] getSignedCertBytes(X509Certificate signedCert) throws IOException {
        ByteArrayOutputStream outputStream = null;
        PEMWriter pemWriter = null;
        try {
            outputStream = new ByteArrayOutputStream();
            pemWriter = new PEMWriter(new PrintWriter(outputStream));
            pemWriter.writeObject(signedCert);
            pemWriter.flush();
            return outputStream.toByteArray();
        } finally {
            IOUtils.closeQuietly(pemWriter);
            IOUtils.closeQuietly(outputStream);
        }
    }
  

}
