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
package org.eventjuggler.services.idb.rest;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.eventjuggler.services.idb.ApplicationService;
import org.eventjuggler.services.idb.model.Application;
import org.eventjuggler.services.simpleauth.rest.Authentication;
import org.eventjuggler.services.simpleauth.rest.AuthenticationRequest;
import org.eventjuggler.services.simpleauth.rest.AuthenticationResponse;
import org.jboss.resteasy.client.ProxyFactory;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
@Path("/dummysocial/{appKey}")
@Stateless
public class DummySocialResource {

    @EJB
    private ApplicationService service;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response getForm(@PathParam("appKey") String appKey, @Context UriInfo uri) {
        Application application = service.getApplication(appKey);
        if (application == null) {
            return Response.status(Status.BAD_REQUEST).build();
        }

        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append("<body>");
        sb.append("<h1>Welcome to Dummy Social</h1>");
        sb.append(application.getName() + " is requesting to authenticate using your account");
        sb.append("<form action='#' method='post'>");
        sb.append("<input type='text' name='username' placeholder='Username' />");
        sb.append("<input type='text' name='password' placeholder='Password' />");
        sb.append("<input type='hidden' name='appkey' value='" + appKey + "' />");
        sb.append("<button type='submit'>Accept</button>");
        sb.append("</form>");
        sb.append("</body>");
        sb.append("</html>");

        return Response.ok(sb.toString()).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public Response login(@PathParam("appKey") String appKey, @FormParam("username") String username,
            @FormParam("password") String password) throws URISyntaxException {
        Application application = service.getApplication(appKey);
        if (application == null) {
            return Response.status(Status.BAD_REQUEST).build();
        }

        Authentication auth = ProxyFactory.create(Authentication.class, "http://localhost:8080/ejs-identity/api");

        AuthenticationRequest request = new AuthenticationRequest();
        request.setUserId(username);
        request.setPassword(password);

        AuthenticationResponse response = auth.login(request);
        if (response.isLoggedIn()) {
            return Response.seeOther(
                    new URI("http://localhost:8080/ejs-identity/api/callback/" + appKey + "?token=" + response.getToken()))
                    .build();
        } else {
            return Response.status(Status.BAD_REQUEST).build();
        }
    }

}
