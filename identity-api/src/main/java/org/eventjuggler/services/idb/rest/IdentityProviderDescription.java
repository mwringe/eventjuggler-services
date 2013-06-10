package org.eventjuggler.services.idb.rest;

import java.net.URI;

public class IdentityProviderDescription {

    private final String id;

    private final String name;

    private final URI icon;

    public IdentityProviderDescription(String id, String name, URI icon) {
        this.id = id;
        this.name = name;
        this.icon = icon;
    }

    public String getId() {
        return id;
    }

    public URI getIcon() {
        return icon;
    }

    public String getName() {
        return name;
    }

}
