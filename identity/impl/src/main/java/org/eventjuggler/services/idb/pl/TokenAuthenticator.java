package org.eventjuggler.services.idb.pl;

import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eventjuggler.services.utils.TokenService;
import org.picketlink.authentication.BaseAuthenticator;
import org.picketlink.idm.model.User;

@ApplicationScoped
public class TokenAuthenticator extends BaseAuthenticator {

    @Inject
    private Token token;

    @EJB
    private TokenService tokenService;

    @Override
    public void authenticate() {
        String t = token.getToken();
        if (t != null) {
            User user = tokenService.get(t);
            if (user != null) {
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
