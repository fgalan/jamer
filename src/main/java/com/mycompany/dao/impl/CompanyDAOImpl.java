package com.mycompany.dao.impl;

import com.mycompany.dao.exception.CompanyConstraintsViolationException;
import com.mycompany.entity.Company;
import com.mycompany.dao.CompanyDAO;
import com.mycompany.dao.exception.CompanyNotFoundException;
import com.mycompany.dao.exception.DuplicatedCompanyException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: fermin
 * Date: 3/11/12
 * Time: 18:36
 * To change this template use File | Settings | File Templates.
 */
@Repository
public class CompanyDAOImpl implements CompanyDAO {

    @PersistenceContext
    private EntityManager em;

    public CompanyDAOImpl() {
    }

    public CompanyDAOImpl(EntityManager em) {
        this.em = em;
    }

    @Transactional
    public Company create(String name)
            throws DuplicatedCompanyException, CompanyConstraintsViolationException {
        Company c = new Company();
        c.setName(name);
        try {
            em.persist(c);
        }
        catch (EntityExistsException e) {
            throw new DuplicatedCompanyException();
        }
        catch (PersistenceException e) {
            /* This is raised when nullable constraint is violated */
            throw new CompanyConstraintsViolationException();
        }
        catch (ConstraintViolationException e) {
            /* This is raised when size constraint is violated */
            throw new CompanyConstraintsViolationException();
        }
        return c;
    }

    public Company load(String name) throws CompanyNotFoundException {
        Query q = em.createQuery("SELECT c FROM Company c WHERE name = ?1");
        q.setParameter(1,name);

        try {
            return (Company) q.getSingleResult();
        }
        catch (NoResultException e) {
            throw new CompanyNotFoundException();
        }

    }

    @Transactional
    /* We use a similar pattern to the one shown in the JPA tutorial:
       http://docs.oracle.com/javaee/6/tutorial/doc/bnbqw.html#bnbre */
    public void delete(String name) throws CompanyNotFoundException {
        Company c = load(name);
        em.remove(c);
    }

    public List<Company> findAll(int limit, int offset) {
        if ((limit <= 0) || (offset < 0)) {
            /* Returning empty list in this case */
            return new Vector<Company>();
        }
        TypedQuery<Company> q = em.createNamedQuery("Company.findAll", Company.class)
                .setMaxResults(limit)
                .setFirstResult(offset);
        return q.getResultList();
    }

    public int countAll()  {
        Query q = em.createNamedQuery("Company.countAll");
        return ((Long) q.getSingleResult()).intValue();
    }

}
