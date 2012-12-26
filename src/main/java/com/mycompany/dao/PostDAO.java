package com.mycompany.dao;


import com.mycompany.dao.exception.NullUserException;
import com.mycompany.dao.exception.NullFeedException;
import com.mycompany.dao.exception.PostConstraintsViolationException;
import com.mycompany.dao.exception.PostNotFoundException;
import com.mycompany.entity.Feed;
import com.mycompany.entity.Post;
import com.mycompany.entity.User;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: fermin
 * Date: 8/12/12
 * Time: 17:25
 * To change this template use File | Settings | File Templates.
 */
public interface PostDAO {

    public Post create(String title, String content, Feed f, User author) throws PostConstraintsViolationException,
            NullFeedException, NullUserException;

    public Post load(Long id) throws PostNotFoundException;

    public void delete(Long id) throws PostNotFoundException;

    public List<Post> findAllByFeed(Feed f, int limit, int offset) throws NullFeedException;

    public int countAllByFeed(Feed f) throws NullFeedException;

    public List<Post> findAllByAuthor(User author, int limit, int offset) throws NullUserException;

    public int countAllByAuthor(User author) throws NullUserException;

}
