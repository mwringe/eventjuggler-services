package org.eventjuggler.services.simpleauth;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.ejb.Singleton;

import org.jboss.logging.Logger;
import org.picketlink.idm.model.User;

@Singleton
public class TokenManager {

    private static final Logger log = Logger.getLogger(TokenManager.class);

    public Map<String, User> users = Collections.synchronizedMap(new HashMap<String, User>());

    public String put(User user) {
        String token = UUID.randomUUID().toString();
        users.put(token, user);
        log.info("Added token " + token);
        return token;
    }

    public boolean valid(String token) {
        return users.containsKey(token);
    }

    public User get(String token) {
        log.info("Get token " + token);
        return users.get(token);
    }

    public void remove(String token) {
        log.info("Remove token " + token);
        users.remove(token);
    }

}
