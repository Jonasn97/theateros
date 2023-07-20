package de.hsos.swa.jonas.theater.eventmanagement.boundary.resources.api;

import de.hsos.swa.jonas.theater.eventmanagement.boundary.dto.api.OutgoingPerformanceDTOApi;
import de.hsos.swa.jonas.theater.eventmanagement.control.EventOperations;
import de.hsos.swa.jonas.theater.eventmanagement.entity.Performance;
import de.hsos.swa.jonas.theater.shared.dto.jsonapi.ErrorDTO;
import de.hsos.swa.jonas.theater.shared.dto.jsonapi.LinksDTO;
import de.hsos.swa.jonas.theater.shared.dto.jsonapi.ResourceObjectDTO;
import de.hsos.swa.jonas.theater.shared.dto.jsonapi.ResponseWrapperDTO;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

import javax.inject.Inject;
import javax.validation.constraints.Positive;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.Collection;

@Path("/api/events")
public class EventIdPerformanceResourceApi {
    @Inject
    EventOperations eventOperations;
    @Context
    UriInfo uriInfo;
    @GET
    @Retry
    @Timeout(5000)
    @Fallback(fallbackMethod = "getPerformancesByEventIdFallback")
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.75, delay = 10000)
    @Operation(summary = "Get Performances For Event", description = "Get Performances for eventId")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Performances for eventId are returned"),
            @APIResponse(responseCode = "400", description = "Bad Request"),
            @APIResponse(responseCode = "404", description = "No Performances found for id"),
            @APIResponse(responseCode = "500", description = "Internal Server Error")
    })
    @Path("/{eventId}/performances")
    public Response getPerformancesByEventId(@Positive @PathParam("eventId") long id){
        Collection<Performance> performances = eventOperations.getPerformancesByEventId(id);
        ResponseWrapperDTO<Object> responseWrapperDTO = new ResponseWrapperDTO<>();
        if(performances.isEmpty()){
            responseWrapperDTO.errors = new ArrayList<>();
            responseWrapperDTO.errors.add(new ErrorDTO("404", "PERF:1","No performances for id found", "Couldn't find any performances with the given ids"));
            return Response.status(Response.Status.NOT_FOUND).entity(responseWrapperDTO).build();
        }
        responseWrapperDTO.data = performances.stream()
                .map(performance -> {
                    String performanceId = String.valueOf(performance.id);
                    String type = "performance";
                    LinksDTO linksDTO = null;//TODO LinkBuilder.createSelfLink(uriInfo, PerformanceResourceApi.class, performanceId);
                    OutgoingPerformanceDTOApi outgoingPerformanceDTOApi = OutgoingPerformanceDTOApi.Converter.toDTO(performance);
                    return new ResourceObjectDTO<>(performanceId, type, outgoingPerformanceDTOApi, null, linksDTO);
                }).toList();

        return Response.ok().entity(responseWrapperDTO).build();
    }
    @Path("/{eventId}/performances/fallback")
    public Response getPerformancesByEventIdFallback(@Positive @PathParam("eventId") long eventId){
        ResponseWrapperDTO<ErrorDTO> responseWrapperDTO = new ResponseWrapperDTO<>();
        responseWrapperDTO.errors = new ArrayList<>();
        responseWrapperDTO.errors.add(new ErrorDTO("500", "EVENTS:2","Internal Server Error", "Something went wrong while processing your request"));
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseWrapperDTO).build();
    }
}
