package de.hsos.swa.jonas.theater.eventmanagement.boundary.resources.api;

import de.hsos.swa.jonas.theater.eventmanagement.boundary.dto.api.OutgoingEventDTOApi;
import de.hsos.swa.jonas.theater.eventmanagement.boundary.dto.QueryParametersDTO;
import de.hsos.swa.jonas.theater.eventmanagement.control.EventOperations;
import de.hsos.swa.jonas.theater.eventmanagement.entity.Event;
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
                              @QueryParam("filter[eventType]") ArrayList<String> playTypeFilter,
                              @QueryParam("filter[performanceType]") ArrayList<String> performanceTypeFilter,
                              @QueryParam("filter[startDateTime]") String startDateTimeFilter,
                              @QueryParam("filter[endDateTime]") String endDateTimeFilter,
                              @QueryParam("include") String include,
                              @PositiveOrZero @DefaultValue(FIRSTPAGE_STRING)@QueryParam("page[number]") Long pageNumber,
                              @Max(50) @Positive @DefaultValue("10")@QueryParam("page[size]") Long pageSize){
        QueryParametersDTO queryParametersDTO = new QueryParametersDTO(nameFilter, statusFilter, playTypeFilter, performanceTypeFilter, startDateTimeFilter, endDateTimeFilter, include, pageNumber, pageSize);
        if (include != null && include.contains("performance")) {

        }
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

                    LinksDTO linksDTO = createSelfLink(id);
                    OutgoingEventDTOApi outgoingEventDTOApi = OutgoingEventDTOApi.Converter.toDTO(play);
                    RelationshipDTO<Object> relationshipDTO = addRelationship(play.id, "performances");
                    return new ResourceObjectDTO<>(id, type, outgoingEventDTOApi, relationshipDTO, linksDTO);
                })
                .toList();
        responseWrapperDTO.links = createPaginationLinks(queryParametersDTO);
        return Response.ok().entity(responseWrapperDTO).build();
    }



    public Response getEventsFallback(@QueryParam("filter[name]") String nameFilter,
                                     @QueryParam("filter[status]") ArrayList<String> statusFilter,
                                     @QueryParam("filter[eventType]") ArrayList<String> playTypeFilter,
                                     @QueryParam("filter[performanceType]") ArrayList<String> performanceTypeFilter,
                                     @QueryParam("filter[startDateTime]") String startDateTimeFilter,
                                     @QueryParam("filter[endDateTime]") String endDateTimeFilter,
                                     @QueryParam("include") String include,
                                     @DefaultValue(FIRSTPAGE_STRING)@QueryParam("page[number]") Long pageNumber,
                                     @DefaultValue("10")@QueryParam("page[size]") Long pageSize) {
        ResponseWrapperDTO<ErrorDTO> responseWrapperDTO = new ResponseWrapperDTO<>();
        responseWrapperDTO.errors = new ArrayList<>();
        responseWrapperDTO.errors.add(new ErrorDTO("500", "EVENTS:2","Internal Server Error", "Something went wrong while processing your request"));
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseWrapperDTO).build();
    }
    private RelationshipDTO<Object> addRelationship(long id, String relationship) {
        LinksDTO linksDTO = createRelationshipLink(String.valueOf(id),relationship);
        RelationshipDTO<Object> relationshipDTO = new RelationshipDTO<>();
        relationshipDTO.links = linksDTO;
        return relationshipDTO;
    }
    private LinksDTO createSelfLink(String id) {
        LinksDTO linksDTO = new LinksDTO();
        linksDTO.self = uriInfo.getBaseUriBuilder()
                .path(EventResourceApi.class)
                .path(id)
                .build()
                .toString();
        return linksDTO;
    }
    private LinksDTO createRelationshipLink(String id, String relationship) {
        LinksDTO linksDTO = new LinksDTO();
        linksDTO.related = uriInfo.getBaseUriBuilder()
                .path(EventResourceApi.class)
                .path(id)
                .path(relationship)
                .build()
                .toString();
        return linksDTO;
    }
    private LinksDTO createPaginationLinks(QueryParametersDTO queryParametersDTO) {
        Log.info("PageNumber: " + queryParametersDTO.pageNumber);
        Log.info("PageSize: " + queryParametersDTO.pageSize);
        Long pageNumber = queryParametersDTO.pageNumber;
        Long pageSize = queryParametersDTO.pageSize;
        long maxSize = eventOperations.getEventsCount(queryParametersDTO);
        Log.info("Size: " + maxSize);
        LinksDTO linksDTO = new LinksDTO();
        UriBuilder uriBuilder = uriInfo.getBaseUriBuilder()
                .path(EventResourceApi.class);
        linksDTO.first = uriBuilder
                .queryParam("page[number]", FIRSTPAGE)
                .queryParam("page[size]", pageSize)
                .build()
                .toString();
        if(pageNumber>FIRSTPAGE)
            linksDTO.prev = uriBuilder
                    .replaceQueryParam("page[number]", "{pageNumber}")
                    .build(pageNumber-1)
                    .toString();
        else
            linksDTO.prev = "";
        if((pageNumber+1) * pageSize < maxSize)
            linksDTO.next = uriBuilder
                .replaceQueryParam("page[number]", "{pageNumber}")
                .build(pageNumber+1)
                .toString();
        else
            linksDTO.next = "";
        linksDTO.last = uriBuilder
                .replaceQueryParam("page[number]", (maxSize/pageSize))
                .build()
                .toString();
        return linksDTO;
    }


}