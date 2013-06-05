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

import java.io.IOException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.eventjuggler.services.idb.IdentityManagerRegistry;
import org.picketlink.idm.model.User;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class AuthFilter implements Filter {

    private static final String REALM = "system";

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        if (request instanceof HttpServletRequest) {
            String token = ((HttpServletRequest) request).getHeader("token");
            if (token != null) {
                IdentityManagerRegistry identityManagerRegistry;
                try {
                    identityManagerRegistry = (IdentityManagerRegistry) new InitialContext()
                            .lookup("java:global/ejs/IdentityManagerRegistry");
                } catch (NamingException e) {
                    throw new IOException(e);
                }

                User user = new SimpleAuthIdmUtil(identityManagerRegistry.createIdentityManager(REALM)).getUser(token);
                if (user != null) {
                    Auth.set(user);
                }
            }
        }

        try {
            chain.doFilter(request, response);
        } finally {
            Auth.remove();
        }
    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {
    }

}
