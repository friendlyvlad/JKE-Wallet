
package org.vaadin.backend;

import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.vaadin.backend.domain.Payment;
import org.vaadin.backend.domain.Account;
import org.vaadin.backend.domain.Check;
import org.vaadin.backend.domain.Transaction;

import org.vaadin.backend.PaymentService;
import org.vaadin.backend.CheckService;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.vaadin.backend.domain.Transaction;
import org.vaadin.backend.domain.AccountType;
import org.vaadin.backend.domain.Account;
import org.vaadin.backend.domain.Gender;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;
import java.util.Calendar;
import java.util.List;
import java.util.Arrays;
import java.util.Random;

@Stateless
public class TransactionService {

    @PersistenceContext(unitName = "transaction-pu")
    private EntityManager entityManager;

	@Inject
    private PaymentService pservice;
    
    @Inject
    private CheckService cservice;
    
    @Inject
    private AccountService aservice;

    public void saveOrPersist(Transaction entity) {
        if (entity.getId() > 0) {
            entityManager.merge(entity);
        } else {
            entityManager.persist(entity);
        }
    }

    public void deleteEntity(Transaction entity) {
        if (entity.getId() > 0) {
            // reattach to remove
            entity = entityManager.merge(entity);
            entityManager.remove(entity);
        }
    }

    public List<Transaction> findAll() {
        CriteriaQuery<Transaction> cq = entityManager.getCriteriaBuilder().
                createQuery(Transaction.class);
        cq.select(cq.from(Transaction.class));
        return entityManager.createQuery(cq).getResultList();
    }

    public List<Transaction> findByName(String filter) {
        if (filter == null || filter.isEmpty()) {
            return findAll();
        }
        filter = filter.toLowerCase();
        return entityManager.createNamedQuery("Transaction.findByName",
                Transaction.class)
                .setParameter("filter", filter + "%").getResultList();
    }
    
    //change to void method called updat
    //use before or after any new payment/check/etc.
    public void updateTransactions(String authname){
	    
	    List<Check> checks = cservice.findPastChecks(authname);
	    List<Payment> payments = pservice.findPastPayments(authname);
	    List<Account> accounts = aservice.findByName(authname);
	    List<Transaction> transactions = new ArrayList<Transaction>();
    	
    	for(int i = 0; i < checks.size(); i++){
    		Transaction temp = new Transaction();
    		
    		temp.setDate(checks.get(i).getDate());
    		temp.setUsername(checks.get(i).getUsername());
    		temp.setAccount(checks.get(i).getAccount());
    		temp.setAmount(checks.get(i).getAmount());
    		temp.setDescription(checks.get(i).getDescription());
    		
    		transactions.add(temp);
    		
    		//save to trans table
    		saveOrPersist(temp);
    		
    		//commit change to balance of appropriate account
    		for(int j = 0; j < accounts.size(); j++){
    			if(accounts.get(j).getAccountName().equals(temp.getAccount()))
    				accounts.get(j).addTransaction(temp.getAmount());
    				aservice.saveOrPersist(accounts.get(j));
    		}
    		
    		//remove from checks table
    		cservice.deleteEntity(checks.get(i));
    	}
    	
    	for(int i = 0; i < payments.size(); i++){
    		Transaction temp = new Transaction();
    		
    		temp.setDate(payments.get(i).getDate());
    		temp.setUsername(payments.get(i).getUsername());
    		temp.setAccount(payments.get(i).getAccType());
    		temp.setAmount(payments.get(i).getAmount());
    		if(payments.get(i).getAmount() < 0)
    			temp.setDescription("Payment from " + payments.get(i).getPayee());
    		else
    			temp.setDescription("Payment to " + payments.get(i).getPayee());
    			
    		transactions.add(temp);
    		
    		//save to trans table
    		saveOrPersist(temp);
    		
    		//commit change to balance of appropriate account
    		for(int j = 0; j < accounts.size(); j++){
    			if(accounts.get(j).getAccountName().equals(temp.getAccount()))
    				accounts.get(j).addTransaction(temp.getAmount());
    				aservice.saveOrPersist(accounts.get(j));
    		}
    		
    		//remove from checks table
    		pservice.deleteEntity(payments.get(i));
    	}
    	
    	//return transactions;
    }

}
