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

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.eventjuggler.services.idb.ApplicationBean;
import org.eventjuggler.services.idb.IdentityManagementBean;
import org.eventjuggler.services.idb.model.Application;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
@Stateless
public class AuthenticationResource implements Authentication {

    @EJB
    private ApplicationBean as;

    @EJB
    private IdentityManagementBean idm;

    @Override
    public AuthenticationResponse login(final AuthenticationRequest authcRequest) {
        Application application = as.getApplication(authcRequest.getApplicationKey());
        return idm.login(application.getRealm(), authcRequest.getUserId(), authcRequest.getPassword());
    }

    @Override
    public void logout(String applicationKey, String token) {
        Application application = as.getApplication(applicationKey);
        idm.logout(application.getRealm(), token);
    }

    @Override
    public UserInfo getInfo(String applicationKey, String token) {
        Application application = as.getApplication(applicationKey);
        return idm.getUserInfoByToken(application.getRealm(), token);
    }

}