package org.eventjuggler.services.idb.pl;

import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eventjuggler.services.common.auth.SimpleAuthIdmUtil;
import org.picketlink.authentication.BaseAuthenticator;
import org.picketlink.idm.IdentityManagerFactory;
import org.picketlink.idm.model.User;

@ApplicationScoped
public class SimpleAuthAuthenticator extends BaseAuthenticator {

    @Inject
    private SimpleAuthToken token;

    @Resource(lookup = "java:/picketlink/ExampleIMF")
    private IdentityManagerFactory imf;

    @Override
    public void authenticate() {
        String t = token.getValue();
        if (t != null) {
            User user = new SimpleAuthIdmUtil(imf.createIdentityManager()).getUser(t);
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
