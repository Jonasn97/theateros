package de.hsos.swa.jonas.theater.eventmanagement.boundary.resources.api;

import de.hsos.swa.jonas.theater.eventmanagement.boundary.dto.QueryParametersDTO;
import de.hsos.swa.jonas.theater.eventmanagement.boundary.dto.api.OutgoingPerformanceDTOApi;
import de.hsos.swa.jonas.theater.eventmanagement.control.PerformanceOperations;
import de.hsos.swa.jonas.theater.eventmanagement.entity.Performance;
import de.hsos.swa.jonas.theater.shared.LinkBuilder;
import de.hsos.swa.jonas.theater.shared.dto.jsonapi.*;
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
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.Collection;

@Path("/api/performances")
public class PerformanceResourceApi {
    private final static long FIRSTPAGE = 0;
    private final static String FIRSTPAGE_STRING = "0";
    @Inject
    PerformanceOperations performanceOperations;
    @Inject
    LinkBuilder linkBuilder;
    @Context
    UriInfo uriInfo;
    @GET
    @Transactional(Transactional.TxType.REQUIRES_NEW)
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
                                    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}")
                                        @QueryParam("filter[performanceType]") ArrayList<String> performanceTypeFilter,
                                    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}")
                                        @QueryParam("filter[startDateTime]") String startDateTimeFilter,
                              @QueryParam("filter[endDateTime]") String endDateTimeFilter,
                              @QueryParam("include") String include,
                                    @PositiveOrZero @DefaultValue(FIRSTPAGE_STRING)@QueryParam("page[number]") Long pageNumber,
                                    @Max(50) @Positive @DefaultValue("10")@QueryParam("page[size]") Long pageSize){
        QueryParametersDTO queryParametersDTO = new QueryParametersDTO(nameFilter, statusFilter, kindFilter, performanceTypeFilter, startDateTimeFilter, endDateTimeFilter, include, pageNumber, pageSize);
        Collection<Performance> performances = performanceOperations.getPerformances(queryParametersDTO);
        ResponseWrapperDTO<Object> responseWrapperDTO = new ResponseWrapperDTO<>();
        if(performances.isEmpty()) {
            responseWrapperDTO.errors = new ArrayList<>();
            responseWrapperDTO.errors.add(new ErrorDTO("404", "PERF:1","No performances found", "Couldn't find any performances with the given filters"));
            return Response.status(Response.Status.NOT_FOUND).entity(responseWrapperDTO).build();
        }

        responseWrapperDTO.data = performances.stream()
                .map( performance -> {
                    String id = String.valueOf(performance.id);
                    String type = "performance";

                    LinksDTO linksDTO = linkBuilder.createSelfLink(PerformanceResourceApi.class, uriInfo, id);
                    OutgoingPerformanceDTOApi outgoingEventDTOApi = OutgoingPerformanceDTOApi.Converter.toDTO(performance);
                    return new ResourceObjectDTO<>(id, type, outgoingEventDTOApi, null, linksDTO);
                })
                .toList();
        long maxSize = performanceOperations.getPerformancesCount(queryParametersDTO);
        responseWrapperDTO.links = linkBuilder.createPaginationLinks(PerformanceResourceApi.class, uriInfo, queryParametersDTO, maxSize);
        return Response.ok().entity(responseWrapperDTO).build();
    }


    @Path("/fallback")
    public Response getPerformancesFallback(@QueryParam("filter[name]") String nameFilter,
                                      @QueryParam("filter[status]") ArrayList<String> statusFilter,
                                      @QueryParam("filter[kind]") ArrayList<String> kindFilter,
                                      @QueryParam("filter[performanceType]") ArrayList<String> performanceTypeFilter,
                                            @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}")
                                                @QueryParam("filter[startDateTime]") String startDateTimeFilter,
                                            @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}")
                                                @QueryParam("filter[endDateTime]") String endDateTimeFilter,
                                      @QueryParam("include") String include,
                                            @PositiveOrZero @DefaultValue(FIRSTPAGE_STRING)@QueryParam("page[number]") Long pageNumber,
                                            @Max(50) @Positive @DefaultValue("10")@QueryParam("page[size]") Long pageSize) {
        ResponseWrapperDTO<ErrorDTO> responseWrapperDTO = new ResponseWrapperDTO<>();
        responseWrapperDTO.errors = new ArrayList<>();
        responseWrapperDTO.errors.add(new ErrorDTO("500", "PERF:2","Internal Server Error", "Something went wrong while processing your request"));
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseWrapperDTO).build();
    }

}
