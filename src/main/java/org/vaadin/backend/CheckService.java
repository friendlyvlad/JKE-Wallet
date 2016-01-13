package org.vaadin.backend;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.vaadin.backend.domain.Check;
import org.vaadin.backend.domain.AccountType;
import org.vaadin.backend.domain.Account;
import org.vaadin.backend.domain.Gender;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;
import java.util.Calendar;
import java.util.List;
import java.util.Arrays;
import java.util.Random;

@Stateless
public class CheckService {

    @PersistenceContext(unitName = "check-pu")
    private EntityManager entityManager;

	GregorianCalendar currDate = new GregorianCalendar();

    public void saveOrPersist(Check entity) {
        if (entity.getId() > 0) {
            entityManager.merge(entity);
        } else {
            entityManager.persist(entity);
        }
    }

    public void deleteEntity(Check entity) {
        if (entity.getId() > 0) {
            // reattach to remove
            entity = entityManager.merge(entity);
            entityManager.remove(entity);
        }
    }

    public List<Check> findAll() {
        CriteriaQuery<Check> cq = entityManager.getCriteriaBuilder().
                createQuery(Check.class);
        cq.select(cq.from(Check.class));
        return entityManager.createQuery(cq).getResultList();
    }

    public List<Check> findByName(String filter) {
        if (filter == null || filter.isEmpty()) {
            return findAll();
        }
        filter = filter.toLowerCase();
        return entityManager.createNamedQuery("Check.findByName",
                Check.class)
                .setParameter("filter", filter + "%").getResultList();
    }
    
    public List<Check> findFutureChecks(String authname){
	    
	    List<Check> allChecks = findByName(authname);
	    List<Check> futureChecks = new ArrayList<Check>();
    	
    	for(int i = 0; i < allChecks.size(); i++){
    		if(!allChecks.get(i).getDate().before(currDate.getTime()))
    			futureChecks.add(allChecks.get(i));
    	}
    	return futureChecks;
    }


	public List<Check> findPastChecks(String authname){
	    
	    List<Check> allChecks = findByName(authname);
	    List<Check> pastChecks = new ArrayList<Check>();
    	
    	for(int i = 0; i < allChecks.size(); i++){
    		if(allChecks.get(i).getDate().before(currDate.getTime()))
    			pastChecks.add(allChecks.get(i));
    	}
    	return pastChecks;
    }
    
    public void addDemoData(String authname){
    	
    	GregorianCalendar tempDate = new GregorianCalendar();
    	
    	tempDate.add(Calendar.DAY_OF_MONTH, 1);
    	Check foo = new Check();
   		foo.setUsername(authname);
   		foo.setDescription("Groceries");
   		foo.setAccount("Checking 1");
    	foo.setAmount(150);
    	foo.setDate(tempDate.getTime());
    	saveOrPersist(foo);
    	
    	tempDate.add(Calendar.DAY_OF_MONTH, 2);
    	foo = new Check();
   		foo.setUsername(authname);
   		foo.setDescription("Fantasy Football");
   		foo.setAccount("Checking 1");
    	foo.setAmount(200);
    	foo.setDate(tempDate.getTime());
    	saveOrPersist(foo);
    	
    	tempDate.add(Calendar.DAY_OF_MONTH, 2);
    	foo = new Check();
   		foo.setUsername(authname);
   		foo.setDescription("Lawn services");
   		foo.setAccount("Checking 2");
    	foo.setAmount(50);
    	foo.setDate(tempDate.getTime());
    	saveOrPersist(foo);
    	
    	tempDate.add(Calendar.DAY_OF_MONTH, 2);
    	foo = new Check();
   		foo.setUsername(authname);
   		foo.setDescription("Daycare for kiddos");
   		foo.setAccount("Checking 1");
    	foo.setAmount(275);
    	foo.setDate(tempDate.getTime());
    	saveOrPersist(foo);
    }

}
