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

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.eventjuggler.services.idb.model.Application;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
@Path("/admin")
@Stateless
public class AdminResource {

    @PersistenceContext(unitName = "idb")
    private EntityManager em;

    @Inject
    private KeyGenerator keyGenerator;

    @POST
    @Path("/applications")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createApplication(Application application, @Context UriInfo uriInfo) {
        if (application.getKey() != null || application.getSecret() != null) {
            return Response.status(Status.BAD_REQUEST).build();
        }

        application.setKey(keyGenerator.createApplicationKey());
        application.setSecret(keyGenerator.createApplicationSecret());

        em.persist(application);

        return Response.created(uriInfo.getAbsolutePathBuilder().build(application.getKey())).build();
    }

    @DELETE
    @Path("/applications/{applicationKey}")
    public Response deleteApplication(@PathParam("applicationKey") String applicationKey) {
        Application application = em.find(Application.class, applicationKey);
        if (application == null) {
            return Response.status(Status.NOT_FOUND).build();
        }

        em.remove(application);

        return Response.noContent().build();
    }

    @GET
    @Path("/applications/{applicationKey}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getApplication(@PathParam("applicationKey") String applicationKey) {
        Application application = em.find(Application.class, applicationKey);

        if (application == null) {
            return Response.status(Status.NOT_FOUND).build();
        } else {
            return Response.ok(application).build();
        }
    }

    @GET
    @Path("/applications")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Application> getApplications() {
        return em.createQuery("from Application", Application.class).getResultList();
    }

    @PUT
    @Path("/applications/{applicationKey}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateApplication(@PathParam("applicationKey") String applicationKey, Application application) {
        System.out.println("Providers before:" + application.getProviders().size());
        application = em.merge(application);
        System.out.println("Providers after:" + application.getProviders().size());
        return Response.noContent().build();
    }

}
