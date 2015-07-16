/**
 * 
 */
package org.connectopensource.interopgui.dataobject;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.connectopensource.interopgui.view.Document;

/**
 * @author msw
 *
 */
@Entity
@Table(name="document")
public class DocumentInfo implements Document {
    
    private Long id;
    private String documentId;
    private String documentType;
    private String comment;
    private OrganizationInfo organizationInfo;
    
    /**
     * @return the id
     */    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)    
    public Long getId() {
        return id;
    }
    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }
    /**
     * @return the documentId
     */
    @Column(name = "docid")
    public String getDocumentId() {
        return documentId;
    }
    /**
     * @param documentId the documentId to set
     */
    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
    /**
     * @return the document type
     */
    @Column(name = "doctype")
    public String getDocumentType() {
        return documentType;
    }
    /**
     * @see documentType the documentType to set
     */
    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }
    /**
     * @return the comment
     */
    @Column(name = "comment")
    public String getComment() {
        return comment;
    }
    /**
     * @param comment the comment to set
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * @return the organizationInfo
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "orginfo_id", nullable = false)
    public OrganizationInfo getOrganizationInfo() {
        return organizationInfo;
    }
    /**
     * @param organizationInfo the organizationInfo to set
     */
    public void setOrganizationInfo(OrganizationInfo organizationInfo) {
        this.organizationInfo = organizationInfo;
    }
    
    
}
