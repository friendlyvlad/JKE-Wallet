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
        @NamedQuery(name="Account.findAll",
                query="SELECT a FROM Account a"),
        @NamedQuery(name="Account.findByName",
                query="SELECT a FROM Account a WHERE LOWER(a.username) LIKE :filter"),
})
@Entity
// Account needs to be a 10 digit account number. will start with 1000000000 and use sequence generator to create new account numbers.
// Errors with seq. removing and will come back if time...
//@SequenceGenerator(name="seq", initialValue=1000000000, allocationSize=10000)
public class Account implements Serializable {


	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int id;

	private String username;
    
    private String accountName;
    
    private int balance;
    
    private AccountType accountType;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

	public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }
    
    public AccountType getAccountType() {
        return accountType;
    }
    
    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }
    
    public void addTransaction(int amount){
    	this.balance = this.balance - amount;
    }
  
    public boolean isPersisted() {
        return id > 0;
    }

}
