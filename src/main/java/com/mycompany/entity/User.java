package com.mycompany.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.validation.constraints.Pattern;
import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: fermin
 * Date: 20/10/12
 * Time: 16:54
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class User {

    /* Fields */

    @Id
    private String name;

    @Pattern(regexp="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\."
            +"[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@"
            +"(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?",
            message="{invalid.email}")
    private String email;

    @ManyToMany(mappedBy = "subscribedUsers")
    private Collection<Feed> subscribedFeeds;

    @OneToMany(mappedBy = "author")
    private Collection<Post> posts;

    /* Setter and Getter methods */

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Collection<Feed> getSubscribedFeeds() {
        return subscribedFeeds;
    }

    public void setSubscribedFeeds(Collection<Feed> subscribedFeeds) {
        this.subscribedFeeds = subscribedFeeds;
    }

    public Collection<Post> getPosts() {
        return posts;
    }

    public void setPosts(Collection<Post> posts) {
        this.posts = posts;
    }
}
