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

import java.io.IOException;
import java.io.InputStream;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eventjuggler.services.simpleauth.TokenManager;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.credential.Credentials;
import org.picketlink.idm.credential.Password;
import org.picketlink.idm.credential.UsernamePasswordCredentials;
import org.picketlink.idm.model.User;

@Path("/")
public class AuthenticationResource {

    @Inject
    private IdentityManager identityManager;

    @EJB
    private TokenManager tokenManager;

    @Path("/signin")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public AuthenticationResponse login(final AuthenticationRequest authcRequest) {
        User user = null;
        String token = null;

        if (authcRequest.getPassword() != null) {
            UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(authcRequest.getUserId(), new Password(
                    authcRequest.getPassword()));

            identityManager.validateCredentials(credentials);

            if (Credentials.Status.VALID.equals(credentials.getStatus())) {
                user = identityManager.getUser(credentials.getUsername());
                token = tokenManager.put(user);
            }
        } else if (authcRequest.getToken() != null) {
            user = tokenManager.get(authcRequest.getToken());
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

    @GET
    @Path("/logout")
    public void logout(@QueryParam("token") String token) {
        if (token != null && tokenManager.valid(token)) {
            tokenManager.remove(token);
        }
    }

    @GET
    @Path("/userinfo")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInfo(@QueryParam("token") String token) {
        if (token != null && tokenManager.valid(token)) {
            User user = tokenManager.get(token);

            String fullName = user.getFirstName();
            if (user.getFirstName() != null) {
                fullName = user.getLastName() != null ? user.getFirstName() + " " + user.getLastName() : user.getFirstName();
            } else {
                fullName = user.getLastName();
            }

            UserInfo userInfo = new UserInfo();
            userInfo.setFullName(fullName);
            userInfo.setUserId(user.getLoginName());

            return Response.ok(userInfo).build();
        } else {
            return Response.status(Status.FORBIDDEN).build();
        }
    }

    @GET
    @Path("/jss/{script}")
    @Produces("text/javascript")
    public Response getLoginScripts(@PathParam("script") String script) throws IOException {
        InputStream is = getClass().getResourceAsStream("/js/" + script);
        if (is != null) {
            return Response.ok(is).type("text/javascript").build();
        } else {
            return Response.status(Status.NOT_FOUND).build();
        }
    }

}