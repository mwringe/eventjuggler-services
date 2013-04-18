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

import java.util.LinkedList;
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
import org.eventjuggler.services.idb.provider.IdentityProvider;
import org.eventjuggler.services.idb.rest.auth.LoggedIn;
import org.eventjuggler.services.utils.UriHelper;
import org.picketlink.Identity;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
@Path("/admin")
public class AdminResource implements Admin {

    @Inject
    @Any
    private Instance<IdentityProvider> providers;

    @EJB
    private ApplicationService service;

    @Context
    private UriInfo uriInfo;

    @Inject
    private Identity identity;

    @Override
    @LoggedIn
    public Response createApplication(Application application) {
        if (application.getKey() != null || application.getSecret() != null || application.getOwner() != null) {
            return Response.status(Status.BAD_REQUEST).build();
        }

        service.create(identity.getUser().getLoginName(), application);

        return Response.created(uriInfo.getAbsolutePathBuilder().build(application.getKey())).build();
    }

    @Override
    @LoggedIn
    public void deleteApplication(@PathParam("applicationKey") String applicationKey) {
        Application application = getApplication(applicationKey);
        if (application == null) {
            throw new WebApplicationException(Status.NOT_FOUND);
        }

        hasAccess(application);

        service.remove(application);
    }

    @Override
    @LoggedIn
    public Application getApplication(@PathParam("applicationKey") String applicationKey) {
        Application application = service.getApplication(applicationKey);
        if (application == null) {
            throw new WebApplicationException(Status.NOT_FOUND);
        }

        hasAccess(application);

        return application;
    }

    @Override
    @LoggedIn
    public List<Application> getApplications() {
        return service.getApplications(identity.getUser().getLoginName());
    }

    @Override
    public List<IdentityProviderDescription> getProviderTypes() {
        UriHelper uriHelper = new UriHelper(uriInfo);

        List<IdentityProviderDescription> descriptions = new LinkedList<>();
        for (IdentityProvider provider : providers) {
            descriptions.add(new IdentityProviderDescription(provider.getId(), provider.getName(), uriHelper.getIcon(provider
                    .getIcon())));
        }
        return descriptions;
    }

    @Override
    @LoggedIn
    public void updateApplication(@PathParam("applicationKey") String applicationKey, Application application) {
        Application a = getApplication(applicationKey);

        if (!a.getKey().equals(application.getKey()) || !a.getSecret().equals(application.getSecret())
                || !a.getOwner().equals(application.getOwner())) {
            throw new WebApplicationException(Status.FORBIDDEN);
        }

        hasAccess(application);

        service.update(application);
    }

    private void hasAccess(Application application) {
        if (!identity.getUser().getLoginName().equals(application.getOwner())) {
            throw new WebApplicationException(Status.FORBIDDEN);
        }
    }

}
