package de.hsos.swa.jonas.theater.eventmanagement.boundary.resources.api;

import de.hsos.swa.jonas.theater.eventmanagement.control.EventOperations;
import de.hsos.swa.jonas.theater.shared.dto.LinksDTO;
import de.hsos.swa.jonas.theater.shared.Event;
import de.hsos.swa.jonas.theater.eventmanagement.boundary.dto.InitialPlayDTO;
import de.hsos.swa.jonas.theater.shared.dto.ResourceObjectDTO;
import de.hsos.swa.jonas.theater.shared.dto.ResponseWrapperDTO;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.Optional;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EventIdResourceApi {

    @Inject
    EventOperations eventOperations;

    @Context
    UriInfo uriInfo;

    @Path("events/{eventId}")
    @GET
    public Response getEventsById(@PathParam("eventId") long playId,
                                  @QueryParam("include") String include){
        Optional<Event> play = eventOperations.getEventsById(playId);
        ResponseWrapperDTO<Object> responseWrapperDTO = new ResponseWrapperDTO<>();
        if(play.isPresent()){
            InitialPlayDTO initialPlayDTO = InitialPlayDTO.Converter.toDTO(play.get());
            ResourceObjectDTO<InitialPlayDTO> resourceObjectDTO = new ResourceObjectDTO<>();
            resourceObjectDTO.id = String.valueOf(play.get().id);
            resourceObjectDTO.type = "play";
            resourceObjectDTO.links = createSelfLink(resourceObjectDTO.id);
            resourceObjectDTO.attributes = initialPlayDTO;
            responseWrapperDTO.data=resourceObjectDTO;
            return Response.ok().entity(responseWrapperDTO).build();
        }
        //TODO: Errorhandling
        return null;
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
}
