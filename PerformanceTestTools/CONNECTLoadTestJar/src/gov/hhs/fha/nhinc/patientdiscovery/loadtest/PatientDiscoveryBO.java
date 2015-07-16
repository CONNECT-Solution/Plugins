/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.hhs.fha.nhinc.patientdiscovery.loadtest;

import gov.hhs.fha.nhinc.common.nhinccommon.AssertionType;
import gov.hhs.fha.nhinc.loadtest.DataManager;
import gov.hhs.fha.nhinc.patientdiscovery.adapter.proxy.AdapterPatientDiscoveryProxy;
import org.hl7.v3.PRPAIN201306UV02;

/**
 *
 * @author mweaver
 */
public class PatientDiscoveryBO implements AdapterPatientDiscoveryProxy {

    public PRPAIN201306UV02 respondingGatewayPRPAIN201305UV02(org.hl7.v3.PRPAIN201305UV02 body, AssertionType assertion) {

        DataManager dm = DataManager.getInstance();
        PRPAIN201306UV02 response = null;

        try {
            response = dm.getCannedPatientDiscoveryResponse();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }
}
