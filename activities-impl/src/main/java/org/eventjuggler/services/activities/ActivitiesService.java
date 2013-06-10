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
package org.eventjuggler.services.activities;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
@Stateless
@EJB(name = "java:global/ejs/Activities", beanInterface = Activities.class)
public class ActivitiesService implements Activities {

    @PersistenceContext(unitName = "activities")
    private EntityManager em;

    @Override
    public void addEvent(ServletRequest request, ServletResponse response) {
        if (!(request instanceof HttpServletRequest)) {
            return;
        }

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        String contextPath = httpRequest.getContextPath();
        long time = System.currentTimeMillis();
        String page = httpRequest.getRequestURI();
        String country = request.getLocale().getISO3Country();
        String language = request.getLocale().getISO3Language();

        String userAgent = httpRequest.getHeader("user-agent");

        String remoteAddr = request.getRemoteAddr();
        if (httpRequest.getHeader("x-forwarded-for") != null) {
            remoteAddr = httpRequest.getHeader("x-forwarded-for");
        }

        final Event event = new EventImpl(time, contextPath, page, remoteAddr, country, language, userAgent);
        em.persist(event);
    }

    @Override
    public void addEvent(final Event event) {
        em.persist(event);
    }

    @Override
    public ActivitiesQuery createQuery() {
        return new ActivitiesQueryImpl(em);
    }

    public EntityManager getEm() {
        return em;
    }

}
