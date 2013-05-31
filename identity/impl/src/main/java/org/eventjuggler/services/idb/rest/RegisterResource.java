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
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.eventjuggler.services.idb.ApplicationService;
import org.eventjuggler.services.idb.IdmService;
import org.eventjuggler.services.idb.model.Application;
import org.eventjuggler.services.utils.UriBuilder;
import org.picketlink.idm.model.SimpleUser;
import org.picketlink.idm.model.User;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
@Path("/register/{appKey}")
@Stateless
public class RegisterResource {

    @Context
    private UriInfo uriInfo;

    @Context
    private HttpHeaders headers;

    @Inject
    private ApplicationService as;

    @EJB
    private IdmService idm;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response getRegister(@PathParam("appKey") String appKey) {
        URI uri = new UriBuilder(headers, uriInfo, "register.html?app=" + appKey).build();
        return Response.seeOther(uri).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public Response register(@PathParam("appKey") String appKey, @FormParam("username") String username,
            @FormParam("email") String email, @FormParam("firstName") String firstName, @FormParam("lastName") String lastName,
            @FormParam("password") String password) throws URISyntaxException {
        Application application = as.getApplication(appKey);

        User user = new SimpleUser(trim(username));
        user.setFirstName(trim(firstName));
        user.setLastName(trim(lastName));
        user.setEmail(trim(email));

        try {
            idm.createUser(application.getRealm(), user, password);

            URI uri = new UriBuilder(headers, uriInfo, "login.html?app=" + appKey + "&info=created").build();
            return Response.seeOther(uri).build();
        } catch (Throwable e) {
            URI uri = new UriBuilder(headers, uriInfo, "register.html?app=" + appKey + "&warning=failed").build();
            return Response.seeOther(uri).build();
        }
    }

    private String trim(String s) {
        if (s != null && s.trim().length() == 0) {
            return null;
        } else {
            return s.trim();
        }
    }

}
