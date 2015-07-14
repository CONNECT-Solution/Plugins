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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStore.ProtectionParameter;
import java.security.cert.X509Certificate;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.connectopensource.interopgui.PropertiesHolder;

/**
 * Thread Safe Trust Store Manager provides synchronized read/write operations against a JCE Keystore.
 */
public final class JceTrustStoreManager {
    
    private final Properties props = PropertiesHolder.getProps();
    private final char[] trustStorePass = props.getProperty("truststore.pass").toCharArray();    
    private final String trustStorePath = props.getProperty("truststore.path");
    private final String trustStorePathTmp = trustStorePath + ".tmp";
    private final String trustStorePathBak = trustStorePath + ".bak";
    
    // Private constructor prevents instantiation from other classes
    private JceTrustStoreManager() {
        // private singleton.
    }

    /**
     * Singleton Holder.
     */
    private static class SingletonHolder { 
        public static final JceTrustStoreManager INSTANCE = new JceTrustStoreManager();
    }

    /**
     * @return singleton instance of trust store manager.
     */
    public static JceTrustStoreManager getInstance() {
        return SingletonHolder.INSTANCE;
    }    

    /**
     * Load the trust store.
     * @return loaded trust store.
     */
    protected synchronized KeyStore loadTrustStore() {

        KeyStore trustStore = null;
        FileInputStream inputStream = null;
        try {
            File file = new File(trustStorePath);
            if (file.exists()) {
                inputStream = new FileInputStream(trustStorePath);
            }
            trustStore = KeyStore.getInstance(props.getProperty("truststore.type"));
            trustStore.load(inputStream, null);
        } catch (Exception e) {
            throw new CertificateServiceException("Unable to load trust store, path = ["
                    + props.getProperty("truststore.path") + "], type = [" + props.getProperty("truststore.type")
                    + "].", e);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
        
        return trustStore;
    }

    /**
     * Disclaimer: This private method is not thread-safe. Caller beware (...or be synchronized).
     */
    private synchronized void store(KeyStore trustStore) {

        FileOutputStream outputStream = null;
        
        // back up before we start...
        try {
            FileUtils.copyFile(new File(trustStorePath),
                    new File(trustStorePathBak + "." + System.currentTimeMillis()));
        } catch (IOException e) {
            throw new CertificateServiceException("Unable to backup trust store [" + trustStorePath + "].", e);
        }

        try {            
            outputStream = new FileOutputStream(trustStorePathTmp);
            trustStore.store(outputStream, trustStorePass);
        } catch (Exception e) {
            throw new CertificateServiceException("Unable to save trust store to file [" + trustStorePathTmp + "].", e);
        } finally {
            IOUtils.closeQuietly(outputStream);
        }
        
        // install new trust store
        try {
            FileUtils.deleteQuietly(new File(trustStorePath));            
            FileUtils.moveFile(new File(trustStorePathTmp), new File(trustStorePath));
        } catch (IOException e) {
            throw new CertificateServiceException("Unable to install trust store [" + trustStorePath + "].", e);
        }
    }

    /**
     * Add an unprotected certificate to the trust store.
     * @param cert X509 certificate to be added to the trust store.
     * @param alias linked to the certificate and included in the keystore entry.
     */
    public synchronized void addTrustedCert(X509Certificate cert, String alias) {
        addTrustedCert(cert, alias, null);
    }
    
    /**
     * Add a certificate to the trust store. (load + add + store in one transaction.)
     * @param cert X509 certificate to be added to the trust store.
     * @param alias linked to the certificate and included in the keystore entry.
     * @param protectionParam used to protect the entry (may be null)
     */
    public synchronized void addTrustedCert(X509Certificate cert, String alias, ProtectionParameter protectionParam) {
        
        try {
            final KeyStore trustStore = loadTrustStore();
            KeyStore.TrustedCertificateEntry trust = new KeyStore.TrustedCertificateEntry(cert);
            trustStore.setEntry(alias, trust, protectionParam);
            store(trustStore);
        } catch (Exception e) {
            throw new CertificateServiceException("Unable to save trust store to file.", e);
        }
    }
    
}