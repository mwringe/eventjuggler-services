/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.eventjuggler.services.idb;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.eventjuggler.services.common.auth.SimpleAuthIdmUtil;
import org.eventjuggler.services.simpleauth.rest.AuthenticationResponse;
import org.eventjuggler.services.simpleauth.rest.UserInfo;
import org.eventjuggler.services.utils.UserFactory;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.IdentityManagerFactory;
import org.picketlink.idm.credential.Credentials;
import org.picketlink.idm.credential.Password;
import org.picketlink.idm.credential.UsernamePasswordCredentials;
import org.picketlink.idm.model.IdentityType;
import org.picketlink.idm.model.User;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
@Stateless
public class IdmService {

    @EJB
    private ApplicationService as;

    @Resource(name = "IdentityManagerFactory")
    private IdentityManagerFactory imf;

    public void createUser(String realm, User user, String password) {
        IdentityManager im = getIdentityManager(realm);

        im.add(user);
        im.updateCredential(user, new Password(password));
    }

    public void deleteUser(String realm, String username) {
        IdentityManager im = getIdentityManager(realm);

        User user = im.getUser(username);
        if (user == null) {
            throw new WebApplicationException(Status.NOT_FOUND);
        }

        im.remove(user);
    }

    private IdentityManager getIdentityManager(String realm) {
        return imf.createIdentityManager(imf.getRealm(realm));
    }

    public User getUser(String realm, String username) {
        IdentityManager im = getIdentityManager(realm);
        return im.getUser(username);
    }

    public User getUserByToken(String realm, String token) {
        IdentityManager im = getIdentityManager(realm);
        SimpleAuthIdmUtil idmUtil = new SimpleAuthIdmUtil(im);
        return idmUtil.getUser(token);
    }

    public UserInfo getUserInfo(String realm, String username) {
        User user = getUser(realm, username);
        if (user != null) {
            return UserFactory.createUserInfo(user);
        }

        return new UserInfo();
    }

    public UserInfo getUserInfoByToken(String realm, String token) {
        if (token != null) {
            User user = getUserByToken(realm, token);
            if (user != null) {
                return UserFactory.createUserInfo(user);
            }
        }

        return new UserInfo();
    }

    public List<UserInfo> getUserInfos(String realm) {
        List<UserInfo> userInfos = new LinkedList<>();
        for (org.picketlink.idm.model.User u : getUsers(realm)) {
            userInfos.add(UserFactory.createUserInfo(u));
        }
        return userInfos;
    }

    public User getUser(String realm, String provider, String username) {
        IdentityManager im = getIdentityManager(realm);

        List<User> l = im.createIdentityQuery(User.class)
                .setParameter(IdentityType.ATTRIBUTE.byName(provider + ".username"), username).getResultList();

        return l.isEmpty() ? null : l.get(0);
    }

    public String createToken(String realm, User user) {
        IdentityManager im = getIdentityManager(realm);
        return new SimpleAuthIdmUtil(im).setToken(user);
    }

    public List<User> getUsers(String realm) {
        IdentityManager im = getIdentityManager(realm);

        return im.createIdentityQuery(User.class).getResultList();
    }

    public AuthenticationResponse login(String realm, String username, String password) {
        IdentityManager im = getIdentityManager(realm);

        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, new Password(password));
        im.validateCredentials(credentials);

        AuthenticationResponse response = new AuthenticationResponse();

        if (Credentials.Status.VALID.equals(credentials.getStatus())) {
            User user = im.getUser(username);

            SimpleAuthIdmUtil idmUtil = new SimpleAuthIdmUtil(im);
            String token = idmUtil.setToken(user);

            response.setLoggedIn(true);
            response.setToken(token);
            response.setUserId(username);
        }

        return response;
    }

    public void logout(String realm, String token) {
        IdentityManager im = getIdentityManager(realm);

        SimpleAuthIdmUtil idmUtil = new SimpleAuthIdmUtil(im);
        idmUtil.removeToken(token);
    }

    public void updateUser(String realm, User user, String password) {
        IdentityManager im = getIdentityManager(realm);

        im.update(user);

        if (password != null) {
            im.updateCredential(user, new Password(password));
        }
    }

}
