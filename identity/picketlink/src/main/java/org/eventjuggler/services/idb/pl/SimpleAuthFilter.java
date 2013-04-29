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

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.picketlink.Identity;
import org.picketlink.Identity.AuthenticationResult;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
@WebFilter(urlPatterns = "/*")
public class SimpleAuthFilter implements Filter {

    @Inject
    private SimpleAuthToken simpleAuthToken;

    @Inject
    private Identity identity;

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpSession session = httpRequest.getSession(false);

        String sessionToken = session != null ? (String) session.getAttribute("token") : null;
        String requestToken = httpRequest.getParameter("token");

        if (requestToken != null && !requestToken.equals(sessionToken)) {
            if (session == null) {
                session = httpRequest.getSession();
            }

            session.setAttribute("token", null);

            if (identity.isLoggedIn()) {
                identity.logout();
            }

            if (!requestToken.equals("logout")) {
                simpleAuthToken.setToken(requestToken);
                AuthenticationResult result = identity.login();
                if (result == AuthenticationResult.SUCCESS) {
                    session.setAttribute("token", requestToken);
                }
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {
    }

}
