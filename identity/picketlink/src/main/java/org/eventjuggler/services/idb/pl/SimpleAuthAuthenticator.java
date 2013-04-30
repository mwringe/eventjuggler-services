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
import org.picketlink.authentication.BaseAuthenticator;
import org.picketlink.idm.IdentityManagerFactory;
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

    @Override
    public void authenticate() {
        String t = token.getValue();
        if (t != null) {
            User user = new SimpleAuthIdmUtil(imf.createIdentityManager()).getUser(t);
            if (user != null) {
                setUser(user);

                setStatus(AuthenticationStatus.SUCCESS);
            } else {
                setStatus(AuthenticationStatus.FAILURE);
            }
        } else {
            setStatus(AuthenticationStatus.DEFERRED);
        }
    }

}
