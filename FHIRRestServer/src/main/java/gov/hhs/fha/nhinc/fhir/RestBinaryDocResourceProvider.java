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
package gov.hhs.fha.nhinc.fhir;

import ca.uhn.fhir.model.dstu.resource.Binary;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.server.IResourceProvider;
import gov.hhs.fha.nhinc.fhir.helper.PropertiesHelper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author achidamb
 */
/**
 * There is a HAPI ResourceProvider interface and FHIRRestServer implements the Binary Resource Provider. Since Binary
 * Storage is not supported by HAPI servers the Binary Mock rest Service has been set up to get Binary encoded document
 * for demo services. There is no persistence layer at this moment and the documents are read from file system based on
 * docReference provided.
 */
public class RestBinaryDocResourceProvider implements IResourceProvider {

    private static final String PROPERTYFILENAME = "fhirRestServer.properties";
    private static final String PROPDIRECTORY = "fhirConfigDirectory";
    private static final PropertiesConfiguration config = PropertiesHelper.getInstance().getProperty(PROPERTYFILENAME);

    private static final Logger LOG = LoggerFactory.getLogger(RestBinaryDocResourceProvider.class);

    public RestBinaryDocResourceProvider() {

    }

    /**
     * This method receives the doc Reference and based on the docReference the corresponding Binary encoded document
     * will be returned. There is a "fhirRestServer.properties" in src/main/resources of this web application and it has
     * the docReference and it's corresponding document location. If the docReference is not available then it throw
     * NullPointerException in logs.
     *
     * @param docReference
     * @return
     *
     */
    @Read
    public Binary getDocument(@IdParam IdDt docReference) {
        PropertiesHelper instance = PropertiesHelper.getInstance();
        String documentFileName = config.getString(docReference.getIdPart());
        if (documentFileName == null || documentFileName.isEmpty()) {
            throw new NullPointerException("Returned Document FileName is not valid");
        }
        return createEncodedDoc(instance.getDocumentFile(documentFileName, config.getString(PROPDIRECTORY)));
    }

    private Binary createEncodedDoc(File document) {
        Binary encodedDoc = new Binary();
        if (document != null && document.isFile()) {
            try {
                encodedDoc.setContentAsBase64(Base64.encodeBase64String(FileUtils.readFileToByteArray(document)));
                encodedDoc.setContentType(Files.probeContentType(Paths.get(document.getAbsolutePath())));
            } catch (IOException ex) {
                LOG.debug("Error While Encoding the Binary Document " + ex.getMessage());
            }

        }
        return encodedDoc;
    }

    /**
     * The getResourceType method comes from IResourceProvider, and must be overridden to indicate what type of resource
     * this provider supplies.
     */
    @Override
    public Class<Binary> getResourceType() {
        return Binary.class;
    }

}
