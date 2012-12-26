package com.mycompany.dao;

import com.mycompany.dao.exception.DuplicatedUserException;
import com.mycompany.dao.exception.UserConstraintsViolationException;
import com.mycompany.dao.exception.UserNotFoundException;
import com.mycompany.dao.impl.UserDAOImpl;
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
public class UserDAOTest {

    private UserDAO dao;
    private EntityManager em;   // we need to preserve the em to use getTransaction() on it

    @Before
    public void setUp() {
        /* Clean database of any previous content */
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("HibernateApp");
        EntityManager em = emf.createEntityManager();
        dao = new UserDAOImpl(em);
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
    public void createUserOk() throws DuplicatedUserException,
            UserConstraintsViolationException, UserNotFoundException {

        User u1, u2;

        /* Insert an user in database */
        em.getTransaction().begin();
        u1 = dao.create("fermin", "fermin@fake.com");
        em.getTransaction().commit();

        /* Check that the user is there */
        u2 = dao.load("fermin@fake.com");
        assertEquals(u1, u2);

    }

    @Test
    public void createUserNullNameFails() throws DuplicatedUserException {
        try {
            em.getTransaction().begin();
            dao.create(null, "fermin@fake.com");
            em.getTransaction().commit();
            fail();
        }
        catch (UserConstraintsViolationException e) {
            /* If we ends here that means that exception was raised and everything is ok */
            em.getTransaction().rollback();
        }
    }

    @Test
    public void createUserTooLongNameFails() throws DuplicatedUserException {
        try {
            em.getTransaction().begin();
            dao.create("looooooooooooooooooooooooooooooooooooooooooooooong", "fermin@fake.com");
            em.getTransaction().commit();
            fail();
        }
        catch (UserConstraintsViolationException e) {
            /* If we ends here that means that exception was raised and everything is ok */
            em.getTransaction().rollback();
        }
    }

    @Test
    public void createUserTooShortNameFails() throws DuplicatedUserException {
        try {
            em.getTransaction().begin();
            dao.create("", "fermin@fake.com");
            em.getTransaction().commit();
            fail();
        }
        catch (UserConstraintsViolationException e) {
            /* If we ends here that means that exception was raised and everything is ok */
            em.getTransaction().rollback();
        }

    }

    @Test
    public void createUserNotUniqueEmailFails() throws  UserConstraintsViolationException, DuplicatedUserException{

        User u1;

        /* Insert an user in database */
        em.getTransaction().begin();
        u1 = dao.create("fermin", "fermin@fake.com");
        em.getTransaction().commit();

        /* Insert an user with the same email */
        try {
            em.getTransaction().begin();
            dao.create("pepito", "fermin@fake.com");
            em.getTransaction().commit();
            fail();
        }
        catch (UserConstraintsViolationException e) {
            /* If we ends here that means that exception was raised and everything is ok */
            em.getTransaction().rollback();
        }

    }

    @Test
    public void createUserNullEmailFails() throws  UserConstraintsViolationException, DuplicatedUserException{
        try {
            em.getTransaction().begin();
            dao.create("fermin", null);
            em.getTransaction().commit();
            fail();
        }
        catch (UserConstraintsViolationException e) {
            /* If we ends here that means that exception was raised and everything is ok */
            em.getTransaction().rollback();
        }
    }

    @Test
    public void createUserWrongEmailFails() throws DuplicatedUserException {
        try {
            em.getTransaction().begin();
            dao.create("fermin", "fermin_without_at_symbol.fake.com");
            em.getTransaction().commit();
            fail();
        }
        catch (UserConstraintsViolationException e) {
            /* If we ends here that means that exception was raised and everything is ok */
            em.getTransaction().rollback();
        }
    }

    @Test
    public void deleteUserOk() throws UserConstraintsViolationException, DuplicatedUserException,
            UserNotFoundException {
        User u1, u2;

        /* Insert "fermin" user in database */
        em.getTransaction().begin();
        u1 = dao.create("fermin", "fermin@fake.es");
        em.getTransaction().commit();

        /* Check that "fermin" is there */
        u2 = dao.load("fermin@fake.es");
        assertEquals(u1, u2);

        /* Now delete the "fermin" user  */
        em.getTransaction().begin();
        dao.delete("fermin@fake.es");
        em.getTransaction().commit();

        /* Check that the user  is not there */
        try {
            dao.load("fermin@fake.es");
            fail();
        }
        catch (UserNotFoundException e) {
            /* If we ends here that means that exception was raised and everything is ok */
        }
    }

    @Test
    public void deleteUserNotExistingFail() {
        /* Check that trying to delete a non existing user causes exception */
        try {
            em.getTransaction().begin();
            dao.delete("fermin@fake.es");
            em.getTransaction().commit();
            fail();
        }
        catch (UserNotFoundException e) {
            /* If we ends here that means that exception was raised and everything is ok */
            em.getTransaction().rollback();
        }
    }

    @Test
    public void findAllUsers() {
        /* Populate 5 users in database */
        createFive();

        /* Find users */
        List<User> l = dao.findAll(5, 0);

        /* Check that the list size is ok */
        assertEquals(null, 5, l.size());

        /* Check individual elements */
        /* Note that find always return users ordered by name, no matter how there were inserted */
        assertEquals("Adolfo Suarez",l.get(0).getName());
        assertEquals("Calvo Sotelo",l.get(1).getName());
        assertEquals("Felipe Gonzalez",l.get(2).getName());
        assertEquals("J. M. Aznar",l.get(3).getName());
        assertEquals("Zapatero",l.get(4).getName());

    }

    @Test
    public void findUsersPaginationOk() {
        /* Populate 5 users in database */
        createFive();

        /* Find users */
        List<User> l = dao.findAll(3, 0);

        /* Check that the list size is ok */
        assertEquals(null, 3, l.size());

        /* Check individual elements */
        /* Note that find always return users ordered by name, no matter how there were inserted */
        assertEquals("Adolfo Suarez",l.get(0).getName());
        assertEquals("Calvo Sotelo",l.get(1).getName());
        assertEquals("Felipe Gonzalez",l.get(2).getName());
    }

    @Test
    public void findUsersPaginationAndOffsetOk() {
        /* Populate 5 users in database */
        createFive();

        /* Find users */
        List<User> l = dao.findAll(3, 2);

        /* Check that the list size is ok */
        assertEquals(null, 3, l.size());

        /* Check individual elements */
        /* Note that find always return users ordered by name, no matter how there were inserted */
        assertEquals("Felipe Gonzalez",l.get(0).getName());
        assertEquals("J. M. Aznar",l.get(1).getName());
        assertEquals("Zapatero",l.get(2).getName());
    }

    @Test
    public void findUsersAllEmpty() {
        /* Search the users */
        List<User> l = dao.findAll(5, 0);

        /* Check that the list size is ok */
        assertEquals(null, 0, l.size());

        /* Same result, no matter pagination limit or offset*/
        l = dao.findAll(3, 2);
        assertEquals(null, 0, l.size());
    }

    @Test
    public void findUsersPaginationWrongLimit() {

        /* Populate 5 users in database */
        createFive();

        /* Search with wrong limit */
        List<User> l = dao.findAll(0, 2);
        assertEquals(null, 0, l.size());

        /* Search with wrong offset */
        l = dao.findAll(5, -1);
        assertEquals(null, 0, l.size());
    }

    @Test
    public void countAllFiveOk() {
        /* Populate 5 users in database */
        createFive();

        /* Count */
        int n = dao.countAll();

        /* Check the count is right */
        assertEquals(null, 5, n);
    }

    @Test
    public void countAllEmptyOk() {
        /* Count */
        int n = dao.countAll();

        /* Check the count is right */
        assertEquals(null, 0, n);
    }

    /************
     * Helper methods
     */

    private void createFive()  {

        /* Insert 5 users */
        User u1, u2, u3, u4, u5;
        u1 = new User();
        u2 = new User();
        u3 = new User();
        u4 = new User();
        u5 = new User();
        try {
            em.getTransaction().begin();
            dao.create("Adolfo Suarez", "adolfo@fake.es");
            dao.create("Calvo Sotelo", "calvo@fake.es");
            dao.create("Felipe Gonzalez", "felipe@fake.es");
            dao.create("J. M. Aznar", "aznar@fake.es");
            dao.create("Zapatero", "zapatero@fake.es");
            em.getTransaction().commit();
        }
        catch (Exception e) {
            // By construction, this can not happen
        }
    }

}
