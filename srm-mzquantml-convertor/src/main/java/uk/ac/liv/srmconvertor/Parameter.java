
package uk.ac.liv.srmconvertor;

/**
 *
 * @author Da Qi
 * @institute University of Liverpool
 * @time 19-Jan-2015 15:04:54
 */
public class Parameter {

    private String firstName;
    private String lastName;
    private String org;
//    private boolean absQuant;
//    private double refQuant;
    
    public Parameter(){
        this.firstName = "";
        this.lastName = "";
        this.org = "";
//        this.absQuant = false;
//        this.refQuant = 1.0;
    }

    /**
     * @return the firstName
     */
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
     * @return the org
     */
    public String getOrg() {
        return org;
    }

    /**
     * @param org the org to set
     */
    public void setOrg(String org) {
        this.org = org;
    }

//    /**
//     * @return the absQuant
//     */
//    public boolean isAbsQuant() {
//        return absQuant;
//    }
//
//    /**
//     * @param absQuant the absQuant to set
//     */
//    public void setAbsQuant(boolean absQuant) {
//        this.absQuant = absQuant;
//    }
//
//    /**
//     * @return the refQuant
//     */
//    public double getRefQuant() {
//        return refQuant;
//    }
//
//    /**
//     * @param refQuant the refQuant to set
//     */
//    public void setRefQuant(double refQuant) {
//        this.refQuant = refQuant;
//    }
    
    
}
