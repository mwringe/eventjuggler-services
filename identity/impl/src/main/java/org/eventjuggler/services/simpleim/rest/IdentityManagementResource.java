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

import java.util.LinkedList;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.eventjuggler.services.common.auth.Auth;
import org.eventjuggler.services.simpleauth.rest.UserInfo;
import org.eventjuggler.services.utils.UserFactory;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.IdentityManagerFactory;
import org.picketlink.idm.credential.Password;
import org.picketlink.idm.model.SimpleUser;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
@Stateless
public class IdentityManagementResource implements IdentityManagement {

    @Resource(name = "IdentityManagerFactory")
    private IdentityManagerFactory imf;

    @Override
    public void deleteUser(@PathParam("username") String username) {
        Auth.requireUser(username);

        IdentityManager im = imf.createIdentityManager();

        org.picketlink.idm.model.User user = im.getUser(username);
        if (user == null) {
            throw new WebApplicationException(Status.NOT_FOUND);
        }

        im.remove(user);
    }

    @Override
    public UserInfo getUser(@PathParam("username") String username) {
        Auth.requireUser(username);

        IdentityManager im = imf.createIdentityManager();

        org.picketlink.idm.model.User user = im.getUser(username);
        if (user == null) {
            throw new WebApplicationException(Status.NOT_FOUND);
        }

        return UserFactory.createUserInfo(user);
    }

    @Override
    public List<UserInfo> getUsers() {
        Auth.requireSuper();

        IdentityManager im = imf.createIdentityManager();

        List<org.picketlink.idm.model.User> users = im.createIdentityQuery(org.picketlink.idm.model.User.class)
                .getResultList();
        List<UserInfo> userInfos = new LinkedList<>();
        for (org.picketlink.idm.model.User u : users) {
            userInfos.add(UserFactory.createUserInfo(u));
        }
        return userInfos;
    }

    @Override
    public void saveUser(String username, User user) {
        IdentityManager im = imf.createIdentityManager();

        org.picketlink.idm.model.User u = im.getUser(username);
        boolean userExists = u != null;

        if (userExists) {
            Auth.requireUser(username);
        } else {
            u = new SimpleUser(username);
        }

        u.setEmail(user.getEmail());
        u.setFirstName(user.getFirstName());
        u.setLastName(user.getLastName());

        // user.setAttribute(new Attribute<String>("address", request.getAddress()));
        // user.setAttribute(new Attribute<String>("city", request.getCity()));
        // user.setAttribute(new Attribute<String>("state", request.getState()));
        // user.setAttribute(new Attribute<String>("postalCode", request.getPostalCode()));
        // user.setAttribute(new Attribute<String>("country", request.getCountry()));

        if (userExists) {
            im.update(u);
        } else {
            im.add(u);
        }

        if (user.getPassword() != null) {
            im.updateCredential(u, new Password(user.getPassword()));
        }
    }

}