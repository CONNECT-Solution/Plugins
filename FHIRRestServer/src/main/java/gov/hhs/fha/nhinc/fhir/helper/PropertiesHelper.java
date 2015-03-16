/*
 * Copyright (c) 2009-2015, United States Government, as represented by the Secretary of Health and Human Services.
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
package gov.hhs.fha.nhinc.fhir.helper;

import java.io.File;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author achidamb
 */
public class PropertiesHelper {

    private static final Logger LOG = LoggerFactory.getLogger(PropertiesHelper.class);

    /* A private Constructor to prevents any other
     * class from instantiating.
     */
    private PropertiesHelper() {

    }

    /**
     * This method retrieves the property from the specified property file.Apache Commons PropertyConfiguration is used
     * to load the property file and Reloads Property Files.
     *
     *
     * @param propertyFile - Name of PropertyFile passed as an argument.
     * @return PropertiesConfiguration object
     */
    public PropertiesConfiguration getProperty(String propertyFile) {
        PropertiesConfiguration config = new PropertiesConfiguration();
        try {
            config.setReloadingStrategy(new FileChangedReloadingStrategy());
            config.load(propertyFile);
            config.refresh();
        } catch (ConfigurationException ex) {
            LOG.debug("Error while loading property file :" + propertyFile + ex.getMessage());
        }
        return config;

    }

    /**
     *
     * @param documentFileName - The Name of the document file need to be read.This has the path of the Document in the
     * server provided in the properties file.
     * @param directoryName - The Directory where all the Binary docs are stored. This property is also in the property
     * File.
     * @return the document file
     */
    public File getDocumentFile(String documentFileName, String directoryName) {

        return (new File(directoryName + File.separator + documentFileName));

    }

    private static class SingletonHolder {

        private static final PropertiesHelper INSTANCE = new PropertiesHelper();
    }

    // singleton
    public static PropertiesHelper getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
