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

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.credential.Password;
import org.picketlink.idm.model.Attribute;
import org.picketlink.idm.model.SimpleUser;
import org.picketlink.idm.model.User;

public class AccountRegistrationResource implements AccountRegistration {

    @Inject
    private IdentityManager identityManager;

    @Override
    public void register(AccountRegistrationRequest request) {
        String userName = request.getUserName();

        if (identityManager.getUser(userName) == null) {
            User user = new SimpleUser(userName);
            user.setEmail(request.getEmail());
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());

            user.setAttribute(new Attribute<String>("address", request.getAddress()));
            user.setAttribute(new Attribute<String>("city", request.getCity()));
            user.setAttribute(new Attribute<String>("state", request.getState()));
            user.setAttribute(new Attribute<String>("postalCode", request.getPostalCode()));
            user.setAttribute(new Attribute<String>("country", request.getCountry()));

            identityManager.add(user);
            identityManager.updateCredential(user, new Password(request.getPassword()));
        } else {
            throw new WebApplicationException(Status.BAD_REQUEST);
        }
    }
}