package com.mycompany.dao;

import com.mycompany.dao.exception.*;
import com.mycompany.dao.impl.CompanyDAOImpl;
import com.mycompany.dao.impl.FeedDAOImpl;
import com.mycompany.dao.impl.PostDAOImpl;
import com.mycompany.dao.impl.UserDAOImpl;
import com.mycompany.entity.Company;
import com.mycompany.entity.Feed;
import com.mycompany.entity.Post;
import com.mycompany.entity.User;
import org.junit.After;
import org.junit.Before;
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
public class PostDAOTest {

    private PostDAO pDao;
    private UserDAO uDao;
    private FeedDAO fDao;
    private CompanyDAO cDao;
    private EntityManager em;   // we need to preserve the em to use getTransaction() on it

    @Before
    public void setUp() {
        /* Clean database of any previous content */
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("HibernateApp");
        EntityManager em = emf.createEntityManager();
        pDao = new PostDAOImpl(em);
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
    public void createPostOk() throws CompanyNotFoundException, FeedNotFoundException,
            UserNotFoundException, PostConstraintsViolationException, NullCompanyException,
            NullUserException, NullFeedException, PostNotFoundException {

        Post p1, p2, p3, p4;

        /* Create auxiliary entities in database (company, feed and user) */
        createAuxiliaryEntities();
        Company c = cDao.load("ACME");
        Feed f = fDao.load("employees", c);
        User u = uDao.load("fermin@fake.es");

        /* Create a couple of posts */
        em.getTransaction().begin();
        p1 = pDao.create("My first post", "Some random stuff", f, u);
        em.getTransaction().commit();

        em.getTransaction().begin();
        p2 = pDao.create("My second post", "Some more random stuff", f, u);
        em.getTransaction().commit();

        /* Check post can be retrieved from database */
        p3 = pDao.load(p1.getId());
        assertEquals(p1, p3);
        p4 = pDao.load(p2.getId());
        assertEquals(p2, p4);

    }

    @Test
    public void createPostNullFeedFails() throws UserNotFoundException,
        PostConstraintsViolationException, NullUserException {

        /* Create auxiliary entities in database (company, feed and user) */
        createAuxiliaryEntities();
        User u = uDao.load("fermin@fake.es");

        try {
            em.getTransaction().begin();
            pDao.create("My first post", "Some random stuff", null, u);
            em.getTransaction().commit();
            fail();
        }
        catch (NullFeedException e) {
            /* If we ends here that means that exception was raised and everything is ok */
            em.getTransaction().rollback();
        }

    }

    @Test
    public void createPostNullAuthorFails() throws CompanyNotFoundException, FeedNotFoundException,
        PostConstraintsViolationException, NullFeedException, NullCompanyException {

        /* Create auxiliary entities in database (company, feed and user) */
        createAuxiliaryEntities();
        Company c = cDao.load("ACME");
        Feed f = fDao.load("employees", c);

        try {
            em.getTransaction().begin();
            pDao.create("My first post", "Some random stuff", f, null);
            em.getTransaction().commit();
            fail();
        }
        catch (NullUserException e) {
            /* If we ends here that means that exception was raised and everything is ok */
            em.getTransaction().rollback();
        }

    }

    @Test
    public void createPostTooLongTitleFails() throws CompanyNotFoundException, FeedNotFoundException,
        UserNotFoundException, NullCompanyException, NullFeedException, NullUserException {

        /* Create auxiliary entities in database (company, feed and user) */
        createAuxiliaryEntities();
        Company c = cDao.load("ACME");
        Feed f = fDao.load("employees", c);
        User u = uDao.load("fermin@fake.es");

        try {
            em.getTransaction().begin();
            pDao.create("loooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo" +
                    "ooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooong", "Some random stuff", f, u);
            em.getTransaction().commit();
            fail();
        }
        catch (PostConstraintsViolationException e) {
            /* If we ends here that means that exception was raised and everything is ok */
            em.getTransaction().rollback();
        }
    }

    @Test
    public void createPostTooShortNameFails() throws CompanyNotFoundException, FeedNotFoundException,
            UserNotFoundException, NullCompanyException, NullFeedException, NullUserException {

        /* Create auxiliary entities in database (company, feed and user) */
        createAuxiliaryEntities();
        Company c = cDao.load("ACME");
        Feed f = fDao.load("employees", c);
        User u = uDao.load("fermin@fake.es");

        try {
            em.getTransaction().begin();
            pDao.create("", "Some random stuff", f, u);
            em.getTransaction().commit();
            fail();
        }
        catch (PostConstraintsViolationException e) {
            /* If we ends here that means that exception was raised and everything is ok */
            em.getTransaction().rollback();
        }
    }

    @Test
    public void createPostNullTitleFails() throws CompanyNotFoundException, FeedNotFoundException,
            UserNotFoundException, NullCompanyException, NullFeedException, NullUserException {

        /* Create auxiliary entities in database (company, feed and user) */
        createAuxiliaryEntities();
        Company c = cDao.load("ACME");
        Feed f = fDao.load("employees", c);
        User u = uDao.load("fermin@fake.es");

        try {
            em.getTransaction().begin();
            pDao.create(null, "Some random stuff", f, u);
            em.getTransaction().commit();
            fail();
        }
        catch (PostConstraintsViolationException e) {
            /* If we ends here that means that exception was raised and everything is ok */
            em.getTransaction().rollback();
        }
    }

    @Test
    public void createPostNullContentFails() throws CompanyNotFoundException, FeedNotFoundException,
            UserNotFoundException, NullCompanyException, NullFeedException, NullUserException {

        /* Create auxiliary entities in database (company, feed and user) */
        createAuxiliaryEntities();
        Company c = cDao.load("ACME");
        Feed f = fDao.load("employees", c);
        User u = uDao.load("fermin@fake.es");

        try {
            em.getTransaction().begin();
            pDao.create("My first post", null, f, u);
            em.getTransaction().commit();
            fail();
        }
        catch (PostConstraintsViolationException e) {
            /* If we ends here that means that exception was raised and everything is ok */
            em.getTransaction().rollback();
        }
    }

    @Test
    public void deletePostOk() throws CompanyNotFoundException, FeedNotFoundException,
            UserNotFoundException, NullCompanyException, NullFeedException, NullUserException,
            PostNotFoundException, PostConstraintsViolationException {

        /* Create auxiliary entities in database (company, feed and user) */
        createAuxiliaryEntities();
        Company c = cDao.load("ACME");
        Feed f = fDao.load("employees", c);
        User u = uDao.load("fermin@fake.es");

        /* Create a post */
        em.getTransaction().begin();
        Post p1 = pDao.create("My first post", "Some random stuff", f, u);
        em.getTransaction().commit();
        Long id = p1.getId();

        /* Check the post is in the database */
        pDao.load(id);

        /* Now, delete the post */
        em.getTransaction().begin();
        pDao.delete(id);
        em.getTransaction().commit();

        /* Check the post is not in database */
        try {
            pDao.load(id);
            fail();
        }
        catch (PostNotFoundException e) {
            /* If we ends here that means that exception was raised and everything is ok */
        }

    }

    @Test
    public void findAllPostByFeed() throws CompanyNotFoundException, FeedNotFoundException,
        NullCompanyException, NullFeedException {

        /* Populate some posts in database */
        createSomePosts();

        /* Search the posts */
        Feed f = fDao.load("employees", cDao.load("ACME"));
        List<Post> l = pDao.findAllByFeed(f, 8, 0);

        /* Check that the list size is ok */
        assertEquals(null, 8, l.size());

        /* Check individual elements */
        /* Note that find always return by insertion order */
        assertEquals("Title fermin-employees 0",l.get(0).getTitle());
        assertEquals("Title paco-employees 0",l.get(1).getTitle());
        assertEquals("Title fermin-employees 1",l.get(2).getTitle());
        assertEquals("Title paco-employees 1",l.get(3).getTitle());
        assertEquals("Title fermin-employees 2",l.get(4).getTitle());
        assertEquals("Title paco-employees 2",l.get(5).getTitle());
        assertEquals("Title fermin-employees 3",l.get(6).getTitle());
        assertEquals("Title paco-employees 3",l.get(7).getTitle());

    }

    @Test
    public void findPostByFeedPaginationOk() throws CompanyNotFoundException, FeedNotFoundException,
            NullCompanyException, NullFeedException {

        /* Populate some posts in database */
        createSomePosts();

        /* Search the posts */
        Feed f = fDao.load("employees", cDao.load("ACME"));
        List<Post> l = pDao.findAllByFeed(f, 5, 0);

        /* Check that the list size is ok */
        assertEquals(null, 5, l.size());

        /* Check individual elements */
        /* Note that find always return by insertion order */
        assertEquals("Title fermin-employees 0",l.get(0).getTitle());
        assertEquals("Title paco-employees 0",l.get(1).getTitle());
        assertEquals("Title fermin-employees 1",l.get(2).getTitle());
        assertEquals("Title paco-employees 1",l.get(3).getTitle());
        assertEquals("Title fermin-employees 2",l.get(4).getTitle());
    }

    @Test
    public void findPostByFeedPaginationAndOffsetOk() throws CompanyNotFoundException, FeedNotFoundException,
            NullCompanyException, NullFeedException {

        /* Populate some posts in database */
        createSomePosts();

        /* Search the posts */
        Feed f = fDao.load("employees", cDao.load("ACME"));
        List<Post> l = pDao.findAllByFeed(f, 5, 2);

        /* Check that the list size is ok */
        assertEquals(null, 5, l.size());

        /* Check individual elements */
        /* Note that find always return by insertion order */
        assertEquals("Title fermin-employees 1",l.get(0).getTitle());
        assertEquals("Title paco-employees 1",l.get(1).getTitle());
        assertEquals("Title fermin-employees 2",l.get(2).getTitle());
        assertEquals("Title paco-employees 2",l.get(3).getTitle());
        assertEquals("Title fermin-employees 3",l.get(4).getTitle());
    }

    @Test
    public void findPostByFeedNullFeedFail() {

        try {
            List<Post> l = pDao.findAllByFeed(null, 5, 0);
            fail();
        }
        catch (NullFeedException e) {
            /* If we ends here that means that exception was raised and everything is ok */
        }
    }

    @Test
    public void findPostByFeedAllEmpty() throws NullFeedException, CompanyNotFoundException,
        NullCompanyException, FeedNotFoundException {

        /* Search the posts */
        createAuxiliaryEntities();
        Feed f = fDao.load("employees", cDao.load("ACME"));
        List<Post> l = pDao.findAllByFeed(f, 5, 0);

        /* Check that the list is empty */
        assertEquals(null, 0, l.size());
    }

    @Test
    public void findPostByFeedPaginationWrongLimit() throws NullFeedException, CompanyNotFoundException,
        FeedNotFoundException, NullCompanyException {

        /* Search the posts */
        createAuxiliaryEntities();
        Feed f = fDao.load("employees", cDao.load("ACME"));

        /* Wrong limit */
        List<Post> l = pDao.findAllByFeed(f, 0, 1);
        assertEquals(null, 0, l.size());

        /* Wrong offset */
        l = pDao.findAllByFeed(f, 8, -1);
        assertEquals(null, 0, l.size());
    }

    @Test
    public void countAllByFeedEightOk() throws CompanyNotFoundException, FeedNotFoundException,
        NullCompanyException, NullFeedException {

        /* Populate some posts in database */
        createSomePosts();
        Feed f = fDao.load("employees", cDao.load("ACME"));

        /* Count */
        int n = pDao.countAllByFeed(f);

        /* Check is right */
        assertEquals(null, 8, n);
    }

    @Test
    public void countAllByFeedEmptyOk() throws CompanyNotFoundException, FeedNotFoundException,
        NullCompanyException, NullFeedException {

        createAuxiliaryEntities();
        Feed f = fDao.load("employees", cDao.load("ACME"));

        /* Count */
        int n = pDao.countAllByFeed(f);

        /* Check is right */
        assertEquals(null, 0, n);
    }

    @Test
    public void countAllByFeedNullFeedFail() {

        try {
            pDao.countAllByFeed(null);
            fail();
        }
        catch (NullFeedException e) {
            /* If we ends here that means that exception was raised and everything is ok */
        }
    }

    @Test
    public void findAllPostByAuthor() throws UserNotFoundException, NullUserException {

        /* Populate some posts in database */
        createSomePosts();

        /* Search the posts */
        User u = uDao.load("fermin@fake.es");
        List<Post> l = pDao.findAllByAuthor(u, 8, 0);

        /* Check that the list size is ok */
        assertEquals(null, 8, l.size());

        /* Check individual elements */
        /* Note that find always return by insertion order */
        assertEquals("Title fermin-employees 0",l.get(0).getTitle());
        assertEquals("Title fermin-general 0",l.get(1).getTitle());
        assertEquals("Title fermin-employees 1",l.get(2).getTitle());
        assertEquals("Title fermin-general 1",l.get(3).getTitle());
        assertEquals("Title fermin-employees 2",l.get(4).getTitle());
        assertEquals("Title fermin-general 2",l.get(5).getTitle());
        assertEquals("Title fermin-employees 3",l.get(6).getTitle());
        assertEquals("Title fermin-general 3",l.get(7).getTitle());
    }

    @Test
    public void findPostByAuthorPaginationOk() throws UserNotFoundException, NullUserException {

        /* Populate some posts in database */
        createSomePosts();

        /* Search the posts */
        User u = uDao.load("fermin@fake.es");
        List<Post> l = pDao.findAllByAuthor(u, 5, 0);

        /* Check that the list size is ok */
        assertEquals(null, 5, l.size());

        /* Check individual elements */
        /* Note that find always return by insertion order */
        assertEquals("Title fermin-employees 0",l.get(0).getTitle());
        assertEquals("Title fermin-general 0",l.get(1).getTitle());
        assertEquals("Title fermin-employees 1",l.get(2).getTitle());
        assertEquals("Title fermin-general 1",l.get(3).getTitle());
        assertEquals("Title fermin-employees 2",l.get(4).getTitle());
    }

    @Test
    public void findPostByAuthorPaginationAndOffsetOk() throws UserNotFoundException, NullUserException {

        /* Populate some posts in database */
        createSomePosts();

        /* Search the posts */
        User u = uDao.load("fermin@fake.es");
        List<Post> l = pDao.findAllByAuthor(u, 5, 2);

        /* Check that the list size is ok */
        assertEquals(null, 5, l.size());

        /* Check individual elements */
        /* Note that find always return by insertion order */
        assertEquals("Title fermin-employees 1",l.get(0).getTitle());
        assertEquals("Title fermin-general 1",l.get(1).getTitle());
        assertEquals("Title fermin-employees 2",l.get(2).getTitle());
        assertEquals("Title fermin-general 2",l.get(3).getTitle());
        assertEquals("Title fermin-employees 3",l.get(4).getTitle());

    }

    @Test
    public void findPostByAuthorAllEmpty() throws NullUserException, UserNotFoundException {

        /* Search the posts */
        createAuxiliaryEntities();
        User u = uDao.load("fermin@fake.es");
        List<Post> l = pDao.findAllByAuthor(u, 5, 0);

        /* Check that the list is empty */
        assertEquals(null, 0, l.size());
    }

    @Test
    public void findPostsByAuthorNullAuthorFail() {

        try {
            List<Post> l = pDao.findAllByAuthor(null, 5, 0);
            fail();
        }
        catch (NullUserException e) {
            /* If we ends here that means that exception was raised and everything is ok */
        }
    }

    @Test
    public void findPostByAuthorPaginationWrongLimit() throws UserNotFoundException, NullUserException {

        /* Search the posts */
        createAuxiliaryEntities();
        User u = uDao.load("fermin@fake.es");

        /* Wrong limit */
        List<Post> l = pDao.findAllByAuthor(u, 0, 1);
        assertEquals(null, 0, l.size());

        /* Wrong offset */
        l = pDao.findAllByAuthor(u, 8, -1);
        assertEquals(null, 0, l.size());
    }

    @Test
    public void countAllByAuthorEightOk() throws UserNotFoundException, NullUserException {

        /* Populate some posts in database */
        createSomePosts();
        User u = uDao.load("fermin@fake.es");

        /* Count */
        int n = pDao.countAllByAuthor(u);

        /* Check is right */
        assertEquals(null, 8, n);
    }

    @Test
    public void countAllByAuthorEmptyOk() throws UserNotFoundException, NullUserException {

        createAuxiliaryEntities();
        User u = uDao.load("fermin@fake.es");

        /* Count */
        int n = pDao.countAllByAuthor(u);

        /* Check is right */
        assertEquals(null, 0, n);
    }

    @Test
    public void countAllByAuthorNullAuthorFail() {

        try {
            pDao.countAllByAuthor(null);
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

            /* Insert two feeds */
            em.getTransaction().begin();
            fDao.create("employees", c);
            fDao.create("general", c);
            em.getTransaction().commit();

            /* Insert two users */
            em.getTransaction().begin();
            uDao.create("Fermin", "fermin@fake.es");
            uDao.create("Paco", "paco@fake.es");
            em.getTransaction().commit();

        }
        catch (Exception e) {
            // By construction, this can not happen
        }

    }

    private void createSomePosts() {

        createAuxiliaryEntities();

        try {

            Feed f1, f2;
            User u1, u2;
            f1 = fDao.load("employees", cDao.load("ACME"));
            f2 = fDao.load("general", cDao.load("ACME"));
            u1 = uDao.load("fermin@fake.es");
            u2 = uDao.load("paco@fake.es");

            /* Insert 4 post per user and feed combination */
            em.getTransaction().begin();
            for (int i = 0; i < 4; i++) {
                pDao.create("Title fermin-employees " + i, "Content fermin-employees" + i, f1, u1);
                pDao.create("Title fermin-general " + i, "Content fermin-employees" + i, f2, u1);
                pDao.create("Title paco-employees " + i, "Content fermin-employees" + i, f1, u2);
                pDao.create("Title paco-general " + i, "Content fermin-employees" + i, f2, u2);
            }
            em.getTransaction().commit();

        }
        catch (Exception e) {
            // By construction, this can not happen
        }

    }

}
