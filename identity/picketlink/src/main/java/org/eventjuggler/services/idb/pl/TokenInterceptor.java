package org.eventjuggler.services.idb.pl;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.core.ResourceMethod;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.interception.PreProcessInterceptor;
import org.picketlink.Identity;

@Provider
@ServerInterceptor
public class TokenInterceptor implements PreProcessInterceptor {

    @Inject
    private Token token;

    @Inject
    private Identity identity;

    @Override
    public ServerResponse preProcess(HttpRequest request, ResourceMethod method) throws Failure, WebApplicationException {
        List<String> tokenParam = request.getHttpHeaders().getRequestHeader("token");
        synchronized (identity) {
            if (!identity.isLoggedIn() && tokenParam != null && !tokenParam.isEmpty()) {
                String t = tokenParam.get(0);
                token.setToken(t);
                identity.login();
            }
        }
        return null;
    }

}
