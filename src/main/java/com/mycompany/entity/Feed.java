package com.mycompany.entity;

import javax.persistence.*;
import javax.validation.constraints.Size;

/**

 * Created with IntelliJ IDEA.
 * User: fermin
 * Date: 20/10/12
 * Time: 16:53
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class Feed {

    /* Fields */

    /* Note that under the "inversion principle", we don't include posts here */
    /* By the "rupture principle" we use the Subscription entity to "link" with users */

    @Id
    @GeneratedValue
    private Long id;

    // TODO: ensure that although Feed name is not globally unique, the combination companyName-feedName is
    @Size(min=1, max=20)
    private String name;

    @ManyToOne
    private Company company;

    /* Setter and Getter methods */

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
}
