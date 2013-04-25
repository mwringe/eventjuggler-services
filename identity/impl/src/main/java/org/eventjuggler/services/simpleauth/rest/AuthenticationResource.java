/*
 * JBoss, Home of Professional Open Source
 *
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.eventjuggler.services.simpleauth.rest;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.QueryParam;

import org.eventjuggler.services.simpleauth.SimpleAuthIdmUtil;
import org.eventjuggler.services.utils.UserFactory;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.IdentityManagerFactory;
import org.picketlink.idm.credential.Credentials;
import org.picketlink.idm.credential.Password;
import org.picketlink.idm.credential.UsernamePasswordCredentials;
import org.picketlink.idm.model.User;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
@Stateless
@EJB(name = "java:global/ejs/Authentication", beanInterface = Authentication.class)
public class AuthenticationResource implements Authentication {

    @Resource(lookup = "java:/picketlink/ExampleIMF")
    private IdentityManagerFactory imf;

    @Override
    public AuthenticationResponse login(final AuthenticationRequest authcRequest) {
        IdentityManager im = imf.createIdentityManager();
        SimpleAuthIdmUtil idmUtil = new SimpleAuthIdmUtil(im);

        User user = null;
        String token = null;

        if (authcRequest.getPassword() != null) {
            UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(authcRequest.getUserId(), new Password(
                    authcRequest.getPassword()));

            im.validateCredentials(credentials);

            if (Credentials.Status.VALID.equals(credentials.getStatus())) {
                user = im.getUser(credentials.getUsername());
                token = idmUtil.setToken(user);
            }
        } else if (authcRequest.getToken() != null) {
            user = idmUtil.getUser(authcRequest.getToken());
            token = authcRequest.getToken();
        }

        AuthenticationResponse response = new AuthenticationResponse();
        if (user != null) {
            response.setLoggedIn(true);
            response.setUserId(user.getLoginName());
            response.setToken(token);
        }

        return response;
    }

    @Override
    public void logout(@QueryParam("token") String token) {
        new SimpleAuthIdmUtil(imf.createIdentityManager()).removeToken(token);
    }

    @Override
    public UserInfo getInfo(@QueryParam("token") String token) {
        if (token != null) {
            User user = new SimpleAuthIdmUtil(imf.createIdentityManager()).getUser(token);
            if (user != null) {
                return UserFactory.createUserInfo(user);
            }
        }

        return new UserInfo();
    }

}