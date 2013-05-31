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
import java.util.LinkedList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
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
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.eventjuggler.services.idb.ApplicationService;
import org.eventjuggler.services.idb.IdmService;
import org.eventjuggler.services.idb.model.Application;
import org.eventjuggler.services.idb.model.IdentityProviderConfig;
import org.eventjuggler.services.idb.provider.IdentityProvider;
import org.eventjuggler.services.idb.provider.IdentityProviderCallback;
import org.eventjuggler.services.idb.provider.IdentityProviderService;
import org.eventjuggler.services.idb.rest.LoginConfig.ProviderLoginConfig;
import org.eventjuggler.services.simpleauth.rest.AuthenticationResponse;
import org.eventjuggler.services.utils.UriBuilder;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
@Path("/login/{appKey}")
@Stateless
public class LoginResource {

    @EJB
    private IdentityProviderService providerService;

    @EJB
    private ApplicationService as;

    @EJB
    private IdmService idm;

    @Context
    private UriInfo uriInfo;

    @Context
    private HttpHeaders headers;

    private Application getApplication(String appKey) {
        Application application = as.getApplication(appKey);
        if (application == null) {
            throw new WebApplicationException(Status.BAD_REQUEST);
        }
        return application;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response getLogin(@PathParam("appKey") String appKey) {
        URI uri = new UriBuilder(headers, uriInfo, "login.html?app=" + appKey).build();
        return Response.seeOther(uri).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLoginConfig(@PathParam("appKey") String appKey) {
        Application application = getApplication(appKey);

        LoginConfig loginConfig = new LoginConfig();
        loginConfig.setName(application.getName());

        List<ProviderLoginConfig> providerLoginConfigs = new LinkedList<>();

        IdentityProviderCallback callback = new IdentityProviderCallback();
        callback.setApplication(application);
        callback.setHeaders(headers);
        callback.setUriInfo(uriInfo);

        for (IdentityProviderConfig c : application.getProviders()) {
            IdentityProvider provider = providerService.getProvider(c.getProviderId());
            callback.setProvider(provider);

            URI loginUri = provider.getLoginUrl(callback);
            URI iconUri = new UriBuilder(headers, uriInfo, "icons/" + provider.getIcon()).build();

            ProviderLoginConfig providerLoginConfig = new ProviderLoginConfig();
            providerLoginConfig.setName(provider.getName());
            providerLoginConfig.setLoginUri(loginUri);
            providerLoginConfig.setIcon(iconUri);

            providerLoginConfigs.add(providerLoginConfig);
        }

        loginConfig.setProviderConfigs(providerLoginConfigs);

        ResponseBuilder response = Response.ok(loginConfig);

        if (application.getJavaScriptOrigin() != null) {
            response.header("Access-Control-Allow-Origin", application.getJavaScriptOrigin());
        }

        return response.build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public Response login(@PathParam("appKey") String applicationKey, @FormParam("username") String username,
            @FormParam("password") String password) throws URISyntaxException {
        Application application = as.getApplication(applicationKey);
        AuthenticationResponse response = idm.login(application.getRealm(), username, password);

        if (response.isLoggedIn()) {
            URI uri = new UriBuilder(headers, uriInfo, application.getCallbackUrl() + "?token=" + response.getToken()).build();
            return Response.seeOther(uri).build();
        } else {
            URI uri = new UriBuilder(headers, uriInfo, "login.html?app=" + applicationKey + "&warning=invalid").build();
            return Response.seeOther(uri).build();
        }
    }

}
