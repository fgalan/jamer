package com.mycompany.dao.impl;

import com.mycompany.entity.Company;
import com.mycompany.dao.CompanyDAO;
import com.mycompany.dao.CompanyNotFoundException;
import com.mycompany.dao.DuplicatedCompanyException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Created with IntelliJ IDEA.
 * User: fermin
 * Date: 3/11/12
 * Time: 18:36
 * To change this template use File | Settings | File Templates.
 */
public class CompanyDAOImpl implements CompanyDAO {

    private EntityManager em;

    public CompanyDAOImpl() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("HibernateApp");
        em = emf.createEntityManager();
    }

    public void create(Company c) throws DuplicatedCompanyException {
        Company co = em.find(Company.class, c.getName());
        if (co != null)
            throw new DuplicatedCompanyException();
        else {
            em.getTransaction().begin();
            em.persist(c);
            em.getTransaction().commit();
        }
    }

    public Company read(String name) throws CompanyNotFoundException {
        Company co = em.find(Company.class, name);
        if (co == null)
            throw new CompanyNotFoundException();
        else
            return co;
    }

    public void delete(Company c) throws CompanyNotFoundException {
        Company co = em.find(Company.class, c.getName());
        if (co == null)
            throw new CompanyNotFoundException();
        else {
            em.getTransaction().begin();
            em.remove(co);
            em.getTransaction().commit();
        }
    }

}
