package com.demo.api.developers;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/developers")
public class DevelopersResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getDevelopers() {
        return "developers";
    }

    @GET
    @Path("/{developerId}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getDeveloper(@PathParam("developerId") String developerId) {
        return "developer: "+developerId;
    }
}