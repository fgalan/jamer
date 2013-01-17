package com.mycompany.rest;

import com.mycompany.dao.*;
import com.mycompany.dao.exception.*;
import com.mycompany.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Iterator;
import java.util.List;

/* TODO: check every 'return' statement to ensure that:
   - 200 is returned as HTTP response code in case successful operation
   - 4xx is returned as HTTP response code in case of unsuccessful operation
*/
@Controller
public class CompanyController {

    @Autowired
    CompanyDAO cDao;
    @Autowired
    FeedDAO fDao;
    @Autowired
    PostDAO pDao;
    @Autowired
    UserDAO uDao;
    @Autowired
    FeedSubscriptionDAO fsDao;
    @Autowired
    PostSubscriptionDAO psDao;

    /* GET methods */

    @RequestMapping(value = "/company", method= RequestMethod.GET)
    public @ResponseBody String getAllCompanies() {
        String s = "<companies>\n";

        // TODO unhardwire (they should be parameters in the URL, e.g /company?max=100,offset=20
        List<Company> l = cDao.findAll(100,0 );

        for (Iterator<Company> i = l.iterator(); i.hasNext(); ) {
           Company c = i.next();
           s += "   <company>" + c.getName() + "</company>\n";
        }
        s += "</companies>\n";
        return s;
    }

    @RequestMapping(value = "/company/{name}", method= RequestMethod.GET)
    public @ResponseBody String getCompany(@PathVariable String name) {
        try {
            Company c = cDao.load(name);
            // TODO unhardwire (they should be parameters in the URL, e.g /company?max=100,offset=20
            List<Feed> l = fDao.findAllByCompany(c, 100, 0);
            String s = "<company name='" + c.getName() + "'/>\n";
            for (Iterator<Feed> i = l.iterator(); i.hasNext(); ) {
                Feed f = i.next();
                s += "   <feed>" + f.getName() + "</feed>\n";
            }
            s += "</company>\n";
            return s;
        }
        catch (CompanyNotFoundException  e) {
            return "company not found\n";
        }
        catch (NullCompanyException  e) {
            // This must not happen!
            return "null company\n";
        }
    }

    @RequestMapping(value = "/company/{companyName}/{feedName}", method= RequestMethod.GET)
    public @ResponseBody String getFeed(@PathVariable String companyName, @PathVariable String feedName) {
        try {
            Company c = cDao.load(companyName);
            Feed f = fDao.load(feedName, c);
            // TODO unhardwire (they should be parameters in the URL, e.g /company?max=100,offset=20
            List<Post> l = pDao.findAllByFeed(f, 100, 0);
            String s = "<feed name='" + f.getName() + "' company='" + c.getName() + "'/>\n";
            for (Iterator<Post> i = l.iterator(); i.hasNext(); ) {
                Post p = i.next();
                s += "   <post>" + p.getId() + "</post>\n";
            }
            s += "</feed>\n";
            return s;
        }
        catch (CompanyNotFoundException  e) {
            return "company not found\n";
        }
        catch (FeedNotFoundException e) {
            return "feed " + feedName + " not found\n";
        }
        catch (NullCompanyException  e) {
            // This must not happen!
            return "null company\n";
        }
        catch (NullFeedException  e) {
            // This must not happen!
            return "null feed\n";
        }
    }

    @RequestMapping(value = "/post/{postId}", method= RequestMethod.GET)
    public @ResponseBody String getPost(@PathVariable String postId) {
        try {
            Post p = pDao.load(Long.parseLong(postId));
            String s = "<post>\n";
            s += "   <company>" + p.getFeed().getCompany().getName() + "</company>\n";
            s += "   <feed>" + p.getFeed().getName() + "</feed>\n";
            s += "   <title>" + p.getTitle() + "</title>\n";
            s += "   <content>" + p.getContent() + "</content>\n";
            s += "   <author>" + p.getAuthor().getName() + "</author>\n";
            s += "</post>\n";
            return s;
        }
        catch (PostNotFoundException e) {
            return "post not found\n";
        }

    }

    @RequestMapping(value = "/company/{companyName}/{feedName}/subscribers", method= RequestMethod.GET)
    public @ResponseBody String getFeedSubscribers(@PathVariable String companyName, @PathVariable String feedName) {
        try {
            String s = "<feed_subscriptions>\n";

            Company c = cDao.load(companyName);
            Feed f = fDao.load(feedName,c);

            // TODO unhardwire (they should be parameters in the URL, e.g /company?max=100,offset=20
            List <FeedSubscription> l = fsDao.findAllByFeed(f, 100, 0);

            for (Iterator<FeedSubscription> i = l.iterator(); i.hasNext(); ) {
                FeedSubscription fs = i.next();
                s += "   <feed_subscription company='" + fs.getFeed().getCompany().getName() + "' feed='" +
                        fs.getFeed().getName() + "' user='" + fs.getUser().getName() + "' />\n";
            }

            s += "</feed_subscriptions>\n";
            return s;
        }
        catch (CompanyNotFoundException  e) {
            return "company " + companyName + " not found\n";
        }
        catch (FeedNotFoundException  e) {
            return "feed " + feedName + " not found\n";
        }
        catch (NullCompanyException  e) {
            // This must not happen!
            return "null company\n";
        }
        catch (NullFeedException  e) {
            // This must not happen!
            return "null feed\n";
        }
    }

