/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.hhs.fha.nhinc.fhir.helper;

import java.io.File;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author achidamb
 */
public class PropertiesHelper {

    private static final Log LOG = LogFactory.getLog(PropertiesHelper.class);

    public String getPropertyFile(String property, String propertyFile) {
        PropertiesConfiguration config = new PropertiesConfiguration();
        try {
            config.load(propertyFile);
            config.save(propertyFile);
            config.setAutoSave(false);
            config.refresh();
        } catch (ConfigurationException ex) {
            LOG.debug("Error while loading property file :" + propertyFile + ex.getMessage());
        }
        return config.getString(property);

    }

    public File getDocumentFile(String documentFileName, String directoryName) {

        return (new File(directoryName + File.separator + documentFileName));

    }
}
