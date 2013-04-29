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

@Path("/admin")
public interface Admin {

    @POST
    @Path("/applications")
    @Consumes(MediaType.APPLICATION_JSON)
    Response save(Application application);

    @DELETE
    @Path("/applications/{key}")
    void delete(@PathParam("key") String applicationKey);

    @GET
    @Path("/applications/{key}")
    @Produces(MediaType.APPLICATION_JSON)
    Application getApplication(@PathParam("key") String applicationKey);

    @GET
    @Path("/applications")
    @Produces(MediaType.APPLICATION_JSON)
    List<Application> getApplications();

    @PUT
    @Path("/applications/{key}")
    @Consumes(MediaType.APPLICATION_JSON)
    void save(@PathParam("key") String applicationKey, Application application);

    @GET
    @Path("/providers")
    @Produces(MediaType.APPLICATION_JSON)
    List<IdentityProviderDescription> getProviderTypes();

}