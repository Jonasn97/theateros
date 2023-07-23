package de.hsos.swa.jonas.theater.userdata.boundary.resource.api;

import de.hsos.swa.jonas.theater.shared.LinkBuilder;
import de.hsos.swa.jonas.theater.shared.dto.jsonapi.ErrorDTO;
import de.hsos.swa.jonas.theater.shared.dto.jsonapi.LinksDTO;
import de.hsos.swa.jonas.theater.shared.dto.jsonapi.ResourceObjectDTO;
import de.hsos.swa.jonas.theater.shared.dto.jsonapi.ResponseWrapperDTO;
import de.hsos.swa.jonas.theater.userdata.boundary.dto.UserParametersDTO;
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
import javax.validation.constraints.Max;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Path("/api/user/userperformance")
@Transactional(Transactional.TxType.REQUIRES_NEW)
@RolesAllowed("user")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserPerformanceResourceApi {

    @Inject
    UserDataOperations userDataOperations;

    @Inject
    LinkBuilder linkBuilder;

    @GET
    @Retry
    @Timeout(5000)
    @Fallback(fallbackMethod = "getPerformancesForUserFallback")
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.75, delay = 10000)
    @Operation(summary = "Get all UserPerformances for a User")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Found UserPerformances for User"),
            @APIResponse(responseCode = "401", description = "Unauthorized"),
            @APIResponse(responseCode = "404", description = "No UserPerformances found for User"),
            @APIResponse(responseCode = "500", description = "Internal Server Error")
    })
    public Response getPerformancesForUser(@PositiveOrZero @DefaultValue("0")@QueryParam("page[number]") Long pageNumber,
                                           @Max(50) @Positive @DefaultValue("10")@QueryParam("page[size]") Long pageSize,
                                           @Context SecurityContext securityContext,
                                           @Context UriInfo uriInfo) {
        ResponseWrapperDTO<Object> responseWrapperDTO = new ResponseWrapperDTO<>();
        if(securityContext== null || securityContext.getUserPrincipal() == null) {
            responseWrapperDTO.errors = new ArrayList<>();
            responseWrapperDTO.errors.add(new ErrorDTO("401", "USER_ES:1","Not authenticated", "You are not authenticated to access this resource"));
            return Response.status(Response.Status.UNAUTHORIZED).entity(responseWrapperDTO).build();
        }
        String username = securityContext.getUserPrincipal().getName();
        UserParametersDTO userParametersDTO = new UserParametersDTO(pageNumber, pageSize, username);
        Collection<UserPerformance> userPerformances = userDataOperations.getUserPerformancesForUser(userParametersDTO);
        if(userPerformances == null || userPerformances.isEmpty()) {
            responseWrapperDTO.errors = new ArrayList<>();
            responseWrapperDTO.errors.add(new ErrorDTO("404", "USER_ES:2","No UserPerformances found", "No UserPerformances found for User"));
            return Response.status(Response.Status.NOT_FOUND).entity(responseWrapperDTO).build();
        }
        responseWrapperDTO.data = userPerformances.stream()
                .map(userPerformance -> {
                    String id = String.valueOf(userPerformance.id);
                    String type = "userperformance";

                    LinksDTO linksDTO = linkBuilder.createSelfLink(UserPerformanceResourceApi.class, uriInfo, id);
                    OutgoingUserPerformanceDTOApi outgoingUserPerformanceDTOApi = OutgoingUserPerformanceDTOApi.Converter.toDTO(userPerformance);
                    return new ResourceObjectDTO<>(id, type, outgoingUserPerformanceDTOApi, null, linksDTO);
                }).toList();
        long maxSize = userDataOperations.getUserPerformancesForUserCount(userParametersDTO);
        responseWrapperDTO.links = linkBuilder.createPaginationLinks(UserPerformanceResourceApi.class, uriInfo, pageNumber, pageSize, maxSize);
        return Response.ok(responseWrapperDTO).build();
    }

    public Response getPerformancesForUserFallback(@PositiveOrZero @DefaultValue("0")@QueryParam("page[number]") Long pageNumber,
                                                   @Max(50) @Positive @DefaultValue("10")@QueryParam("page[size]") Long pageSize,
                                                   @Context SecurityContext securityContext,
                                                   @Context UriInfo uriInfo) {
        ResponseWrapperDTO<Object> responseWrapperDTO = new ResponseWrapperDTO<>();
        responseWrapperDTO.errors = new ArrayList<>();
        responseWrapperDTO.errors.add(new ErrorDTO("500", "USER_ES:3","Internal Server Error", "An internal Server Error occurred"));
        return Response.status(Response.Status.NOT_FOUND).entity(responseWrapperDTO).build();
    }

    @POST
    @Retry
    @Timeout(5000)
    @Fallback(fallbackMethod = "createUserPerformanceFallback")
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.75, delay = 10000)
    @Operation(summary = "Create a UserPerformance")
    @APIResponses(value = {
            @APIResponse(responseCode = "201", description = "Created UserPerformance"),
            @APIResponse(responseCode = "401", description = "Unauthorized"),
            @APIResponse(responseCode = "404", description = "No UserPerformance found for User"),
            @APIResponse(responseCode = "500", description = "Internal Server Error")
    })
    public Response createUserPerformance(@Context SecurityContext securityContext,
                                          @Context UriInfo uriInfo,
                                          IncomingUserPerformanceDTO incomingUserPerformanceDTO) {
        ResponseWrapperDTO<Object> responseWrapperDTO = new ResponseWrapperDTO<>();
        if(securityContext== null || securityContext.getUserPrincipal() == null) {
            responseWrapperDTO.errors = new ArrayList<>();
            responseWrapperDTO.errors.add(new ErrorDTO("401", "USER_ES:1", "Not Authorized", "You are not authorized to access this resource"));
            return Response.status(Response.Status.UNAUTHORIZED).entity(responseWrapperDTO).build();
        }
        String username = securityContext.getUserPrincipal().getName();
        Optional<UserPerformance> userPerformance = userDataOperations.createUserPerformance(username, incomingUserPerformanceDTO);
        if(userPerformance.isEmpty()) {
            responseWrapperDTO.errors = new ArrayList<>();
            responseWrapperDTO.errors.add(new ErrorDTO("500", "USER_ES:6", "Internal Server Error", "UserPerformance could not be created"));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseWrapperDTO).build();
        }
        String id = String.valueOf(userPerformance.get().id);
        String type = "userperformance";
        LinksDTO linksDTO = linkBuilder.createSelfLink(UserPerformanceResourceApi.class, uriInfo, id);
        OutgoingUserPerformanceDTOApi outgoingUserPerformanceDTOApi = OutgoingUserPerformanceDTOApi.Converter.toDTO(userPerformance.get());
        responseWrapperDTO.data = new ResourceObjectDTO<>(id, type, outgoingUserPerformanceDTOApi, null, linksDTO);
        return Response.status(Response.Status.CREATED).entity(responseWrapperDTO).build();
    }
    @Path("/fallback")
    public Response createUserPerformanceFallback(@Context SecurityContext securityContext,
                                                  @Context UriInfo uriInfo,
                                                  IncomingUserPerformanceDTO incomingUserPerformanceDTO) {
        ResponseWrapperDTO<Object> responseWrapperDTO = new ResponseWrapperDTO<>();
        responseWrapperDTO.errors = new ArrayList<>();
        responseWrapperDTO.errors.add(new ErrorDTO("500", "USER_ES:6", "Internal Server Error", "UserPerformance could not be created"));
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseWrapperDTO).build();
    }
}
