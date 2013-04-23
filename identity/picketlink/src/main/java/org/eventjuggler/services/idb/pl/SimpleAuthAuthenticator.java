package org.eventjuggler.services.idb.pl;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.naming.InitialContext;

import org.eventjuggler.services.simpleauth.rest.Authentication;
import org.eventjuggler.services.simpleauth.rest.UserInfo;
import org.picketlink.authentication.BaseAuthenticator;
import org.picketlink.idm.model.SimpleUser;
import org.picketlink.idm.model.User;

@ApplicationScoped
public class SimpleAuthAuthenticator extends BaseAuthenticator {

    @Inject
    private SimpleAuthToken token;

    @Override
    public void authenticate() {
        String t = token.getValue();
        if (t != null) {
            UserInfo info;
            Authentication authentication;

            try {
                authentication = (Authentication) new InitialContext()
                        .lookup("java:global/ejs-identity/AuthenticationResource");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

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

}
