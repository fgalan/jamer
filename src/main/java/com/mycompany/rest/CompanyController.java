package com.mycompany.rest;

import com.mycompany.dao.CompanyDAO;
import com.mycompany.dao.CompanyNotFoundException;
import com.mycompany.dao.DuplicatedCompanyException;
import com.mycompany.entity.Company;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Iterator;
import java.util.List;

@Controller
public class CompanyController {

    @Autowired
    CompanyDAO dao;

    @RequestMapping(value = "/company/{name}", method= RequestMethod.GET)
    public @ResponseBody String getCompany(@PathVariable String name) {
        try {
            Company c1 = dao.read(name);
            String s = "<company>" + c1.getName() + "</company>\n";
            // TODO: ensure 200 is returned as HTTP response code
            return s;
        }
        catch (CompanyNotFoundException e) {
            // TODO: ensure 404 is returned as HTTP response code
            return "company not found\n";
        }
    }

    @RequestMapping(value = "/company", method= RequestMethod.GET)
    public @ResponseBody String getAllCompanies() {
        List<Company> l = dao.findAll();
        String s = "<companies>\n";
        for (Iterator<Company> i = l.iterator(); i.hasNext(); ) {
            Company c = i.next();
            s += "   <company>" + c.getName() + "</company>\n";
        }
        s += "</companies>\n";
        // TODO: ensure 200 is returned as HTTP response code
        return s;
    }

    @RequestMapping(value = "/company", method= RequestMethod.POST)
    public @ResponseBody String createCompany(@RequestParam("name") String name) {
        Company c = new Company();
        c.setName(name);
        try {
            dao.create(c);
            // TODO: ensure 200 is returned as HTTP response code
            return "ok\n";
        }
        catch (DuplicatedCompanyException e) {
            // TODO: ensure 400 is returned as HTTP response code
            return "duplicated company name\n";
        }
    }

    @RequestMapping(value = "/company/{name}", method= RequestMethod.DELETE)
    public @ResponseBody String deleteCompany(@PathVariable String name) {
        Company c = new Company();
        c.setName(name);
        try {
            dao.delete(c);
            // TODO: ensure 200 is returned as HTTP response code
            return "ok\n";
        }
        catch (CompanyNotFoundException e) {
            // TODO: ensure 404 is returned as HTTP response code
            return "company not found\n";
        }
    }

}
