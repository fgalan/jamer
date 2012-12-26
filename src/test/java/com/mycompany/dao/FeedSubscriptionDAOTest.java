package com.mycompany.dao;

import com.mycompany.dao.exception.*;
import com.mycompany.dao.impl.CompanyDAOImpl;
import com.mycompany.dao.impl.FeedDAOImpl;
import com.mycompany.dao.impl.FeedSubscriptionDAOImpl;
import com.mycompany.dao.impl.UserDAOImpl;
import com.mycompany.entity.Company;
import com.mycompany.entity.Feed;
import com.mycompany.entity.FeedSubscription;
import com.mycompany.entity.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created with IntelliJ IDEA.
 * User: fermin
 * Date: 8/12/12
 * Time: 17:28
 * To change this template use File | Settings | File Templates.
 */
@RunWith(MockitoJUnitRunner.class)
public class FeedSubscriptionDAOTest {

    private FeedSubscriptionDAO fsDao;
    private UserDAO uDao;
    private FeedDAO fDao;
    private CompanyDAO cDao;
    private EntityManager em;   // we need to preserve the em to use getTransaction() on it

    @Before
    public void setUp() {
        /* Clean database of any previous content */
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("HibernateApp");
        EntityManager em = emf.createEntityManager();
        fsDao = new FeedSubscriptionDAOImpl(em);
        uDao = new UserDAOImpl(em);
        fDao = new FeedDAOImpl(em);
        cDao = new CompanyDAOImpl(em);
        this.em = em;
    }

    @After
    public void tearDown () {
        /* End pending transaction we could have (otherwise the test runner "blocks"
           between test and test */
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
    }

    @Test
    public void createFeedSubscriptionOk() throws CompanyNotFoundException, FeedNotFoundException,
            NullCompanyException, UserNotFoundException, DuplicatedFeedSubscriptionException,
            FeedSubscriptionNotFoundException, NullFeedException, NullUserException {

        FeedSubscription fs1, fs2, fs3, fs4;

        /* Create auxiliary entities in database (company, feed and user) */
        createAuxiliaryEntities();
        Company c = cDao.load("ACME");
        Feed f = fDao.load("employees", c);
        User u1 = uDao.load("fermin@fake.es");
        User u2 = uDao.load("paco@fake.es");

        /* Create a couple of feed-subscriptions */
        em.getTransaction().begin();
        fs1 = fsDao.create(f, u1);
        em.getTransaction().commit();

        em.getTransaction().begin();
        fs2 = fsDao.create(f, u2);
        em.getTransaction().commit();

        /* Check they can be retrieved from database */
        fs3 = fsDao.load(f, u1);
        assertEquals(fs1, fs3);
        fs4 = fsDao.load(f, u2);
        assertEquals(fs2, fs4);

    }

    @Test
    public void createFeedSubscriptionNullFeedFails() throws CompanyNotFoundException, UserNotFoundException,
            NullCompanyException, DuplicatedFeedSubscriptionException, NullUserException {
        /* Create auxiliary entities in database (company, feed and user) */
        createAuxiliaryEntities();
        User u = uDao.load("fermin@fake.es");
        try {
            em.getTransaction().begin();
            fsDao.create(null, u);
            em.getTransaction().commit();
            fail();
        }
        catch (NullFeedException e) {
            /* If we ends here that means that exception was raised and everything is ok */
            em.getTransaction().rollback();
        }

    }

    @Test
    public void createFeedSubscriptionNullUserFails() throws CompanyNotFoundException, FeedNotFoundException,
        NullCompanyException, DuplicatedFeedSubscriptionException, NullFeedException {

        /* Create auxiliary entities in database (company, feed and user) */
        createAuxiliaryEntities();
        Company c = cDao.load("ACME");
        Feed f = fDao.load("employees", c);
        try {
            em.getTransaction().begin();
            fsDao.create(f, null);
            em.getTransaction().commit();
            fail();
        }
        catch (NullUserException e) {
            /* If we ends here that means that exception was raised and everything is ok */
            em.getTransaction().rollback();
        }
    }

