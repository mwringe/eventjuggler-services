package org.eventjuggler.services.activities.rest;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eventjuggler.services.activities.Event;
import org.eventjuggler.services.activities.Statistics;

@Path("/")
public interface Activities {

    @POST
    @Path("/events")
    @Consumes(MediaType.APPLICATION_JSON)
    Response addEvent(Event event);

    @GET
    @Path("/events")
    @Produces(MediaType.APPLICATION_JSON)
    List<Event> getEvents(@QueryParam("first") Integer firstResult, @QueryParam("max") Integer maxResult,
            @QueryParam("context") String context, @QueryParam("page") String pagePattern);

    @GET
    @Path("/popular")
    @Produces(MediaType.APPLICATION_JSON)
    List<String> getPopular(@QueryParam("first") Integer firstResult, @QueryParam("max") Integer maxResult,
            @QueryParam("context") String context, @QueryParam("page") String pagePattern);

    @GET
    @Path("/related/{page}")
    @Produces(MediaType.APPLICATION_JSON)
    List<String> getRelated(@PathParam("page") String page, @QueryParam("first") Integer firstResult,
            @QueryParam("max") Integer maxResult, @QueryParam("context") String context, @QueryParam("page") String pagePattern);

    @GET
    @Path("/statistics")
    @Produces(MediaType.APPLICATION_JSON)
    Statistics getStatistics(@QueryParam("first") Integer firstResult, @QueryParam("max") Integer maxResult,
            @QueryParam("context") String context, @QueryParam("page") String pagePattern);

}