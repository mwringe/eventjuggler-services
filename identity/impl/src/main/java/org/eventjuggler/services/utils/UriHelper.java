package org.eventjuggler.services.utils;

import java.net.URI;

import javax.ws.rs.core.UriInfo;

public class UriHelper {

    private final URI contextRoot;

    public UriHelper(UriInfo uriInfo) {
        contextRoot = uriInfo.getBaseUri().resolve("../");
    }

    public URI getContextRoot() {
        return contextRoot;
    }

    public URI getIcon(String icon) {
        return contextRoot.resolve("icons/" + icon);
    }

}
