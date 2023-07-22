package de.hsos.swa.jonas.theater.eventmanagement.boundary.resources.api;

import de.hsos.swa.jonas.theater.eventmanagement.boundary.dto.api.OutgoingPerformanceDTOApi;
import de.hsos.swa.jonas.theater.eventmanagement.control.PerformanceOperations;
import de.hsos.swa.jonas.theater.eventmanagement.entity.Performance;
import de.hsos.swa.jonas.theater.shared.LinkBuilder;
import de.hsos.swa.jonas.theater.shared.dto.jsonapi.ErrorDTO;
import de.hsos.swa.jonas.theater.shared.dto.jsonapi.ResourceObjectDTO;
import de.hsos.swa.jonas.theater.shared.dto.jsonapi.ResponseWrapperDTO;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Location;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.constraints.Positive;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.Optional;
@Path("api/performances/")
public class PerformanceIdResourceApi {

    @Inject
    PerformanceOperations performanceOperations;
    @Inject
    LinkBuilder linkBuilder;
    @Context
    UriInfo uriInfo;

    @Path("{performanceId}")
    @GET
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    @Retry
    @Timeout(5000)
    @Fallback(fallbackMethod = "getPerformanceByIdFallback")
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.75, delay = 10000)
    @Operation(summary = "Get Performance by id", description = "Get Performance by performanceId")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Performance for performanceId is returned"),
            @APIResponse(responseCode = "400", description = "Bad Request"),
            @APIResponse(responseCode = "404", description = "No Performances found for id"),
            @APIResponse(responseCode = "500", description = "Internal Server Error")
    })
    public Response getPerformanceById(@Positive @PathParam("performanceId") long eventId){
        Optional<Performance> performance = performanceOperations.getPerformance(eventId);
        ResponseWrapperDTO<Object> responseWrapperDTO = new ResponseWrapperDTO<>();
        if(performance.isPresent()){
            OutgoingPerformanceDTOApi outgoingEventIdDTOApi = OutgoingPerformanceDTOApi.Converter.toDTO(performance.get());
            ResourceObjectDTO<OutgoingPerformanceDTOApi> resourceObjectDTO = new ResourceObjectDTO<>();
            resourceObjectDTO.id = String.valueOf(performance.get().id);
            resourceObjectDTO.type = "performance";
            resourceObjectDTO.links =  linkBuilder.createSelfLink(PerformanceIdResourceApi.class, uriInfo, resourceObjectDTO.id);
            resourceObjectDTO.attributes = outgoingEventIdDTOApi;
            responseWrapperDTO.data=resourceObjectDTO;
            return Response.ok().entity(responseWrapperDTO).build();
        }
        responseWrapperDTO.errors = new ArrayList<>();
        responseWrapperDTO.errors.add(new ErrorDTO("404", "PERF:5","Performance not found", "Couldn't find Performance with the given id"));
        return Response.status(Response.Status.NOT_FOUND).entity(responseWrapperDTO).build();
    }
    @Path("{performanceId}/calendar")
    @GET
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    @Retry
    @Timeout(5000)
    @Fallback(fallbackMethod = "getCalendarFileFallback")
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.75, delay = 10000)
    @Operation(summary = "Get Calendarentry by id", description = "Get mime: text/calendar for performanceId")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Mimetype text/calendar for performanceId is returned"),
            @APIResponse(responseCode = "400", description = "Bad Request"),
            @APIResponse(responseCode = "404", description = "No Performance found for id to create Calendarentry"),
            @APIResponse(responseCode = "500", description = "Internal Server Error")
    })
    @Produces("text/calendar")
    public Response getCalendarFile(@Positive @PathParam("performanceId") Long id){
        Calendar calenderFile = new Calendar();
        Optional<Performance> optionalPerformance = performanceOperations.getPerformance(id);
        if(optionalPerformance.isEmpty()){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        Performance performance = optionalPerformance.get();
        String eventName = performance.getEvent().getTitle();
        Location eventLocation =new Location(performance.getEvent().getLocation());
        Date date = new Date(performance.getDatetime().toEpochSecond(java.time.ZoneOffset.UTC));
        VEvent vEvent = new VEvent(date, eventName);
        vEvent.getProperties().add(eventLocation);
        calenderFile.getComponents().add(vEvent);
        String fileName = eventName.replaceAll("\\s+", "_") + ".ics";
        String calendarContent = calenderFile.toString();
        return Response.ok(calendarContent)
                .header("Content-Disposition", "attachment; filename=\"" + fileName + "\"")
                .build();
    }
    @Path("{performanceId}/fallback")
    public Response getPerformanceByIdFallback(@Positive @PathParam("performanceId") long performanceId){
        ResponseWrapperDTO<ErrorDTO> responseWrapperDTO = new ResponseWrapperDTO<>();
        responseWrapperDTO.errors = new ArrayList<>();
        responseWrapperDTO.errors.add(new ErrorDTO("500", "PERF:2","Internal Server Error", "Something went wrong while processing your request"));
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseWrapperDTO).build();
    }
    @Path("{performanceId}/calendar/fallback")
    public Response getCalendarFileFallback(@Positive @PathParam("performanceId") Long performanceId){
        ResponseWrapperDTO<ErrorDTO> responseWrapperDTO = new ResponseWrapperDTO<>();
        responseWrapperDTO.errors = new ArrayList<>();
        responseWrapperDTO.errors.add(new ErrorDTO("500", "PERF:3","Internal Server Error", "Something went wrong while processing your request"));
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseWrapperDTO).build();
    }
}
