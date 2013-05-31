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

import org.eventjuggler.services.common.auth.Auth;
import org.eventjuggler.services.idb.IdmService;
import org.eventjuggler.services.simpleauth.rest.UserInfo;
import org.picketlink.idm.model.SimpleUser;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
@Stateless
public class IdentityManagementResource implements IdentityManagement {

    @EJB
    private IdmService idm;

    @Override
    public void deleteUser(String realm, String username) {
        Auth.requireUser(username);

        idm.deleteUser(realm, username);
    }

    @Override
    public UserInfo getUser(String realm, String username) {
        Auth.requireUser(username);

        return idm.getUserInfo(realm, username);
    }

    @Override
    public List<UserInfo> getUsers(String realm) {
        Auth.requireSuper();

        return idm.getUserInfos(realm);
    }

    @Override
    public void saveUser(String realm, String username, User user) {
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