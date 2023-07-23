package de.hsos.swa.jonas.theater.userdata.boundary.resource.mobile;

import io.quarkus.logging.Log;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

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
import java.net.URI;

@Path("/mobile")
public class UserResourceMobile {
    @Inject
    Template yours;

    @Path("/deins")
    @RolesAllowed("user")
    @GET
    @Retry
    @Timeout(5000)
    @Fallback(fallbackMethod = "getLoggedInPageFallback")
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.75, delay = 10000)
    @Operation(summary = "Get logged in page", description = "Get logged in page")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Logged in page successfully returned"),
            @APIResponse(responseCode = "400", description = "Logged in page could not be returned")
    })
    @Produces(MediaType.TEXT_HTML)
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public Response getLoggedInPage(@Context SecurityContext securityContext){
        String username = securityContext.getUserPrincipal().getName();
        int active = 0;
        TemplateInstance instance = yours.data("user", username, "active",active);
        String html = instance.render();
        return Response.ok().entity(html).build();
    }
    @Path("/deins/fallback")
    public Response getLoggedInPageFallback(@Context SecurityContext securityContext){
        return Response.seeOther(URI.create("errors.html")).build();
    }
}