    @Test
    @Ignore("this will not work until we fix FeedSubscription entity")
    public void createDuplicatedFeedSubscriptionFail() throws CompanyNotFoundException, FeedNotFoundException,
        NullCompanyException, UserNotFoundException, NullUserException, NullFeedException,
        DuplicatedFeedSubscriptionException {

        /* Create auxiliary entities in database (company, feed and user) */
        createAuxiliaryEntities();
        Company c = cDao.load("ACME");
        Feed f = fDao.load("employees", c);
        User u = uDao.load("fermin@fake.es");

        /* Create a couple of feed-subscriptions */
        em.getTransaction().begin();
        FeedSubscription fs = fsDao.create(f, u);
        em.getTransaction().commit();

        /* Create again another with the same feed and user */
        try {
            em.getTransaction().begin();
            fsDao.create(f, u);
            em.getTransaction().commit();
            fail();
        }
        catch (DuplicatedFeedSubscriptionException e) {
            /* If we ends here that means that exception was raised and everything is ok */
            em.getTransaction().rollback();
        }
    }

    @Test
    public void deleteFeedSubscriptionOk() throws CompanyNotFoundException, FeedNotFoundException,
        NullCompanyException, UserNotFoundException, NullFeedException, NullUserException,
        DuplicatedFeedSubscriptionException, FeedSubscriptionNotFoundException {

        /* Create auxiliary entities in database (company, feed and user) */
        createAuxiliaryEntities();
        Company c = cDao.load("ACME");
        Feed f = fDao.load("employees", c);
        User u = uDao.load("fermin@fake.es");

        /* Create a feed-subscription */
        em.getTransaction().begin();
        FeedSubscription fs = fsDao.create(f, u);
        em.getTransaction().commit();

        /* Check the feed-subscription is in the database */
        fsDao.load(f, u);

        /* Now, delete the feed-subscription */
        em.getTransaction().begin();
        fsDao.delete(f, u);
        em.getTransaction().commit();

        /* Check the feed-subscription is not in database */
        try {
            fsDao.load(f, u);
            fail();
        }
        catch (FeedSubscriptionNotFoundException e) {
            /* If we ends here that means that exception was raised and everything is ok */
        }

    }

    @Test
    public void deleteFeedSubscriptionNotExistingFail() throws CompanyNotFoundException, FeedNotFoundException,
        UserNotFoundException, NullCompanyException, NullFeedException, NullUserException {

        /* Create auxiliary entities in database (company, feed and user) */
        createAuxiliaryEntities();
        Company c = cDao.load("ACME");
        Feed f = fDao.load("employees", c);
        User u = uDao.load("fermin@fake.es");

        /* Check the feed-subscription is not in database */
        try {
            /* Now, delete non existing entity */
            em.getTransaction().begin();
            fsDao.delete(f, u);
            em.getTransaction().commit();
            fail();
        }
        catch (FeedSubscriptionNotFoundException e) {
            /* If we ends here that means that exception was raised and everything is ok */
            em.getTransaction().rollback();
        }
    }

    @Test
    public void findAllFeedSubscriptionsByFeed() throws NullFeedException, CompanyNotFoundException,
        FeedNotFoundException, NullCompanyException {

        /* Populate some posts in database */
        createSomeFeedSubscriptions();

        /* Search the feed-subcriptions  */
        Feed f = fDao.load("employees", cDao.load("ACME"));
        List<FeedSubscription> l = fsDao.findAllByFeed(f, 4, 0);

        /* Check that the list size is ok */
        assertEquals(null, 4, l.size());

        /* Check individual elements */
        /* Note that find always return by user email order */
        assertEquals("alberto@fake.es",l.get(0).getUser().getEmail());
        assertEquals(f,l.get(0).getFeed());
        assertEquals("fermin@fake.es",l.get(1).getUser().getEmail());
        assertEquals(f,l.get(1).getFeed());
        assertEquals("maria@fake.es",l.get(2).getUser().getEmail());
        assertEquals(f,l.get(2).getFeed());
        assertEquals("paco@fake.es",l.get(3).getUser().getEmail());
        assertEquals(f,l.get(3).getFeed());

    }

