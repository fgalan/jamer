package com.mycompany.dao.impl;

import com.mycompany.dao.exception.*;
import com.mycompany.dao.FeedSubscriptionDAO;
import com.mycompany.entity.Feed;
import com.mycompany.entity.FeedSubscription;
import com.mycompany.entity.User;
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
public class FeedSubscriptionDAOImpl implements FeedSubscriptionDAO {

    @PersistenceContext
    private EntityManager em;

    public FeedSubscriptionDAOImpl() {
    }

    public FeedSubscriptionDAOImpl(EntityManager em) {
        this.em = em;
    }

    @Transactional
    public FeedSubscription create(Feed f, User u) throws DuplicatedFeedSubscriptionException,
            NullUserException, NullFeedException {

        if (f == null) {
            throw new NullFeedException();
        }
        if (u == null) {
            throw new NullUserException();
        }

        FeedSubscription fs = new FeedSubscription();
        fs.setFeed(f);
        fs.setUser(u);

        // TODO: a try-catch need to be done to detect and raise DuplicatedFeedSubscriptionException
        em.persist(fs);

        return fs;
    }

    public FeedSubscription load(Feed f, User u) throws FeedSubscriptionNotFoundException,
        NullUserException, NullFeedException {

        if (f == null) {
            throw new NullFeedException();
        }
        if (u == null) {
            throw new NullUserException();
        }

        Query q = em.createQuery("SELECT fs FROM FeedSubscription fs WHERE fs.feed.name = ?1 AND fs.user.email = ?2");
        q.setParameter(1,f.getName());
        q.setParameter(2,u.getEmail());

        try {
            /* Note that, by construction, as much one result is obtained. If this
               fails, an un-cached error will be raised when invoking getSingleResult */
            return (FeedSubscription) q.getSingleResult();
        }
        catch (NoResultException e) {
            throw new FeedSubscriptionNotFoundException();
        }
    }

    @Transactional
    /* We use a similar pattern to the one shown in the JPA tutorial:
       http://docs.oracle.com/javaee/6/tutorial/doc/bnbqw.html#bnbre */
    public void delete(Feed f, User u) throws FeedSubscriptionNotFoundException, NullFeedException, NullUserException {
        FeedSubscription fs = load(f, u);
        em.remove(fs);
    }

    public List<FeedSubscription> findAllByFeed(Feed f, int limit, int offset) throws NullFeedException {
        if (f == null) {
            throw new NullFeedException();
        }

        if ((limit <= 0) || (offset < 0)) {
            /* Returning empty list in this case */
            return new Vector<FeedSubscription>();
        }

        Query q = em.createQuery("SELECT fs FROM FeedSubscription fs WHERE fs.feed.name = ?1 ORDER BY fs.user.email");
        q.setParameter(1,f.getName());
        q.setMaxResults(limit);
        q.setFirstResult(offset);

        return q.getResultList();
    }

    public int countAllByFeed(Feed f) throws NullFeedException{

        if (f == null) {
            throw new NullFeedException();
        }

        Query q = em.createQuery("SELECT COUNT(*) FROM FeedSubscription fs WHERE fs.feed.name = ?1");
        q.setParameter(1,f.getName());

        return ((Long) q.getSingleResult()).intValue();
    }

    public List<FeedSubscription> findAllByUser(User u, int limit, int offset) throws NullUserException {
        if (u == null) {
            throw new NullUserException();
        }

        if ((limit <= 0) || (offset < 0)) {
            /* Returning empty list in this case */
            return new Vector<FeedSubscription>();
        }

        Query q = em.createQuery("SELECT fs FROM FeedSubscription fs WHERE fs.user.email = ?1 ORDER BY fs.feed.name");
        q.setParameter(1,u.getEmail());
        q.setMaxResults(limit);
        q.setFirstResult(offset);

        return q.getResultList();
    }

    public int countAllByUser(User u) throws NullUserException {

        if (u == null) {
            throw new NullUserException();
        }

        Query q = em.createQuery("SELECT COUNT(*) FROM FeedSubscription fs WHERE fs.user.email = ?1");
        q.setParameter(1,u.getEmail());

        return ((Long) q.getSingleResult()).intValue();
    }

}
