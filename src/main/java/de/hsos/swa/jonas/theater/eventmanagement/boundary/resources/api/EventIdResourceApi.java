package de.hsos.swa.jonas.theater.eventmanagement.boundary.resources.api;

import de.hsos.swa.jonas.theater.eventmanagement.boundary.dto.api.OutgoingEventIdDTOApi;
import de.hsos.swa.jonas.theater.eventmanagement.control.EventOperations;
import de.hsos.swa.jonas.theater.shared.dto.jsonapi.LinksDTO;
import de.hsos.swa.jonas.theater.eventmanagement.entity.Event;
import de.hsos.swa.jonas.theater.shared.dto.jsonapi.ResourceObjectDTO;
import de.hsos.swa.jonas.theater.shared.dto.jsonapi.ResponseWrapperDTO;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.Optional;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("api/events/")
public class EventIdResourceApi {

    @Inject
    EventOperations eventOperations;

    @Context
    UriInfo uriInfo;

    @Path("{eventId}")
    @GET
    public Response getEventsById(@PathParam("eventId") long eventId){
        Optional<Event> play = eventOperations.getEventsById(eventId);
        ResponseWrapperDTO<Object> responseWrapperDTO = new ResponseWrapperDTO<>();
        if(play.isPresent()){
            OutgoingEventIdDTOApi outgoingEventIdDTOApi = OutgoingEventIdDTOApi.Converter.toDTO(play.get());
            ResourceObjectDTO<OutgoingEventIdDTOApi> resourceObjectDTO = new ResourceObjectDTO<>();
            resourceObjectDTO.id = String.valueOf(play.get().id);
            resourceObjectDTO.type = "play";
            resourceObjectDTO.links = createSelfLink(resourceObjectDTO.id);
            resourceObjectDTO.attributes = outgoingEventIdDTOApi;
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