    @Test
    public void findFeedSubscriptionsByFeedPaginationOk() throws CompanyNotFoundException, FeedNotFoundException,
        NullCompanyException, NullFeedException {

        /* Populate some posts in database */
        createSomeFeedSubscriptions();

        /* Search the feed-subcriptions  */
        Feed f = fDao.load("employees", cDao.load("ACME"));
        List<FeedSubscription> l = fsDao.findAllByFeed(f, 2, 0);

        /* Check that the list size is ok */
        assertEquals(null, 2, l.size());

        /* Check individual elements */
        /* Note that find always return by user email order */
        assertEquals("alberto@fake.es",l.get(0).getUser().getEmail());
        assertEquals(f,l.get(0).getFeed());
        assertEquals("fermin@fake.es",l.get(1).getUser().getEmail());
        assertEquals(f,l.get(1).getFeed());
    }

    @Test
    public void findFeedSubscriptionsByFeedPaginationAndOffsetOk() throws CompanyNotFoundException, FeedNotFoundException,
            NullCompanyException, NullFeedException {

        /* Populate some posts in database */
        createSomeFeedSubscriptions();

        /* Search the feed-subcriptions  */
        Feed f = fDao.load("employees", cDao.load("ACME"));
        List<FeedSubscription> l = fsDao.findAllByFeed(f, 2, 2);

        /* Check that the list size is ok */
        assertEquals(null, 2, l.size());

        /* Check individual elements */
        /* Note that find always return by user email order */
        assertEquals("maria@fake.es",l.get(0).getUser().getEmail());
        assertEquals(f,l.get(0).getFeed());
        assertEquals("paco@fake.es",l.get(1).getUser().getEmail());
        assertEquals(f,l.get(1).getFeed());
    }

    @Test
    public void findFeedSubscriptionsByFeedAllEmpty() throws CompanyNotFoundException, FeedNotFoundException,
            NullCompanyException, NullFeedException {

        createAuxiliaryEntities();

        /* Search the feed-subcriptions  */
        Feed f = fDao.load("employees", cDao.load("ACME"));
        List<FeedSubscription> l = fsDao.findAllByFeed(f, 4, 0);

        /* Check that the list size is empty */
        assertEquals(null, 0, l.size());

    }

    @Test
    public void findFeedSubscriptionsByFeedPaginationWrongLimit() throws CompanyNotFoundException, FeedNotFoundException,
            NullCompanyException, NullFeedException {

        createAuxiliaryEntities();
        Feed f = fDao.load("employees", cDao.load("ACME"));

        /* Wrong limit */
        List<FeedSubscription> l = fsDao.findAllByFeed(f, 0, 1);
        assertEquals(null, 0, l.size());

        /* Wrong offset */
        l = fsDao.findAllByFeed(f, 4, -1);
        assertEquals(null, 0, l.size());
    }

    @Test
    public void findFeedSubscriptionByFeedNullFeedFails() {

        try {
            fsDao.findAllByFeed(null, 0, 1);
            fail();
        }
        catch (NullFeedException e) {
            /* If we ends here that means that exception was raised and everything is ok */
        }

    }

    @Test
    public void countAllByFeedFourOk() throws CompanyNotFoundException, NullCompanyException, NullFeedException,
        FeedNotFoundException {

        /* Populate */
        createSomeFeedSubscriptions();
        Feed f = fDao.load("employees", cDao.load("ACME"));

        /* Count */
        int n = fsDao.countAllByFeed(f);

        /* Check */
        assertEquals(null, 4, n);
    }

