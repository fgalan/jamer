package com.mycompany.dao;

import com.mycompany.entity.Company;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.mycompany.dao.CompanyDAO;
import com.mycompany.dao.impl.*;

import java.util.List;

import static org.junit.Assert.assertEquals;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: fermin
 * Date: 3/11/12
 * Time: 18:48
 * To change this template use File | Settings | File Templates.
 */
@RunWith(MockitoJUnitRunner.class)
public class CompanyDAOTest {

    private CompanyDAO dao = new CompanyDAOImpl();

    @Before
    public void setUp() {
        /* Clean database of any previous content */
    }

    @Test
    public void createCompanyOk()
            throws DuplicatedCompanyException, CompanyNotFoundException {

        Company c1, c2;

        /* Check that "ACME" company is not initially in the database */
        boolean thrown = false;
        try {
            dao.read("ACME");
        }
        catch (CompanyNotFoundException e) {
            thrown = true;
        }
        assertTrue(thrown);

        /* Insert "ACME" company in database */
        c1 = new Company();
        c1.setName("ACME");
        dao.create(c1);

        /* Check that the company is there */
        c2 = dao.read("ACME");
        assertEquals(c1, c2);
    }

    @Test
    public void createCompanyDuplicatedFails()
            throws DuplicatedCompanyException {

        Company c1;

        /* Insert "ACME" company in database */
        c1 = new Company();
        c1.setName("ACME");
        dao.create(c1);

        /* Try to insert again the same */
        boolean thrown = false;
        try {
            dao.create(c1);
        }
        catch (DuplicatedCompanyException e) {
            thrown = true;
        }
        assertTrue(thrown);

    }

    @Test
    public void deleteCompanyOk()
            throws DuplicatedCompanyException, CompanyNotFoundException {

        Company c1, c2;

        /* Insert "ACME" company in database */
        c1 = new Company();
        c1.setName("ACME");
        dao.create(c1);

        /* Check that "ACME" is there */
        c2 = dao.read("ACME");
        assertEquals(c1, c2);

        /* Now delete the "ACME" company (we use a new Company object) */
        c1 = new Company();
        c1.setName("ACME");
        dao.delete(c1);

        /* Check that the company is not there */
        boolean thrown = false;
        try {
            dao.read("ACME");
        }
        catch (CompanyNotFoundException e) {
            thrown = true;
        }
        assertTrue(thrown);

    }

    @Test
    public void deleteCompanyNotExistingFail() {

        /* Check that trying to delete a non existing company causes exception */
        Company c1 = new Company();
        c1.setName("ACME");
        boolean thrown = false;
        try {
            dao.delete(c1);
        }
        catch (CompanyNotFoundException e) {
            thrown = true;
        }
        assertTrue(thrown);

    }

    @Test
    public void findAllCompaniesOk() throws DuplicatedCompanyException {

         /* Insert two companies */
        Company c1 = new Company();
        Company c2 = new Company();
        c1.setName("ACME");
        c2.setName("Weyland Yutani");
        dao.create(c1);
        dao.create(c2);

        /* Get the list */
        List<Company> l = dao.findAll();

        /* Check that the list is ok */
        assertEquals(null, 2, l.size());
        String n1 = l.get(0).getName();
        String n2 = l.get(1).getName();
        assertTrue((n1.equals("ACME") && n2.equals("Weyland Yutani"))
                || (n2.equals("ACME") && n1.equals("Weyland Yutani")) );
    }

    @Test
    public void findAllCompaniesEmpty() {
        /* Get the list and check that it's empty*/
        List<Company> l = dao.findAll();
        assertEquals(null, 0, l.size());
    }
}
