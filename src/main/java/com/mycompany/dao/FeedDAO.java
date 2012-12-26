package com.mycompany.dao;

import com.mycompany.dao.exception.DuplicatedFeedException;
import com.mycompany.dao.exception.FeedConstraintsViolationException;
import com.mycompany.dao.exception.FeedNotFoundException;
import com.mycompany.dao.exception.NullCompanyException;
import com.mycompany.entity.Company;
import com.mycompany.entity.Feed;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: fermin
 * Date: 8/12/12
 * Time: 17:24
 * To change this template use File | Settings | File Templates.
 */
public interface FeedDAO {

    public Feed create(String name, Company c) throws DuplicatedFeedException, FeedConstraintsViolationException,
            NullCompanyException;

    public Feed load(String name, Company c) throws FeedNotFoundException, NullCompanyException;

    public void delete(String name, Company c) throws FeedNotFoundException, NullCompanyException;

    public List<Feed> findAllByCompany(Company c, int limit, int offset) throws NullCompanyException;

    public int countAllByCompany(Company c) throws NullCompanyException;
}
