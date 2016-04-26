package org.testfun.jee.examples;

import org.testfun.jee.real.SomeDao;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.ws.rs.core.Response.Status;
import java.util.LinkedList;
import java.util.List;

@Path("/example")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ExampleResource {

    @EJB
    private SomeDao someDao;

    @GET
    @Path("/data/{id}")
    public Response get(@PathParam("id") int id) {
        if (id <= 0) return Response.status(Response.Status.NOT_FOUND).entity("Data with ID " + id + " wasn't found").build();
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

    @POST
    @Path("/data")
    public Response create(RestData data) {
        return Response.created(UriBuilder.fromPath("/example/data/").path(String.valueOf(data.getKey())).build()).build();
    }

    @GET
    @Path("/use_ejb")
    public String getEntityName(@HeaderParam("index") int index) {
        return someDao.getAll().get(index).getName();
    }

    @POST
    @Path("/form")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response postParam(@FormParam("p1") String p1, @FormParam("p2") String p2)
    {
        return Response.status(Status.OK).entity(p1 + "-" + p2).build();
    }

    @GET
    @Path("/user_from_security_context")
    public String getUserFromSecurityContext(@Context SecurityContext sc) {
        return sc.getUserPrincipal().getName();
    }
}