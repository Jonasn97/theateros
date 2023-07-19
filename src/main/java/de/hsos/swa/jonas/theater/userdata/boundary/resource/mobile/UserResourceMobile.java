package de.hsos.swa.jonas.theater.userdata.boundary.resource.mobile;

import io.quarkus.logging.Log;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@Path("/mobile")
public class UserResourceMobile {
    @Inject
    Template yours;

    @Path("/deins")
    @RolesAllowed("user")
    @GET
    @Produces(MediaType.TEXT_HTML)
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public Response getLoggedInPage(@Context SecurityContext securityContext){
        String username = securityContext.getUserPrincipal().getName();
        int active = 0;
        TemplateInstance instance = yours.data("user", username, "active",active);
        String html = instance.render();
        return Response.ok().entity(html).build();
    }
}
