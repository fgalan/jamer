package com.mycompany.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import java.util.Collection;
import static javax.persistence.CascadeType.ALL;

/**
 * Created with IntelliJ IDEA.
 * User: fermin
 * Date: 20/10/12
 * Time: 16:53
 * To change this template use File | Settings | File Templates.
 */
@Entity
@NamedQuery(name="Company.findAll", query="SELECT c FROM Company c")
public class Company {

    /* Fields */

    @Id
    private String name;

    @OneToMany(cascade = ALL, mappedBy = "company")
    private Collection<Feed> feeds;

    /* Setter and Getter methods */

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<Feed> getFeeds() {
        return feeds;
    }

    public void setFeeds(Collection<Feed> feeds) {
        this.feeds = feeds;
    }
}
