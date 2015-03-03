/*
 * Copyright (c) 2009-2015, United States Government, as represented by the Secretary of Health and Human Services.  * All rights reserved. * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *   * Redistributions of source code must retain the above
 *     copyright notice, this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *   * Neither the name of the United States Government nor the
 *     names of its contributors may be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *DISCLAIMED. IN NO EVENT SHALL THE UNITED STATES GOVERNMENT BE LIABLE FOR ANY
 *DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package gov.hhs.onc.hpdclient;

import gov.hhs.onc.hpdclient.service.HPDService;
import gov.hhs.onc.hpdclient.service.HPDServiceImpl;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import oasis.names.tc.dsml._2._0.core.BatchResponse;
import oasis.names.tc.dsml._2._0.core.ErrorResponse;
import oasis.names.tc.dsml._2._0.core.SearchResponse;

/**
 *
 * @author tjafri
 *
 * This is a command line application use to search PDTI. It accepts 3 command line arguments in the form arg1 arg2 arg3
 * where, arg1 - DN arg2 - search field arg3 - search field value for example, relationship cn JonesPracticeGroup
 *
 */
public class HPDClient {

    public static void main(String[] args) {
        try {
            HPDService service = new HPDServiceImpl();
            if (args.length == 0 || args.length != 3) {
                printUsage();
            } else {
                if (service != null) {
                    printSearchResult(service.searchQuery(args[0], args[1], args[2]));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void printUsage() {
        System.out.println("USAGE");
        System.out.println("professional givenName Thomas");
        System.out.println("organization businessCategory MRI");
        System.out.println("membership hpdHasAProvider uid=2.16.840.1.113883.3.4295:provider1,ou=HcProfessional,o=dev.provider-directories.com,dc=hpd");
        System.out.println("services hpdServiceId 6");
        System.out.println("credential credentialNumber 1");
        System.out.println("relationship cn JonesPracticeGroup");
    }

    private static void printSearchResult(BatchResponse response) {
        JAXBContext jbc;
        try {
            jbc = JAXBContext.newInstance(SearchResponse.class.getPackage().getName());
            Marshaller marshaller = jbc.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            Object result = response.getBatchResponses().get(0).getValue();
            if (result instanceof SearchResponse) {
                marshaller.marshal(new JAXBElement<SearchResponse>(new QName("uri", "local"), SearchResponse.class, (SearchResponse) result), System.out);
            } else {
                marshaller.marshal(new JAXBElement<ErrorResponse>(new QName("uri", "local"), ErrorResponse.class, (ErrorResponse) result), System.out);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
