package de.hsos.swa.jonas.theater.security.boundary.mobile;

import de.hsos.swa.jonas.theater.security.entity.User;
import io.quarkus.logging.Log;
import io.quarkus.qute.Template;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Path("/mobile")
public class RegisterResourceMobile {
    @Inject
    javax.enterprise.event.Event<String> registerEvent;
    @Inject
    Template register;

    @GET
    @Path("/register")
    @Produces(MediaType.TEXT_HTML)
    public Response getRegisterPage() {
        int active = 0;
        String html = register.data("active", active).render();
        return Response.ok().entity(html).build();
    }


    @Path("/register")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public Response registerUser(@FormParam("username") String username, @FormParam("password") String password) {
        User.add(username, password, "user");
       registerEvent.fire(username);
        return Response.seeOther(URI.create("/mobile/login")).build();
        }
}
