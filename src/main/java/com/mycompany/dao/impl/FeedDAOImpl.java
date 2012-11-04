package com.mycompany.dao.impl;

import com.mycompany.dao.*;
import com.mycompany.entity.Company;
import com.mycompany.entity.Feed;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: fermin
 * Date: 3/11/12
 * Time: 18:36
 * To change this template use File | Settings | File Templates.
 */
@Repository
public class FeedDAOImpl implements FeedDAO {

    private EntityManager em;

    public FeedDAOImpl() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("HibernateApp");
        em = emf.createEntityManager();
    }

    public void create(Feed f) throws DuplicatedFeedException, CompanyNotFoundException {
        Feed fe = em.find(Feed.class, f.getId());
        if (fe != null)
            throw new DuplicatedFeedException();
        else {
            /* Check that the associated company is ok */
            Company c;
            if (f.getCompany() == null || (c = em.find(Company.class, f.getCompany().getName())) == null ) {
                throw new CompanyNotFoundException();
            }
            else {
                /* We add the feed to the company feeds collection and persist the Company object */
                c.getFeeds().add(f);
                em.getTransaction().begin();
                em.persist(c);
                em.getTransaction().commit();
            }
        }
    }

    public Feed read(int id) throws FeedNotFoundException {
        Feed fe = em.find(Feed.class, id);
        if (fe == null)
            throw new FeedNotFoundException();
        else
            return fe;
    }

    public void update(Feed f) throws FeedNotFoundException, CompanyNotFoundException {
        Feed fe = em.find(Feed.class, f.getId());
        if (fe == null)
            throw new FeedNotFoundException();
        else {
            /* Check that the associated company is ok */
            if (f.getCompany() == null || em.find(Company.class, f.getCompany().getName()) == null ) {
                throw new CompanyNotFoundException();
            }
            else {
                /* Is this the usual way of doing an update DAO? */
                fe.setCompany(f.getCompany());
                fe.setName(f.getName());
                fe.setPosts(f.getPosts());
                fe.setSubscribedUsers(f.getSubscribedUsers());
                em.getTransaction().begin();
                em.persist(fe);
                em.getTransaction().commit();
            }
        }
    }

    public void delete(Feed f) throws FeedNotFoundException {
        Company fe = em.find(Company.class, f.getId());
        if (fe == null)
            throw new FeedNotFoundException();
        else {
            em.getTransaction().begin();
            em.remove(fe);
            em.getTransaction().commit();
        }
    }

    // TODO: not working yet (a named query need to be defined in Feed class)
    public List<Feed> findFeedsByCompany(Company c) throws CompanyNotFoundException {
        TypedQuery<Feed> q = em.createNamedQuery("Feed.findAllByCompany", Feed.class);
        return q.getResultList();
    }

}
