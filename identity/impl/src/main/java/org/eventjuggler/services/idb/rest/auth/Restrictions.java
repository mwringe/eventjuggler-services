package org.eventjuggler.services.idb.rest.auth;

import org.picketlink.Identity;
import org.picketlink.deltaspike.Secures;

public class Restrictions {

    @Secures
    @LoggedIn
    public boolean isLoggedIn(Identity identity) {
        return identity.isLoggedIn();
    }

}
