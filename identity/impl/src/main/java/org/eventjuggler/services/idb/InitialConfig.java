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

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.eventjuggler.services.idb.model.Application;
import org.eventjuggler.services.idb.model.Realm;
import org.jboss.logging.Logger;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.credential.Password;
import org.picketlink.idm.model.SimpleUser;
import org.picketlink.idm.model.User;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
@Singleton
@Startup
public class InitialConfig {

    public static final String APPLICATIONS_REALM = "applications";

    public static final String ROOT_USERNAME = "root";

    public static final String SYSTEM_APPLICATION_KEY = "system";

    public static final String SYSTEM_APPLICATION_NAME = "system";

    public static final String SYSTEM_APPLICATION_SECRET = "system";

    public static final String SYSTEM_REALM = "system";

    @EJB
    private ApplicationBean applicationService;

    @PersistenceContext(unitName = "idb")
    private EntityManager em;

    @EJB
    private IdentityManagerRegistry identityManagerService;

    private final Logger log = Logger.getLogger(InitialConfig.class);

    @PostConstruct
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void init() {
        if (!identityManagerService.containsRealm(APPLICATIONS_REALM)) {
            Realm realm = new Realm();
            realm.setName(APPLICATIONS_REALM);

            identityManagerService.createRealm(realm);

            log.info("Created applications realm");
        }

        if (!identityManagerService.containsRealm(SYSTEM_REALM)) {
            Realm realm = new Realm();
            realm.setName(SYSTEM_REALM);

            identityManagerService.createRealm(realm);

            log.info("Created system realm");
        }

        Application application = em.find(Application.class, SYSTEM_APPLICATION_NAME);
        if (applicationService.getApplication(SYSTEM_APPLICATION_KEY) == null) {
            application = new Application();
            application.setName(SYSTEM_APPLICATION_NAME);
            application.setKey(SYSTEM_APPLICATION_KEY);
            application.setOwner(ROOT_USERNAME);
            application.setCallbackUrl("/ejs-admin/#");
            application.setRealm(SYSTEM_REALM);

            applicationService.create(application);

            log.info("Created system application");
        }

        IdentityManager im = identityManagerService.createIdentityManager(SYSTEM_REALM);

        User root = im.getUser(ROOT_USERNAME);
        if (root == null) {
            root = new SimpleUser(ROOT_USERNAME);
            im.add(root);
            im.updateCredential(root, new Password(ROOT_USERNAME));

            log.info("Created root user");
        }
    }

}
