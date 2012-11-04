package com.mycompany.dao;

import com.mycompany.entity.Company;
import com.mycompany.entity.Feed;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: fermin
 * Date: 3/11/12
 * Time: 18:26
 * To change this template use File | Settings | File Templates.
 */
public interface FeedDAO {

    public void create(Feed feed) throws DuplicatedFeedException, CompanyNotFoundException;

    public Feed read(int id) throws FeedNotFoundException;

    public void update(Feed f) throws FeedNotFoundException, CompanyNotFoundException;

    public void delete(Feed c) throws FeedNotFoundException;

    public List<Feed> findFeedsByCompany(Company c) throws CompanyNotFoundException;
}
