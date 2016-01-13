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
        @NamedQuery(name="Payment.findAll",
                query="SELECT p FROM Payment p"),
        @NamedQuery(name="Payment.findByName",
                query="SELECT p FROM Payment p WHERE LOWER(p.username) LIKE :filter"),
})
@Entity
public class Payment implements Serializable {

    //@Version int version;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    
    private String username;
    
    private int amount;

    private String accType;
    
    private String payee;
   
   	@Temporal(javax.persistence.TemporalType.DATE)
    private Date date;


	public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
    
    public String getAccType() {
        return accType;
    }

    public void setAccType(String accType) {
        this.accType = accType;
    }
    
    public String getPayee() {
        return payee;
    }

    public void setPayee(String payee) {
        this.payee = payee;
    }
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    
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
