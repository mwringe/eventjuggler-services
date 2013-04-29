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
package org.eventjuggler.services.activities.rest;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.eventjuggler.services.activities.ActivitiesQuery;
import org.eventjuggler.services.activities.Event;
import org.eventjuggler.services.activities.Statistics;
import org.eventjuggler.services.common.auth.Auth;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
@Stateless
public class ActivitiesResource implements Activities {

    @EJB
    private org.eventjuggler.services.activities.Activities activities;

    @Context
    private UriInfo uriInfo;

    @Override
    public Response addEvent(Event event) {
        Auth.requireUser();

        activities.addEvent(event);
        return Response.created(uriInfo.getAbsolutePathBuilder().build(event.getId())).build();
    }

    private ActivitiesQuery createQuery(Integer firstResult, Integer maxResult, String context, String pagePattern) {
        ActivitiesQuery query = activities.createQuery();

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

    @Override
    public List<Event> getEvents(Integer firstResult, Integer maxResult, String context, String pagePattern) {
        Auth.requireSuper();

        return createQuery(firstResult, maxResult, context, pagePattern).getResults();
    }

    @Override
    public List<String> getPopular(Integer firstResult, Integer maxResult, String context, String pagePattern) {
        Auth.requireSuper();

        return createQuery(firstResult, maxResult, context, pagePattern).maxResult(10).getPopularPages();
    }

    @Override
    public List<String> getRelated(String page, Integer firstResult, Integer maxResult, String context,
            String pagePattern) {
        Auth.requireSuper();

        return createQuery(firstResult, maxResult, context, pagePattern).getRelatedPages(page);
    }

    @Override
    public Statistics getStatistics(Integer firstResult, Integer maxResult, String context, String pagePattern) {
        Auth.requireSuper();

        return createQuery(firstResult, maxResult, context, pagePattern).getStatistics();
    }

}
