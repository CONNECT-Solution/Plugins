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
package gov.hhs.fha.nhinc.transformerutility;

import gov.hhs.fha.nhinc.connectmgr.ConnectionManagerException;
import gov.hhs.fha.nhinc.connectmgr.persistance.dao.ExchangeInfoDAOFileImpl;
import gov.hhs.fha.nhinc.connectmgr.persistance.dao.InternalConnectionInfoDAOFileImpl;
import gov.hhs.fha.nhinc.connectmgr.persistance.dao.UddiConnectionInfoDAOFileImpl;
import gov.hhs.fha.nhinc.exchange.ExchangeInfoType;
import gov.hhs.fha.nhinc.exchange.ExchangeListType;
import gov.hhs.fha.nhinc.exchange.ExchangeType;
import gov.hhs.fha.nhinc.exchange.OrganizationListType;
import gov.hhs.fha.nhinc.exchange.transform.ExchangeTransformException;
import gov.hhs.fha.nhinc.exchange.transform.uddi.UDDITransform;
import gov.hhs.fha.nhinc.exchangemgr.ExchangeManagerException;
import java.io.File;
import java.math.BigInteger;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.AbstractFileFilter;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;

/**
 *
 * @author Poornima Venkatakrishnan
 */
public class UDDIExchangeTransformerUtility {

    private static final String ADAPTER = "adapter";
    private static final String LOCAL = "local";
    private static final String UDDI = "uddi";
    private static final String EXCHANGE_1 = "Exchange 1";
    private static final String PLACE_HOLDER = "https://testurl/uddi/list";

    public static void main(String[] args) {
        try {
            if (args.length > 0) {
                fetchFiles(args[0]);
            }
        } catch (ConnectionManagerException | ExchangeManagerException | ExchangeTransformException ex) {
            Logger.getLogger(UDDIExchangeTransformerUtility.class.getName()).log(Level.SEVERE,
                "Exception thrown in UDDIExchangeTransformerUtility", ex);
        }
    }

    private static void fetchFiles(String path) throws ExchangeManagerException,
        ExchangeTransformException,
        ConnectionManagerException {
        File aFile = new File(path);
        Collection<File> list = FileUtils.listFiles(aFile, getConnectionInfoFileFilter(),
            DirectoryFileFilter.INSTANCE);

        UDDITransform transfomer = new UDDITransform();
        UddiConnectionInfoDAOFileImpl uddiDAO = UddiConnectionInfoDAOFileImpl.getInstance();
        InternalConnectionInfoDAOFileImpl inDAO = InternalConnectionInfoDAOFileImpl.getInstance();
        ExchangeInfoDAOFileImpl exDAO = ExchangeInfoDAOFileImpl.getInstance();

        for (File vfile : list) {
            ExchangeInfoType exInfo = null;
            if (isInternalConnectionInfo(vfile)) {
                inDAO.setFileName(vfile.getAbsolutePath());
                exInfo = buildExchangeInfo(LOCAL, ADAPTER,
                    transfomer.transform(inDAO.loadBusinessDetail()), false);
            } else {
                uddiDAO.setFileName(vfile.getAbsolutePath());
                exInfo = buildExchangeInfo(UDDI, EXCHANGE_1, transfomer.transform(uddiDAO.loadBusinessDetail()), true);
            }
            exDAO.setFileName(transformedFileName(vfile));
            exDAO.saveExchangeInfo(exInfo);
            Logger.getLogger(UDDIExchangeTransformerUtility.class.getName()).log(Level.INFO, "FileName: " + vfile.
                getAbsolutePath() + "---> Transformed File Name: "
                + transformedFileName(vfile));
        }
    }

    private static String transformedFileName(File afile) {
        String filename = afile.getAbsolutePath();
        if (filename.contains("internalConnectionInfo")) {
            return filename.replaceAll("internalConnectionInfo", "internalExchangeInfo");
        }
        return filename.replaceAll("uddiConnectionInfo", "exchangeInfo");
    }

    private static boolean isInternalConnectionInfo(File aFile) {
        if (aFile.getName().contains("internalConnectionInfo")) {
            return true;
        }
        return false;
    }

    private static ExchangeInfoType buildExchangeInfo(String type, String exchangeName, OrganizationListType orgList,
        boolean uddiFile) {
        ExchangeInfoType exinfo = new ExchangeInfoType();
        ExchangeListType exList = new ExchangeListType();
        ExchangeType exchange = new ExchangeType();
        exchange.setType(type);
        exchange.setName(exchangeName);
        exchange.setOrganizationList(orgList);
        exchange.setDisabled(true);
        exList.getExchange().add(exchange);
        exinfo.setDefaultExchange(exchangeName);
        if (uddiFile) {
            exchange.setUrl(PLACE_HOLDER);
            exinfo.setRefreshInterval(1440l);
            exinfo.setMaxNumberOfBackups(BigInteger.ONE);
        }
        exinfo.setExchanges(exList);
        return exinfo;
    }

    private static IOFileFilter getConnectionInfoFileFilter() {
        return new AbstractFileFilter() {
            @Override
            public boolean accept(File file) {
                if (file.getName().contains("ConnectionInfo") && file.isFile() && FilenameUtils.isExtension(file.
                    getAbsolutePath(), "xml")) {
                    return true;
                } else {
                    return false;
                }
            }
        };
    }
}
