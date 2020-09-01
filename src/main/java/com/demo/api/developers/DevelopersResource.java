package com.demo.api.developers;

import com.demo.api.developers.model.Developer;
import com.demo.api.developers.service.DeveloperService;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

@Path("/developers")
public class DevelopersResource {

    private static Logger logger = LoggerFactory.getLogger(DevelopersResource.class);

    @Inject
    DeveloperService service;

    @ConfigProperty(name="developer.greeting.message")
    String message;

    @GET
    @Path("/hello")
    @Produces(MediaType.TEXT_PLAIN)
    public String hello(){
        logger.debug("In hello() method");
        return message;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDevelopers() {
        return Response.ok().entity(service.getDevelopers()).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addDeveloper(Developer developer) {
        service.addDeveloper(developer);

        return Response.created(UriBuilder.fromResource(DevelopersResource.class).
                path(developer.getId()).build()).entity(developer).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDeveloper(@PathParam("id") String id) {
        return Response.ok().entity(service.getDeveloper(id)).build();
    }
}