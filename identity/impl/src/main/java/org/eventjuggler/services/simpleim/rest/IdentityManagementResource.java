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
package org.eventjuggler.services.simpleim.rest;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eventjuggler.services.idb.IdentityManagementBean;
import org.eventjuggler.services.idb.auth.Auth;
import org.eventjuggler.services.simpleauth.rest.UserInfo;
import org.picketlink.idm.model.SimpleUser;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
@Stateless
@Path("im")
public class IdentityManagementResource {

    @EJB
    private IdentityManagementBean idm;

    @DELETE
    @Path("{realm}/users/{username}")
    public void deleteUser(@PathParam("realm") String realm, @PathParam("username") String username) {
        Auth.requireUser(username);

        idm.deleteUser(realm, username);
    }

    @GET
    @Path("{realm}/users/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public UserInfo getUser(@PathParam("realm") String realm, @PathParam("username") String username) {
        Auth.requireUser(username);

        return idm.getUserInfo(realm, username);
    }

    @GET
    @Path("{realm}/users")
    @Produces(MediaType.APPLICATION_JSON)
    public List<UserInfo> getUsers(@PathParam("realm") String realm) {
        Auth.requireSuper();

        return idm.getUserInfos(realm);
    }

    @PUT
    @Path("{realm}/users/{username}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void saveUser(@PathParam("realm") String realm, @PathParam("username") String username, UserDetails user) {
        org.picketlink.idm.model.User u = idm.getUser(realm, username);
        boolean userExists = u != null;

        if (userExists) {
            Auth.requireUser(username);
        } else {
            u = new SimpleUser(username);
        }

        u.setEmail(user.getEmail());
        u.setFirstName(user.getFirstName());
        u.setLastName(user.getLastName());

        if (userExists) {
            idm.updateUser(realm, u, user.getPassword());
        } else {
            idm.createUser(realm, u, user.getPassword());
        }
    }

}