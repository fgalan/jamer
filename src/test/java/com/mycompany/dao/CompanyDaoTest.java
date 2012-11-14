package com.mycompany.dao;

import com.mycompany.entity.Company;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.mycompany.dao.impl.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
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

    private CompanyDAO dao;

    @Before
    public void setUp() {
        /* Clean database of any previous content */
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("HibernateApp");
        EntityManager em = emf.createEntityManager();
        dao = new CompanyDAOImpl(em);
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

        Company c1, c2;

        /* Insert "ACME" company in database */
        c1 = new Company();
        c1.setName("ACME");
        dao.create(c1);

        /* Try to insert again the same */
        c2 = new Company();
        c2.setName("ACME");
        boolean thrown = false;
        try {
            dao.create(c2);
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
    public void findAllCompanies() throws InvalidPaginationParametersException {

            /* Populate 5 companies in database */
            createFive();

            /* Search the companies */
            List<Company> l = dao.findAll(5);

            /* Check that the list size is ok */
            assertEquals(null, 5, l.size());

            /* Check individual elements */
            /* Note that find always return companies ordered by name, no matter how there were inserted */
            assertEquals("ACME",l.get(0).getName());
            assertEquals("Big Evil Corp.",l.get(1).getName());
            assertEquals("OCP",l.get(2).getName());
            assertEquals("Other",l.get(3).getName());
            assertEquals("Weyland Yutani",l.get(4).getName());

    }

    @Test
    public void findCompaniesPaginationOk() throws InvalidPaginationParametersException {

        /* Populate 5 companies in database */
        createFive();

        /* Search the companies */
        List<Company> l = dao.findAll(3);

        /* Check that the list size is ok */
        assertEquals(null, 3, l.size());

        /* Check individual elements */
        /* Note that find always return companies ordered by name, no matter how there were inserted */
        assertEquals("ACME",l.get(0).getName());
        assertEquals("Big Evil Corp.",l.get(1).getName());
        assertEquals("OCP",l.get(2).getName());
    }

    @Test
    public void findCompaniesPaginationAndOffsetOk() throws DuplicatedCompanyException, InvalidPaginationParametersException {

        /* Populate 5 companies in database */
        createFive();

        /* Search the companies */
        List<Company> l = dao.findAll(3, 2);

        /* Check that the list is ok */
        assertEquals(null, 3, l.size());
        /* Check individual elements */
        /* Note that find always return companies ordered by name, no matter how there were inserted */
        assertEquals("OCP",l.get(0).getName());
        assertEquals("Other",l.get(1).getName());
        assertEquals("Weyland Yutani",l.get(2).getName());
    }

    @Test
    public void findCompaniesAllEmpty() throws InvalidPaginationParametersException {

        /* Search the companies */
        List<Company> l = dao.findAll(5);

        /* Check that the list size is ok */
        assertEquals(null, 0, l.size());

        /* Same result, no matter pagination limit */
        l = dao.findAll(3);
        assertEquals(null, 0, l.size());
    }

    @Test
    public void findCompaniesPaginationWrongLimit() {

        /* Populate 5 companies in database */
        createFive();

        /* Search the companies */
        boolean thrown = false;
        try {
            List<Company> l = dao.findAll(0);
        }
        catch (InvalidPaginationParametersException e) {
            thrown = true;
        }
        assertTrue(thrown);

    }

    @Test
    public void findCompaniesPaginationWrongOffset() {

        /* Populate 5 companies in database */
        createFive();

        /* Search the companies */
        boolean thrown = false;
        try {
            List<Company> l = dao.findAll(5, -1);
        }
        catch (InvalidPaginationParametersException e) {
            thrown = true;
        }
        assertTrue(thrown);

    }

    @Test
    public void countAllFiveOk() {
        /* Populate 5 companies in database */
        createFive();

        /* Count */
        int n = dao.countAll();

        /* Check the count is right */
        assertEquals(null, 5, n);

    }

    @Test
    public void countAllEmptyOk() {

        /* Count */
        int n = dao.countAll();

        /* Check the count is right */
        assertEquals(null, 0, n);

    }

    /************
     * Helper methods
     */

    private void createFive()  {

        /* Insert 5 companies */
        Company c1, c2, c3, c4, c5;
        c1 = new Company();
        c2 = new Company();
        c3 = new Company();
        c4 = new Company();
        c5 = new Company();
        c1.setName("ACME");
        c2.setName("Weyland Yutani");
        c3.setName("Big Evil Corp.");
        c4.setName("OCP");
        c5.setName("Other");
        try {
            dao.create(c1);
            dao.create(c2);
            dao.create(c3);
            dao.create(c4);
            dao.create(c5);
        }
        catch (DuplicatedCompanyException e) {
            // By construction, this can not happen
        }
    }

}
