package org.eventjuggler.services.simpleauth.rest;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.picketlink.idm.IdentityManager;

public class IdentityManagerLookup {

    public static IdentityManager createIdentityManager() {
        try {
            return (IdentityManager) new InitialContext().lookup("java:jboss/IdentityManager");
        } catch (NamingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
