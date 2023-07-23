package de.hsos.swa.jonas.theater.security.boundary.mobile;

import de.hsos.swa.jonas.theater.security.entity.User;
import io.quarkus.logging.Log;
import io.quarkus.qute.Template;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * ServersideRendering of the register page
 * and adding users to the database with PostRequest
 */
@Path("/mobile")
public class RegisterResourceMobile {
    @Inject
    javax.enterprise.event.Event<String> registerEvent;
    @Inject
    Template register;

    /**
     * @return the register page and sets the active tab to 0
     */
    @GET
    @Path("/register")
    @Produces(MediaType.TEXT_HTML)
    public Response getRegisterPage() {
        int active = 0;
        String html = register.data("active", active).render();
        return Response.ok().entity(html).build();
    }


    /**
     * @param username the username of the user to add has a min length of 3 and a max length of 25
     * @param password the password of the user to add has a min length of 8 and a max length of 20 and has to contain at least one uppercase letter, one lowercase letter, one number and one special character
     * @return a redirect to the login page
     * Sends an event which is used for the userdata package @see CreateUserEvent observes the event and adds a user to its database
     */
    @Path("/register")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public Response registerUser(@Valid @Size(min=3, max=25) @Pattern(regexp = "^[a-zA-z ]*$") @FormParam("username") String username, @Size(min=8, max=20) @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$", message = "the password has to contain at least one uppercase letter, one lowercase letter, one number and one special character") @FormParam("password") String password) {
        User.add(username, password, "user");
       registerEvent.fire(username);
        return Response.seeOther(URI.create("/mobile/login")).build();
        }
}
