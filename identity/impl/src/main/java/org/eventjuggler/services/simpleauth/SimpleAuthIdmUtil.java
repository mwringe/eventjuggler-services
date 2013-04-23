package org.eventjuggler.services.simpleauth;

import java.util.List;

import org.eventjuggler.services.utils.KeyGenerator;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.model.Attribute;
import org.picketlink.idm.model.IdentityType;
import org.picketlink.idm.model.User;
import org.picketlink.idm.query.IdentityQuery;

public class SimpleAuthIdmUtil {

    private final IdentityManager im;

    public SimpleAuthIdmUtil(IdentityManager im) {
        this.im = im;
    }

    public String setToken(User user) {
        String token = KeyGenerator.createToken();
        user.setAttribute(new Attribute<String>("simpleAuthToken", token));
        im.update(user);
        return token;
    }

    public User getUser(String token) {
        IdentityQuery<User> query = im.createIdentityQuery(User.class);
        query.setParameter(IdentityType.ATTRIBUTE.byName("simpleAuthToken"), token);

        List<User> users = query.getResultList();
        if (users.isEmpty()) {
            return null;
        } else {
            return users.get(0);
        }
    }

    public void removeToken(String token) {
        if (token != null) {
            User user = getUser(token);
            if (user != null) {
                user.removeAttribute("simpleAuthToken");
                im.update(user);
            }
        }
    }

}
