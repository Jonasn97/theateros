package de.hsos.swa.jonas.theater.eventmanagement.boundary.resources.mobile;

import de.hsos.swa.jonas.theater.eventmanagement.boundary.dto.mobile.OutgoingPerformanceEventDTOMobile;
import de.hsos.swa.jonas.theater.eventmanagement.boundary.dto.QueryParametersDTO;
import de.hsos.swa.jonas.theater.eventmanagement.control.PerformanceOperations;
import de.hsos.swa.jonas.theater.eventmanagement.entity.Performance;
import de.hsos.swa.jonas.theater.shared.PerformanceState;
import io.quarkus.qute.Template;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.constraints.Max;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Resource for performances view
 *
 */
@Path("mobile")
public class PerformanceResourceMobile {

    private final static String FIRSTPAGE_STRING = "0";
    @Inject
    PerformanceOperations performanceOperations;

    @Inject
    Template spielzeiten;

    /**
     * @param nameFilter Filter for title of event
     * @param statusFilter Filter for status of event
     * @param kindFilter Filter for kind of event
     * @param performanceTypeFilter Filter for type of performance
     * @param startDateTimeFilter Filter for start date of performance of a event.
     * @param endDateTimeFilter Filter for end date of performance of a event. Is either for the next 7days or 30days
     * @param include Filter for included relations
     * @param pageNumber Page number of the page
     * @param pageSize Size of the page
     * @param securityContext needed for showing a performance state of a user
     * @return Response with all filtered performances as a page
     */
    @Path("/performances")
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    @GET
    @Retry
    @Timeout(5000)
    @Fallback(fallbackMethod = "getPerformancesFallback")
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.75, delay = 10000)
    @Operation(summary = "Get filtered Performances", description = "Get filtered and paged Performances")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Filtered Performances are returned"),
            @APIResponse(responseCode = "400", description = "Bad Request"),
            @APIResponse(responseCode = "404", description = "No Performances found for selected filter"),
            @APIResponse(responseCode = "500", description = "Internal Server Error")
    })
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
        long maxSize = performanceOperations.getPerformancesCount(queryParametersDTO);
        boolean hasNextPage = (pageNumber + 1) * pageSize < maxSize;
        int active = 2;
        String html = spielzeiten.data("performances", outgoingPerformanceEventDTOMobiles, "queryParameters", queryParametersDTO, "active", active, "showMore", hasNextPage).render();
        return Response.ok().entity(html).build();
    }

    @Path("/performances/fallback")
    public Response getPerformancesFallback(@QueryParam("filter[name]") String nameFilter,
                                            @QueryParam("filter[status]") ArrayList<String> statusFilter,
                                            @QueryParam("filter[kind]") ArrayList<String> kindFilter,
                                            @QueryParam("filter[performanceType]") ArrayList<String> performanceTypeFilter,
                                            @QueryParam("filter[startDateTime]") String startDateTimeFilter,
                                            @Pattern(regexp = "7days|30days")@QueryParam("filter[endDateTime]") String endDateTimeFilter,
                                            @QueryParam("include") String include,
                                            @PositiveOrZero @DefaultValue(FIRSTPAGE_STRING)@QueryParam("page[number]") Long pageNumber,
                                            @Positive @Max(50) @DefaultValue("10")@QueryParam("page[size]") Long pageSize,
                                            @Context SecurityContext securityContext) {
        return Response.seeOther(URI.create("errors.html")).build();
    }
}
