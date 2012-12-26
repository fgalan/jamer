package com.mycompany.dao.impl;

import com.mycompany.dao.exception.NullAuthorException;
import com.mycompany.dao.exception.NullFeedException;
import com.mycompany.dao.exception.PostConstraintsViolationException;
import com.mycompany.dao.PostDAO;
import com.mycompany.dao.exception.PostNotFoundException;
import com.mycompany.entity.Feed;
import com.mycompany.entity.Post;
import com.mycompany.entity.User;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import javax.security.sasl.AuthorizeCallback;
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
public class PostDAOImpl implements PostDAO {

    @PersistenceContext
    private EntityManager em;

    public PostDAOImpl() {
    }

    public PostDAOImpl(EntityManager em) {
        this.em = em;
    }

    @Transactional
    public Post create(String title, String content, Feed f, User author) throws PostConstraintsViolationException,
            NullFeedException, NullAuthorException {
        if (f == null) {
            throw new NullFeedException();
        }
        if (author == null) {
            throw new NullAuthorException();
        }

        Post p = new Post();
        p.setTitle(title);
        p.setContent(content);
        p.setFeed(f);
        p.setAuthor(author);

        try {
            em.persist(p);
        }
        catch (PersistenceException e) {
            /* This is raised when nullable constraint is violated */
            throw new PostConstraintsViolationException();
        }
        catch (ConstraintViolationException e) {
            /* This is raised when size constraint is violated */
            throw new PostConstraintsViolationException();
        }
        return p;

    }

    public Post load(Long id) throws PostNotFoundException {

        Post p = em.find(Post.class, id);

        if (p == null) {
            throw new PostNotFoundException();
        }

        return p;

    }

    @Transactional
    /* We use a similar pattern to the one shown in the JPA tutorial:
       http://docs.oracle.com/javaee/6/tutorial/doc/bnbqw.html#bnbre */
    public void delete(Long id) throws PostNotFoundException {
        Post p = load(id);
        em.remove(p);
    }

    public List<Post> findAllByFeed(Feed f, int limit, int offset) throws NullFeedException {
        if (f == null) {
            throw new NullFeedException();
        }

        if ((limit <= 0) || (offset < 0)) {
            /* Returning empty list in this case */
            return new Vector<Post>();
        }

        Query q = em.createQuery("SELECT p FROM Post p WHERE p.feed.name = ?1 ORDER BY p.id");
        q.setParameter(1,f.getName());
        q.setMaxResults(limit);
        q.setFirstResult(offset);

        return q.getResultList();
    }

    public int countAllByFeed(Feed f) throws NullFeedException {

        if (f == null) {
            throw new NullFeedException();
        }

        Query q = em.createQuery("SELECT COUNT(*) FROM Post p WHERE p.feed.name = ?1");
        q.setParameter(1,f.getName());

        return ((Long) q.getSingleResult()).intValue();
    }

    public List<Post> findAllByAuthor(User author, int limit, int offset) throws NullAuthorException {

        if (author == null) {
            throw new NullAuthorException();
        }

        if ((limit <= 0) || (offset < 0)) {
            /* Returning empty list in this case */
            return new Vector<Post>();
        }

        Query q = em.createQuery("SELECT p FROM Post p WHERE p.author.email = ?1 ORDER BY p.id");
        q.setParameter(1,author.getEmail());
        q.setMaxResults(limit);
        q.setFirstResult(offset);

        return q.getResultList();

    }

    public int countAllByAuthor(User author) throws NullAuthorException {

        if (author == null) {
            throw new NullAuthorException();
        }

        Query q = em.createQuery("SELECT COUNT(*) FROM Post p WHERE p.author.email = ?1");
        q.setParameter(1,author.getEmail());

        return ((Long) q.getSingleResult()).intValue();

    }

}
