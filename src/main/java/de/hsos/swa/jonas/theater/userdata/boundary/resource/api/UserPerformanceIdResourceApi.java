package de.hsos.swa.jonas.theater.userdata.boundary.resource.api;


import de.hsos.swa.jonas.theater.shared.LinkBuilder;
import de.hsos.swa.jonas.theater.shared.dto.jsonapi.ErrorDTO;
import de.hsos.swa.jonas.theater.shared.dto.jsonapi.ResourceObjectDTO;
import de.hsos.swa.jonas.theater.shared.dto.jsonapi.ResponseWrapperDTO;
import de.hsos.swa.jonas.theater.userdata.boundary.dto.api.IncomingUserPerformanceDTO;
import de.hsos.swa.jonas.theater.userdata.boundary.dto.api.OutgoingUserPerformanceDTOApi;
import de.hsos.swa.jonas.theater.userdata.control.UserDataOperations;
import de.hsos.swa.jonas.theater.userdata.entity.UserPerformance;
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
import java.util.Optional;

@Path("/api/user/userperformance")
@Transactional(Transactional.TxType.REQUIRES_NEW)
@RolesAllowed("user")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserPerformanceIdResourceApi {

    @Inject
    UserDataOperations userDataOperations;

    @Inject
    LinkBuilder linkBuilder;

    @Path("/{id}")
    @GET
    @Retry
    @Timeout(5000)
    //@Fallback(fallbackMethod = "getPerformancesByIdForUserFallback")
    //@CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.75, delay = 10000)
    @Operation(summary = "Get all UserPerformances for a User")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Found UserPerformance for User"),
            @APIResponse(responseCode = "401", description = "Unauthorized"),
            @APIResponse(responseCode = "404", description = "No UserPerformance found for User"),
            @APIResponse(responseCode = "500", description = "Internal Server Error")
    })
    public Response getPerformancesByIdForUser(@Positive @PathParam("id") long id,
                                               @Context SecurityContext securityContext, @Context UriInfo uriInfo) {
        ResponseWrapperDTO<Object> responseWrapperDTO = new ResponseWrapperDTO<>();
        if (securityContext == null || securityContext.getUserPrincipal() == null) {
            responseWrapperDTO.errors = new ArrayList<>();
            responseWrapperDTO.errors.add(new ErrorDTO("401", "USER_ES:1", "Not Authorized", "You are not authorized to access this resource"));
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        String username = securityContext.getUserPrincipal().getName();
        Optional<UserPerformance> userPerformance = userDataOperations.getUserPerformanceByIdForUser(id, username);
        if (userPerformance.isEmpty()) {
            responseWrapperDTO.errors = new ArrayList<>();
            responseWrapperDTO.errors.add(new ErrorDTO("404", "USER_ES:2", "UserPerformance not found", "No UserPerformance found for User"));
            return Response.status(Response.Status.NOT_FOUND).entity(responseWrapperDTO).build();
        }
        OutgoingUserPerformanceDTOApi outgoingUserPerformanceDTOApi = OutgoingUserPerformanceDTOApi.Converter.toDTO(userPerformance.get());
        ResourceObjectDTO<OutgoingUserPerformanceDTOApi> resourceObjectDTO = new ResourceObjectDTO<>();
        resourceObjectDTO.attributes = outgoingUserPerformanceDTOApi;
        resourceObjectDTO.type = "Userperformance";
        resourceObjectDTO.id = userPerformance.get().id.toString();
        resourceObjectDTO.links = linkBuilder.createSelfLink(UserPerformanceIdResourceApi.class, uriInfo, resourceObjectDTO.id);

        responseWrapperDTO.data = resourceObjectDTO;
        return Response.ok().entity(responseWrapperDTO).build();
    }/*
    @Path("/{id}/fallback")
    public Response getPerformancesByIdForUserFallback(@Positive @PathParam("id") Long id,
                                                       @Context SecurityContext securityContext, @Context UriInfo uriInfo) {
        ResponseWrapperDTO<Object> responseWrapperDTO = new ResponseWrapperDTO<>();
        responseWrapperDTO.errors = new ArrayList<>();
        responseWrapperDTO.errors.add(new ErrorDTO("500", "USER_ES:3", "Internal Server Error", "Internal Server Error"));
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseWrapperDTO).build();
    }*/

    @Path("/{id}")
    @DELETE
    @Retry
    @Timeout(5000)
    @Fallback(fallbackMethod = "deletePerformancesByIdForUserFallback")
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.75, delay = 10000)
    @Operation(summary = "Delete a UserPerformance for a User")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Deleted UserPerformance for User"),
            @APIResponse(responseCode = "401", description = "Unauthorized"),
            @APIResponse(responseCode = "404", description = "No UserPerformance found for User"),
            @APIResponse(responseCode = "500", description = "Internal Server Error")
    })
    public Response deletePerformancesByIdForUser(@Positive @PathParam("id") long id,
                                                  @Context SecurityContext securityContext, @Context UriInfo uriInfo) {
        ResponseWrapperDTO<Object> responseWrapperDTO = new ResponseWrapperDTO<>();
        if (securityContext == null || securityContext.getUserPrincipal() == null) {
            responseWrapperDTO.errors = new ArrayList<>();
            responseWrapperDTO.errors.add(new ErrorDTO("401", "USER_ES:4", "Not Authorized", "You are not authorized to access this resource"));
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        String username = securityContext.getUserPrincipal().getName();
        Optional<UserPerformance> userPerformance = userDataOperations.getUserPerformanceByIdForUser(id, username);
        if (userPerformance.isEmpty()) {
            responseWrapperDTO.errors = new ArrayList<>();
            responseWrapperDTO.errors.add(new ErrorDTO("404", "USER_ES:5", "UserPerformance not found", "No UserPerformance found for User"));
            return Response.status(Response.Status.NOT_FOUND).entity(responseWrapperDTO).build();
        }
        if(!userDataOperations.deleteUserPerformance(username, id))
        {
            responseWrapperDTO.errors = new ArrayList<>();
            responseWrapperDTO.errors.add(new ErrorDTO("500", "USER_ES:6", "Internal Server Error", "Internal Server Error"));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseWrapperDTO).build();
        }
        return Response.ok().build();
    }

    @Path("/{id}/fallback")
    public Response deletePerformancesByIdForUserFallback(@Positive @PathParam("id") long id,
                                                          @Context SecurityContext securityContext, @Context UriInfo uriInfo) {
        ResponseWrapperDTO<Object> responseWrapperDTO = new ResponseWrapperDTO<>();
        responseWrapperDTO.errors = new ArrayList<>();
        responseWrapperDTO.errors.add(new ErrorDTO("500", "USER_ES:6", "Internal Server Error", "Internal Server Error"));
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseWrapperDTO).build();
    }

    @Path("/{id}")
    @PUT
    @Retry
    @Timeout(5000)
    @Fallback(fallbackMethod = "updatePerformancesByIdForUserFallback")
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.75, delay = 10000)
    @Operation(summary = "Update a UserPerformance for a User")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Updated UserPerformance for User"),
            @APIResponse(responseCode = "401", description = "Unauthorized"),
            @APIResponse(responseCode = "404", description = "No UserPerformance found for User"),
            @APIResponse(responseCode = "500", description = "Internal Server Error")
    })
    public Response updatePerformancesByIdForUser(@Positive @PathParam("id") long id,
                                                  @Valid IncomingUserPerformanceDTO incomingUserPerformanceDTO,
                                                  @Context SecurityContext securityContext, @Context UriInfo uriInfo) {
        ResponseWrapperDTO<Object> responseWrapperDTO = new ResponseWrapperDTO<>();
        if (securityContext == null || securityContext.getUserPrincipal() == null) {
            responseWrapperDTO.errors = new ArrayList<>();
            responseWrapperDTO.errors.add(new ErrorDTO("401", "USER_ES:7", "Not Authorized", "You are not authorized to access this resource"));
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        String username = securityContext.getUserPrincipal().getName();
        Optional<UserPerformance> updatedUserPerformance = userDataOperations.updateUserPerformance(username, id, incomingUserPerformanceDTO);
        if (updatedUserPerformance.isEmpty()) {
            responseWrapperDTO.errors = new ArrayList<>();
            responseWrapperDTO.errors.add(new ErrorDTO("404", "USER_ES:8", "UserPerformance not found", "No UserPerformance found for User"));
            return Response.status(Response.Status.NOT_FOUND).entity(responseWrapperDTO).build();
        }
        OutgoingUserPerformanceDTOApi updatedUserPerformanceDTO = OutgoingUserPerformanceDTOApi.Converter.toDTO(updatedUserPerformance.get());
        ResourceObjectDTO<OutgoingUserPerformanceDTOApi> resourceObjectDTO = new ResourceObjectDTO<>();
        resourceObjectDTO.id = String.valueOf(updatedUserPerformance.get().id);
        resourceObjectDTO.type = "userevent";
        resourceObjectDTO.links = linkBuilder.createSelfLink(UserPerformanceIdResourceApi.class, uriInfo, resourceObjectDTO.id);
        resourceObjectDTO.attributes = updatedUserPerformanceDTO;
        responseWrapperDTO.data = resourceObjectDTO;
        return Response.ok().build();
    }

    @Path("/{id}/fallback")
    public Response updatePerformancesByIdForUserFallback(@Positive @PathParam("id") long id,
                                                          @Valid IncomingUserPerformanceDTO incomingUserPerformanceDTO,
                                                          @Context SecurityContext securityContext, @Context UriInfo uriInfo) {
        ResponseWrapperDTO<Object> responseWrapperDTO = new ResponseWrapperDTO<>();
        responseWrapperDTO.errors = new ArrayList<>();
        responseWrapperDTO.errors.add(new ErrorDTO("500", "USER_ES:9", "Internal Server Error", "Internal Server Error"));
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseWrapperDTO).build();

    }
}
