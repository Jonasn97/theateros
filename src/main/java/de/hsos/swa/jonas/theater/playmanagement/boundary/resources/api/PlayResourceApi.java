package de.hsos.swa.jonas.theater.playmanagement.boundary.resources.api;

import de.hsos.swa.jonas.theater.playmanagement.boundary.dto.InitialPlayDTO;
import de.hsos.swa.jonas.theater.playmanagement.boundary.dto.QueryParametersDTO;
import de.hsos.swa.jonas.theater.playmanagement.control.PlayOperations;
import de.hsos.swa.jonas.theater.shared.*;
import io.quarkus.logging.Log;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
//2023-07-01T10:15:30
//2023-06-27T10:15:30
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/play")
public class PlayResourceApi {
    private final static long FIRSTPAGE = 0;
    private final static String FIRSTPAGESTRING = "0";

    @Inject
    PlayOperations playOperations;
    @Context
    UriInfo uriInfo;


    @GET
    public Response getPlays(@QueryParam("filter[name]") String nameFilter,
                             @QueryParam("filter[status]") ArrayList<String> statusFilter,
                             @QueryParam("filter[playType]") ArrayList<String> playTypeFilter,
                             @QueryParam("filter[performanceType]") ArrayList<String> performanceTypeFilter,
                             @QueryParam("filter[startDateTime]") String startDateTimeFilter,
                             @QueryParam("filter[endDateTime]") String endDateTimeFilter,
                             @QueryParam("include") String include,
                             @DefaultValue(FIRSTPAGESTRING)@QueryParam("page[number]") Long pageNumber,
                             @DefaultValue("10")@QueryParam("page[size]") Long pageSize){
        LocalDateTime parsedStartDateTime = null;
        LocalDateTime parsedEndDateTime = null;
        if(startDateTimeFilter != null&& !startDateTimeFilter.isEmpty()){
            parsedStartDateTime = LocalDateTime.parse(startDateTimeFilter);
        }
        if(endDateTimeFilter != null&& !endDateTimeFilter.isEmpty()){
            parsedEndDateTime = LocalDateTime.parse(endDateTimeFilter);
        }
        if (parsedEndDateTime != null && parsedStartDateTime!= null&& parsedEndDateTime.isBefore(parsedStartDateTime)) {
            LocalDateTime temp = parsedEndDateTime;
            parsedEndDateTime = parsedStartDateTime;
            parsedStartDateTime = temp;
        }
        QueryParametersDTO queryParametersDTO = new QueryParametersDTO(nameFilter, statusFilter, playTypeFilter, performanceTypeFilter, parsedStartDateTime, parsedEndDateTime, include, pageNumber, pageSize);

        Log.info(startDateTimeFilter);
        Log.info(endDateTimeFilter);
        Collection<Play> plays = playOperations.getPlays(queryParametersDTO);
        ResponseWrapperDTO responseWrapperDTO = new ResponseWrapperDTO();
        if(plays.isEmpty()) {
            List<ErrorDTO> errors = new ArrayList<>();
            errors.add(new ErrorDTO("404", "PLAYS:1","No plays found", "Couldn't find any plays with the given filters"));
            return Response.status(Response.Status.NOT_FOUND).entity(errors).build();
        }

        List<ResourceObjectDTO<InitialPlayDTO>> resourceObjectDTOList = plays.stream()
                .map( play -> {
                    String id = String.valueOf(play.id);
                    String type = "play";
                    LinksDTO linksDTO = createSelfLink(id);
                    InitialPlayDTO initialPlayDTO = InitialPlayDTO.Converter.toDTO(play);
                    return new ResourceObjectDTO<>(id, type, initialPlayDTO, linksDTO);
                })
                .toList();
        responseWrapperDTO.data = resourceObjectDTOList;
        responseWrapperDTO.links = createPaginationLinks(queryParametersDTO);
        return Response.ok().entity(responseWrapperDTO).build();
    }

    private LinksDTO createSelfLink(String id) {
        LinksDTO linksDTO = new LinksDTO();
        linksDTO.self = uriInfo.getBaseUriBuilder()
                .path(PlayResourceApi.class)
                .path(id)
                .build()
                .toString();
        return linksDTO;
    }
    private LinksDTO createPaginationLinks(QueryParametersDTO queryParametersDTO) {
        Log.info("PageNumber: " + queryParametersDTO.pageNumber);
        Log.info("PageSize: " + queryParametersDTO.pageSize);
        Long pageNumber = queryParametersDTO.pageNumber;
        Long pageSize = queryParametersDTO.pageSize;
        long maxSize = playOperations.getPlaysCount(queryParametersDTO);
        Log.info("Size: " + maxSize);
        LinksDTO linksDTO = new LinksDTO();
        UriBuilder uriBuilder = uriInfo.getBaseUriBuilder()
                .path(PlayResourceApi.class);
        linksDTO.first = uriBuilder
                .queryParam("page[number]", FIRSTPAGE)
                .queryParam("page[size]", pageSize)
                .build()
                .toString();
        if(pageNumber>FIRSTPAGE)
            linksDTO.prev = uriBuilder
                    .replaceQueryParam("page[number]", pageNumber-1)
                    .build()
                    .toString();
        else
            linksDTO.prev = "";
        if((pageNumber+1) * pageSize < maxSize)
            linksDTO.next = uriBuilder
                .replaceQueryParam("page[number]", pageNumber+1)
                .build()
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