    @Test
    public void countAllByFeedEmptyOk() throws CompanyNotFoundException, NullCompanyException, NullFeedException,
            FeedNotFoundException {

        createAuxiliaryEntities();
        Feed f = fDao.load("employees", cDao.load("ACME"));

        /* Count */
        int n = fsDao.countAllByFeed(f);

        /* Check */
        assertEquals(null, 0, n);
    }

    @Test
    public void countAllByFeedNullFeedFails() {

        try {
            fsDao.countAllByFeed(null);
            fail();
        }
        catch (NullFeedException e) {
            /* If we ends here that means that exception was raised and everything is ok */
        }

    }

    @Test
    public void findAllFeedSubscriptionsByUser() throws UserNotFoundException, NullUserException  {
        /* Populate some posts in database */
        createSomeFeedSubscriptions();

        /* Search the feed-subcriptions  */
        User u = uDao.load("fermin@fake.es");
        List<FeedSubscription> l = fsDao.findAllByUser(u, 4, 0);

        /* Check that the list size is ok */
        assertEquals(null, 4, l.size());

        /* Check individual elements */
        /* Note that find always return by feed name order */
        assertEquals("employees",l.get(0).getFeed().getName());
        assertEquals(u,l.get(0).getUser());
        assertEquals("general",l.get(1).getFeed().getName());
        assertEquals(u,l.get(1).getUser());
        assertEquals("hr",l.get(2).getFeed().getName());
        assertEquals(u,l.get(2).getUser());
        assertEquals("public",l.get(3).getFeed().getName());
        assertEquals(u,l.get(3).getUser());
    }

    @Test
    public void findFeedSubscriptionsByUserPaginationOk() throws UserNotFoundException, NullUserException {

        /* Populate some posts in database */
        createSomeFeedSubscriptions();

        /* Search the feed-subcriptions  */
        User u = uDao.load("fermin@fake.es");
        List<FeedSubscription> l = fsDao.findAllByUser(u, 4, 0);

        /* Check that the list size is ok */
        assertEquals(null, 4, l.size());

        /* Check individual elements */
        /* Note that find always return by feed name order */
        assertEquals("employees",l.get(0).getFeed().getName());
        assertEquals(u,l.get(0).getUser());
        assertEquals("general",l.get(1).getFeed().getName());
        assertEquals(u,l.get(1).getUser());
        assertEquals("hr",l.get(2).getFeed().getName());
        assertEquals(u,l.get(2).getUser());
        assertEquals("public",l.get(3).getFeed().getName());
        assertEquals(u,l.get(3).getUser());
    }

    @Test
    public void findFeedSubscriptionsByUserPaginationAndOffsetOk() throws UserNotFoundException, NullUserException {

        /* Populate some posts in database */
        createSomeFeedSubscriptions();

        /* Search the feed-subcriptions  */
        User u = uDao.load("fermin@fake.es");
        List<FeedSubscription> l = fsDao.findAllByUser(u, 4, 0);

        /* Check that the list size is ok */
        assertEquals(null, 4, l.size());

        /* Check individual elements */
        /* Note that find always return by feed name order */
        assertEquals("employees",l.get(0).getFeed().getName());
        assertEquals(u,l.get(0).getUser());
        assertEquals("general",l.get(1).getFeed().getName());
        assertEquals(u,l.get(1).getUser());
        assertEquals("hr",l.get(2).getFeed().getName());
        assertEquals(u,l.get(2).getUser());
        assertEquals("public",l.get(3).getFeed().getName());
        assertEquals(u,l.get(3).getUser());
    }

    @Test
    public void findFeedSubscriptionsByUserAllEmpty() throws UserNotFoundException, NullUserException {

        createAuxiliaryEntities();
        User u = uDao.load("fermin@fake.es");

        /* Search the feed-subscriptions  */
        List<FeedSubscription> l = fsDao.findAllByUser(u, 4, 0);

        /* Check that the list is empty */
        assertEquals(null, 0, l.size());

    }

