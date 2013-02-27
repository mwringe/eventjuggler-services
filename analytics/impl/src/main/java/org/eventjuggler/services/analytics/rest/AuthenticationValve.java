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
package org.eventjuggler.services.analytics.rest;

import java.io.IOException;

import javax.naming.NamingException;
import javax.servlet.ServletException;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;
import org.jboss.as.security.plugins.SecurityDomainContext;
import org.jboss.security.AuthenticationManager;
import org.jboss.security.SimplePrincipal;
import org.jboss.util.Base64;

/**
 * TODO Check if user is member of 'analytics' group
 *
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class AuthenticationValve extends ValveBase {

    private final SecurityDomainContext securityDomainContext;

    public AuthenticationValve(SecurityDomainContext securityDomainContext) throws NamingException {
        this.securityDomainContext = securityDomainContext;
    }

    @Override
    public void invoke(Request request, Response response) throws IOException, ServletException {
        String auth = request.getHeader("Authorization");

        if (auth != null) {
            String[] credentials = parseCredentials(auth);

            AuthenticationManager authenticationManager = securityDomainContext.getAuthenticationManager();
            if (authenticationManager.isValid(new SimplePrincipal(credentials[0]), credentials[1])) {
                getNext().invoke(request, response);
                return;
            }
        }

        response.setStatus(401);
        response.addHeader("WWW-Authenticate", "Basic realm=\"insert realm\"");
    }

    private String[] parseCredentials(String auth) {
        try {
            auth = auth.split(" ")[1];
            return new String(Base64.decode(auth)).split(":");
        } catch (Throwable t) {
            return null;
        }
    }

}
