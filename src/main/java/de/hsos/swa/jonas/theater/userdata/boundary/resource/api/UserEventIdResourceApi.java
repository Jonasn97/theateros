package de.hsos.swa.jonas.theater.userdata.boundary.resource.api;

import de.hsos.swa.jonas.theater.eventmanagement.boundary.dto.api.OutgoingEventIdDTOApi;
import de.hsos.swa.jonas.theater.eventmanagement.boundary.resources.api.EventResourceApi;
import de.hsos.swa.jonas.theater.shared.LinkBuilder;
import de.hsos.swa.jonas.theater.shared.dto.jsonapi.*;
import de.hsos.swa.jonas.theater.userdata.boundary.dto.UserParametersDTO;
import de.hsos.swa.jonas.theater.userdata.boundary.dto.api.IncomingPatchUserEventDTO;
import de.hsos.swa.jonas.theater.userdata.boundary.dto.api.IncomingUpdateUserEventDTO;
import de.hsos.swa.jonas.theater.userdata.boundary.dto.api.IncomingUserEventDTO;
import de.hsos.swa.jonas.theater.userdata.boundary.dto.api.OutgoingUserEventDTOApi;
import de.hsos.swa.jonas.theater.userdata.control.UserDataOperations;
import de.hsos.swa.jonas.theater.userdata.entity.UserEvent;
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
import javax.validation.constraints.Positive;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Path("api/user/userevents")
@Transactional(Transactional.TxType.REQUIRES_NEW)
@RolesAllowed("user")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserEventIdResourceApi {

    @Inject
    UserDataOperations userDataOperations;
    @Inject
    LinkBuilder linkBuilder;
    @Path("{userEventId}")
    @GET
    @Retry
    @Timeout(5000)
    @Fallback(fallbackMethod = "getUserEventByIdForUserFallback")
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.75, delay = 10000)
    @Operation(summary = "Get event by id", description = "Get event by id via PathParam")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Requested Userevent is returned"),
            @APIResponse(responseCode = "400", description = "Bad Request"),
            @APIResponse(responseCode = "404", description = "No Userevent found for userEventId"),
            @APIResponse(responseCode = "500", description = "Internal Server Error")
    })
    public Response getUserEventByIdForUser(@Positive @PathParam("userEventId") long userEventId,
                                             @Context SecurityContext securityContext,
                                             @Context UriInfo uriInfo){
        ResponseWrapperDTO<Object> responseWrapperDTO = new ResponseWrapperDTO<>();
        if(securityContext==null || securityContext.getUserPrincipal() == null) {
            responseWrapperDTO.errors = new ArrayList<>();
            responseWrapperDTO.errors.add(new ErrorDTO("401", "USER_ES:1","Not Authorized", "You are not authorized to access this resource"));
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        String username = securityContext.getUserPrincipal().getName();
        Optional<UserEvent> userEvent = userDataOperations.getUserEventByIdForUser(userEventId, username);
        if(userEvent.isEmpty()) {
            responseWrapperDTO.errors = new ArrayList<>();
            responseWrapperDTO.errors.add(new ErrorDTO("404", "USER_ES:4","No userevents found", "Couldn't find any event with the given id"));
            return Response.status(Response.Status.NOT_FOUND).entity(responseWrapperDTO).build();
        }
        OutgoingUserEventDTOApi outgoingUserEventIdDTOApi = OutgoingUserEventDTOApi.Converter.toDTO(userEvent.get());
        ResourceObjectDTO<OutgoingUserEventDTOApi> resourceObjectDTO = new ResourceObjectDTO<>();
        resourceObjectDTO.id = String.valueOf(userEvent.get().id);
        resourceObjectDTO.type = "userevent";
        resourceObjectDTO.links = linkBuilder.createSelfLink(UserEventIdResourceApi.class, uriInfo, resourceObjectDTO.id);
        resourceObjectDTO.attributes=outgoingUserEventIdDTOApi;
        responseWrapperDTO.data = resourceObjectDTO;
        return Response.ok().entity(responseWrapperDTO).build();
    }
    @PUT
    @Path("{userEventId}")
    @Retry
    @Timeout(5000)
    @Fallback(fallbackMethod = "updateUserEventFallback")
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.75, delay = 10000)
    @Operation(summary = "Update UserEvent", description = "Update UserEvent")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "UserEvent updated"),
            @APIResponse(responseCode = "400", description = "Bad Request"),
            @APIResponse(responseCode = "500", description = "Internal Server Error")
    })
    public Response updateUserEvent(@Context SecurityContext securityContext,
                                    @Context UriInfo uriInfo,
                                    @Positive @PathParam("userEventId") long userEventId,
                                    @Valid IncomingUpdateUserEventDTO incomingUserEventDTO) {
        ResponseWrapperDTO<Object> responseWrapperDTO = new ResponseWrapperDTO<>();
        if (securityContext == null || securityContext.getUserPrincipal() == null) {
            responseWrapperDTO.errors = new ArrayList<>();
            responseWrapperDTO.errors.add(new ErrorDTO("401", "USER_ES:1", "Not Authorized", "You are not authorized to access this resource"));
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        String username = securityContext.getUserPrincipal().getName();
        Optional<UserEvent> userEventUpdated = userDataOperations.updateUserEvent(username, userEventId, incomingUserEventDTO);
        if (userEventUpdated.isEmpty()) {
            responseWrapperDTO.errors = new ArrayList<>();
            responseWrapperDTO.errors.add(new ErrorDTO("500", "USER_ES:7", "Internal Server Error", "UserEvent could not be updated"));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseWrapperDTO).build();
        }
        OutgoingUserEventDTOApi outgoingUserEventDTOApi = OutgoingUserEventDTOApi.Converter.toDTO(userEventUpdated.get());
        ResourceObjectDTO<OutgoingUserEventDTOApi> resourceObjectDTO = new ResourceObjectDTO<>();
        resourceObjectDTO.id = String.valueOf(userEventUpdated.get().id);
        resourceObjectDTO.type = "userevent";
        resourceObjectDTO.links = linkBuilder.createSelfLink(UserEventIdResourceApi.class, uriInfo, resourceObjectDTO.id);
        resourceObjectDTO.attributes = outgoingUserEventDTOApi;
        responseWrapperDTO.data = resourceObjectDTO;
        return Response.ok().entity(responseWrapperDTO).build();
    }
    @PATCH
    @Path("{userEventId}")
    @Retry
    @Timeout(5000)
    @Fallback(fallbackMethod = "patchUserEventFallback")
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.75, delay = 10000)
    @Operation(summary = "Patch isFavorite or EventState", description = "Patch either isFavorite or EventState of userEventId, isFavorite is prioritized over EventState if both are given")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "UserEvent patched"),
            @APIResponse(responseCode = "400", description = "Bad Request"),
            @APIResponse(responseCode = "500", description = "Internal Server Error")
    })
    public Response patchUserEvent(@Context SecurityContext securityContext,
                                   @Context UriInfo uriInfo,
                                   @Positive @PathParam("userEventId") long userEventId,
                                   @Valid IncomingPatchUserEventDTO incomingPatchUserEventDTO) {
        ResponseWrapperDTO<Object> responseWrapperDTO = new ResponseWrapperDTO<>();
        if (securityContext == null || securityContext.getUserPrincipal() == null) {
            responseWrapperDTO.errors = new ArrayList<>();
            responseWrapperDTO.errors.add(new ErrorDTO("401", "USER_ES:1", "Not Authorized", "You are not authorized to access this resource"));
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        String username = securityContext.getUserPrincipal().getName();
        Optional<UserEvent> userEventPatched = Optional.empty();
        if(incomingPatchUserEventDTO.isFavorite!=null)
            userEventPatched = userDataOperations.patchUserEvent(username, userEventId, incomingPatchUserEventDTO.isFavorite);
        if(incomingPatchUserEventDTO.eventState!=null)
            userEventPatched = userDataOperations.patchUserEvent(username, userEventId, incomingPatchUserEventDTO.eventState);
        if (userEventPatched.isEmpty()) {
            responseWrapperDTO.errors = new ArrayList<>();
            responseWrapperDTO.errors.add(new ErrorDTO("500", "USER_ES:7", "Internal Server Error", "UserEvent could not be patched"));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseWrapperDTO).build();
        }
        OutgoingUserEventDTOApi outgoingUserEventDTOApi = OutgoingUserEventDTOApi.Converter.toDTO(userEventPatched.get());
        responseWrapperDTO.data = userEventPatched.get();
        ResourceObjectDTO<OutgoingUserEventDTOApi> resourceObjectDTO = new ResourceObjectDTO<>();
        resourceObjectDTO.id = String.valueOf(userEventPatched.get().id);
        resourceObjectDTO.type = "userevent";
        resourceObjectDTO.links = linkBuilder.createSelfLink(UserEventIdResourceApi.class, uriInfo, resourceObjectDTO.id);
        resourceObjectDTO.attributes = outgoingUserEventDTOApi;
        return Response.ok().entity(responseWrapperDTO).build();
    }

    @Path("{userEventId}/fallback")
    public Response patchUserEventFallback(@Context SecurityContext securityContext,
                                           @Context UriInfo uriInfo,
                                           @Positive @PathParam("userEventId") long userEventId,
                                           @Valid IncomingPatchUserEventDTO incomingPatchUserEventDTO) {
        ResponseWrapperDTO<Object> responseWrapperDTO = new ResponseWrapperDTO<>();
        responseWrapperDTO.errors = new ArrayList<>();
        responseWrapperDTO.errors.add(new ErrorDTO("500", "USER_ES:7", "Internal Server Error", "UserEvent could not be patched"));
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseWrapperDTO).build();
    }



    @DELETE
    @Path("{userEventId}")
    @Retry
    @Timeout(5000)
    @Fallback(fallbackMethod = "deleteUserEventFallback")
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.75, delay = 10000)
    @Operation(summary = "Delete UserEvent", description = "Delete UserEvent")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "UserEvent deleted"),
            @APIResponse(responseCode = "400", description = "Bad Request"),
            @APIResponse(responseCode = "500", description = "Internal Server Error")
    })
    public Response deleteUserEvent(@Context SecurityContext securityContext,
                                    @Context UriInfo uriInfo,
                                    @Positive @PathParam("userEventId") long userEventId) {
        ResponseWrapperDTO<Object> responseWrapperDTO = new ResponseWrapperDTO<>();
        if (securityContext == null || securityContext.getUserPrincipal() == null) {
            responseWrapperDTO.errors = new ArrayList<>();
            responseWrapperDTO.errors.add(new ErrorDTO("401", "USER_ES:1", "Not Authorized", "You are not authorized to access this resource"));
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        String username = securityContext.getUserPrincipal().getName();
        boolean userEventDeleted = userDataOperations.deleteUserEvent(username, userEventId);
        if (!userEventDeleted) {
            responseWrapperDTO.errors = new ArrayList<>();
            responseWrapperDTO.errors.add(new ErrorDTO("500", "USER_ES:8", "Internal Server Error", "Event could not be deleted"));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseWrapperDTO).build();
        }
        return Response.status(Response.Status.OK).build();
    }

    @Path("{userEventId}/fallback")
    public Response deleteUserEventFallback(@Context SecurityContext securityContext,
                                            @Context UriInfo uriInfo,
                                            @Positive @PathParam("userEventId") long userEventId) {
        ResponseWrapperDTO<Object> responseWrapperDTO = new ResponseWrapperDTO<>();
        responseWrapperDTO.errors = new ArrayList<>();
        responseWrapperDTO.errors.add(new ErrorDTO("500", "USER_ES:8", "Internal Server Error", "Event could not be deleted"));
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseWrapperDTO).build();
    }




    @Path("{userEventId}/fallback")
    public Response getUserEventByIdForUserFallback(@Positive @PathParam("userEventId") long userEventId,
                                                    @Context SecurityContext securityContext,
                                                    @Context UriInfo uriInfo){
        ResponseWrapperDTO<ErrorDTO> responseWrapperDTO = new ResponseWrapperDTO<>();
        responseWrapperDTO.errors = new ArrayList<>();
        responseWrapperDTO.errors.add(new ErrorDTO("500", "USER_ES:5","Internal Server Error", "Something went wrong while processing your request"));
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseWrapperDTO).build();
    }

    @Path("{userEventId}/fallback")
    public Response updateUserEventFallback(@Context SecurityContext securityContext,
                                            @Context UriInfo uriInfo,
                                            @Positive @PathParam("userEventId") long userEventId,
                                            @Valid IncomingUpdateUserEventDTO incomingUserEventDTO) {
        ResponseWrapperDTO<Object> responseWrapperDTO = new ResponseWrapperDTO<>();
        responseWrapperDTO.errors = new ArrayList<>();
        responseWrapperDTO.errors.add(new ErrorDTO("500", "USER_ES:7", "Internal Server Error", "Event could not be updated"));
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseWrapperDTO).build();
    }
}
