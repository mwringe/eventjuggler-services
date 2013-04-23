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
package org.eventjuggler.services.utils;

import java.util.LinkedList;
import java.util.List;

import org.eventjuggler.services.simpleauth.rest.Attribute;
import org.eventjuggler.services.simpleauth.rest.UserInfo;
import org.picketlink.idm.model.User;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class UserFactory {

    public static UserInfo createUserInfo(User user) {
        UserInfo userInfo = new UserInfo();
        userInfo.setEmail(user.getEmail());
        userInfo.setFirstName(user.getFirstName());
        userInfo.setLastName(user.getLastName());
        userInfo.setUserId(user.getLoginName());

        if (!user.getAttributes().isEmpty()) {
            List<Attribute> attributes = new LinkedList<>();
            for (org.picketlink.idm.model.Attribute<?> a : user.getAttributes()) {
                if (a.getValue() instanceof String) {
                    attributes.add(new Attribute(a.getName(), (String) a.getValue()));
                }
            }
            userInfo.setAttributes(attributes);
        }

        return userInfo;
    }

}