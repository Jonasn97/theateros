package de.hsos.swa.jonas.theater.eventmanagement.boundary.resources.mobile;

import de.hsos.swa.jonas.theater.eventmanagement.boundary.dto.OutgoingEventDTO;
import de.hsos.swa.jonas.theater.eventmanagement.boundary.dto.OutgoingNextPerformanceDTO;
import de.hsos.swa.jonas.theater.eventmanagement.boundary.dto.QueryParametersDTO;
import de.hsos.swa.jonas.theater.eventmanagement.control.EventOperations;
import de.hsos.swa.jonas.theater.shared.Performance;
import de.hsos.swa.jonas.theater.shared.Event;
import de.hsos.swa.jonas.theater.shared.EventState;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.qute.Template;
import io.vertx.core.eventbus.EventBus;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
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
                               @QueryParam("filter[playType]") ArrayList<String> playTypeFilter,
                               @QueryParam("filter[performanceType]") ArrayList<String> performanceTypeFilter,
                               @QueryParam("filter[startDateTime]") String startDateTimeFilter,
                               @QueryParam("filter[endDateTime]") String endDateTimeFilter,
                               @QueryParam("include") String include,
                               @DefaultValue(FIRSTPAGE_STRING)@QueryParam("page[number]") Long pageNumber,
                               @DefaultValue("10")@QueryParam("page[size]") Long pageSize,@Context SecurityContext securityContext) {
        QueryParametersDTO queryParametersDTO = new QueryParametersDTO(nameFilter, statusFilter, playTypeFilter, performanceTypeFilter, startDateTimeFilter, endDateTimeFilter, include, pageNumber, pageSize);
        Collection<Event> events = eventOperations.getEvents(queryParametersDTO);
        String username;
        Map<Long, EventState> eventStates = null;
        if(securityContext.getUserPrincipal()!= null && securityContext.getUserPrincipal().getName()!= null) {
            username = securityContext.getUserPrincipal().getName();
        Set<Long> eventIds = events.stream().map(event -> event.id).collect(Collectors.toSet());
        eventStates = eventOperations.getEventStatus(username, eventIds);
        }
        Map<Long, EventState> finalEventStates = eventStates;
        List<OutgoingEventDTO> playDTOS = events.stream().map(play -> {
            LocalDateTime currentTime = LocalDateTime.now();
            //find next performance with date and time
            Optional<Performance> nextPerformance = play.performances.stream().filter(performance -> !performance.isCancelled) // Filtere abgesagte Vorstellungen aus
                    .filter(performance -> performance.datetime != null) // Filtere Vorstellungen ohne datetime aus
                    .filter(performance -> performance.datetime.isAfter(currentTime)) // Filtere vergangene Vorstellungen aus
                    .min(Comparator.comparing(performance -> performance.datetime));
            OutgoingEventDTO outgoingEventDTO = OutgoingEventDTO.Converter.toDTO(play);
            if(finalEventStates != null)
                outgoingEventDTO.eventState = finalEventStates.get(play.id);
            if (nextPerformance.isPresent()) {
                Performance performance = nextPerformance.get();
                outgoingEventDTO.nextPerformance = OutgoingNextPerformanceDTO.Converter.toDTO(performance);
            }
            return outgoingEventDTO;
        }).collect(Collectors.toList());

        int active = 1;
        TemplateInstance templateInstance = stuecke.data("events", playDTOS, "queryParameters", queryParametersDTO, "active", active);
    String html = templateInstance.render();
    return Response.ok().entity(html).build();
    }
}
