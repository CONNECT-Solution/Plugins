/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.hhs.fha.nhinc.fhir.adapter;

import gov.hhs.fha.nhinc.adaptermpi.AdapterMpiPortType;
import gov.hhs.fha.nhinc.fhir.adapter.impl.MpiFHIRAdapterImpl;
import javax.xml.ws.BindingType;
import org.hl7.v3.PRPAIN201306UV02;
import org.hl7.v3.RespondingGatewayPRPAIN201305UV02RequestType;

/**
 *
 * @author jassmit
 */
@BindingType(value = javax.xml.ws.soap.SOAPBinding.SOAP12HTTP_BINDING)
public class MpiFHIRAdapter implements AdapterMpiPortType{

    private final MpiFHIRAdapterImpl adapter = new MpiFHIRAdapterImpl();
    
    @Override
    public PRPAIN201306UV02 findCandidates(RespondingGatewayPRPAIN201305UV02RequestType r) {
        return adapter.query(r.getPRPAIN201305UV02());
    }
    
}
