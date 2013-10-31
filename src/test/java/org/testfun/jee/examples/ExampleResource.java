package org.testfun.jee.examples;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.LinkedList;
import java.util.List;

@Path("/example")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ExampleResource {

    @GET
    @Path("/data/{id}")
    public Response get(@PathParam("id") int id) {
        if (id <= 0) return Response.status(Response.Status.NOT_FOUND).build();
        else return Response.ok(new RestData(id, "Got " + id)).build();
    }

    @GET
    @Path("/data")
    public List<RestData> getAll(@QueryParam("min") Integer minParam, @QueryParam("max") Integer maxParam) {
        int min = minParam == null ? 0 : minParam;
        int max = maxParam == null ? 10 : maxParam;

        List<RestData> data = new LinkedList<>();
        for (int i = min; i < max; ++i) {
            data.add(new RestData(i, "Got " + i));
        }

        return data;
    }

}