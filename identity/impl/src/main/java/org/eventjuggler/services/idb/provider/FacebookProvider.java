package org.eventjuggler.services.idb.provider;

import java.net.URI;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.UriBuilder;

import org.eventjuggler.services.idb.model.Application;
import org.eventjuggler.services.idb.model.IdentityProviderConfig;
import org.picketlink.idm.model.User;

public class FacebookProvider implements IdentityProvider {

    @Override
    public String getIcon() {
        return "facebook.png";
    }

    @Override
    public String getId() {
        return "facebook";
    }

    @Override
    public URI getLoginUrl(Application application, IdentityProviderConfig provider) {
        UriBuilder builder = UriBuilder.fromUri("https://www.facebook.com");
        builder.replaceQueryParam("key", provider.getKey());
        return builder.build(); // TODO
    }

    @Override
    public String getName() {
        return "Facebook";
    }

    @Override
    public User getUser(Map<String, List<String>> headers, Map<String, List<String>> queryParameters) {
        return null; // TODO
    }

    @Override
    public boolean isCallbackHandler(Map<String, List<String>> headers, Map<String, List<String>> queryParameters) {
        return false; // TODO

    }

}
