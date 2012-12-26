package com.mycompany.dao;

import com.mycompany.dao.exception.DuplicatedPostSubscriptionException;
import com.mycompany.dao.exception.NullPostException;
import com.mycompany.dao.exception.NullUserException;
import com.mycompany.dao.exception.PostSubscriptionNotFoundException;
import com.mycompany.entity.Post;
import com.mycompany.entity.PostSubscription;
import com.mycompany.entity.User;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: fermin
 * Date: 8/12/12
 * Time: 17:26
 * To change this template use File | Settings | File Templates.
 */
public interface PostSubscriptionDAO {

    public PostSubscription create(Post p, User u, boolean read) throws DuplicatedPostSubscriptionException, NullPostException,
            NullUserException;

    public PostSubscription load(Post p, User u) throws PostSubscriptionNotFoundException, NullPostException, NullUserException;

    public void delete(Post p, User u) throws PostSubscriptionNotFoundException, NullPostException, NullUserException;

    public List<PostSubscription> findAllByPost(Post p, int limit, int offset) throws NullPostException;

    public int countAllByPost(Post p) throws NullPostException;

    public List<PostSubscription> findAllByUser(User u, int limit, int offset) throws NullUserException;

    public int countAllByUser(User u) throws NullUserException;

}
