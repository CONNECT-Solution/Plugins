/**
 * 
 */
package org.connectopensource.interopgui.view;

/**
 * @author msw
 *
 */
public interface Document {

    public String getDocumentId();
    public void setDocumentId(String documentId);
    
    public String getDocumentType();
    public void setDocumentType(String documentType);
    
    public String getComment();
    public void setComment(String comment);
}
