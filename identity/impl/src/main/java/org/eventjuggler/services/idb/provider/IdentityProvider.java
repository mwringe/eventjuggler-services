package org.eventjuggler.services.idb.provider;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.eventjuggler.services.idb.model.Application;
import org.eventjuggler.services.idb.model.IdentityProviderConfig;
import org.picketlink.idm.model.User;

public interface IdentityProvider {

    String getId();

    URI getLoginUrl(Application application, IdentityProviderConfig provider);

    String getIcon();

    String getName();

    User getUser(Map<String, List<String>> headers, Map<String, List<String>> queryParameters);

    boolean isCallbackHandler(Map<String, List<String>> headers, Map<String, List<String>> queryParameters);

}
