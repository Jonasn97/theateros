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
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import java.time.LocalDateTime;
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
                                    @QueryParam("filter[kind]") ArrayList<String> kindFilter,
                                    @QueryParam("filter[performanceType]") ArrayList<String> performanceTypeFilter,
                                    @QueryParam("filter[startDateTime]") String startDateTimeFilter,
                                    @Pattern(regexp = "7days|30days")@QueryParam("filter[endDateTime]") String endDateTimeFilter,
                                    @QueryParam("include") String include,
                                    @PositiveOrZero @DefaultValue(FIRSTPAGE_STRING)@QueryParam("page[number]") Long pageNumber,
                                    @Positive @Max(50) @DefaultValue("10")@QueryParam("page[size]") Long pageSize,
                                    @Context SecurityContext securityContext){
        if(endDateTimeFilter!=null &&endDateTimeFilter.equals("7days")) {
            endDateTimeFilter = String.valueOf(LocalDateTime.now().plusDays(7));
            startDateTimeFilter = String.valueOf(LocalDateTime.now());
        } else if(endDateTimeFilter!=null &&endDateTimeFilter.equals("30days")) {
            endDateTimeFilter = String.valueOf(LocalDateTime.now().plusDays(30));
            startDateTimeFilter = String.valueOf(LocalDateTime.now());
        }
        QueryParametersDTO queryParametersDTO = new QueryParametersDTO(nameFilter, statusFilter, kindFilter, performanceTypeFilter, startDateTimeFilter, endDateTimeFilter, include, pageNumber, pageSize);
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
}
