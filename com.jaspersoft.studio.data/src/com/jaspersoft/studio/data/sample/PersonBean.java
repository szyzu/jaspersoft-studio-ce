/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.data.sample;

public class PersonBean {
private String firstName;
    
    private String lastName;
    
    private String email;

    private HobbyBean[] hobbies;
    
    private AddressBean address;
    
    /** Creates a new instance of PersonBean */
    public PersonBean() {
	this(null);
    }
    
    public PersonBean(String name) {
    	this.setFirstName( name );
        hobbies = new HobbyBean[0];
    }
    
    /**
     * Getter for property firstName.
     * @return Value of property firstName.
     */
    public java.lang.String getFirstName() {
        return firstName;
    }
    
    /**
     * Setter for property firstName.
     * @param firstName New value of property firstName.
     */
    public void setFirstName(java.lang.String firstName) {
        this.firstName = firstName;
    }
    
    /**
     * Getter for property lastName.
     * @return Value of property lastName.
     */
    public java.lang.String getLastName() {
        return lastName;
    }
    
    /**
     * Setter for property lastName.
     * @param lastName New value of property lastName.
     */
    public void setLastName(java.lang.String lastName) {
        this.lastName = lastName;
    }
    
    /**
     * Getter for property hobbies.
     * @return Value of property hobbies.
     */
    public HobbyBean[] getHobbies() {
        return this.hobbies;
    }
    
    /**
     * Setter for property hobbies.
     * @param hobbies New value of property hobbies.
     */
    public void setHobbies(HobbyBean[] hobbies) {
        this.hobbies = hobbies;
    }
    
    /**
     * Getter for property address.
     * @return Value of property address.
     */
    public AddressBean getAddress() {
        return address;
    }
    
    /**
     * Setter for property address.
     * @param address New value of property address.
     */
    public void setAddress(AddressBean address) {
        this.address = address;
    }
    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
