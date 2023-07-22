package de.hsos.swa.jonas.theater.userdata.boundary.resource.api;

import de.hsos.swa.jonas.theater.eventmanagement.boundary.resources.api.EventResourceApi;
import de.hsos.swa.jonas.theater.shared.LinkBuilder;
import de.hsos.swa.jonas.theater.shared.dto.jsonapi.*;
import de.hsos.swa.jonas.theater.userdata.boundary.dto.UserParametersDTO;
import de.hsos.swa.jonas.theater.userdata.boundary.dto.api.IncomingUserEventDTO;
import de.hsos.swa.jonas.theater.userdata.boundary.dto.api.OutgoingUserEventDTOApi;
import de.hsos.swa.jonas.theater.userdata.control.UserDataOperations;
import de.hsos.swa.jonas.theater.userdata.entity.UserEvent;
import io.quarkus.logging.Log;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Path("api/user/userevents")
@Transactional(Transactional.TxType.REQUIRES_NEW)
@RolesAllowed("user")
public class UserEventsResourceApi {

    @Inject
    UserDataOperations userDataOperations;
    @Inject
    LinkBuilder linkBuilder;

    @GET
    public Response getEventsForUser(@PositiveOrZero @DefaultValue("0")@QueryParam("page[number]") Long pageNumber,
                                     @Max(50) @Positive @DefaultValue("10")@QueryParam("page[size]") Long pageSize,
                                             @Context SecurityContext securityContext,
                                             @Context UriInfo uriInfo){
        ResponseWrapperDTO<Object> responseWrapperDTO = new ResponseWrapperDTO<>();
        if(securityContext==null || securityContext.getUserPrincipal() == null) {
            responseWrapperDTO.errors = new ArrayList<>();
            responseWrapperDTO.errors.add(new ErrorDTO("401", "USER_ES:1","Not Authorized", "You are not authorized to access this resource"));
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        String username = securityContext.getUserPrincipal().getName();
        UserParametersDTO userParametersDTO = new UserParametersDTO(pageNumber, pageSize,username);
        Collection<UserEvent> userEvents = userDataOperations.getUserEventsForUser(userParametersDTO);
        if(userEvents.isEmpty()) {
            responseWrapperDTO.errors = new ArrayList<>();
            responseWrapperDTO.errors.add(new ErrorDTO("404", "USER_ES:2","No userevents found", "Couldn't find any events for this user"));
            return Response.status(Response.Status.NOT_FOUND).entity(responseWrapperDTO).build();
        }
        responseWrapperDTO.data = userEvents.stream()
                .map( userEvent -> {
                    String id = String.valueOf(userEvent.id);
                    String type = "userevent";

                    LinksDTO linksDTO = linkBuilder.createSelfLink(EventResourceApi.class, uriInfo, id);
                    OutgoingUserEventDTOApi outgoingEventDTOApi = OutgoingUserEventDTOApi.Converter.toDTO(userEvent);
                    RelationshipDTO<Object> relationshipDTO = linkBuilder.addRelationship(EventResourceApi.class, uriInfo, userEvent.id, "userevent");
                    return new ResourceObjectDTO<>(id, type, outgoingEventDTOApi, relationshipDTO, linksDTO);
                })
                .toList();
        long maxSize = userDataOperations.getUserEventsForUserCount(userParametersDTO);
        responseWrapperDTO.links = linkBuilder.createPaginationLinks(UserEventsResourceApi.class, uriInfo, pageNumber, pageSize, maxSize);
        return Response.ok().entity(responseWrapperDTO).build();
    }
    @POST
    @Retry
    @Timeout(5000)
    @Fallback(fallbackMethod = "createUserEventFallback")
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.75, delay = 10000)
    @Operation(summary = "Create new UserEvent", description = "Create new UserEvent")
    @APIResponses(value = {
            @APIResponse(responseCode = "201", description = "UserEvent created"),
            @APIResponse(responseCode = "400", description = "Bad Request"),
            @APIResponse(responseCode = "500", description = "Internal Server Error")
    })
    public Response createUserEvent(@Context SecurityContext securityContext,
                                    @Context UriInfo uriInfo,
                                    @Valid IncomingUserEventDTO incomingUserEventDTO) {
        ResponseWrapperDTO<Object> responseWrapperDTO = new ResponseWrapperDTO<>();
        if (securityContext == null || securityContext.getUserPrincipal() == null) {
            responseWrapperDTO.errors = new ArrayList<>();
            responseWrapperDTO.errors.add(new ErrorDTO("401", "USER_ES:1", "Not Authorized", "You are not authorized to access this resource"));
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        String username = securityContext.getUserPrincipal().getName();
        Log.info("Creating UserEvent for user: " + username);
        Log.info("Incoming UserEvent: " + incomingUserEventDTO.eventId + incomingUserEventDTO.eventState + incomingUserEventDTO.isFavorite);
        Optional<UserEvent> userEventAdded = userDataOperations.createUserEvent(username, incomingUserEventDTO);
        Log.info("UserEvent added: " + userEventAdded);
        if (userEventAdded.isEmpty()) {
            responseWrapperDTO.errors = new ArrayList<>();
            responseWrapperDTO.errors.add(new ErrorDTO("500", "USER_ES:6", "Internal Server Error", "Event could not be created"));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseWrapperDTO).build();
        }
        String id = String.valueOf(userEventAdded.get().id);
        String type = "userevent";
        LinksDTO linksDTO = linkBuilder.createSelfLink(EventResourceApi.class, uriInfo, id);
        OutgoingUserEventDTOApi outgoingEventDTOApi = OutgoingUserEventDTOApi.Converter.toDTO(userEventAdded.get());
        responseWrapperDTO.data = new ResourceObjectDTO<Object>(id, type, outgoingEventDTOApi, null, linksDTO);
        return Response.status(Response.Status.CREATED).entity(responseWrapperDTO).build();
    }
    public Response createUserEventFallback(@Context SecurityContext securityContext,
                                            @Context UriInfo uriInfo,
                                            @Valid IncomingUserEventDTO incomingUserEventDTO) {
        ResponseWrapperDTO<Object> responseWrapperDTO = new ResponseWrapperDTO<>();
        responseWrapperDTO.errors = new ArrayList<>();
        responseWrapperDTO.errors.add(new ErrorDTO("500", "USER_ES:6", "Internal Server Error", "Event could not be created"));
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseWrapperDTO).build();
    }
}
