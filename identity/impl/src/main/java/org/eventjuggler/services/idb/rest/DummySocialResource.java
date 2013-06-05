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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.eventjuggler.services.idb.ApplicationBean;
import org.eventjuggler.services.idb.IdentityManagerRegistry;
import org.eventjuggler.services.idb.model.Application;
import org.eventjuggler.services.idb.model.Realm;
import org.eventjuggler.services.idb.utils.KeyGenerator;
import org.eventjuggler.services.utils.UriBuilder;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.credential.Credentials;
import org.picketlink.idm.credential.Password;
import org.picketlink.idm.credential.UsernamePasswordCredentials;
import org.picketlink.idm.model.SimpleUser;
import org.picketlink.idm.model.User;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
@Path("/dummysocial/{appKey}")
@Singleton
@EJB(name = "java:global/ejs/DummySocialResource", beanInterface = DummySocialResource.class)
public class DummySocialResource {

    @EJB
    private ApplicationBean applicationService;

    @EJB
    private IdentityManagerRegistry identityManagerService;

    private static final String REALM = "dummy-social";

    @Context
    private UriInfo uriInfo;

    @Context
    private HttpHeaders headers;

    private final Map<String, User> loggedInUsers = Collections.synchronizedMap(new HashMap<String, User>());

    @PostConstruct
    public void init() {
        if (!identityManagerService.containsRealm(REALM)) {
            Realm realm = new Realm();
            realm.setName(REALM);

            identityManagerService.createRealm(realm);
        }
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response getLoginForm(@PathParam("appKey") String appKey, @Context UriInfo uri) {
        Application application = applicationService.getApplication(appKey);
        if (application == null) {
            return Response.status(Status.BAD_REQUEST).build();
        }

        StringBuilder sb = createLoginForm(appKey, application, null);
        return Response.ok(sb.toString()).build();
    }

    private StringBuilder createLoginForm(String appKey, Application application, String errorMessage) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append("<body>");
        sb.append("<h1>Welcome to Dummy Social</h1>");
        sb.append("<p>" + application.getName() + " is requesting to authenticate using your account </p>");
        if (errorMessage != null) {
            sb.append("<p>" + errorMessage + "</p>");
        }
        sb.append("<form action='#' method='post'>");

        boolean loggedIn = false;
        if (headers.getCookies().containsKey("dummy.cookie")) {
            String username = headers.getCookies().get("dummy.cookie").getValue();
            IdentityManager im = identityManagerService.createIdentityManager(REALM);
            loggedIn = im.getUser(username) != null;
            if (loggedIn) {
                sb.append("<p>Logged in as: " + username + "</p>");
            }
        }

        if (!loggedIn) {
            sb.append("<input type='text' name='username' placeholder='Username' />");
            sb.append("<input type='password' name='password' placeholder='Password' />");
        }

        sb.append("<input type='hidden' name='appkey' value='" + appKey + "' />");
        sb.append("<button name='submit' type='submit' value='login'>Accept</button>");
        if (!loggedIn) {
            sb.append("<button name='submit' type='submit' value='register'>Register</button>");
        }
        sb.append("</form>");
        sb.append("</body>");
        sb.append("</html>");
        return sb;
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public Response login(@PathParam("appKey") String appKey, @FormParam("username") String username,
            @FormParam("password") String password, @FormParam("submit") String submit) throws URISyntaxException {
        Application application = applicationService.getApplication(appKey);
        if (application == null) {
            return Response.status(Status.BAD_REQUEST).build();
        }

        boolean valid = false;
        String error = null;

        IdentityManager im = identityManagerService.createIdentityManager(REALM);

        if (submit.equals("login")) {
            if (headers.getCookies().containsKey("dummy.cookie")) {
                username = headers.getCookies().get("dummy.cookie").getValue();
                valid = im.getUser(username) != null;
            } else {
                UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, new Password(password));

                im.validateCredentials(credentials);
                if (Credentials.Status.VALID.equals(credentials.getStatus())) {
                    valid = true;
                } else {
                    error = "Invalid username or password";
                }
            }
        } else if (submit.equals("register")) {
            if (im.getUser(username) != null) {
                valid = false;
                error = "User already exists";
            } else {
                User user = new SimpleUser(username);
                im.add(user);
                im.updateCredential(user, new Password(password));

                valid = true;
            }
        } else if (submit.equals("logout")) {
            valid = false;
        } else {
            valid = false;
        }

        if (valid) {
            User user = im.getUser(username);
            String token = KeyGenerator.createToken();
            loggedInUsers.put(token, user);

            URI uri = new UriBuilder(headers, uriInfo, "api/callback/" + appKey + "?dummytoken=" + token).build();
            return Response.seeOther(uri).cookie(new NewCookie("dummy.cookie", user.getLoginName())).build();
        } else {
            StringBuilder sb = createLoginForm(appKey, application, error);
            return Response.ok(sb.toString()).build();
        }
    }

    public User getUser(String dummytoken) {
        return loggedInUsers.get(dummytoken);
    }

}
