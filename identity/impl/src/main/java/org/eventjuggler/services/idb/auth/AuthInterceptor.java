package org.eventjuggler.services.idb.auth;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.Provider;

import org.eventjuggler.services.utils.TokenService;
import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.core.ResourceMethod;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.interception.PostProcessInterceptor;
import org.jboss.resteasy.spi.interception.PreProcessInterceptor;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.model.User;

@Provider
@ServerInterceptor
public class AuthInterceptor implements PreProcessInterceptor, PostProcessInterceptor {

    @Inject
    private IdentityManager identityManager;

    @EJB
    private TokenService tokenService;

    @Override
    public ServerResponse preProcess(HttpRequest request, ResourceMethod method) throws Failure, WebApplicationException {
        String token = request.getHttpHeaders().getRequestHeaders().getFirst("token");
        User user = token != null ? tokenService.get(token) : null;

        if (user != null) {
            Auth.set(user);
        }

        return null;
    }

    @Override
    public void postProcess(ServerResponse response) {
        Auth.remove();
    }

}
