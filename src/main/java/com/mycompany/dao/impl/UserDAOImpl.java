package com.mycompany.dao.impl;

import com.mycompany.dao.exception.DuplicatedUserException;
import com.mycompany.dao.exception.UserConstraintsViolationException;
import com.mycompany.dao.UserDAO;
import com.mycompany.dao.exception.UserNotFoundException;
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
 * Time: 17:27
 * To change this template use File | Settings | File Templates.
 */
@Repository
public class UserDAOImpl implements UserDAO {

    @PersistenceContext
    private EntityManager em;

    public UserDAOImpl() {
    }

    public UserDAOImpl(EntityManager em) {
        this.em = em;
    }

    @Transactional
    public User create(String name, String email) throws DuplicatedUserException, UserConstraintsViolationException {
        User u = new User();
        u.setName(name);
        u.setEmail(email);
        try {
            em.persist(u);
        }
        catch (EntityExistsException e) {
            throw new DuplicatedUserException();
        }
        catch (PersistenceException e) {
            /* This is raised when nullable constraint is violated */
            throw new UserConstraintsViolationException();
        }
        catch (ConstraintViolationException e) {
            /* This is raised when size (and pattern?) constraint are violated */
            throw new UserConstraintsViolationException();
        }
        return u;
    }

    public User load(String email) throws UserNotFoundException {
        Query q = em.createQuery("SELECT u FROM User u WHERE email = ?1");
        q.setParameter(1,email);

        try {
            return (User) q.getSingleResult();
        }
        catch (NoResultException e) {
            throw new UserNotFoundException();
        }
    }

    @Transactional
    /* We use a similar pattern to the one shown in the JPA tutorial:
       http://docs.oracle.com/javaee/6/tutorial/doc/bnbqw.html#bnbre */
    public void delete(String email) throws UserNotFoundException {
        User u = load(email);
        em.remove(u);
    }

    public List<User> findAll(int limit, int offset) {
        if ((limit <= 0) || (offset < 0)) {
            /* Returning empty list in this case */
            return new Vector<User>();
        }
        TypedQuery<User> q = em.createNamedQuery("User.findAll", User.class)
                .setMaxResults(limit)
                .setFirstResult(offset);
        return q.getResultList();
    }

    public int countAll() {
        Query q = em.createNamedQuery("User.countAll");
        return ((Long) q.getSingleResult()).intValue();
    }

}
