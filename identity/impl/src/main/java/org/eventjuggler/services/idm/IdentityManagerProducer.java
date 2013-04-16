package org.eventjuggler.services.idm;

import javax.annotation.Resource;
import javax.enterprise.inject.Produces;

import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.IdentityManagerFactory;

public class IdentityManagerProducer {

    @Resource(lookup = "java:/picketlink/ExampleIMF")
    IdentityManagerFactory imf;

    @Produces
    public IdentityManager identityManager() {
        return imf.createIdentityManager();
    }

}
