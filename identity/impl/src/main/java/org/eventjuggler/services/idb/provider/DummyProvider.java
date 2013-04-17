package org.eventjuggler.services.idb.provider;

import java.net.URI;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.UriBuilder;

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
        return UriBuilder.fromUri("http://localhost:8080/ejs-identity/api/dummysocial/" + application.getKey()).build();
    }

    @Override
    public String getName() {
        return "My Dummy Social Site";
    }

    @Override
    public User getUser(Map<String, List<String>> headers, Map<String, List<String>> queryParameters) {
        String token = queryParameters.get("token").get(0);

        Authentication auth = ProxyFactory.create(Authentication.class, "http://localhost:8080/ejs-identity/api");
        UserInfo userInfo = auth.getInfo(token);

        User user = new SimpleUser(userInfo.getUserId());
        user.setEmail(userInfo.getEmail());
        user.setFirstName(userInfo.getFirstName());
        user.setLastName(userInfo.getLastName());
        return user;
    }

    @Override
    public boolean isCallbackHandler(Map<String, List<String>> headers, Map<String, List<String>> queryParameters) {
        return queryParameters.containsKey("token");
    }

}
