package org.eventjuggler.services.idb.rest;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ejb.EJB;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
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
import org.eventjuggler.services.idb.provider.IdentityProvider;
import org.eventjuggler.services.utils.TokenService;
import org.picketlink.idm.model.User;

@Path("/callback/{appKey}")
public class CallbackResource {

    @Inject
    private ApplicationService service;

    @Inject
    @Any
    private Instance<IdentityProvider> providers;

    @Context
    private UriInfo info;

    @Context
    private HttpHeaders headers;

    @EJB
    private TokenService tokenManager;

    @GET
    public Response callback(@PathParam("appKey") String appKey) throws URISyntaxException {
        Application application = service.getApplication(appKey);
        if (application == null) {
            return Response.status(Status.BAD_REQUEST).build();
        }

        for (IdentityProvider provider : providers) {
            if (provider.isCallbackHandler(headers, info)) {
                User user = provider.getUser(headers, info);
                String token = tokenManager.put(user);

                return Response.seeOther(new URI(application.getCallbackUrl() + "?token=" + token)).build();
            }
        }

        return Response.status(Status.BAD_REQUEST).build();
    }

}
