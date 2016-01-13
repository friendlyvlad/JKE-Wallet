package org.vaadin.backend.domain;

import com.vividsolutions.jts.geom.Point;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Date;

/**
 * A standard JPA entity, like in any other Java application.
 */
@NamedQueries({
        @NamedQuery(name="Customer.findAll",
                query="SELECT c FROM Customer c"),
        @NamedQuery(name="Customer.findByName",
                query="SELECT c FROM Customer c WHERE LOWER(c.firstName) LIKE :filter OR LOWER(c.lastName) LIKE :filter OR LOWER(c.username) LIKE :filter"),
})
@Entity
public class Customer implements Serializable {

    @Id
    private String username;

    private String firstName;

    private String lastName;

    private CustomerStatus status;

    private Gender gender;
    
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date birthDate;

    @NotNull(message = "Email is required")
    @Pattern(regexp = ".+@.+\\.[a-z]+", message = "Must be valid email")
    private String email;

    //@Lob
    //private Point location;

    /**
     * Get the value of email
     *
     * @return the value of email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Set the value of email
     *
     * @param email new value of email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Get the value of status
     *
     * @return the value of status
     */
    public CustomerStatus getStatus() {
        return status;
    }

    /**
     * Set the value of status
     *
     * @param status new value of status
     */
    public void setStatus(CustomerStatus status) {
        this.status = status;
    }


    /**
     * Get the value of lastName
     *
     * @return the value of lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Set the value of lastName
     *
     * @param lastName new value of lastName
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Get the value of firstName
     *
     * @return the value of firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Set the value of firstName
     *
     * @param firstName new value of firstName
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }
    
    public Date getBirthDate() {
        return birthDate;
    }
  
    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }
/*
    public void setLocation(Point location) {
        this.location = location;
    }


    public Point getLocation() {
        return location;
    }
*/
    public boolean isPersisted() {
        if (username != null){
        	return true;
        }
        else
        	return false;
    }
}