    @RequestMapping(value = "/post/{postId}/subscribers", method= RequestMethod.GET)
    public @ResponseBody String getPostSubscribers(@PathVariable String postId) {
        try {
            String s = "<post_subscriptions>\n";

            Post p = pDao.load(Long.parseLong(postId));

            // TODO unhardwire (they should be parameters in the URL, e.g /company?max=100,offset=20
            List <PostSubscription> l = psDao.findAllByPost(p, 100, 0);

            for (Iterator<PostSubscription> i = l.iterator(); i.hasNext(); ) {
                PostSubscription ps = i.next();
                s += "   <post_subscription post='" + ps.getPost().getId() + "' user='" + ps.getUser().getName() +
                        "' read='" + ps.getIsRead() + "' />\n";
            }

            s += "</post_subscriptions>\n";
            return s;
        }
        catch (PostNotFoundException e) {
            return "post not found\n";
        }
        catch (NullPostException  e) {
            // This must not happen!
            return "null post\n";
        }

    }

    @RequestMapping(value = "/user", method= RequestMethod.GET)
    public @ResponseBody String getAllUsers() {
        String s = "<users>\n";

        // TODO unhardwire (they should be parameters in the URL, e.g /company?max=100,offset=20
        List<User> l = uDao.findAll(100,0 );

        for (Iterator<User> i = l.iterator(); i.hasNext(); ) {
            User u = i.next();
            s += "   <user>" + u.getEmail() + "</user>\n";
        }
        s += "</users>\n";
        return s;
    }

    // Note that we are using a regular expression for the PathVariable, see http://forum.springsource.org/showthread.php?96629-mapping-a-parameter-with-a-Dot
    @RequestMapping(value = "/user/{userEmail:.+}", method= RequestMethod.GET)
    public @ResponseBody String getUser(@PathVariable String userEmail) {
        try {
            User u = uDao.load(userEmail);
            // TODO unhardwire (they should be parameters in the URL, e.g /company?max=100,offset=20
            String s = "<user name='" + u.getName() + "' email='" + u.getEmail() + "'/>\n";
            return s;
        }
        catch (UserNotFoundException  e) {
            return "user " + userEmail + " not found\n";
        }
    }

    // Note that we are using a regular expression for the PathVariable, see http://forum.springsource.org/showthread.php?96629-mapping-a-parameter-with-a-Dot
    @RequestMapping(value = "/user/{userEmail:.+}/subscribedFeeds", method= RequestMethod.GET)
    public @ResponseBody String getUserSubscribedFeeds(@PathVariable String userEmail) {
        try {
            String s = "<feed_subscriptions>\n";

            User u = uDao.load(userEmail);
            // TODO unhardwire (they should be parameters in the URL, e.g /company?max=100,offset=20
            List <FeedSubscription> l = fsDao.findAllByUser(u, 100, 0);

            for (Iterator<FeedSubscription> i = l.iterator(); i.hasNext(); ) {
                FeedSubscription fs = i.next();
                s += "   <feed_subscription company='" + fs.getFeed().getCompany().getName() + "' feed='" +
                        fs.getFeed().getName() + "' user='" + fs.getUser().getName() + "' />\n";
            }

            s += "</feed_subscriptions>\n";
            return s;
        }
        catch (UserNotFoundException  e) {
            return "user " + userEmail + " not found\n";
        }
        catch (NullUserException  e) {
            // This must not happen!
            return "null user\n";
        }
    }

    // Note that we are using a regular expression for the PathVariable, see http://forum.springsource.org/showthread.php?96629-mapping-a-parameter-with-a-Dot
    @RequestMapping(value = "/user/{userEmail:.+}/subscribedPosts", method= RequestMethod.GET)
    public @ResponseBody String getUserSubscribedPosts(@PathVariable String userEmail) {
        try {
            String s = "<post_subscriptions>\n";

            User u = uDao.load(userEmail);

            // TODO unhardwire (they should be parameters in the URL, e.g /company?max=100,offset=20
            List <PostSubscription> l = psDao.findAllByUser(u, 100, 0);

            for (Iterator<PostSubscription> i = l.iterator(); i.hasNext(); ) {
                PostSubscription ps = i.next();
                s += "   <post_subscription post='" + ps.getPost().getId() + "' user='" + ps.getUser().getName() +
                        "' read='" + ps.getIsRead() + "' />\n";
            }

            s += "</post_subscriptions>\n";
            return s;
        }
        catch (UserNotFoundException e) {
            return "user not found\n";
        }
        catch (NullUserException  e) {
            // This must not happen!
            return "null user\n";
        }
    }

    /* POST methods */

