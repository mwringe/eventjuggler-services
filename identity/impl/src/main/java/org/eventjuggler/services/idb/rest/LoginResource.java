package org.eventjuggler.services.idb.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.eventjuggler.services.idb.ApplicationService;
import org.eventjuggler.services.idb.model.Application;
import org.eventjuggler.services.idb.model.IdentityProviderConfig;
import org.eventjuggler.services.idb.provider.IdentityProvider;
import org.eventjuggler.services.idb.rest.LoginConfig.ProviderLoginConfig;
import org.eventjuggler.services.simpleauth.rest.Authentication;
import org.eventjuggler.services.simpleauth.rest.AuthenticationRequest;
import org.eventjuggler.services.simpleauth.rest.AuthenticationResponse;
import org.eventjuggler.services.utils.UriHelper;
import org.jboss.resteasy.client.ProxyFactory;

@Path("/login/{appKey}")
public class LoginResource {

    @Inject
    @Any
    private Instance<IdentityProvider> providers;

    @Inject
    private ApplicationService service;

    @Context
    private UriInfo uriInfo;

    @Context
    private HttpHeaders headers;

    private Application getApplication(String appKey) {
        Application application = service.getApplication(appKey);
        if (application == null) {
            throw new WebApplicationException(Status.BAD_REQUEST);
        }
        return application;
    }

    private IdentityProvider getIdentityProvider(String id) {
        for (IdentityProvider p : providers) {
            if (p.getId().equals(id)) {
                return p;
            }
        }
        return null;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public LoginConfig getLoginConfig(@PathParam("appKey") String appKey) {
        Application application = getApplication(appKey);
        UriHelper uriHelper = new UriHelper(uriInfo);

        LoginConfig loginConfig = new LoginConfig();
        loginConfig.setName(application.getName());

        List<ProviderLoginConfig> providerLoginConfigs = new LinkedList<>();

        for (IdentityProviderConfig c : application.getProviders()) {
            IdentityProvider provider = getIdentityProvider(c.getProviderId());

            URI loginUri = provider.getLoginUrl(application, c);

            ProviderLoginConfig providerLoginConfig = new ProviderLoginConfig();
            providerLoginConfig.setName(provider.getName());
            providerLoginConfig.setLoginUri(loginUri);
            providerLoginConfig.setIcon(uriHelper.getIcon(provider.getIcon()));

            providerLoginConfigs.add(providerLoginConfig);
        }

        loginConfig.setProviderConfigs(providerLoginConfigs);

        return loginConfig;
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public Response login(@PathParam("appKey") String appKey, @FormParam("username") String username,
            @FormParam("password") String password) throws URISyntaxException {
        Application application = getApplication(appKey);

        Authentication auth = ProxyFactory.create(Authentication.class, uriInfo.getBaseUri().toString());

        AuthenticationRequest request = new AuthenticationRequest();
        request.setUserId(username);
        request.setPassword(password);

        AuthenticationResponse response = auth.login(request);
        if (response.isLoggedIn()) {
            return Response.seeOther(new URI(application.getCallbackUrl() + "?token=" + response.getToken())).build();
        } else {
            URI uri = UriBuilder.fromUri(headers.getRequestHeader("referer").get(0)).replaceQueryParam("error", "invalid")
                    .build();
            return Response.seeOther(uri).build();
        }
    }

}
