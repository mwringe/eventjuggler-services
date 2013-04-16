package org.eventjuggler.services.idb.rest;

import java.net.URI;
import java.net.URISyntaxException;

import javax.inject.Inject;
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

@Path("/callback/{appKey}")
public class CallbackResource {

    @Inject
    private ApplicationService service;

    @GET
    public Response callback(@PathParam("appKey") String appKey, @Context UriInfo info, @Context HttpHeaders headers)
            throws URISyntaxException {
        Application application = service.getApplication(appKey);
        if (application == null) {
            return Response.status(Status.BAD_REQUEST).build();
        }

        String token = info.getPathParameters().getFirst("token"); // Hack as dummy social uses simple-auth to login ;)

        return Response.seeOther(new URI(application.getCallbackUrl() + "?token=" + token)).build();
    }

}
