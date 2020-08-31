package com.demo.api.developers;

import com.demo.api.developers.model.Developer;
import com.demo.api.developers.model.Skill;
import com.demo.api.developers.service.DeveloperService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

@Path("/developers")
public class DevelopersResource {

    @Inject
    DeveloperService service;

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