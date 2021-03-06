/*
 * Copyright (c) 2009-2017, United States Government, as represented by the Secretary of Health and Human Services.
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
package gov.hhs.fha.nhinc.util.jaxb;

import gov.hhs.fha.nhinc.transform.marshallers.JAXBContextHandler;
import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

/**
 *
 * @author sopawar
 */
public class JAXBXMLUtils {

    JAXBContext context = null;

    /**
     * This method takes in an XML String and returns an JAXB object with get and set methods. parameters: xmlStr - The
     * passed in XML String parameters: validate - Flag if the validation has to be enabled or not
     *
     * @param xmlStr
     * @param namespace
     * @return
     * @throws javax.xml.bind.JAXBException
     */
    public Object parseXML(String xmlStr, String namespace) throws JAXBException {
        if (xmlStr == null || xmlStr.isEmpty()) {
            throw new JAXBException("XML passed for parsing is Null or Empty.");
        }

        // create a JAXBContext capable of handling classes generated into
        JAXBContext jc = getJAXBContext(namespace);
        Unmarshaller u = jc.createUnmarshaller();

        return u.unmarshal(new StreamSource(new StringReader(xmlStr)));
    }

    /**
     * This method takes in a JAXB object and returns an XMLString parameters: Object - The JAXB object which has to
     * converted to xml
     *
     * @param obj
     * @param namespace
     * @return
     * @throws javax.xml.bind.JAXBException
     */
    public String getXML(Object obj, String namespace) throws JAXBException {
        if (obj == null) {
            throw new JAXBException("The object passed to parse is null.");
        }

        JAXBContext jc = getJAXBContext(namespace);
        Marshaller m = jc.createMarshaller();

        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE);

        StringWriter strWrite = new StringWriter();
        m.marshal(obj, strWrite);

        return strWrite.toString();
    }

    private JAXBContext getJAXBContext(String nameSpace) throws JAXBException {
        if (context == null) {
            context = new JAXBContextHandler().getJAXBContext(nameSpace);
        }
        return context;
    }
}
