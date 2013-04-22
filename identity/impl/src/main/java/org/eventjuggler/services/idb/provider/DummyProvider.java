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
package org.eventjuggler.services.idb.provider;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;

import org.eventjuggler.services.idb.model.Application;
import org.eventjuggler.services.idb.model.IdentityProviderConfig;
import org.eventjuggler.services.idb.rest.DummySocialResource;
import org.picketlink.idm.model.User;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class DummyProvider implements IdentityProvider {

    @Override
    public String getIcon() {
        return "dummy.png";
    }

    @Override
    public String getId() {
        return "dummy";
    }

    @Override
    public URI getLoginUrl(Application application, IdentityProviderConfig provider) {
        try {
            return new URI("/ejs-identity/api/dummysocial/" + application.getKey());
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getName() {
        return "My Dummy Social Site";
    }

    @Override
    public User getUser(Map<String, List<String>> headers, Map<String, List<String>> queryParameters) {
        String dummytoken = queryParameters.get("dummytoken").get(0);

        try {
            DummySocialResource dummySocialResource = (DummySocialResource) new InitialContext()
                    .lookup("java:global/ejs-identity/DummySocialResource");
            return dummySocialResource.getUser(dummytoken);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean isCallbackHandler(Map<String, List<String>> headers, Map<String, List<String>> queryParameters) {
        return queryParameters.containsKey("dummytoken");
    }

}
