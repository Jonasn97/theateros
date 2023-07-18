package de.hsos.swa.jonas.theater.eventmanagement.boundary.resources.mobile;

import de.hsos.swa.jonas.theater.eventmanagement.boundary.dto.PerformanceEventDTO;
import de.hsos.swa.jonas.theater.eventmanagement.boundary.dto.QueryParametersDTO;
import de.hsos.swa.jonas.theater.eventmanagement.control.EventOperations;
import de.hsos.swa.jonas.theater.shared.Performance;
import io.quarkus.qute.Template;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Location;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Path("mobile")
public class PerformanceResourceMobile {

    private final static String FIRSTPAGE_STRING = "0";
    @Inject
    EventOperations eventOperations;

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
                                    @DefaultValue(FIRSTPAGE_STRING)@QueryParam("page[number]") Long pageNumber,
                                    @DefaultValue("10")@QueryParam("page[size]") Long pageSize){
        QueryParametersDTO queryParametersDTO = new QueryParametersDTO(nameFilter, statusFilter, playTypeFilter, performanceTypeFilter, startDateTimeFilter, endDateTimeFilter, include, pageNumber, pageSize);
        Collection<Performance> performances = eventOperations.getPerformances(queryParametersDTO);
        Collection<PerformanceEventDTO> performanceEventDTOS = performances.stream().map(PerformanceEventDTO.Converter::toDTO).collect(java.util.stream.Collectors.toList());
        String html = spielzeiten.data("performances", performanceEventDTOS, "queryParameters", queryParametersDTO).render();
        return Response.ok().entity(html).build();
    }
    @Path("/performances/{id}")
    @GET
    @Produces("text/calendar")
    public Response getCalenderFile(@PathParam("id") Long id){
        Calendar calenderFile = new Calendar();
        Optional<Performance> optionalPerformance = eventOperations.getPerformance(id);
        if(optionalPerformance.isEmpty()){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        Performance performance = optionalPerformance.get();
        String eventName = performance.event.title;
        Location eventLocation =new Location(performance.event.location);
        Date date = new Date(performance.datetime.toEpochSecond(java.time.ZoneOffset.UTC));
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
