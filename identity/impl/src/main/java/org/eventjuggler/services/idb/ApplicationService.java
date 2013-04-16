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

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.PathParam;

import org.eventjuggler.services.idb.model.Application;
import org.eventjuggler.services.utils.KeyGenerator;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
@Stateless
public class ApplicationService {

    @PersistenceContext(unitName = "idb")
    private EntityManager em;

    @Inject
    private KeyGenerator keyGenerator;

    public void create(Application application) {
        application.setKey(keyGenerator.createApplicationKey());
        application.setSecret(keyGenerator.createApplicationSecret());

        em.persist(application);
    }

    public boolean remove(String applicationKey) {
        Application application = em.find(Application.class, applicationKey);
        if (application == null) {
            return false;
        }

        em.remove(application);
        return true;
    }

    public Application update(Application application) {
        return em.merge(application);
    }

    public Application getApplication(@PathParam("applicationKey") String applicationKey) {
        Application application = em.find(Application.class, applicationKey);
        return application;
    }

    public List<Application> getApplications() {
        return em.createQuery("from Application", Application.class).getResultList();
    }

}