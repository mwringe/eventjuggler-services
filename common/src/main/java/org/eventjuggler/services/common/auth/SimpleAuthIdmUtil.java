package org.eventjuggler.services.common.auth;

import org.eventjuggler.services.common.KeyGenerator;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.model.Agent;
import org.picketlink.idm.model.Attribute;
import org.picketlink.idm.model.SimpleAgent;
import org.picketlink.idm.model.User;

public class SimpleAuthIdmUtil {

    private final IdentityManager im;

    public SimpleAuthIdmUtil(IdentityManager im) {
        this.im = im;
    }

    public String setToken(User user) {
        String token = KeyGenerator.createToken();

        Agent tokenAgent = new SimpleAgent(token);
        tokenAgent.setAttribute(new Attribute<String>("loginName", user.getLoginName()));
        im.add(tokenAgent);

        return token;
    }

    public User getUser(String token) {
        Agent tokenAgent = im.getAgent(token);
        if (tokenAgent == null) {
            return null;
        }

        String loginName = (String) tokenAgent.getAttribute("loginName").getValue();
        if (loginName == null) {
            return null;
        }

        return im.getUser(loginName);
    }

    public void removeToken(String token) {
        Agent tokenAgent = im.getAgent(token);
        if (tokenAgent != null) {
            im.remove(tokenAgent);
        }
    }

}
