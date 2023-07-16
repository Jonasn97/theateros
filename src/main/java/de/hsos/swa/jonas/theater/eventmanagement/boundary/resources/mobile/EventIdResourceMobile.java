package de.hsos.swa.jonas.theater.eventmanagement.boundary.resources.mobile;

import de.hsos.swa.jonas.theater.eventmanagement.boundary.dto.InitialPlayDTO;
import de.hsos.swa.jonas.theater.eventmanagement.boundary.dto.OutgoingDetailEventDTO;
import de.hsos.swa.jonas.theater.eventmanagement.boundary.dto.OutgoingEventDTO;
import de.hsos.swa.jonas.theater.eventmanagement.control.EventOperations;
import de.hsos.swa.jonas.theater.shared.Event;
import de.hsos.swa.jonas.theater.shared.dto.ResourceObjectDTO;
import de.hsos.swa.jonas.theater.shared.dto.ResponseWrapperDTO;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;

@Produces(MediaType.TEXT_HTML)
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
public class EventIdResourceMobile {
    @Inject
    EventOperations eventOperations;

    @Inject
    Template details;

    @GET
    public Response getEventById(@PathParam("eventId") long playId,
                                 @QueryParam("include") String include){
        Optional<Event> play = eventOperations.getEventsById(playId);
        if(play.isPresent()){
        OutgoingDetailEventDTO outgoingDetailEventDTO = OutgoingDetailEventDTO.Converter.toDTO(play.get());
            TemplateInstance instance = details.data("event", outgoingDetailEventDTO);
            String html = instance.render();
            return Response.ok().entity(html).build();
        }
        //TODO: Errorhandling
        return null;
    }
}
