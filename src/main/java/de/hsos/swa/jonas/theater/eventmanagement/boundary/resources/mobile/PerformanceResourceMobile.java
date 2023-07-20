package de.hsos.swa.jonas.theater.eventmanagement.boundary.resources.mobile;

import de.hsos.swa.jonas.theater.eventmanagement.boundary.dto.mobile.OutgoingPerformanceEventDTOMobile;
import de.hsos.swa.jonas.theater.eventmanagement.boundary.dto.QueryParametersDTO;
import de.hsos.swa.jonas.theater.eventmanagement.control.PerformanceOperations;
import de.hsos.swa.jonas.theater.eventmanagement.entity.Performance;
import de.hsos.swa.jonas.theater.shared.EventState;
import de.hsos.swa.jonas.theater.userdata.entity.PerformanceState;
import io.quarkus.qute.Template;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Location;

import javax.inject.Inject;
import javax.validation.constraints.Max;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.*;
import java.util.stream.Collectors;

@Path("mobile")
public class PerformanceResourceMobile {

    private final static String FIRSTPAGE_STRING = "0";
    @Inject
    PerformanceOperations performanceOperations;

    @Inject
    Template spielzeiten;

    @Path("/performances")
    @GET
    public Response getPerformances(@QueryParam("filter[name]") String nameFilter,
                                    @QueryParam("filter[status]") ArrayList<String> statusFilter,
                                    @QueryParam("filter[playType]") ArrayList<String> playTypeFilter,
                                    @QueryParam("filter[performanceType]") ArrayList<String> performanceTypeFilter,
                                    @QueryParam("filter[startDateTime]") String startDateTimeFilter,
                                    @QueryParam("filter[endDateTime]") String endDateTimeFilter,
                                    @QueryParam("include") String include,
                                    @PositiveOrZero @DefaultValue(FIRSTPAGE_STRING)@QueryParam("page[number]") Long pageNumber,
                                    @Positive @Max(50) @DefaultValue("10")@QueryParam("page[size]") Long pageSize,
                                    @Context SecurityContext securityContext){
        QueryParametersDTO queryParametersDTO = new QueryParametersDTO(nameFilter, statusFilter, playTypeFilter, performanceTypeFilter, startDateTimeFilter, endDateTimeFilter, include, pageNumber, pageSize);
        Collection<Performance> performances = performanceOperations.getPerformances(queryParametersDTO);
        String username;
        Map<Long, PerformanceState> performanceStates = null;
        if(securityContext.getUserPrincipal()!= null && securityContext.getUserPrincipal().getName()!= null) {
            username = securityContext.getUserPrincipal().getName();
            Set<Long> eventIds = performances.stream().map(performance -> performance.id).collect(Collectors.toSet());
            performanceStates = performanceOperations.getPerformanceStatus(username, eventIds);
        }
        Map<Long, PerformanceState> finalPerformanceStates = performanceStates;
        Collection<OutgoingPerformanceEventDTOMobile> outgoingPerformanceEventDTOMobiles = performances.stream().map(performance-> {
            OutgoingPerformanceEventDTOMobile outgoingPerformanceEventDTOMobile = OutgoingPerformanceEventDTOMobile.Converter.toDTO(performance);
            if(finalPerformanceStates != null && finalPerformanceStates.containsKey(performance.id)){
                outgoingPerformanceEventDTOMobile.setPerformanceState(finalPerformanceStates.get(performance.id));
            }
            return outgoingPerformanceEventDTOMobile;
        }).collect(java.util.stream.Collectors.toList());
        int active = 2;
        String html = spielzeiten.data("performances", outgoingPerformanceEventDTOMobiles, "queryParameters", queryParametersDTO, "active", active).render();
        return Response.ok().entity(html).build();
    }
    @Path("/performances/{id}")
    @GET
    @Produces("text/calendar")
    public Response getCalenderFile(@Positive @PathParam("id") Long id){
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
}
