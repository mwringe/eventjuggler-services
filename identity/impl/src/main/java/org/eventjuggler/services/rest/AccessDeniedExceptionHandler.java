package org.eventjuggler.services.rest;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.picketlink.deltaspike.security.api.authorization.AccessDeniedException;

@Provider
public class AccessDeniedExceptionHandler implements ExceptionMapper<AccessDeniedException> {

    @Override
    public Response toResponse(AccessDeniedException arg0) {
        return Response.status(Status.FORBIDDEN).build();
    }

}
