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
package org.eventjuggler.analytics.rest;

import java.util.List;

import javax.naming.InitialContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.eventjuggler.analytics.Analytics;
import org.eventjuggler.analytics.AnalyticsQuery;
import org.eventjuggler.analytics.Event;
import org.eventjuggler.analytics.EventImpl;
import org.eventjuggler.analytics.Statistics;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
@Path("/")
public class AnalyticsResource {

    private final Analytics analytics;

    public AnalyticsResource() throws Exception {
        analytics = (Analytics) new InitialContext().lookup("java:jboss/AnalyticsService");
    }

    @PUT
    @Path("/event")
    @Consumes(MediaType.APPLICATION_JSON)
    public void addEvent(EventImpl event) {
        analytics.addEvent(event);
    }

    private AnalyticsQuery createQuery(Integer firstResult, Integer maxResult, String context, String pagePattern) {
        AnalyticsQuery query = analytics.createQuery();

        if (firstResult != null) {
            query.firstResult(firstResult);
        }

        if (maxResult != null) {
            query.maxResult(maxResult);
        }

        if (context != null) {
            query.contextPath(context);
        }

        if (pagePattern != null) {
            query.page(pagePattern);
        }

        return query;
    }

    @GET
    @Path("/events")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Event> getEvents(@QueryParam("first") Integer firstResult, @QueryParam("max") Integer maxResult,
            @QueryParam("context") String context, @QueryParam("page") String pagePattern) {
        return createQuery(firstResult, maxResult, context, pagePattern).getResults();
    }

    @GET
    @Path("/popular")
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> getPopular(@QueryParam("first") Integer firstResult, @QueryParam("max") Integer maxResult,
            @QueryParam("context") String context, @QueryParam("page") String pagePattern) {
        return createQuery(firstResult, maxResult, context, pagePattern).maxResult(10).getPopularPages();
    }

    @GET
    @Path("/related/{page}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> getRelated(@PathParam("page") String page, @QueryParam("first") Integer firstResult,
            @QueryParam("max") Integer maxResult, @QueryParam("context") String context, @QueryParam("page") String pagePattern) {
        return createQuery(firstResult, maxResult, context, pagePattern).getRelatedPages(page);
    }

    @GET
    @Path("/statistics")
    @Produces(MediaType.APPLICATION_JSON)
    public Statistics getStatistics(@QueryParam("first") Integer firstResult, @QueryParam("max") Integer maxResult,
            @QueryParam("context") String context, @QueryParam("page") String pagePattern) {
        return createQuery(firstResult, maxResult, context, pagePattern).getStatistics();
    }

}
