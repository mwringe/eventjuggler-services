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
import java.util.LinkedList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.eventjuggler.services.idb.ApplicationBean;
import org.eventjuggler.services.idb.IdentityManagerRegistry;
import org.eventjuggler.services.idb.auth.Auth;
import org.eventjuggler.services.idb.model.Application;
import org.eventjuggler.services.idb.model.Realm;
import org.eventjuggler.services.idb.provider.IdentityProvider;
import org.eventjuggler.services.idb.provider.IdentityProviderBean;
import org.eventjuggler.services.utils.UriBuilder;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
@Stateless
@Path("/admin")
public class AdminResource {

    @EJB
    private ApplicationBean applicationService;

    @Context
    private HttpHeaders headers;

    @EJB
    private IdentityManagerRegistry identityManagerRegistry;

    @EJB
    private IdentityProviderBean providerService;

    @Context
    private UriInfo uriInfo;

    public AdminResource() {
    }

    @DELETE
    @Path("/applications/{key}")
    public void deleteApplication(@PathParam("key") String key) {
        Application application = getApplication(key);
        if (application == null) {
            throw new WebApplicationException(Status.NOT_FOUND);
        }

        Auth.requireUser(application.getOwner());

        applicationService.remove(application);
    }

    @DELETE
    @Path("/realms/{name}")
    public void deleteRealm(@PathParam("name") String name) {
        if (!identityManagerRegistry.containsRealm(name)) {
            throw new WebApplicationException(Status.NOT_FOUND);
        }

        Auth.requireUser();

        identityManagerRegistry.deleteRealm(name);
    }

    @GET
    @Path("/applications/{key}")
    @Produces(MediaType.APPLICATION_JSON)
    public Application getApplication(@PathParam("key") String key) {
        Application application = applicationService.getApplication(key);
        if (application == null) {
            throw new WebApplicationException(Status.NOT_FOUND);
        }

        Auth.requireUser(application.getOwner());

        return application;
    }

    @GET
    @Path("/applications")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Application> getApplications() {
        Auth.requireUser();

        if (Auth.isSuper()) {
            return applicationService.getApplications();
        } else {
            return applicationService.getApplications(Auth.getUserId());
        }
    }

    @GET
    @Path("/providers")
    @Produces(MediaType.APPLICATION_JSON)
    public List<IdentityProviderDescription> getProviderTypes() {
        List<IdentityProviderDescription> descriptions = new LinkedList<>();
        for (IdentityProvider provider : providerService.getProviders()) {
            URI icon = new UriBuilder(headers, uriInfo, "icons/" + provider.getIcon()).build();
            descriptions.add(new IdentityProviderDescription(provider.getId(), provider.getName(), icon));
        }
        return descriptions;
    }

    @GET
    @Path("/realms")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Realm> getRealms() {
        Auth.requireUser();

        return identityManagerRegistry.getRealms();
    }

    @POST
    @Path("/applications")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response save(Application application) {
        Auth.requireUser();

        if (application.getKey() != null) {
            return Response.status(Status.BAD_REQUEST).build();
        }

        if (!Auth.isSuper() && application.getSecret() != null) {
            return Response.status(Status.FORBIDDEN).build();
        }

        if (application.getOwner() == null) {
            application.setOwner(Auth.getUserId());
        } else {
            Auth.requireUser(application.getOwner());
        }

        applicationService.create(application);

        return Response.created(URI.create("/applications/" + application.getKey())).build();
    }

    @PUT
    @Path("/applications/{key}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void save(@PathParam("key") String key, Application application) {
        Application a = applicationService.getApplication(key);

        if (a == null) {
            if (Auth.isSuper()) {
                if (application.getOwner() == null) {
                    application.setOwner(Auth.getUserId());
                }

                applicationService.create(application);
            } else {
                throw new WebApplicationException(Status.FORBIDDEN);
            }
        } else {
            if (!a.getKey().equals(application.getKey())) {
                throw new WebApplicationException(Status.BAD_REQUEST);
            }

            if (!Auth.isSuper()) {
                if (!a.getSecret().equals(application.getSecret())) {
                    throw new WebApplicationException(Status.FORBIDDEN);
                }
            }

            Auth.requireUser(application.getOwner());

            applicationService.update(application);
        }
    }

    @PUT
    @Path("/realms/{name}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void save(@PathParam("name") String name, Realm realm) {
        Auth.requireUser();

        if (!identityManagerRegistry.containsRealm(name)) {
            identityManagerRegistry.createRealm(realm);
        }
    }

}
