package de.hsos.swa.jonas.theater.security.boundary.mobile;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.security.identity.SecurityIdentity;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.Set;

/**
 * ServersideRendering of the login page
 */
@Path("/mobile")
public class LoginResourceMobile {
    @Inject
    Template login;

    /**
     * @return the login page and sets the active tab to 0
     */
    @Path("/login")
    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response getLoginPage(){
        int active = 0;
        String html = login.data("active", active).render();
        return Response.ok().entity(html).build();
    }


}
