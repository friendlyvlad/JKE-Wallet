
package org.vaadin.backend;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.vaadin.backend.domain.Payee;
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
public class PayeeService {

    @PersistenceContext(unitName = "payee-pu")
    private EntityManager entityManager;

    public void saveOrPersist(Payee entity) {
        if (entity.getName() != null) {
            entityManager.merge(entity);
        } else {
            entityManager.persist(entity);
        }
    }

    public void deleteEntity(Payee entity) {
        if (entity.getName() != null) {
            // reattach to remove
            entity = entityManager.merge(entity);
            entityManager.remove(entity);
        }
    }

    public List<Payee> findAll() {
        CriteriaQuery<Payee> cq = entityManager.getCriteriaBuilder().
                createQuery(Payee.class);
        cq.select(cq.from(Payee.class));
        return entityManager.createQuery(cq).getResultList();
    }

    public List<Payee> findByName(String filter) {
        if (filter == null || filter.isEmpty()) {
            return findAll();
        }
        filter = filter.toLowerCase();
        return entityManager.createNamedQuery("Payee.findByName",
                Payee.class)
                .setParameter("filter", filter + "%").getResultList();
    }
    
    public List<Payee> uniquePayees() {
        CriteriaQuery<Payee> cq = entityManager.getCriteriaBuilder().
                createQuery(Payee.class);
        cq.select(cq.from(Payee.class)).distinct(true);
        return entityManager.createQuery(cq).getResultList();
    }
    
    public String[] findPayeeNames(String authname){
    	List<Payee> uniquePayees = findByName(authname);
    	String[] names = new String[uniquePayees.size()];
    	for(int i = 0; i < uniquePayees.size(); i++){
    		names[i] = uniquePayees.get(i).getName();
    	}
    	return names;
    }
    
    public void addDemoData(String authname){
    	List payees = Arrays.asList(findPayeeNames(authname));
    	
    	if(!payees.contains("Watson Water Works")){
    		Payee foo = new Payee();
    		foo.setName("Watson Water Works");
    		foo.setAccount(123);
    		foo.setUsername(authname);
    		saveOrPersist(foo);
    	}
		if(!payees.contains("Bluemix Electric")){
			Payee foo = new Payee();
    		foo.setName("Bluemix Electric");
    		foo.setAccount(88);
    		foo.setUsername(authname);
    		saveOrPersist(foo);
		}
		if(!payees.contains("DevOps Gas Services")){
			Payee foo = new Payee();
    		foo.setName("DevOps Gas Services");
    		foo.setAccount(321);
    		foo.setUsername(authname);
    		saveOrPersist(foo);
		}
		if(!payees.contains("IBM Payroll")){
			Payee foo = new Payee();
    		foo.setName("IBM Payroll");
    		foo.setAccount(512);
    		foo.setUsername(authname);
    		saveOrPersist(foo);
		}
    }
}