    @Test
    public void findFeedSubscriptionsByUserPaginationWrongLimit() throws UserNotFoundException,
        NullUserException {

        createAuxiliaryEntities();
        User u = uDao.load("fermin@fake.es");

        /* Wrong limit  */
        List<FeedSubscription> l = fsDao.findAllByUser(u, 0, 1);
        assertEquals(null, 0, l.size());

        /* Wrong offset  */
        l = fsDao.findAllByUser(u, 4, -1);
        assertEquals(null, 0, l.size());
    }

    @Test
    public void findFeedSubscriptionByUserNullUserFail() {

        try {
            fsDao.findAllByUser(null, 0, 1);
            fail();
        }
        catch (NullUserException e) {
            /* If we ends here that means that exception was raised and everything is ok */
        }

    }

    @Test
    public void countAllByUserFourOk() throws UserNotFoundException, NullUserException {

        /* Populate */
        createSomeFeedSubscriptions();
        User u = uDao.load("fermin@fake.es");

        /* Count */
        int n = fsDao.countAllByUser(u);

        /* Check */
        assertEquals(null, 4, n);
    }

    @Test
    public void countAllByUserEmptyOk() throws UserNotFoundException, NullUserException {

        createAuxiliaryEntities();
        User u = uDao.load("fermin@fake.es");

        /* Count */
        int n = fsDao.countAllByUser(u);

        /* Check */
        assertEquals(null, 0, n);

    }

    @Test
    public void countAllByUserNullUserFails() {

        try {
            fsDao.countAllByUser(null);
            fail();
        }
        catch (NullUserException e) {
            /* If we ends here that means that exception was raised and everything is ok */
        }

    }

    /************
     * Helper methods
     */

    private void createAuxiliaryEntities()  {

        try {
            /* Insert one company */
            em.getTransaction().begin();
            Company c = cDao.create("ACME");
            em.getTransaction().commit();

            /* Insert 4 feeds */
            em.getTransaction().begin();
            fDao.create("employees", c);
            fDao.create("general", c);
            fDao.create("hr", c);
            fDao.create("public", c);
            em.getTransaction().commit();

            /* Insert 4 users */
            em.getTransaction().begin();
            uDao.create("Fermin", "fermin@fake.es");
            uDao.create("Paco", "paco@fake.es");
            uDao.create("Maria", "maria@fake.es");
            uDao.create("Alberto", "alberto@fake.es");
            em.getTransaction().commit();

        }
        catch (Exception e) {
            // By construction, this can not happen
        }

    }

    private void createSomeFeedSubscriptions() {

        createAuxiliaryEntities();

        try {
            /* Insert feed-subscription for all the feed-user combinations */
            Company c = cDao.load("ACME");
            Feed f1 = fDao.load("employees", c);
            Feed f2 = fDao.load("general", c);
            Feed f3 = fDao.load("hr", c);
            Feed f4 = fDao.load("public", c);
            User u1 = uDao.load("fermin@fake.es");
            User u2 = uDao.load("paco@fake.es");
            User u3 = uDao.load("maria@fake.es");
            User u4 = uDao.load("alberto@fake.es");

            em.getTransaction().begin();
            fsDao.create(f1,u1);
            fsDao.create(f1,u2);
            fsDao.create(f1,u3);
            fsDao.create(f1,u4);
            fsDao.create(f2,u1);
            fsDao.create(f2,u2);
            fsDao.create(f2,u3);
            fsDao.create(f2,u4);
            fsDao.create(f3,u1);
            fsDao.create(f3,u2);
            fsDao.create(f3,u3);
            fsDao.create(f3,u4);
            fsDao.create(f4,u1);
            fsDao.create(f4,u2);
            fsDao.create(f4,u3);
            fsDao.create(f4,u4);
            em.getTransaction().commit();

        }
        catch (Exception e) {
            // By construction, this can not happen
        }
    }

}
