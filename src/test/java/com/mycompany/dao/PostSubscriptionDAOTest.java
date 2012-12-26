package com.mycompany.dao;

import com.mycompany.dao.exception.*;
import com.mycompany.dao.impl.*;
import com.mycompany.entity.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import java.util.Iterator;
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
public class PostSubscriptionDAOTest {

    private PostSubscriptionDAO psDao;
    private CompanyDAO cDao;
    private FeedDAO fDao;
    private UserDAO uDao;
    private PostDAO pDao;
    private EntityManager em;   // we need to preserve the em to use getTransaction() on it

    @Before
    public void setUp() {
        /* Clean database of any previous content */
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("HibernateApp");
        EntityManager em = emf.createEntityManager();
        psDao = new PostSubscriptionDAOImpl(em);
        cDao = new CompanyDAOImpl(em);
        fDao = new FeedDAOImpl(em);
        uDao = new UserDAOImpl(em);
        pDao = new PostDAOImpl(em);
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
    public void createPostSubscriptionOk() throws CompanyNotFoundException, UserNotFoundException, NullUserException,
        NullPostException, DuplicatedPostSubscriptionException, PostSubscriptionNotFoundException {

        PostSubscription ps1, ps2, ps3, ps4;

        /* Create auxiliary entities in database */
        createAuxiliaryEntities();
        Company c = cDao.load("ACME");
        Post p1 = pDao.findAllByAuthor(uDao.load("anonymous@fake.es"), 1, 0).get(0);
        Post p2 = pDao.findAllByAuthor(uDao.load("anonymous@fake.es"), 1, 1).get(0);
        User u1 = uDao.load("fermin@fake.es");
        User u2 = uDao.load("paco@fake.es");

        /* Create a couple of post-subscriptions */
        em.getTransaction().begin();
        ps1 = psDao.create(p1, u1, true);
        em.getTransaction().commit();

        em.getTransaction().begin();
        ps2 = psDao.create(p2, u2, false);
        em.getTransaction().commit();

        /* Check they can be retrieved from database */
        ps3 = psDao.load(p1, u1);
        assertEquals(ps1, ps3);
        ps4 = psDao.load(p2, u2);
        assertEquals(ps2, ps4);

    }

    @Test
    public void createPostSubscriptionNullPostFails() throws UserNotFoundException, DuplicatedPostSubscriptionException,
        NullUserException {

        /* Create auxiliary entities in database */
        createAuxiliaryEntities();
        User u = uDao.load("fermin@fake.es");
        try {
            em.getTransaction().begin();
            psDao.create(null, u, true);
            em.getTransaction().commit();
            fail();
        }
        catch (NullPostException e) {
            /* If we ends here that means that exception was raised and everything is ok */
            em.getTransaction().rollback();
        }
    }

    @Test
    public void createPostSubscriptionNullUserFails() throws UserNotFoundException, NullUserException,
            NullPostException, DuplicatedPostSubscriptionException {

        /* Create auxiliary entities in database */
        createAuxiliaryEntities();
        Post p = pDao.findAllByAuthor(uDao.load("anonymous@fake.es"), 1, 0).get(0);
        try {
            em.getTransaction().begin();
            psDao.create(p, null, true);
            em.getTransaction().commit();
            fail();
        }
        catch (NullUserException e) {
            /* If we ends here that means that exception was raised and everything is ok */
            em.getTransaction().rollback();
        }

    }

    @Test
    @Ignore("this will not work until we fix PostSubscription entity")
    public void createDuplicatedPostSubscriptionFail() throws UserNotFoundException, NullUserException,
        NullPostException, DuplicatedPostSubscriptionException {

        /* Create auxiliary entities in database */
        createAuxiliaryEntities();
        Post p = pDao.findAllByAuthor(uDao.load("anonymous@fake.es"), 1, 0).get(0);
        User u = uDao.load("fermin@fake.es");

        /* Create a couple of feed-subscriptions */
        em.getTransaction().begin();
        PostSubscription fs = psDao.create(p, u, true);
        em.getTransaction().commit();

        /* Create again another with the same feed and user */
        try {
            em.getTransaction().begin();
            psDao.create(p, u, true);
            em.getTransaction().commit();
            fail();
        }
        catch (DuplicatedPostSubscriptionException e) {
            /* If we ends here that means that exception was raised and everything is ok */
            em.getTransaction().rollback();
        }

    }

    @Test
    public void deletePostSubscriptionOk() throws UserNotFoundException, NullUserException, NullPostException,
        DuplicatedPostSubscriptionException, PostSubscriptionNotFoundException{

        /* Create auxiliary entities in database */
        createAuxiliaryEntities();
        Post p = pDao.findAllByAuthor(uDao.load("anonymous@fake.es"), 1, 0).get(0);
        User u = uDao.load("fermin@fake.es");

        /* Create a feed-subscription */
        em.getTransaction().begin();
        PostSubscription fs = psDao.create(p, u, true);
        em.getTransaction().commit();

        /* Check the feed-subscription is in the database */
        psDao.load(p, u);

        /* Now, delete the feed-subscription */
        em.getTransaction().begin();
        psDao.delete(p, u);
        em.getTransaction().commit();

        /* Check the feed-subscription is not in database */
        try {
            psDao.load(p, u);
            fail();
        }
        catch (PostSubscriptionNotFoundException e) {
            /* If we ends here that means that exception was raised and everything is ok */
        }

    }

    @Test
    public void deletePostSubscriptionNotExistingFail() throws UserNotFoundException, NullUserException,
        NullPostException {

        /* Create auxiliary entities in database */
        createAuxiliaryEntities();
        Post p = pDao.findAllByAuthor(uDao.load("anonymous@fake.es"), 1, 0).get(0);
        User u = uDao.load("fermin@fake.es");

        /* Check the feed-subscription is not in database */
        try {
            /* Now, delete non existing entity */
            em.getTransaction().begin();
            psDao.delete(p, u);
            em.getTransaction().commit();
            fail();
        }
        catch (PostSubscriptionNotFoundException e) {
            /* If we ends here that means that exception was raised and everything is ok */
            em.getTransaction().rollback();
        }
    }

    @Test
    public void findAllPostSubscriptionsByPost() throws UserNotFoundException, NullUserException,
        NullPostException {

        /* Populate some post-subscriptions in database */
        createSomePostSubscriptions();

        /* Search the post-subscriptions  */
        Post p = pDao.findAllByAuthor(uDao.load("anonymous@fake.es"), 1, 0).get(0);
        List<PostSubscription> l = psDao.findAllByPost(p, 4, 0);

        /* Check that the list size is ok */
        assertEquals(null, 4, l.size());

        /* Check individual elements */
        /* Note that find always return by user email order */
        assertEquals("alberto@fake.es",l.get(0).getUser().getEmail());
        assertEquals(p,l.get(0).getPost());
        assertEquals("fermin@fake.es",l.get(1).getUser().getEmail());
        assertEquals(p,l.get(1).getPost());
        assertEquals("maria@fake.es",l.get(2).getUser().getEmail());
        assertEquals(p,l.get(2).getPost());
        assertEquals("paco@fake.es",l.get(3).getUser().getEmail());
        assertEquals(p,l.get(3).getPost());
    }

    @Test
    public void findPostSubscriptionsByPostPaginationOk() throws UserNotFoundException, NullUserException,
            NullPostException {

        /* Populate some post-subscriptions in database */
        createSomePostSubscriptions();

        /* Search the post-subscriptions  */
        Post p = pDao.findAllByAuthor(uDao.load("anonymous@fake.es"), 1, 0).get(0);
        List<PostSubscription> l = psDao.findAllByPost(p, 3, 0);

        /* Check that the list size is ok */
        assertEquals(null, 3, l.size());

        /* Check individual elements */
        /* Note that find always return by user email order */
        assertEquals("alberto@fake.es",l.get(0).getUser().getEmail());
        assertEquals(p,l.get(0).getPost());
        assertEquals("fermin@fake.es",l.get(1).getUser().getEmail());
        assertEquals(p,l.get(1).getPost());
        assertEquals("maria@fake.es",l.get(2).getUser().getEmail());
        assertEquals(p,l.get(2).getPost());

    }

    @Test
    public void findPostSubscriptionsByPostPaginationAndOffsetOk() throws UserNotFoundException, NullUserException,
            NullPostException {

        /* Populate some post-subscriptions in database */
        createSomePostSubscriptions();

        /* Search the post-subscriptions  */
        Post p = pDao.findAllByAuthor(uDao.load("anonymous@fake.es"), 1, 0).get(0);
        List<PostSubscription> l = psDao.findAllByPost(p, 2, 1);

        /* Check that the list size is ok */
        assertEquals(null, 2, l.size());

        /* Check individual elements */
        /* Note that find always return by user email order */
        assertEquals("fermin@fake.es",l.get(0).getUser().getEmail());
        assertEquals(p,l.get(0).getPost());
        assertEquals("maria@fake.es",l.get(1).getUser().getEmail());
        assertEquals(p,l.get(1).getPost());

    }

    @Test
    public void findPostSubscriptionsByPostAllEmpty() throws UserNotFoundException, NullUserException,
            NullPostException {

        createAuxiliaryEntities();

        /* Search the post-subscriptions  */
        Post p = pDao.findAllByAuthor(uDao.load("anonymous@fake.es"), 1, 0).get(0);
        List<PostSubscription> l = psDao.findAllByPost(p, 4, 0);

        /* Check that the list is empty */
        assertEquals(null, 0, l.size());

    }

    @Test
    public void findPostSubscriptionsByPostPaginationWrongLimit() throws UserNotFoundException, NullUserException,
            NullPostException {

        createAuxiliaryEntities();
        Post p = pDao.findAllByAuthor(uDao.load("anonymous@fake.es"), 1, 0).get(0);

        /* Wrong limit  */
        List<PostSubscription> l = psDao.findAllByPost(p, 0, 1);
        assertEquals(null, 0, l.size());

        /* Wrong offset  */
        l = psDao.findAllByPost(p, 0, 1);
        assertEquals(null, 0, l.size());
    }

    @Test
    public void findPostSubscriptionByPostNullPostFails() {

        try {
            psDao.findAllByPost(null, 0, 1);
            fail();
        }
        catch (NullPostException e) {
            /* If we ends here that means that exception was raised and everything is ok */
        }

    }

    @Test
    public void countAllByPostFourOk() throws UserNotFoundException, NullUserException,
            NullPostException {

        /* Populate */
        createSomePostSubscriptions();
        Post p = pDao.findAllByAuthor(uDao.load("anonymous@fake.es"), 1, 0).get(0);

        /* Count */
        int n = psDao.countAllByPost(p);

        /* Check */
        assertEquals(null, 4, n);
    }

    @Test
    public void countAllByPostEmptyOk() throws UserNotFoundException, NullUserException,
            NullPostException {

        createAuxiliaryEntities();
        Post p = pDao.findAllByAuthor(uDao.load("anonymous@fake.es"), 1, 0).get(0);

        /* Count */
        int n = psDao.countAllByPost(p);

        /* Check */
        assertEquals(null, 0, n);

    }

    @Test
    public void countAllByPostNullPostFails() {

        try {
            psDao.countAllByPost(null);
            fail();
        }
        catch (NullPostException e) {
            /* If we ends here that means that exception was raised and everything is ok */
        }

    }


    @Test
    public void findAllPostSubscriptionsByUser() throws UserNotFoundException, NullUserException,
            NullPostException {

        /* Populate some post-subscriptions in database */
        createSomePostSubscriptions();

        /* Search the post-subscriptions  */
        User u = uDao.load("fermin@fake.es");
        List<PostSubscription> l = psDao.findAllByUser(u, 4, 0);

        /* Check that the list size is ok */
        assertEquals(null, 4, l.size());

        /* Check individual elements */
        /* Note that find always return by post insertion order */
        assertEquals("Post number is 0" ,l.get(0).getPost().getTitle());
        assertEquals(u,l.get(0).getUser());
        assertEquals("Post number is 1" ,l.get(1).getPost().getTitle());
        assertEquals(u,l.get(1).getUser());
        assertEquals("Post number is 2" ,l.get(2).getPost().getTitle());
        assertEquals(u,l.get(2).getUser());
        assertEquals("Post number is 3" ,l.get(3).getPost().getTitle());
        assertEquals(u,l.get(3).getUser());
    }

    @Test
    public void findPostSubscriptionsByUserPaginationOk() throws UserNotFoundException, NullUserException,
            NullPostException {

        /* Populate some post-subscriptions in database */
        createSomePostSubscriptions();

        /* Search the post-subscriptions  */
        User u = uDao.load("fermin@fake.es");
        List<PostSubscription> l = psDao.findAllByUser(u, 3, 0);

        /* Check that the list size is ok */
        assertEquals(null, 3, l.size());

        /* Check individual elements */
        /* Note that find always return by post insertion order */
        assertEquals("Post number is 0" ,l.get(0).getPost().getTitle());
        assertEquals(u,l.get(0).getUser());
        assertEquals("Post number is 1" ,l.get(1).getPost().getTitle());
        assertEquals(u,l.get(1).getUser());
        assertEquals("Post number is 2" ,l.get(2).getPost().getTitle());
        assertEquals(u,l.get(2).getUser());
    }

    @Test
    public void findPostSubscriptionsByUserPaginationAndOffsetOk() throws UserNotFoundException, NullUserException,
            NullPostException {

        /* Populate some post-subscriptions in database */
        createSomePostSubscriptions();

        /* Search the post-subscriptions  */
        User u = uDao.load("fermin@fake.es");
        List<PostSubscription> l = psDao.findAllByUser(u, 2, 1);

        /* Check that the list size is ok */
        assertEquals(null, 2, l.size());

        /* Check individual elements */
        /* Note that find always return by post insertion order */
        assertEquals("Post number is 1" ,l.get(0).getPost().getTitle());
        assertEquals(u,l.get(0).getUser());
        assertEquals("Post number is 2" ,l.get(1).getPost().getTitle());
        assertEquals(u,l.get(1).getUser());

    }

    @Test
    public void findPostSubscriptionsByUserAllEmpty() throws UserNotFoundException, NullUserException,
            NullPostException {

        createAuxiliaryEntities();
        User u = uDao.load("fermin@fake.es");

        /* Search the post-subscriptions  */
        List<PostSubscription> l = psDao.findAllByUser(u, 4, 0);

        /* Check that the list is empty */
        assertEquals(null, 0, l.size());
    }

    @Test
    public void findPostSubscriptionsByUserPaginationWrongLimit() throws UserNotFoundException, NullUserException,
            NullPostException {

        createAuxiliaryEntities();
        User u = uDao.load("fermin@fake.es");

        /* Wrong limit  */
        List<PostSubscription> l = psDao.findAllByUser(u, 0, 1);
        assertEquals(null, 0, l.size());

        /* Wrong offset  */
        l = psDao.findAllByUser(u, 0, -1);
        assertEquals(null, 0, l.size());

    }

    @Test
    public void findPostSubscriptionByUserNullUserFail() {

        try {
            psDao.findAllByUser(null, 0, 1);
            fail();
        }
        catch (NullUserException e) {
            /* If we ends here that means that exception was raised and everything is ok */
        }

    }

    @Test
    public void countAllByUserFourOk() throws UserNotFoundException, NullUserException,
            NullPostException {

        /* Populate */
        createSomePostSubscriptions();
        User u = uDao.load("fermin@fake.es");

        /* Count */
        int n = psDao.countAllByUser(u);

        /* Check */
        assertEquals(null, 4, n);
    }

    @Test
    public void countAllByUserEmptyOk() throws UserNotFoundException, NullUserException,
            NullPostException {

        createAuxiliaryEntities();
        User u = uDao.load("fermin@fake.es");

        /* Count */
        int n = psDao.countAllByUser(u);

        /* Check */
        assertEquals(null, 0, n);
    }

    @Test
    public void countAllByUserNullUserFails() {

        try {
            psDao.countAllByUser(null);
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

            /* Insert a company and feed (we can not create post without them) */
            Company c = cDao.create("ACME");
            Feed f = fDao.create("employees", c);

            /* Insert 4 users (+1 anonymous, just to be the author of the posts) */
            em.getTransaction().begin();
            uDao.create("Fermin", "fermin@fake.es");
            uDao.create("Paco", "paco@fake.es");
            uDao.create("Maria", "maria@fake.es");
            uDao.create("Alberto", "alberto@fake.es");
            User u = uDao.create("Anonymous", "anonymous@fake.es");
            em.getTransaction().commit();

            /* Insert 4 posts authored by the 'Anonymous' */
            em.getTransaction().begin();
            for (int i = 0; i < 4; i++ ) {
                pDao.create("Post number is " + i , "Some random stuff", f, u);
            }
            em.getTransaction().commit();

        }
        catch (Exception e) {
            // By construction, this can not happen
        }

    }

    private void createSomePostSubscriptions() {

        createAuxiliaryEntities();

        try {
            /* Insert post-subscription for all the combinations */
            em.getTransaction().begin();
            Iterator<Post> i = pDao.findAllByAuthor(uDao.load("anonymous@fake.es"), 4, 0).iterator();
            for ( ; i.hasNext(); ) {
                Post p = i.next();
                psDao.create(p, uDao.load("fermin@fake.es"), true);
                psDao.create(p, uDao.load("paco@fake.es"), false);
                psDao.create(p, uDao.load("maria@fake.es"), true);
                psDao.create(p, uDao.load("alberto@fake.es"), false);
            }
            em.getTransaction().commit();

        }
        catch (Exception e) {
            // By construction, this can not happen
        }
    }


}
