package de.hsos.swa.jonas.theater.eventmanagement.boundary.resources.api;

import de.hsos.swa.jonas.theater.eventmanagement.boundary.dto.api.OutgoingEventDTOApi;
import de.hsos.swa.jonas.theater.eventmanagement.boundary.dto.QueryParametersDTO;
import de.hsos.swa.jonas.theater.eventmanagement.control.EventOperations;
import de.hsos.swa.jonas.theater.eventmanagement.entity.Event;
import de.hsos.swa.jonas.theater.shared.LinkBuilder;
import de.hsos.swa.jonas.theater.shared.dto.jsonapi.*;
import io.quarkus.logging.Log;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

import javax.inject.Inject;
import javax.validation.constraints.Max;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.ArrayList;
import java.util.Collection;
//2023-07-01T10:15:30
//2023-06-27T10:15:30
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("api/events")
public class EventResourceApi {
    private final static long FIRSTPAGE = 0;
    private final static String FIRSTPAGE_STRING = "0";

    @Inject
    EventOperations eventOperations;
    @Inject
    LinkBuilder linkBuilder;
    @Context
    UriInfo uriInfo;


    @GET
    @Retry
    @Timeout(5000)
    @Fallback(fallbackMethod = "getEventsFallback")
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.75, delay = 10000)
    @Operation(summary = "Get filtered Events", description = "Get filtered and paged Events")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Filtered Events are returned"),
            @APIResponse(responseCode = "400", description = "Bad Request"),
            @APIResponse(responseCode = "404", description = "No Events found for selected filter"),
            @APIResponse(responseCode = "500", description = "Internal Server Error")
    })
    public Response getEvents(@QueryParam("filter[name]") String nameFilter,
                              @QueryParam("filter[status]") ArrayList<String> statusFilter,
                              @QueryParam("filter[kind]") ArrayList<String> kindFilter,
                              @QueryParam("filter[performanceType]") ArrayList<String> performanceTypeFilter,
                              @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}") // yyyy-MM-ddTHH:mm:ss
                              @QueryParam("filter[startDateTime]") String startDateTimeFilter,
                              @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}") // yyyy-MM-ddTHH:mm:ss
                                @QueryParam("filter[endDateTime]") String endDateTimeFilter,
                              @QueryParam("include") String include,
                              @PositiveOrZero @DefaultValue(FIRSTPAGE_STRING)@QueryParam("page[number]") Long pageNumber,
                              @Max(50) @Positive @DefaultValue("10")@QueryParam("page[size]") Long pageSize){
        QueryParametersDTO queryParametersDTO = new QueryParametersDTO(nameFilter, statusFilter, kindFilter, performanceTypeFilter, startDateTimeFilter, endDateTimeFilter, include, pageNumber, pageSize);

        Collection<Event> events = eventOperations.getEvents(queryParametersDTO);
        ResponseWrapperDTO<Object> responseWrapperDTO = new ResponseWrapperDTO<>();
        if(events.isEmpty()) {
            responseWrapperDTO.errors = new ArrayList<>();
            responseWrapperDTO.errors.add(new ErrorDTO("404", "EVENTS:1","No events found", "Couldn't find any events with the given filters"));
            return Response.status(Response.Status.NOT_FOUND).entity(responseWrapperDTO).build();
        }

        responseWrapperDTO.data = events.stream()
                .map( play -> {
                    String id = String.valueOf(play.id);
                    String type = "play";

                    LinksDTO linksDTO = linkBuilder.createSelfLink(EventResourceApi.class, uriInfo, id);
                    OutgoingEventDTOApi outgoingEventDTOApi = OutgoingEventDTOApi.Converter.toDTO(play);
                    RelationshipDTO<Object> relationshipDTO = linkBuilder.addRelationship(EventResourceApi.class, uriInfo, play.id, "performances");
                    return new ResourceObjectDTO<>(id, type, outgoingEventDTOApi, relationshipDTO, linksDTO);
                })
                .toList();
        long maxSize = eventOperations.getEventsCount(queryParametersDTO);
        responseWrapperDTO.links = linkBuilder.createPaginationLinks(EventResourceApi.class, uriInfo, queryParametersDTO, maxSize);
        return Response.ok().entity(responseWrapperDTO).build();
    }


    @Path("/fallback")
    public Response getEventsFallback(@QueryParam("filter[name]") String nameFilter,
                                     @QueryParam("filter[status]") ArrayList<String> statusFilter,
                                     @QueryParam("filter[kind]") ArrayList<String> kindFilter,
                                     @QueryParam("filter[performanceType]") ArrayList<String> performanceTypeFilter,
                                     @QueryParam("filter[startDateTime]") String startDateTimeFilter,
                                     @QueryParam("filter[endDateTime]") String endDateTimeFilter,
                                     @QueryParam("include") String include,
                                      @PositiveOrZero @DefaultValue(FIRSTPAGE_STRING)@QueryParam("page[number]") Long pageNumber,
                                      @Max(50) @Positive @DefaultValue("10")@QueryParam("page[size]") Long pageSize) {
        ResponseWrapperDTO<ErrorDTO> responseWrapperDTO = new ResponseWrapperDTO<>();
        responseWrapperDTO.errors = new ArrayList<>();
        responseWrapperDTO.errors.add(new ErrorDTO("500", "EVENTS:2","Internal Server Error", "Something went wrong while processing your request"));
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseWrapperDTO).build();
    }
}