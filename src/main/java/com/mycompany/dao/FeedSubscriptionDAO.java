package com.mycompany.dao;

import com.mycompany.dao.exception.DuplicatedFeedSubscriptionException;
import com.mycompany.dao.exception.FeedSubscriptionNotFoundException;
import com.mycompany.dao.exception.NullFeedException;
import com.mycompany.dao.exception.NullUserException;
import com.mycompany.entity.Feed;
import com.mycompany.entity.FeedSubscription;
import com.mycompany.entity.User;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: fermin
 * Date: 8/12/12
 * Time: 17:25
 * To change this template use File | Settings | File Templates.
 */
public interface FeedSubscriptionDAO {

    public FeedSubscription create(Feed f, User u) throws DuplicatedFeedSubscriptionException, NullUserException,
            NullFeedException;

    public FeedSubscription load(Feed f, User u) throws FeedSubscriptionNotFoundException, NullFeedException,
            NullUserException;

    public void delete(Feed f, User u) throws FeedSubscriptionNotFoundException, NullFeedException, NullUserException;

    public List<FeedSubscription> findAllByFeed(Feed f, int limit, int offset) throws NullFeedException;

    public int countAllByFeed(Feed f) throws NullFeedException;

    public List<FeedSubscription> findAllByUser(User u, int limit, int offset) throws NullUserException;

    public int countAllByUser(User u) throws NullUserException;

}
