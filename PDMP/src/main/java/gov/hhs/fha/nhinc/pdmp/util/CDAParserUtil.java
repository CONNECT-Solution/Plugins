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

import javax.xml.bind.JAXBIntrospector;

import java.io.InputStream;
import java.io.StringWriter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.hl7.v3.ObjectFactory;
import org.hl7.v3.POCDMT000040ClinicalDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mpnguyen
 *
 */
public class CDAParserUtil {
    private final static Logger logger = LoggerFactory.getLogger(CDAParserUtil.class);

    /**
     * Convert xml into object
     *
     * @param <T>
     *
     * @param cdaInputStream
     * @return
     * @return
     */
    public static <T> T convertXMLToCDA(InputStream cdaInputStream, Class<T> responseClass) {
        try {
            logger.debug("Convert CDA XML into Java Obj");
            JAXBContext jContext = JAXBContext.newInstance(responseClass);
            Unmarshaller unmarshaller = jContext.createUnmarshaller();
            return (T) JAXBIntrospector.getValue(unmarshaller.unmarshal(cdaInputStream));
        } catch (JAXBException e) {
            logger.error("Unable to parsing xml into object due to {}", e);
            return null;
        }

    }
    /**
     * @param ccDAInputStream
     * @return
     */
    public static String convertCDAToXML(POCDMT000040ClinicalDocument cdaDoc) {
        try {
            logger.debug("Convert CDA Java Obj to XML");
            ObjectFactory factory = new ObjectFactory();
            JAXBContext jContext = JAXBContext.newInstance(POCDMT000040ClinicalDocument.class);
            Marshaller marshaller = jContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

            StringWriter strWrite = new StringWriter();
            marshaller.marshal(factory.createClinicalDocument(cdaDoc), strWrite);
            return strWrite.toString();

        } catch (JAXBException e) {
            logger.error("Unable to convert cdaObj to xml due to {}", e);
        }
        return null;
    }

}