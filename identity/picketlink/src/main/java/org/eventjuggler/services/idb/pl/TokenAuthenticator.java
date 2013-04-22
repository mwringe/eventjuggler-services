package org.eventjuggler.services.idb.pl;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.naming.InitialContext;

import org.eventjuggler.services.simpleauth.rest.Authentication;
import org.eventjuggler.services.simpleauth.rest.UserInfo;
import org.jboss.resteasy.client.ProxyFactory;
import org.picketlink.authentication.BaseAuthenticator;
import org.picketlink.idm.model.SimpleUser;
import org.picketlink.idm.model.User;

@ApplicationScoped
public class TokenAuthenticator extends BaseAuthenticator {

    @Inject
    private Token token;

    @Override
    public void authenticate() {
        String t = token.getValue();
        if (t != null) {
            UserInfo info;

            Authentication authentication = getAuthentication();
            info = authentication.getInfo(t);

            if (info != null) {
                User user = new SimpleUser(info.getUserId());
                user.setEmail(info.getEmail());
                user.setFirstName(info.getFirstName());
                user.setLastName(info.getLastName());
                setUser(user);

                setStatus(AuthenticationStatus.SUCCESS);
            } else {
                setStatus(AuthenticationStatus.FAILURE);
            }
        } else {
            setStatus(AuthenticationStatus.DEFERRED);
        }
    }

    private Authentication getAuthentication() {
        try {
            InitialContext ctx = new InitialContext();
            Authentication authentication = (Authentication) ctx.lookup("java:global/ejs-identity/AuthenticationResource");
            if (authentication != null) {
                return authentication;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ProxyFactory.create(Authentication.class, "http://localhost:8080/ejs-identity/api");
    }

}
