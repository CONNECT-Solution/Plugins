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

import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.X509Extension;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.PKCS10CertificationRequest;
import org.bouncycastle.jce.PrincipalUtil;
import org.bouncycastle.jce.X509Principal;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequestHolder;


/**
 * Sign CSR.
 */
public class JceCsrSignedCertGenerator {

    private static final long ONE_YEAR_IN_MILLIS = 365 * 24 * 60 * 60 * 1000;
    
    /**
     * @param inputCSR certificate signing request (pkcs10)
     * @param caCert root ca certificate
     * @param caPrivate private key used in ca generation (no password protection)
     * @return signed x509 certificate
     * @throws OperatorCreationException operation creation
     * @throws CertificateException certificate exception
     */
    public static X509Certificate sign(PKCS10CertificationRequest inputCSR, X509Certificate caCert, 
            PrivateKey caPrivate) throws OperatorCreationException, CertificateException {

        X509Principal issuer = PrincipalUtil.getIssuerX509Principal(caCert);
        PKCS10CertificationRequestHolder pk10Holder = new PKCS10CertificationRequestHolder(inputCSR);
        SubjectPublicKeyInfo subjectPublicKeyInfo = pk10Holder.getSubjectPublicKeyInfo();

        X509v3CertificateBuilder certBuilder = new X509v3CertificateBuilder(new X500Name(issuer.getName()),
                BigInteger.ONE, new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()
                        + ONE_YEAR_IN_MILLIS), pk10Holder.getSubject(), pk10Holder.getSubjectPublicKeyInfo());

        certBuilder.addExtension(X509Extension.subjectKeyIdentifier, false, subjectPublicKeyInfo);
        ContentSigner contentSigner = new JcaContentSignerBuilder("SHA1withRSA").setProvider("BC").build(caPrivate);

        return (new JcaX509CertificateConverter()).setProvider("BC").getCertificate(certBuilder.build(contentSigner));
    }
}
