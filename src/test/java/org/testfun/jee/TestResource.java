package org.testfun.jee;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Path("/rest/test")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TestResource {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement
    private static class ObjectWithCollectionAsMember {
        private List<JaxRsTestObject> collection = new ArrayList<>();
    }

    @GET
    @Path("/list")
    public ObjectWithCollectionAsMember list(@QueryParam("length") int length){
        ObjectWithCollectionAsMember objectWithCollectionAsMember = new ObjectWithCollectionAsMember();

        for (int i = 0; i < length; ++i){
            objectWithCollectionAsMember.getCollection().add(new JaxRsTestObject(String.valueOf(i), i));
        }

        return objectWithCollectionAsMember;
    }

    @POST
    @Path("/list")
    public void list(ObjectWithCollectionAsMember collectionAsMember) {
        System.out.println(collectionAsMember);
    }

    @GET
    @Path("/unknown")
    public Response notFound() {
        return Response.status(Response.Status.NOT_FOUND).entity(new JaxRsTestObject("I can't find it", 654)).type(MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/known")
    public JaxRsTestObject found(@QueryParam("num") Integer num) {
        return new JaxRsTestObject("Here it is", num == null ? 3456 : num);
    }

    @PUT
    @Path("/put")
    public JaxRsTestObject putWizardState(@HeaderParam("str") String str, @HeaderParam("num") int num){
        return new JaxRsTestObject(str, num);
    }

    @POST
    @Path("/post")
    public JaxRsTestObject postWizardState(JaxRsTestObject jaxRsTestObject){
        return jaxRsTestObject;
    }

    @POST
    @Path("/create")
    public Response postCreated(){
        return Response.status(Response.Status.CREATED).entity(new JaxRsTestObject("diet", 4)).location(URI.create("http://localhost/location")).type(MediaType.APPLICATION_JSON).build();
    }
}
