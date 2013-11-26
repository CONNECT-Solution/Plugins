/**
 * 
 */
package gov.hhs.fha.nhinc.adapterdocrepository;

import gov.hhs.fha.nhinc.common.nhinccommon.AssertionType;
import gov.hhs.fha.nhinc.messaging.client.CONNECTCXFClient;
import gov.hhs.fha.nhinc.messaging.service.decorator.cxf.WsAddressingServiceEndpointDecorator;
import gov.hhs.fha.nhinc.messaging.service.port.CachingCXFWSAServicePortBuilder;
import gov.hhs.fha.nhinc.messaging.service.port.ServicePortDescriptor;

/**
 * @author achidambaram
 *
 */
public class AdapterDocRepositoryClientUnsecured<T> extends CONNECTCXFClient<T> {

    /**
     * @param portDescriptor
     * @param url
     * @param assertion
     * @param portBuilder
     */
    protected AdapterDocRepositoryClientUnsecured(ServicePortDescriptor<T> portDescriptor, String url, AssertionType assertion) {
        super(portDescriptor, url, assertion, new CachingCXFWSAServicePortBuilder<T>(portDescriptor));
        decorateEndpoint(assertion, url, portDescriptor.getWSAddressingAction());
        serviceEndpoint.configure();
        
    }
    
    /**
     * @param assertion
     * @param url
     * @param wsAddressingAction
     */
    private void decorateEndpoint(AssertionType assertion, String url, String wsAddressingActionId) {
        serviceEndpoint = new WsAddressingServiceEndpointDecorator<T>(serviceEndpoint, url, wsAddressingActionId,
                assertion);
    }

    public T getPort() {
        return serviceEndpoint.getPort();
    }

   
   

}
