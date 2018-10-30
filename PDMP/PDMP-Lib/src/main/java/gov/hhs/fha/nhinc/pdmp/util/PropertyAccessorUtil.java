/*
 * Copyright (c) 2009-2018, United States Government, as represented by the Secretary of Health and Human Services.
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
package gov.hhs.fha.nhinc.pdmp.util;

import java.io.File;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.slf4j.LoggerFactory;

/**
 * Accesses property file in nhinc.properties.dir location that includes URL and needed information for prescription service query.
 * Accessor caches and reloads the property data using apache commons library
 * 
 * @author jassmit
 */
public class PropertyAccessorUtil {

    private static final String FILE_NAME = "pdmp";
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(PropertyAccessorUtil.class);
    PropertiesConfiguration properties = new PropertiesConfiguration();

    /**
     * Default constructor.
     */
    protected PropertyAccessorUtil() {
        
        String propertyFileDirAbsolutePath = System.getProperty("nhinc.properties.dir");
        propertyFileDirAbsolutePath = addFileSeparatorSuffix(propertyFileDirAbsolutePath);
        File propertyFile = new File(getPropertyFileLocation(propertyFileDirAbsolutePath, FILE_NAME));
        try {
            properties.setReloadingStrategy(new FileChangedReloadingStrategy());
            properties.load(propertyFile);
            properties.setFile(propertyFile);
            properties.setAutoSave(false);
            properties.refresh();
        } catch (ConfigurationException ex) {
            LOG.error("Failed to load property file.  Error: " + ex.getMessage(), ex);
        }
    }

    private static class SingletonHolder {

        public static final PropertyAccessorUtil INSTANCE = new PropertyAccessorUtil();
    }

    /**
     * 
     * @return Singleton of Property Accessor 
     */
    public static PropertyAccessorUtil getInstance() {
        return PropertyAccessorUtil.SingletonHolder.INSTANCE;
    }
    
    /**
     * 
     * @param key 
     * @return  the value in property file for the key
     */
    public String getProperty(String key) {
        return properties.getString(key);
    }
    
    private String addFileSeparatorSuffix(String dirPath) {
        if (dirPath != null && !dirPath.endsWith(File.separator)) {
            dirPath = dirPath + File.separator;
        }

        return dirPath;
    }
    
    private String getPropertyFileLocation(String path, String propertyFile) {
        return path + propertyFile + ".properties";
    }

}
