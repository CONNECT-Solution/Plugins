/**
 * 
 */
package org.connectopensource.interopgui.view;

import java.util.Date;

import org.connectopensource.interopgui.dataobject.Gender;

/**
 * @author msw
 *
 */
public interface Patient {
    public Gender[] getGenders();
    
    public String getFirstName();
    public void setFirstName(String firstName);
    
    public String getLastName();
    public void setLastName(String lastName);
    
    public Date getDateOfBirth();
    public void setDateOfBirth(Date dateOfBirth);
    
    public Gender getGender();
    public void setGender(Gender gender);
}
