package de.hsos.swa.jonas.theater.eventmanagement.boundary.resources.api;

import de.hsos.swa.jonas.theater.eventmanagement.boundary.dto.api.OutgoingEventIdDTOApi;
import de.hsos.swa.jonas.theater.eventmanagement.control.EventOperations;
import de.hsos.swa.jonas.theater.shared.LinkBuilder;
import de.hsos.swa.jonas.theater.shared.dto.jsonapi.ErrorDTO;
import de.hsos.swa.jonas.theater.shared.dto.jsonapi.LinksDTO;
import de.hsos.swa.jonas.theater.eventmanagement.entity.Event;
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
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.Optional;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("api/events/")
public class EventIdResourceApi {

    @Inject
    EventOperations eventOperations;
    @Inject
    LinkBuilder linkBuilder;

    @Context
    UriInfo uriInfo;

    @Path("{eventId}")
    @GET
    @Retry
    @Timeout(5000)
    @Fallback(fallbackMethod = "getEventByIdFallback")
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.75, delay = 10000)
    @Operation(summary = "Get event by id", description = "Get event by id via PathParam")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Requested Event is returned"),
            @APIResponse(responseCode = "400", description = "Bad Request"),
            @APIResponse(responseCode = "404", description = "No Event found for eventId"),
            @APIResponse(responseCode = "500", description = "Internal Server Error")
    })
    public Response getEventById(@Positive @PathParam("eventId") long eventId){
        Optional<Event> event = eventOperations.getEventById(eventId);
        ResponseWrapperDTO<Object> responseWrapperDTO = new ResponseWrapperDTO<>();
        if(event.isPresent()){
            OutgoingEventIdDTOApi outgoingEventIdDTOApi = OutgoingEventIdDTOApi.Converter.toDTO(event.get());
            ResourceObjectDTO<OutgoingEventIdDTOApi> resourceObjectDTO = new ResourceObjectDTO<>();
            resourceObjectDTO.id = String.valueOf(event.get().id);
            resourceObjectDTO.type = "event";
            resourceObjectDTO.links = linkBuilder.createSelfLink(EventIdResourceApi.class, uriInfo, resourceObjectDTO.id);
            resourceObjectDTO.attributes = outgoingEventIdDTOApi;
            responseWrapperDTO.data=resourceObjectDTO;
            return Response.ok().entity(responseWrapperDTO).build();
        }
        responseWrapperDTO.errors = new ArrayList<>();
        responseWrapperDTO.errors.add(new ErrorDTO("404", "EVENTS:5","Event not found", "Couldn't find event with the given id"));
        return Response.status(Response.Status.NOT_FOUND).entity(responseWrapperDTO).build();
    }
    @Path("{eventId}/fallback")
    public Response getEventByIdFallback(@Positive @PathParam("eventId") long eventId){
        ResponseWrapperDTO<ErrorDTO> responseWrapperDTO = new ResponseWrapperDTO<>();
        responseWrapperDTO.errors = new ArrayList<>();
        responseWrapperDTO.errors.add(new ErrorDTO("500", "EVENTS:2","Internal Server Error", "Something went wrong while processing your request"));
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseWrapperDTO).build();
    }
}
