package com.mycompany.dao;

import com.mycompany.entity.Company;

/**
 * Created with IntelliJ IDEA.
 * User: fermin
 * Date: 3/11/12
 * Time: 18:26
 * To change this template use File | Settings | File Templates.
 */
public interface CompanyDAO {

    public void create(Company c) throws DuplicatedCompanyException;

    public Company read(String name) throws CompanyNotFoundException;

    public void delete(Company c) throws CompanyNotFoundException;
}