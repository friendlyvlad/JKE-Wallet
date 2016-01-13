// changes for the demo in Rio
package org.vaadin.backend;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.vaadin.backend.domain.Customer;
import org.vaadin.backend.domain.CustomerStatus;
import org.vaadin.backend.domain.Gender;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

@Stateless
public class CustomerService {

    @PersistenceContext(unitName = "customer-pu")
    private EntityManager entityManager;

    public void saveOrPersist(Customer entity) {
        if (entity.getUsername() != null) {
            entityManager.merge(entity);
        } else {
            entityManager.persist(entity);
        }
    }

    public void deleteEntity(Customer entity) {
        if (entity.getUsername() != null) {
            // reattach to remove
            entity = entityManager.merge(entity);
            entityManager.remove(entity);
        }
    }

    public List<Customer> findAll() {
        CriteriaQuery<Customer> cq = entityManager.getCriteriaBuilder().
                createQuery(Customer.class);
        cq.select(cq.from(Customer.class));
        return entityManager.createQuery(cq).getResultList();
    }

    public List<Customer> findByName(String filter) {
        if (filter == null || filter.isEmpty()) {
            return findAll();
        }
        filter = filter.toLowerCase();
        return entityManager.createNamedQuery("Customer.findByName",
                Customer.class)
                .setParameter("filter", filter + "%").getResultList();
    }

}