    @RequestMapping(value = "/company", method= RequestMethod.POST)
    public @ResponseBody String createCompany(@RequestParam("name") String name) {
        try {
            cDao.create(name);
            return "ok\n";
        }
        catch (Exception e) {
            return "duplicated company name\n";
        }
    }

    @RequestMapping(value = "/company/{companyName}", method= RequestMethod.POST)
    public @ResponseBody String createFeed(@PathVariable String companyName, @RequestParam("name") String feedName) {
        try {
            Company c = cDao.load(companyName);
            fDao.create(feedName, c);
            return "ok\n";
        }
        catch (CompanyNotFoundException e) {
            return "company " + companyName + " not found\n";
        }
        catch (DuplicatedFeedException e) {
            return "duplicated feed name\n";
        }
        catch (FeedConstraintsViolationException e) {
            return "feed constraint violation\n";
        }
        catch (NullCompanyException e) {
            // This must not happen!
            return "null company\n";
        }
    }

    @RequestMapping(value = "/company/{companyName}/{feedName}", method= RequestMethod.POST)
    public @ResponseBody String createPost(@PathVariable String companyName, @PathVariable String feedName,
                                           @RequestParam("title") String title,
                                           @RequestParam("content") String content,
                                           @RequestParam("author") String userEmail) {
        /* TODO: title, content and author should be in the payload, not in URl. Thus, I have to research how to parse
           XML and JSON payloads in the future */
        try {
            Company c = cDao.load(companyName);
            Feed f = fDao.load(feedName, c);
            User u = uDao.load(userEmail);
            pDao.create(title, content, f, u);
            return "ok\n";
        }
        catch (CompanyNotFoundException e) {
            return "company " + companyName + " not found\n";
        }
        catch (FeedNotFoundException e) {
            return "feed " + feedName + " not found\n";
        }
        catch (UserNotFoundException e) {
            return "user " + userEmail + " not found\n";
        }
        catch (PostConstraintsViolationException e) {
            return "post constraint violation\n";
        }
        catch (NullCompanyException e) {
            // This must not happen!
            return "null company\n";
        }
        catch (NullFeedException e) {
            // This must not happen!
            return "null feed\n";
        }
        catch (NullUserException e) {
            // This must not happen!
            return "null feed\n";
        }
    }

    @RequestMapping(value = "/company/{companyName}/{feedName}/subscribe", method= RequestMethod.POST)
    public @ResponseBody String subscribeUserToFeed(@PathVariable String companyName, @PathVariable String feedName,
                                                    @RequestParam("user") String userEmail) {
        try {
            Company c = cDao.load(companyName);
            Feed f = fDao.load(feedName, c);
            User u = uDao.load(userEmail);
            fsDao.create(f, u);
            return "ok\n";
        }
        catch (CompanyNotFoundException e) {
            return "company " + companyName + " not found\n";
        }
        catch (FeedNotFoundException e) {
            return "feed " + feedName + " not found\n";
        }
        catch (UserNotFoundException e) {
            return "user " + userEmail + " not found\n";
        }
        catch (NullCompanyException e) {
            // This must not happen!
            return "null company\n";
        }
        catch (DuplicatedFeedSubscriptionException e) {
            return "duplicated feed subscription\n";
        }
        catch (NullUserException e) {
            // This must not happen!
            return "null user\n";
        }
        catch (NullFeedException e) {
            // This must not happen!
            return "null feed\n";
        }
    }

    @RequestMapping(value = "/post/{postId}/subscribe", method= RequestMethod.POST)
    public @ResponseBody String subscribeUserToPost(@PathVariable String postId, @RequestParam("user") String userEmail,
                                                    @RequestParam("read") String read) {
        try {
            Post p = pDao.load(Long.parseLong(postId));
            User u = uDao.load(userEmail);
            psDao.create(p, u, Boolean.parseBoolean(read));
            return "ok\n";
        }
        catch (PostNotFoundException e) {
            return "post not found\n";
        }
        catch (UserNotFoundException e) {
            return "user " + userEmail + " not found\n";
        }
        catch (DuplicatedPostSubscriptionException e) {
            return "duplicated post subscription exception\n";
        }
        catch (NullUserException e) {
            // This must not happen!
            return "null user\n";
        }
        catch (NullPostException e) {
            // This must not happen!
            return "null post\n";
        }
    }

    @RequestMapping(value = "/user", method= RequestMethod.POST)
    public @ResponseBody String createUser(@RequestParam("name") String name, @RequestParam("email") String email) {
        try {
            uDao.create(name, email);
            return "ok\n";
        }
        catch (Exception e) {
            return "duplicated user name\n";
        }
    }

    /* DELETE methods */

    @RequestMapping(value = "/company/{name}", method= RequestMethod.DELETE)
    public @ResponseBody String deleteCompany(@PathVariable String name) {
        try {
            cDao.delete(name);
            // TODO: ensure 200 is returned as HTTP response code
            return "ok\n";
        }
        catch (CompanyNotFoundException e) {
            // TODO: ensure 404 is returned as HTTP response code
            return "company not found\n";
        }
    }

    // TODO: complete DELETE methods

}
