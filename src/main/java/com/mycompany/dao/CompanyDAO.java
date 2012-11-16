package com.mycompany.dao;

import com.mycompany.entity.Company;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: fermin
 * Date: 3/11/12
 * Time: 18:26
 * To change this template use File | Settings | File Templates.
 */
public interface CompanyDAO {

    public Company  create(String name) throws DuplicatedCompanyException, CompanyConstraintsViolationException;

    public Company load(String name) throws CompanyNotFoundException;

    public void delete(String name) throws CompanyNotFoundException;

    public List<Company> findAll(int limit, int offset);

    public int countAll();
}
