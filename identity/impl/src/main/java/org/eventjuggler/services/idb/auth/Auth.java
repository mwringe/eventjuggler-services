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
package org.eventjuggler.services.idb.auth;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.picketlink.idm.model.User;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class Auth {

    public static final ThreadLocal<User> instances = new ThreadLocal<>();

    public static User get() {
        User user = instances.get();
        if (user == null) {
            throw new WebApplicationException(Status.FORBIDDEN);
        }
        return user;
    }

    public static String getUserId() {
        return get().getLoginName();
    }

    public static void requireUser(String requiredUser) {
        String userId = getUserId();
        if (!userId.equals(requiredUser)) {
            requireSuper();
        }
    }

    public static void requireSuper() {
        if (!getUserId().equals("root")) {
            throw new WebApplicationException(Status.FORBIDDEN);
        }
    }

    public static void remove() {
        instances.remove();
    }

    public static void set(User user) {
        instances.set(user);
    }

}
