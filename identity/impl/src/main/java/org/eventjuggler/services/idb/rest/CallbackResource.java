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

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.eventjuggler.services.idb.ApplicationService;
import org.eventjuggler.services.idb.model.Application;
import org.eventjuggler.services.idb.provider.IdentityProvider;
import org.eventjuggler.services.idb.provider.IdentityProviderService;
import org.eventjuggler.services.utils.TokenService;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.IdentityManagerFactory;
import org.picketlink.idm.model.User;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
@Path("/callback/{appKey}")
@Stateless
public class CallbackResource {

    @EJB
    private ApplicationService applicationService;

    @Context
    private HttpHeaders headers;

    @Resource(lookup = "java:/picketlink/ExampleIMF")
    private IdentityManagerFactory imf;

    @Context
    private UriInfo info;

    @EJB
    private IdentityProviderService providerService;

    @EJB
    private TokenService tokenManager;

    @GET
    public Response callback(@PathParam("appKey") String appKey) throws URISyntaxException {
        IdentityManager im = imf.createIdentityManager();

        Application application = applicationService.getApplication(appKey);
        if (application == null) {
            return Response.status(Status.BAD_REQUEST).build();
        }

        for (IdentityProvider provider : providerService.getProviders()) {
            if (provider.isCallbackHandler(headers.getRequestHeaders(), info.getQueryParameters())) {
                User user = provider.getUser(headers.getRequestHeaders(), info.getQueryParameters());

                if (im.getUser(user.getLoginName()) != null) {
                    im.add(user);
                }

                String token = tokenManager.put(user);
                return Response.seeOther(new URI(application.getCallbackUrl() + "?token=" + token)).build();
            }
        }

        return Response.status(Status.BAD_REQUEST).build();
    }

}
