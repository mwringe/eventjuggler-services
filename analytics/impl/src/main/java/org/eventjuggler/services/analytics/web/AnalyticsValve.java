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
package org.eventjuggler.services.analytics.web;

import java.io.IOException;

import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;
import org.eventjuggler.services.analytics.Analytics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class AnalyticsValve extends ValveBase {

    private static final Logger log = LoggerFactory.getLogger("org.eventjuggler.services.analytics");

    private Analytics analytics;

    public AnalyticsValve() {
        try {
            analytics = (Analytics) new InitialContext().lookup("java:jboss/AnalyticsService");
        } catch (Throwable t) {
            log.warn("Failed to lookup analytics service", t);
        }
    }

    private void addEvent(ServletRequest request, ServletResponse response) {
        try {
            if (analytics != null) {
                analytics.addEvent(request, response);
            }
        } catch (Throwable t) {
            log.warn("Unexpected error while processing request for analytics", t);
        }
    }

    @Override
    public void invoke(Request request, Response response) throws IOException, ServletException {
        getNext().invoke(request, response);

        addEvent(request, response);
    }

}
