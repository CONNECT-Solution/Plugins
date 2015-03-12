package gov.hhs.fha.nhinc.fhir;

import ca.uhn.fhir.model.dstu.resource.Binary;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.server.IResourceProvider;
import gov.hhs.fha.nhinc.fhir.helper.MockDocumentLoader;
import gov.hhs.fha.nhinc.fhir.helper.PropertiesHelper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
/**
 *
 * @author achidamb
 */
public class RestBinaryDocResourceProvider implements IResourceProvider {

    private static final String PROPERTYFILENAME = "fhirRestServer.properties";
    private static final String PROPDIRECTORY = "fhirConfigDirectory";

    private static final Log LOG = LogFactory.getLog(RestBinaryDocResourceProvider.class);

    /**
     *
     * @param docReference
     * @return
     * @throws java.io.IOException
     */
    /* @Read
     public Binary getDocument(@IdParam IdDt docReference) throws IOException {
     File document = null;
     MockDocumentLoader loader = new MockDocumentLoader();
     HashMap<String, File> docReferenceMap = loader.createDocumentLoader();
     document = docReferenceMap.get(docReference.getIdPart());
     return createEncodedDoc(document);

     }*/
    @Read
    public Binary getDocument(@IdParam IdDt docReference) {
        PropertiesHelper propHelper = new PropertiesHelper();
        Binary encodedDoc = null;
        File document = propHelper.getDocumentFile(propHelper.getPropertyFile(docReference.getIdPart(),
            PROPERTYFILENAME), propHelper.getPropertyFile(PROPDIRECTORY, PROPERTYFILENAME));
        encodedDoc = createEncodedDoc(document);
        return encodedDoc;

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
