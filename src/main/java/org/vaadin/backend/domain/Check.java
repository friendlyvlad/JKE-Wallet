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
        @NamedQuery(name="Check.findAll",
                query="SELECT c FROM Check c"),
        @NamedQuery(name="Check.findByName",
                query="SELECT c FROM Check c WHERE LOWER(c.username) LIKE :filter"),
})
@Entity
public class Check implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String username;
    
    private String description;

    //foreign key that maps to Account
	//@ManyToOne(fetch = FetchType.LAZY)
    //@JoinColumn(name = "ID")
    private String account;
    
    private int amount;

    @Temporal(javax.persistence.TemporalType.DATE)
    private Date date;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

	public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

	public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Date getDate() {
        return date;
    }
  
    public void setDate(Date date) {
        this.date = date;
    }
    
    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }
    
    public boolean isPersisted() {
        return id > 0;
    }

}
