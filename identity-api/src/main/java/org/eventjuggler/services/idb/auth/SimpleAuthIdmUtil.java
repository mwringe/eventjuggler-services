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
package org.eventjuggler.services.idb.auth;

import org.eventjuggler.services.idb.utils.KeyGenerator;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.model.Agent;
import org.picketlink.idm.model.Attribute;
import org.picketlink.idm.model.SimpleAgent;
import org.picketlink.idm.model.User;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class SimpleAuthIdmUtil {

    private final IdentityManager im;

    public SimpleAuthIdmUtil(IdentityManager im) {
        this.im = im;
    }

    public String setToken(User user) {
        String token = KeyGenerator.createToken();

        Agent tokenAgent = new SimpleAgent(token);
        tokenAgent.setAttribute(new Attribute<String>("loginName", user.getLoginName()));
        im.add(tokenAgent);

        return token;
    }

    public User getUser(String token) {
        Agent tokenAgent = im.getAgent(token);
        if (tokenAgent == null) {
            return null;
        }

        String loginName = (String) tokenAgent.getAttribute("loginName").getValue();
        if (loginName == null) {
            return null;
        }

        return im.getUser(loginName);
    }

    public void removeToken(String token) {
        Agent tokenAgent = im.getAgent(token);
        if (tokenAgent != null) {
            im.remove(tokenAgent);
        }
    }

}
