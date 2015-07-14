/**
 * 
 */
package org.connectopensource.interopgui.view;

import org.apache.myfaces.custom.fileupload.UploadedFile;

/**
 * @author msw
 *
 */
public interface Certificate {

    public enum CertificateType { 

        CERT("Trusted Cert (pem)"), CERT_REQ("CSR (pem)");
        
        private final String label;
        
        private CertificateType(String label) {
            this.label = label;
        }

        /**
         * @return the label
         */
        public String getLabel() {
            return label;
        }
    }
    
    public CertificateType[] getCertificateTypes();
        
    public CertificateType getCertType();
    public void setCertType(CertificateType certType);
    
    public UploadedFile getFile();
    public void setFile(UploadedFile file);
    
    public String getAlias();
    public void setAlias(String alias);
    
    public String getPemString();
    public void setPemString(String pemString);

}
