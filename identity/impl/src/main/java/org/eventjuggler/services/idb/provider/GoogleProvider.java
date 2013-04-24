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
import java.util.UUID;

import org.picketlink.idm.model.SimpleUser;
import org.picketlink.idm.model.User;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Tokeninfo;
import com.google.api.services.oauth2.model.Userinfo;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class GoogleProvider implements IdentityProvider {

    private static final JacksonFactory JSON_FACTORY = new JacksonFactory();

    private static final NetHttpTransport TRANSPORT = new NetHttpTransport();

    // TODO This needs to be unique per client
    private static final String state = UUID.randomUUID().toString();

    @Override
    public String getIcon() {
        return "google.png";
    }

    @Override
    public String getId() {
        return "google";
    }

    @Override
    public URI getLoginUrl(IdentityProviderCallback callback) {
        return callback.createUri("https://accounts.google.com/o/oauth2/auth").setQueryParam("client_id", callback.getProviderKey())
                .setQueryParam("response_type", "code")
                .setQueryParam("scope", "https://www.googleapis.com/auth/userinfo.profile")
                .setQueryParam("redirect_uri", callback.getBrokerCallbackUrl().toString()).setQueryParam("state", state)
                .build();
    }

    @Override
    public String getName() {
        return "Google";
    }

    @Override
    public User getUser(IdentityProviderCallback callback) {
        String code = callback.getQueryParam("code");

        try {
            GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(TRANSPORT, JSON_FACTORY,
                    callback.getProviderKey(), callback.getProviderSecret(), code, callback.getBrokerCallbackUrl().toString()).execute();

            GoogleCredential credential = new GoogleCredential.Builder().setJsonFactory(JSON_FACTORY).setTransport(TRANSPORT)
                    .setClientSecrets(callback.getProviderKey(), callback.getProviderSecret()).build().setFromTokenResponse(tokenResponse);

            Oauth2 oauth2 = new Oauth2.Builder(TRANSPORT, JSON_FACTORY, credential).build();

            Tokeninfo tokenInfo = oauth2.tokeninfo().setAccessToken(credential.getAccessToken()).execute();

            if (tokenInfo.containsKey("error")) {
                throw new RuntimeException("error");
            }

            Userinfo userInfo = oauth2.userinfo().get().execute();
            User user = new SimpleUser(userInfo.getId());
            user.setFirstName(userInfo.getGivenName());
            user.setLastName(userInfo.getFamilyName());
            user.setEmail(userInfo.getEmail());

            return user;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isCallbackHandler(IdentityProviderCallback callback) {
        return callback.containsQueryParam("state") && callback.getQueryParam("state").equals(state);
    }

}
