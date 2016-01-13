//for demo in L&L
package org.vaadin.backend;

import org.vaadin.backend.domain.Payment;
import java.util.ArrayList;
import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.vaadin.backend.PaymentService;
import org.vaadin.backend.CheckService;
import org.vaadin.backend.TransactionService;
import org.vaadin.backend.domain.Transaction;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.vaadin.backend.domain.Account;
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
public class AccountService {

    @PersistenceContext(unitName = "account-pu")
    private EntityManager entityManager;

	@Inject
    private TransactionService tservice;

    public void saveOrPersist(Account entity) {
        if (entity.getId() > 0) {
            entityManager.merge(entity);
        } else {
            entityManager.persist(entity);
        }
    }

    public void deleteEntity(Account entity) {
        if (entity.getId() > 0) {
            // reattach to remove
            entity = entityManager.merge(entity);
            entityManager.remove(entity);
        }
    }

    public List<Account> findAll() {
        CriteriaQuery<Account> cq = entityManager.getCriteriaBuilder().
                createQuery(Account.class);
        cq.select(cq.from(Account.class));
        return entityManager.createQuery(cq).getResultList();
    }

    public List<Account> findByName(String filter) {
        if (filter == null || filter.isEmpty()) {
            return findAll();
        }
        filter = filter.toLowerCase();
        return entityManager.createNamedQuery("Account.findByName",
                Account.class)
                .setParameter("filter", filter + "%").getResultList();
    }
    
    public String[] findAccounts(String authname){
    	//updateAccounts(authname);
    	List<Account> accounts = findByName(authname);
    	String[] names = new String[accounts.size()];
    	for(int i = 0; i < accounts.size(); i++){
    		names[i] = accounts.get(i).getAccountName();
    	}
    	return names;
    }
    
    public void updateAccounts(String authname){
    	List<Transaction> transactions = tservice.findByName(authname);
    	List<Account> accounts = findByName(authname);
    	
    	for(int i = 0; i < accounts.size(); i++){
    		//accounts.get(i).setBalance(0);
    		for(int j = 0; j < transactions.size(); j++){
    			if(accounts.get(i).getAccountName().equals(transactions.get(j).getAccount()))
    				accounts.get(i).addTransaction(transactions.get(j).getAmount());
    		}
    		saveOrPersist(accounts.get(i));
    	}
    }
    
    public String[] findCheckingAccounts(String authname){
    	//updateAccounts(authname);
    	List<Account> accounts = findByName(authname);
    	
    	for(int i = 0; i < accounts.size(); i++){
    		if(accounts.get(i).getAccountType() != AccountType.Checking){
    			accounts.remove(i);
    		}	
    	}
    	
    	String[] names = new String[accounts.size()];
    	
    	for(int i = 0; i < accounts.size(); i++){
    		names[i] = accounts.get(i).getAccountName();
    	}
    	return names;
    }
    
    public void addDemoData(String authname){
    	List accounts = Arrays.asList(findAccounts(authname));
    	
    	if(!accounts.contains("Checking 1")){
    		Account checking = new Account();
    		checking.setBalance(4500);
    		checking.setUsername(authname);
    		checking.setAccountName("Checking 1");
    		checking.setAccountType(AccountType.Checking);
    		saveOrPersist(checking);
    	}
    		
    	if(!accounts.contains("Checking 2")){
    		Account checking = new Account();
    		checking.setBalance(2500);
    		checking.setUsername(authname);
    		checking.setAccountName("Checking 2");
    		checking.setAccountType(AccountType.Checking);
    		saveOrPersist(checking);
    	}	
    		
		if(!accounts.contains("Savings")){
			Account savings = new Account();
			savings.setBalance(24000);
			savings.setUsername(authname);
			savings.setAccountName("Savings");
			savings.setAccountType(AccountType.Savings);
			saveOrPersist(savings);
		}
    }
}
