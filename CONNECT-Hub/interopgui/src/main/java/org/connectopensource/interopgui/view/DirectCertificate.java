/**
 * 
 */
package org.connectopensource.interopgui.view;

/**
 * @author msw
 *
 */
public interface DirectCertificate extends Certificate {
    
    public String getTrustBundleUrl();
    public void setTrustBundleUrl(String url);

}
