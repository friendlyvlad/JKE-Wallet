
package org.vaadin.backend;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.vaadin.backend.domain.Payment;
import org.vaadin.backend.domain.Payee;
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
import java.util.Date;
import java.util.GregorianCalendar;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import javax.security.auth.Subject;
import java.security.Principal;
import javax.security.auth.login.CredentialExpiredException;
import com.ibm.websphere.security.auth.WSSubject;

import javax.security.auth.Subject;
import java.security.Principal;
import javax.security.auth.login.CredentialExpiredException;
import com.ibm.websphere.security.auth.WSSubject;

@Stateless
public class PaymentService {

    @PersistenceContext(unitName = "payment-pu")
    private EntityManager entityManager;

	GregorianCalendar currDate = new GregorianCalendar();

    public void saveOrPersist(Payment entity) {
        if (entity.getAmount() != 0) {
            entityManager.merge(entity);
        } else {
            entityManager.persist(entity);
        }
    }

    public void deleteEntity(Payment entity) {
        if (entity.getAmount() != 0) {
            // reattach to remove
            entity = entityManager.merge(entity);
            entityManager.remove(entity);
        }
    }
    
/*    public void deleteEntity(Payment entity, String auth) {
        if (entity.getAmount() != 0) {
            // reattach to remove
            entity.setProperty("USERNAME", auth);
            entity = entityManager.merge(entity);
            entityManager.remove(entity);
        }
    }
*/

    public List<Payment> findAll() {
        CriteriaQuery<Payment> cq = entityManager.getCriteriaBuilder().
                createQuery(Payment.class);
        cq.select(cq.from(Payment.class));
    
        return entityManager.createQuery(cq).getResultList();
    }


    public List<Payment> findByName(String filter) {
        if (filter == null || filter.isEmpty()) {
            return findAll();
        }
        filter = filter.toLowerCase();
        return entityManager.createNamedQuery("Payment.findByName",
                Payment.class)
                .setParameter("filter", filter + "%").getResultList();
    }


//  will need to add a future and past payments method here   
    public List<Payment> findFuturePayments(String authname){
	    
	    List<Payment> allPayments = findByName(authname);
	    List<Payment> futurePayments = new ArrayList<Payment>();
    	
    	for(int i = 0; i < allPayments.size(); i++){
    		if(!allPayments.get(i).getDate().before(currDate.getTime()))
    			futurePayments.add(allPayments.get(i));
    	}
    	return futurePayments;
    }


	public List<Payment> findPastPayments(String authname){
	    
	    List<Payment> allPayments = findByName(authname);
	    List<Payment> pastPayments = new ArrayList<Payment>();
    	
    	for(int i = 0; i < allPayments.size(); i++){
    		if(allPayments.get(i).getDate().before(currDate.getTime()))
    			pastPayments.add(allPayments.get(i));
    	}
    	return pastPayments;
    }
    
    public void addDemoData(String authname){
		
		GregorianCalendar tempDate = new GregorianCalendar();
		
		tempDate.add(Calendar.DAY_OF_MONTH, 2);
    	Payment foo = new Payment();
   		foo.setUsername(authname);
   		foo.setPayee("Watson Water Works");
   		foo.setAccType("Checking 2");
    	foo.setAmount(150);
    	foo.setDate(tempDate.getTime());
    	saveOrPersist(foo);
    	
    	tempDate.add(Calendar.DAY_OF_MONTH, 2);
    	foo = new Payment();
   		foo.setUsername(authname);
   		foo.setPayee("Bluemix Electric");
   		foo.setAccType("Checking 1");
    	foo.setAmount(200);
    	foo.setDate(tempDate.getTime());
    	saveOrPersist(foo);
    	
    	tempDate.add(Calendar.DAY_OF_MONTH, 2);
    	foo = new Payment();
   		foo.setUsername(authname);
   		foo.setPayee("IBM Payroll");
   		foo.setAccType("Savings");
    	foo.setAmount(-3000);
    	foo.setDate(tempDate.getTime());
    	saveOrPersist(foo);
    	
    	tempDate.add(Calendar.DAY_OF_MONTH, 2);
    	foo = new Payment();
   		foo.setUsername(authname);
   		foo.setPayee("DevOps Gas Services");
   		foo.setAccType("Checking 1");
    	foo.setAmount(150);
    	foo.setDate(tempDate.getTime());
    	saveOrPersist(foo);
    }

}
