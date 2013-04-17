package org.eventjuggler.services.idb.provider;

import java.net.URI;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;

import org.eventjuggler.services.idb.model.Application;
import org.eventjuggler.services.idb.model.IdentityProviderConfig;
import org.picketlink.idm.model.User;

public interface IdentityProvider {

    String getId();

    URI getLoginUrl(Application application, IdentityProviderConfig provider);

    String getIcon();

    String getName();

    User getUser(HttpHeaders headers, UriInfo info);

    boolean isCallbackHandler(HttpHeaders headers, UriInfo info);

}
