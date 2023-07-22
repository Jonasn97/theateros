package de.hsos.swa.jonas.theater.userdata.boundary.resource.mobile;

import de.hsos.swa.jonas.theater.shared.EventState;
import de.hsos.swa.jonas.theater.userdata.control.UserDataOperations;
import io.quarkus.logging.Log;
import io.quarkus.qute.Template;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.net.URI;

@Path("mobile/user/userevents/")
@Produces(MediaType.TEXT_HTML)
@Transactional(Transactional.TxType.REQUIRES_NEW)
public class UserEventResourceMobile {

    @Inject
    Template eventstates;

    @Inject
    UserDataOperations userDataOperations;

    @GET
    @Retry
    @Timeout(5000)
    @Fallback(fallbackMethod = "getEventStatesOptionsFallback")
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.75, delay = 10000)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Operation(summary = "Get eventstates", description = "Get eventstates via PathParam")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Eventstates successfully returned"),
            @APIResponse(responseCode = "400", description = "Eventstates could not be returned")
    })
    @Path("{eventId}")
    public Response getEventStatesOptions(@PathParam("eventId") long eventId, @Context SecurityContext securityContext, @HeaderParam("Referer") String referrer){
        //Return a set of possible Eventstates in response
        int active=3;
        return Response.ok(eventstates.data("eventstates", EventState.values(),"eventId", eventId, "active", active)).build();
    }
    @Path("{eventId}/fallback")
    public Response getEventStatesOptionsFallback(@PathParam("eventId") long eventId, @Context SecurityContext securityContext, @HeaderParam("Referer") String referrer){
        return Response.seeOther(URI.create(referrer)).build();
    }

    @POST
    @Retry
    @Timeout(5000)
    @Fallback(fallbackMethod = "updateEventStatebyEventIdOfUserFallback")
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.75, delay = 10000)
    @Operation(summary = "Update eventstate", description = "Update eventstate via PathParam")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Eventstate successfully updated"),
            @APIResponse(responseCode = "400", description = "Eventstate could not be updated"),
            @APIResponse(responseCode = "303", description = "SecurityContext is missing. Try to login again")
    })
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("{eventId}")
    public Response updateEventStatebyEventIdOfUser(@PathParam("eventId") long eventId, @FormParam("isFavorite") boolean isFavorite, @FormParam("eventState") EventState eventState, @Context SecurityContext securityContext){
        Log.info(eventState);
        Log.info(eventId);
        String username;
        int active=3;
        if(securityContext== null || securityContext.getUserPrincipal() == null){
            return Response.seeOther(URI.create("/mobile/login")).build();
        }
        username = securityContext.getUserPrincipal().getName();
        if(eventState!= null)
            userDataOperations.updateEventStatebyEventIdOfUser(eventId, eventState, username);
        else
            userDataOperations.updateIsFavoritebyEventIdOfUser(eventId, isFavorite, username);
        return Response.seeOther(URI.create("/mobile/events/"+eventId)).build();

    }
    @Path("{eventId}/fallback")
    public Response updateEventStatebyEventIdOfUserFallback(@PathParam("eventId") long eventId, @FormParam("isFavorite") boolean isFavorite, @FormParam("eventState") EventState eventState, @Context SecurityContext securityContext) {
        return Response.seeOther(URI.create("/mobile/login")).build();
    }
}
