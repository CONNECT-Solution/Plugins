/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.hhs.fha.nhinc.fhir.exception;

/**
 *
 * @author jassmit
 */
public class UnknownResourceLocation extends Exception {
    
    public UnknownResourceLocation() {
        super();
    }
    
    public UnknownResourceLocation(String message) {
        super(message);
    }
    
    public UnknownResourceLocation(String message, Throwable cause) {
        super(message, cause);
    }
}
