package org.eventjuggler.services.idb.social;

import java.net.URI;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.eventjuggler.services.idb.model.Application;
import org.eventjuggler.services.idb.model.IdentityProviderConfig;
import org.picketlink.idm.model.User;

public class GoogleProvider implements IdentityProvider {

    @Override
    public String getIcon() {
        return "google.png";
    }

    @Override
    public String getId() {
        return "google";
    }

    @Override
    public URI getLoginUrl(Application application, IdentityProviderConfig provider) {
        UriBuilder builder = UriBuilder.fromUri("https://www.google.com");
        builder.replaceQueryParam("key", provider.getKey());
        return builder.build(); // TODO
    }

    @Override
    public String getName() {
        return "Google";
    }

    @Override
    public User getUser(HttpHeaders headers, UriInfo info) {
        return null; // TODO
    }

    @Override
    public boolean isCallbackHandler(HttpHeaders headers, UriInfo info) {
        return false; // TODO

    }

}
