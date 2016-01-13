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
        @NamedQuery(name="Transaction.findAll",
                query="SELECT t FROM Transaction t"),
        @NamedQuery(name="Transaction.findByName",
                query="SELECT t FROM Transaction t WHERE LOWER(t.username) LIKE :filter"),
})
@Entity
public class Transaction implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    //@Version int version;
    
    private int amount;

    @Temporal(javax.persistence.TemporalType.DATE)
    private Date date;
    
    //private AccountType accounttype;

    private String description;
    
    private String username;
    
    private String account;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

	public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

	public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    
   // public AccountType getAccountType() {
   //     return accounttype;
    //}
    
   // public void setAccountType(AccountType accounttype) {
    //    this.accounttype = accounttype;
    //}
    
    public Date getDate() {
        return date;
    }
  
    public void setDate(Date date) {
        this.date = date;
    }
    
    public boolean isPersisted() {
        return id > 0;
    }

}
