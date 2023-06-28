package de.hsos.swa.jonas.theater.playmanagement.boundary.resources.api;

import de.hsos.swa.jonas.theater.playmanagement.boundary.dto.InitialPlayDTO;
import de.hsos.swa.jonas.theater.playmanagement.control.PlayOperations;
import de.hsos.swa.jonas.theater.shared.*;
import io.quarkus.logging.Log;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
//2023-07-01T10:15:30
//2023-06-27T10:15:30
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/play")
public class PlayResource {

    @Inject
    PlayOperations playOperations;


    @GET
    public Response getPlays(@QueryParam("filter[name]") String nameFilter,
                             @QueryParam("filter[status]") ArrayList<String> statusFilter,
                             @QueryParam("filter[playType]") ArrayList<String> playTypeFilter,
                             @QueryParam("filter[performanceType]") ArrayList<String> performanceTypeFilter,
                             @QueryParam("filter[startDateTime]") String startDateTimeFilter,
                             @QueryParam("filter[endDateTime]") String endDateTimeFilter,
                             @QueryParam("include") String include,
                             @DefaultValue("1")@QueryParam("page[number]") Long pageNumber,
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
        Log.info(startDateTimeFilter);
        Log.info(endDateTimeFilter);
        Collection<Play> plays = playOperations.getPlays(nameFilter, statusFilter, playTypeFilter, performanceTypeFilter, parsedStartDateTime, parsedEndDateTime, include, pageNumber-1, pageSize);
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
                    LinkDTO linkDTO = createLink(type, id);
                    InitialPlayDTO initialPlayDTO = InitialPlayDTO.Converter.toDTO(play);
                    return new ResourceObjectDTO<>(id, type, initialPlayDTO, linkDTO);
                })
                .toList();
        responseWrapperDTO.data = resourceObjectDTOList;
        responseWrapperDTO.links = new LinksDTO();
        responseWrapperDTO.links.first = "/play?page[number]=1&page[size]="+pageSize;
        if(pageNumber > 1)
            responseWrapperDTO.links.prev = "/play?page[number]="+ (pageNumber-1) +"&page[size]="+pageSize;
        else
            responseWrapperDTO.links.prev = "";
        if(pageNumber * pageSize < resourceObjectDTOList.size())
            responseWrapperDTO.links.next = "/play?page[number]="+ (pageNumber+1) +"&page[size]="+pageSize;
        else
            responseWrapperDTO.links.next = "";
        responseWrapperDTO.links.last = "/play?page[number]="+pageNumber+"&page[size]="+pageSize;
        return Response.ok().entity(responseWrapperDTO).build();
    }

    private LinkDTO createLink(String type, String id) {
        return null;
    }


}
