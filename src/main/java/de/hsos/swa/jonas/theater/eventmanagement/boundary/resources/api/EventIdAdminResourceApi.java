package de.hsos.swa.jonas.theater.eventmanagement.boundary.resources.api;

import de.hsos.swa.jonas.theater.eventmanagement.boundary.dto.api.IncomingEventIdDTOApi;
import de.hsos.swa.jonas.theater.eventmanagement.boundary.dto.api.OutgoingEventIdDTOApi;
import de.hsos.swa.jonas.theater.eventmanagement.control.EventOperations;
import de.hsos.swa.jonas.theater.eventmanagement.entity.Event;
import de.hsos.swa.jonas.theater.shared.LinkBuilder;
import de.hsos.swa.jonas.theater.shared.dto.jsonapi.ErrorDTO;
import de.hsos.swa.jonas.theater.shared.dto.jsonapi.ResourceObjectDTO;
import de.hsos.swa.jonas.theater.shared.dto.jsonapi.ResponseWrapperDTO;
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
import javax.validation.constraints.Positive;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.Optional;

@RolesAllowed("admin")
@Path("api/admin/events")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Transactional(Transactional.TxType.REQUIRES_NEW)
public class EventIdAdminResourceApi {
    @Inject
    LinkBuilder linkBuilder;
    @Inject
    EventOperations eventOperations;

    @Path("{eventId}")
    @DELETE
    @Retry
    @Timeout(5000)
    @Fallback(fallbackMethod = "deleteEventByIdFallback")
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.75, delay = 10000)
    @Operation(summary = "Delete event by id", description = "Delete event by id via PathParam")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Event is deleted"),
            @APIResponse(responseCode = "400", description = "Bad Request"),
            @APIResponse(responseCode = "404", description = "No Event found for eventId"),
            @APIResponse(responseCode = "500", description = "Internal Server Error")
    })
    public Response deleteEventById(@Positive @PathParam("eventId") long eventId){
        ResponseWrapperDTO<Object> responseWrapperDTO = new ResponseWrapperDTO<>();

        if(eventOperations.deleteEventById(eventId))
        {
            responseWrapperDTO.data = "Event with id: " + eventId + " was deleted";
            return Response.ok().entity(responseWrapperDTO).build();
        }
        responseWrapperDTO.errors = new ArrayList<>();
        responseWrapperDTO.errors.add(new ErrorDTO("404", "EVENTS:5","Event not deleted", "Couldn't delete event with the given id"));
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseWrapperDTO).build();
    }

    @Path("{eventId}/fallback")
    public Response deleteEventByIdFallback(@Positive @PathParam("eventId") long eventId){
        ResponseWrapperDTO<ErrorDTO> responseWrapperDTO = new ResponseWrapperDTO<>();
        responseWrapperDTO.errors = new ArrayList<>();
        responseWrapperDTO.errors.add(new ErrorDTO("500", "EVENTS:2","Internal Server Error", "Something went wrong while processing your request"));
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseWrapperDTO).build();
    }

    @Path("{eventId}")
    @PUT
    @Retry
    @Timeout(5000)
    @Fallback(fallbackMethod = "updateEventByIdFallback")
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.75, delay = 10000)
    @Operation(summary = "Update event by id", description = "Update event by id via PathParam")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Event is updated"),
            @APIResponse(responseCode = "400", description = "Bad Request"),
            @APIResponse(responseCode = "404", description = "No Event found for eventId"),
            @APIResponse(responseCode = "500", description = "Internal Server Error")
    })
    public Response updateEventById(@Positive @PathParam("eventId") long eventId, IncomingEventIdDTOApi incomingEventIdDTOApi, @Context UriInfo uriInfo){
        ResponseWrapperDTO<Object> responseWrapperDTO = new ResponseWrapperDTO<>();
        Optional<Event> event = eventOperations.updateEventById(eventId, incomingEventIdDTOApi);
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
        responseWrapperDTO.errors.add(new ErrorDTO("404", "EVENTS:5","Event not updated", "Couldn't update event with the given id"));
        return Response.status(Response.Status.NOT_FOUND).entity(responseWrapperDTO).build();
    }

    @Path("{eventId}/fallback")
    public Response updateEventByIdFallback(@Positive @PathParam("eventId") long eventId, IncomingEventIdDTOApi incomingEventIdDTOApi){
        ResponseWrapperDTO<ErrorDTO> responseWrapperDTO = new ResponseWrapperDTO<>();
        responseWrapperDTO.errors = new ArrayList<>();
        responseWrapperDTO.errors.add(new ErrorDTO("500", "EVENTS:2","Internal Server Error", "Something went wrong while processing your request"));
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseWrapperDTO).build();
    }
}
