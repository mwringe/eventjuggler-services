package org.eventjuggler.services.idb.rest;

import java.net.URISyntaxException;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.credential.Password;
import org.picketlink.idm.model.SimpleUser;
import org.picketlink.idm.model.User;

@Path("/register")
public class RegistrationFormResource {

    @Inject
    private IdentityManager identityManager;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response getForm() {
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append("<body>");
        sb.append("<form action='#' method='post'>");
        sb.append("<input type='text' name='email' placeholder='Email' />");
        sb.append("<input type='text' name='password' placeholder='Password' />");
        sb.append("<button type='submit'>Register</button>");
        sb.append("</form>");
        sb.append("</body>");
        sb.append("</html>");

        return Response.ok(sb.toString()).build();
    }

    @POST
    @Path("/{appKey}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public void register(@PathParam("appKey") String appKey, @FormParam("email") String email,
            @FormParam("password") String password) throws URISyntaxException {
        if (identityManager.getUser(email) == null) {
            User user = new SimpleUser(email);
            user.setEmail(email);

            identityManager.add(user);
            identityManager.updateCredential(user, new Password(password));
        } else {
            throw new WebApplicationException(Status.BAD_REQUEST);
        }
    }

}
