package org.eventjuggler.services.idb.provider;

import java.net.URI;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.eventjuggler.services.idb.model.Application;
import org.eventjuggler.services.idb.model.IdentityProviderConfig;
import org.eventjuggler.services.simpleauth.rest.Authentication;
import org.eventjuggler.services.simpleauth.rest.UserInfo;
import org.jboss.resteasy.client.ProxyFactory;
import org.picketlink.idm.model.SimpleUser;
import org.picketlink.idm.model.User;

public class DummyProvider implements IdentityProvider {

    @Override
    public String getIcon() {
        return "dummy.png";
    }

    @Override
    public String getId() {
        return "dummy";
    }

    @Override
    public URI getLoginUrl(Application application, IdentityProviderConfig provider) {
        UriBuilder builder = UriBuilder.fromUri("http://localhost:8080/ejs-identity/dummysocial/" + application.getKey());
        return builder.build();
    }

    @Override
    public String getName() {
        return "My Dummy Social Site";
    }

    @Override
    public User getUser(HttpHeaders headers, UriInfo info) {
        String token = info.getQueryParameters().getFirst("token");

        Authentication auth = ProxyFactory.create(Authentication.class, "http://localhost:8080/ejs-identity");
        UserInfo userInfo = auth.getInfo(token);

        User user = new SimpleUser(userInfo.getUserId());
        user.setEmail(userInfo.getEmail());
        user.setFirstName(userInfo.getFirstName());
        user.setLastName(userInfo.getLastName());
        return user;
    }

    @Override
    public boolean isCallbackHandler(HttpHeaders headers, UriInfo info) {
        return info.getQueryParameters().containsKey("token");
    }

}
