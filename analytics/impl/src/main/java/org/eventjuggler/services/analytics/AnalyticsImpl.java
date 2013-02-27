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
package org.eventjuggler.services.analytics;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.UserTransaction;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class AnalyticsImpl implements Analytics {

    private final List<String> acceptedContentTypes = Arrays.asList("text/html", "application/json", "application/xml");

    private final EntityManagerFactory emf;

    private final EntityManager em;

    private final UserTransaction userTransaction;

    private final ExecutorService executor;

    public AnalyticsImpl(EntityManagerFactory emf, UserTransaction userTransaction) {
        this.emf = emf;
        this.userTransaction = userTransaction;

        // TODO This may be a problem under high load, but then again logging with JPA isn't brilliant either
        executor = Executors.newSingleThreadExecutor();
        em = emf.createEntityManager();
    }

    @Override
    public synchronized void addEvent(ServletRequest request, ServletResponse response) {
        if (!(request instanceof HttpServletRequest)) {
            return;
        }

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        String contentType = response.getContentType();
        if (contentType == null || !acceptedContentTypes.contains(contentType.split(";")[0])) {
            return;
        }

        String contextPath = httpRequest.getContextPath();
        if (contextPath.startsWith("/analytics-web")) {
            return;
        }

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
        addEvent(event);
    }

    @Override
    public void addEvent(final Event event) {
        executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    userTransaction.begin();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                try {
                    em.joinTransaction();
                    em.persist(event);
                } finally {
                    try {
                        userTransaction.commit();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }

    @Override
    public AnalyticsQuery createQuery() {
        return new AnalyticsQueryImpl(emf.createEntityManager());
    }

    public EntityManager getEm() {
        return em;
    }

}
