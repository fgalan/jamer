package com.mycompany.dao.impl;

import com.mycompany.dao.*;
import com.mycompany.dao.exception.DuplicatedFeedException;
import com.mycompany.dao.exception.FeedConstraintsViolationException;
import com.mycompany.dao.exception.FeedNotFoundException;
import com.mycompany.dao.exception.NullCompanyException;
import com.mycompany.entity.Company;
import com.mycompany.entity.Feed;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: fermin
 * Date: 8/12/12
 * Time: 17:26
 * To change this template use File | Settings | File Templates.
 */
@Repository
public class FeedDAOImpl implements FeedDAO {

    @PersistenceContext
    private EntityManager em;

    public FeedDAOImpl() {
    }

    public FeedDAOImpl(EntityManager em) {
        this.em = em;
    }

    @Transactional
    public Feed create(String name, Company c) throws DuplicatedFeedException, FeedConstraintsViolationException,
            NullCompanyException {

        if (c == null) {
            throw new NullCompanyException();
        }

        Feed f = new Feed();
        f.setName(name);
        f.setCompany(c);
        try {
            em.persist(f);
        }
        catch (EntityExistsException e) {
            throw new DuplicatedFeedException();
        }
        catch (PersistenceException e) {
            /* This is raised when nullable constraint is violated */
            throw new FeedConstraintsViolationException();
        }
        catch (ConstraintViolationException e) {
            /* This is raised when size constraint is violated */
            throw new FeedConstraintsViolationException();
        }
        return f;
    }

    public Feed load(String name, Company c) throws FeedNotFoundException, NullCompanyException {

        if (c == null) {
            throw new NullCompanyException();
        }

        Query q = em.createQuery("SELECT f FROM Feed f WHERE f.name = ?1 AND f.company.name = ?2");
        q.setParameter(1,name);
        q.setParameter(2,c.getName());

        try {
            /* Note that, by construction, as much one result is obtained. If this
               fails, an un-cached error will be raised when invoking getSingleResult */
            return (Feed) q.getSingleResult();
        }
        catch (NoResultException e) {
            throw new FeedNotFoundException();
        }
    }

    @Transactional
    /* We use a similar pattern to the one shown in the JPA tutorial:
       http://docs.oracle.com/javaee/6/tutorial/doc/bnbqw.html#bnbre */
    public void delete(String name, Company c) throws FeedNotFoundException, NullCompanyException {
        if (c == null) {
            throw new NullCompanyException();
        }
        Feed f = load(name, c);
        em.remove(f);
    }

    public List<Feed> findAllByCompany(Company c, int limit, int offset) throws NullCompanyException {
        if (c == null) {
            throw new NullCompanyException();
        }

        if ((limit <= 0) || (offset < 0)) {
            /* Returning empty list in this case */
            return new Vector<Feed>();
        }

        Query q = em.createQuery("SELECT f FROM Feed f WHERE f.company.name = ?1 ORDER BY f.name");
        q.setParameter(1,c.getName());
        q.setMaxResults(limit);
        q.setFirstResult(offset);

        return q.getResultList();

    }

    public int countAllByCompany(Company c) throws NullCompanyException {
        if (c == null) {
            throw new NullCompanyException();
        }

        Query q = em.createQuery("SELECT COUNT(*) FROM Feed f WHERE f.company.name = ?1");
        q.setParameter(1,c.getName());

        return ((Long) q.getSingleResult()).intValue();

    }
}
