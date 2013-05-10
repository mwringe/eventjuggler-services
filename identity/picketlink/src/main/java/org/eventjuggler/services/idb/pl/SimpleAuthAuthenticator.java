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
package org.eventjuggler.services.idb.pl;

import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eventjuggler.services.common.auth.SimpleAuthIdmUtil;
import org.eventjuggler.services.simpleauth.rest.Attribute;
import org.eventjuggler.services.simpleauth.rest.Authentication;
import org.eventjuggler.services.simpleauth.rest.UserInfo;
import org.jboss.resteasy.client.ProxyFactory;
import org.picketlink.authentication.BaseAuthenticator;
import org.picketlink.idm.IdentityManagerFactory;
import org.picketlink.idm.model.SimpleUser;
import org.picketlink.idm.model.User;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
@ApplicationScoped
public class SimpleAuthAuthenticator extends BaseAuthenticator {

    @Inject
    private SimpleAuthToken token;

    @Resource(name = "IdentityManagerFactory")
    private IdentityManagerFactory imf;

    @Inject
    private SimpleAuthConfig config;

    @Override
    public void authenticate() {
        String t = token.getValue();
        if (t != null) {
            if (config.isLocal()) {
                User user = new SimpleAuthIdmUtil(imf.createIdentityManager()).getUser(t);
                if (user != null) {
                    setUser(user);
                    setStatus(AuthenticationStatus.SUCCESS);
                } else {
                    setStatus(AuthenticationStatus.FAILURE);
                }
            } else {
                Authentication authentication = ProxyFactory
                        .create(Authentication.class, config.getUrl() + "/ejs-identity/api");
                UserInfo userInfo = authentication.getInfo(t);
                if (userInfo != null) {
                    User user = new SimpleUser(userInfo.getUserId());
                    user.setFirstName(userInfo.getFirstName());
                    user.setLastName(userInfo.getLastName());
                    user.setEmail(userInfo.getEmail());

                    if (userInfo.getAttributes() != null) {
                        for (Attribute a : userInfo.getAttributes()) {
                            user.setAttribute(new org.picketlink.idm.model.Attribute<String>(a.getName(), a.getValue()));
                        }
                    }

                    setUser(user);
                    setStatus(AuthenticationStatus.SUCCESS);
                } else {
                    setStatus(AuthenticationStatus.FAILURE);

                }
            }
        } else {
            setStatus(AuthenticationStatus.DEFERRED);
        }
    }

}
