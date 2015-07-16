/**
 * 
 */
package org.connectopensource.interopgui.dataobject;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.connectopensource.interopgui.view.Patient;

/**
 * @author msw
 */
@Entity
@Table(name="patient")
public class PatientInfo implements Patient {
    
    private Long id;
    private String firstName;
    private String lastName;
    private Date dateOfBirth;
    private Gender gender;
    private OrganizationInfo organizationInfo;

    
    @Transient
    public Gender[] getGenders() {
        return Gender.values();
    }
    
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
     * @return the firstName
     */
    @Column(name = "first")
    public String getFirstName() {
        return firstName;
    }
    /**
     * @param firstName the firstName to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    /**
     * @return the lastName
     */
    @Column(name = "last")
    public String getLastName() {
        return lastName;
    }
    /**
     * @param lastName the lastName to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    /**
     * @return the dateOfBirth
     */
    @Column(name = "dob")
    @Temporal(TemporalType.DATE)
    public Date getDateOfBirth() {
        return dateOfBirth;
    }
    /**
     * @param dateOfBirth the dateOfBirth to set
     */
    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    /**
     * @return the gender
     */
    @Column(name="gender")
    @Enumerated(EnumType.STRING)
    public Gender getGender() {
        return gender;
    }
    /**
     * @param gender the gender to set
     */
    public void setGender(Gender gender) {
        this.gender = gender;
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

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "PatientInfo [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", dateOfBirth="
                + dateOfBirth + ", gender=" + gender + ", organizationInfo=" + organizationInfo.getId() + "]";
    }
    
    

}
