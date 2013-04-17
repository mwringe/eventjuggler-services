package org.eventjuggler.services.idb.rest;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eventjuggler.services.idb.model.Application;

public interface Admin {

    @POST
    @Path("/applications")
    @Consumes(MediaType.APPLICATION_JSON)
    Response createApplication(Application application);

    @DELETE
    @Path("/applications/{applicationKey}")
    void deleteApplication(@PathParam("applicationKey") String applicationKey);

    @GET
    @Path("/applications/{applicationKey}")
    @Produces(MediaType.APPLICATION_JSON)
    Application getApplication(@PathParam("applicationKey") String applicationKey);

    @GET
    @Path("/applications")
    @Produces(MediaType.APPLICATION_JSON)
    List<Application> getApplications();

    @PUT
    @Path("/applications/{applicationKey}")
    @Consumes(MediaType.APPLICATION_JSON)
    void updateApplication(@PathParam("applicationKey") String applicationKey, Application application);

    @GET
    @Path("/providers")
    @Produces(MediaType.APPLICATION_JSON)
    List<IdentityProviderDescription> getProviderTypes();

}