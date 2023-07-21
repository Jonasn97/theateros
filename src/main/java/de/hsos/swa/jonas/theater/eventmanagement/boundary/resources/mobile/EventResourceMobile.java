package de.hsos.swa.jonas.theater.eventmanagement.boundary.resources.mobile;

import de.hsos.swa.jonas.theater.eventmanagement.boundary.dto.mobile.OutgoingEventDTOMobile;
import de.hsos.swa.jonas.theater.eventmanagement.boundary.dto.mobile.OutgoingEventNextPerformanceDTOMobile;
import de.hsos.swa.jonas.theater.eventmanagement.boundary.dto.QueryParametersDTO;
import de.hsos.swa.jonas.theater.eventmanagement.control.EventOperations;
import de.hsos.swa.jonas.theater.eventmanagement.entity.Performance;
import de.hsos.swa.jonas.theater.eventmanagement.entity.Event;
import de.hsos.swa.jonas.theater.shared.EventState;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.qute.Template;
import io.vertx.core.eventbus.EventBus;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.constraints.Max;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Path("mobile/events")
public class EventResourceMobile {
    private final static String FIRSTPAGE_STRING = "0";
    @Inject
    EventOperations eventOperations;
    @Inject
    Template stuecke;
    @Inject
    EventBus eventBus;
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @GET
    public Response listEvents(@QueryParam("filter[name]") String nameFilter,
                               @QueryParam("filter[status]") ArrayList<String> statusFilter,
                               @QueryParam("filter[kind]") ArrayList<String> kindFilter,
                               @QueryParam("filter[performanceType]") ArrayList<String> performanceTypeFilter,
                               @QueryParam("filter[startDateTime]") String startDateTimeFilter,
                               @Pattern(regexp = "7days|30days") @QueryParam("filter[endDateTime]") String endDateTimeFilter,
                               @QueryParam("include") String include,
                               @PositiveOrZero @DefaultValue(FIRSTPAGE_STRING)@QueryParam("page[number]") Long pageNumber,
                               @Positive @Max(50) @DefaultValue("10")@QueryParam("page[size]") Long pageSize,
                               @Context SecurityContext securityContext) {
        if(endDateTimeFilter!=null &&endDateTimeFilter.equals("7days")) {
            endDateTimeFilter = String.valueOf(LocalDateTime.now().plusDays(7));
            startDateTimeFilter = String.valueOf(LocalDateTime.now());
        } else if(endDateTimeFilter!=null &&endDateTimeFilter.equals("30days")) {
            endDateTimeFilter = String.valueOf(LocalDateTime.now().plusDays(30));
            startDateTimeFilter = String.valueOf(LocalDateTime.now());
        }
        QueryParametersDTO queryParametersDTO = new QueryParametersDTO(nameFilter, statusFilter, kindFilter, performanceTypeFilter, startDateTimeFilter, endDateTimeFilter, include, pageNumber, pageSize);
        Collection<Event> events = eventOperations.getEvents(queryParametersDTO);
        String username;
        Map<Long, EventState> eventStates = null;
        if(securityContext.getUserPrincipal()!= null && securityContext.getUserPrincipal().getName()!= null) {
            username = securityContext.getUserPrincipal().getName();
        Set<Long> eventIds = events.stream().map(event -> event.id).collect(Collectors.toSet());
        eventStates = eventOperations.getEventStatus(username, eventIds);
        }
        Map<Long, EventState> finalEventStates = eventStates;
        List<OutgoingEventDTOMobile> outgoingEventDTOMobiles = events.stream().map(event -> {
            LocalDateTime currentTime = LocalDateTime.now();
            Optional<Performance> nextPerformance = event.getPerformances().stream().filter(performance -> !performance.isCancelled())
                    .filter(performance -> performance.getDatetime() != null)
                    .filter(performance -> performance.getDatetime().isAfter(currentTime))
                    .min(Comparator.comparing(performance -> performance.getDatetime()));
            OutgoingEventDTOMobile outgoingEventDTOMobile = OutgoingEventDTOMobile.Converter.toDTO(event);
            if(finalEventStates != null && finalEventStates.containsKey(event.id))
                outgoingEventDTOMobile.eventState = finalEventStates.get(event.id);
            if (nextPerformance.isPresent()) {
                Performance performance = nextPerformance.get();
                outgoingEventDTOMobile.nextPerformance = OutgoingEventNextPerformanceDTOMobile.Converter.toDTO(performance);
            }
            return outgoingEventDTOMobile;
        }).collect(Collectors.toList());

        int active = 1;
        TemplateInstance templateInstance = stuecke.data("events", outgoingEventDTOMobiles, "queryParameters", queryParametersDTO, "active", active);
    String html = templateInstance.render();
    return Response.ok().entity(html).build();
    }
}
