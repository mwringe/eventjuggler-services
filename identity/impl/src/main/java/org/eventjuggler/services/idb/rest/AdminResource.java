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

import java.util.List;

import javax.ejb.EJB;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.eventjuggler.services.idb.ApplicationService;
import org.eventjuggler.services.idb.model.Application;
import org.eventjuggler.services.idb.model.providers.IdentityProviderDescription;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
@Path("/admin")
public class AdminResource implements Admin {

    @EJB
    private ApplicationService service;

    @Inject
    @Any
    private Instance<IdentityProviderDescription> providerDescriptions;

    @Override
    public Response createApplication(Application application, @Context UriInfo uriInfo) {
        if (application.getKey() != null || application.getSecret() != null) {
            return Response.status(Status.BAD_REQUEST).build();
        }

        service.create(application);

        return Response.created(uriInfo.getAbsolutePathBuilder().build(application.getKey())).build();
    }

    @Override
    public void deleteApplication(@PathParam("applicationKey") String applicationKey) {
        if (!service.remove(applicationKey)) {
            throw new WebApplicationException(Status.NOT_FOUND);
        }
    }

    @Override
    public Application getApplication(@PathParam("applicationKey") String applicationKey) {
        Application application = service.getApplication(applicationKey);
        if (application == null) {
            throw new WebApplicationException(Status.NOT_FOUND);
        }

        return application;
    }

    @Override
    public List<Application> getApplications() {
        return service.getApplications();
    }

    @Override
    public void updateApplication(@PathParam("applicationKey") String applicationKey, Application application) {
        service.update(application);
    }

    @Override
    public IdentityProviderDescription[] getProviderTypes() {
        return IdentityProviderDescription.getProviders();
    }

}
