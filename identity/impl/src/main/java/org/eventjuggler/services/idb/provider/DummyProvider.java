/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
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

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
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
