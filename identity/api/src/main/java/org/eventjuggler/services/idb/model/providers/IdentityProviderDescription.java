package org.eventjuggler.services.idb.model.providers;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public abstract class IdentityProviderDescription {

    private static IdentityProviderDescription[] providers = new IdentityProviderDescription[] { new FacebookDescription(),
            new GoogleDescription() };

    public static IdentityProviderDescription[] getProviders() {
        return providers;
    }

    public abstract String getName();

}
