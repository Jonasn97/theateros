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
@Path("")
public class LoginResourceMobile {
    @Inject
    Template login;
    @Inject
    Template loggedIn;
    @Inject
    SecurityIdentity identity;

    @Path("/login")
    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response getLoginPage(){
        String html = login.render();
        return Response.ok().entity(html).build();
    }

    @Path("/deins")
    @RolesAllowed("user")
    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response getLoggedInPage(@Context SecurityContext securityContext){
        TemplateInstance instance = loggedIn.data("user", identity.getPrincipal().getName());
        String html = instance.render();
        return Response.ok().entity(html).build();
    }
}
