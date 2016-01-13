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
        @NamedQuery(name="Payee.findAll",
                query="SELECT pe FROM Payee pe"),
        @NamedQuery(name="Payee.findByName",
                query="SELECT pe FROM Payee pe WHERE LOWER(pe.username) LIKE :filter"),
        @NamedQuery(name = "Payee.uniquePayees", 
            	query = "SELECT distinct pe.name FROM Payee pe"),
})
@Entity
public class Payee implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    
   	private String name;
    
    private String username;
    
    private int account;

	public int getId() {
        return id;
    }

	public void setId(int id) {
        this.id = id;
    }


	public int getAccount() {
        return account;
    }

    public void setAccount(int account) {
        this.account = account;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    
    public boolean isPersisted() {
        return id > 0;
    }

}
