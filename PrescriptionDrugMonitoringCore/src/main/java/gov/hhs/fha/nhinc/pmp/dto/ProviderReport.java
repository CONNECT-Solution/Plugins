/**
 *
 */
package gov.hhs.fha.nhinc.pmp.dto;

/**
 * @author mpnguyen
 *
 */
public class ProviderReport {
    private String providerName;
    private String address;
    private String city;
    private String state;
    private String zipCode;
    private String dea;

    /**
     * @return the providerName
     */
    public String getProviderName() {
        return providerName;
    }

    /**
     * @param providerName the providerName to set
     */
    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    /**
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param address the address to set
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * @return the city
     */
    public String getCity() {
        return city;
    }

    /**
     * @param city the city to set
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * @return the state
     */
    public String getState() {
        return state;
    }

    /**
     * @param state the state to set
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * @return the zipCode
     */
    public String getZipCode() {
        return zipCode;
    }

    /**
     * @param zipCode the zipCode to set
     */
    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    /**
     * @return the dea
     */
    public String getDea() {
        return dea;
    }

    /**
     * @param dea the dea to set
     */
    public void setDea(String dea) {
        this.dea = dea;
    }

}
