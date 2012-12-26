package com.mycompany.dao;

import com.mycompany.dao.exception.DuplicatedUserException;
import com.mycompany.dao.exception.UserConstraintsViolationException;
import com.mycompany.dao.exception.UserNotFoundException;
import com.mycompany.entity.User;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: fermin
 * Date: 8/12/12
 * Time: 17:26
 * To change this template use File | Settings | File Templates.
 */
public interface UserDAO {

    public User create(String name, String email) throws DuplicatedUserException, UserConstraintsViolationException;

    public User load(String email) throws UserNotFoundException;

    public void delete(String email) throws UserNotFoundException;

    public List<User> findAll(int limit, int offset);

    public int countAll();
}
