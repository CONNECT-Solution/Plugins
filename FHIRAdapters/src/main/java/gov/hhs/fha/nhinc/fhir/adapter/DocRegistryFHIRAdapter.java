/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.hhs.fha.nhinc.fhir.adapter;

import ihe.iti.xds_b._2007.DocumentRegistryPortType;
import oasis.names.tc.ebxml_regrep.xsd.lcm._3.SubmitObjectsRequest;
import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryRequest;
import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryResponse;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryResponseType;
import org.hl7.v3.MCCIIN000002UV01;
import org.hl7.v3.PRPAIN201301UV02;
import org.hl7.v3.PRPAIN201302UV02;
import org.hl7.v3.PRPAIN201304UV02;

/**
 *
 * @author jassmit
 */
public class DocRegistryFHIRAdapter implements DocumentRegistryPortType {

    @Override
    public RegistryResponseType documentRegistryRegisterDocumentSetB(SubmitObjectsRequest sor) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public MCCIIN000002UV01 documentRegistryPRPAIN201304UV02(PRPAIN201304UV02 prpnv) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public MCCIIN000002UV01 documentRegistryPRPAIN201301UV02(PRPAIN201301UV02 prpnv) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public MCCIIN000002UV01 documentRegistryPRPAIN201302UV02(PRPAIN201302UV02 prpnv) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public AdhocQueryResponse documentRegistryRegistryStoredQuery(AdhocQueryRequest aqr) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
