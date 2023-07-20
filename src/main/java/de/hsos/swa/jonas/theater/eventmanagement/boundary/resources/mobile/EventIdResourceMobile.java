package de.hsos.swa.jonas.theater.eventmanagement.boundary.resources.mobile;

import de.hsos.swa.jonas.theater.eventmanagement.boundary.dto.mobile.OutgoingEventIdDTOMobile;
import de.hsos.swa.jonas.theater.eventmanagement.control.EventOperations;
import de.hsos.swa.jonas.theater.eventmanagement.entity.Event;
import de.hsos.swa.jonas.theater.shared.EventState;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.constraints.Positive;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.Map;
import java.util.Optional;

@Produces(MediaType.TEXT_HTML)
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@Path("mobile/events")
public class EventIdResourceMobile {
    @Inject
    EventOperations eventOperations;

    @Inject
    Template details;
    @Path("/{eventId}")
    @GET
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public Response getEventById(@Positive @PathParam("eventId") long playId,
                                 @QueryParam("include") String include, @HeaderParam("Referer") String referrer, @Context SecurityContext securityContext){
        Optional<Event> play = eventOperations.getEventById(playId);
        String username;
        Map<Long, EventState> eventStates = null;

        if(play.isPresent()){
            OutgoingEventIdDTOMobile outgoingEventIdDTOMobile = OutgoingEventIdDTOMobile.Converter.toDTO(play.get());
            if(securityContext.getUserPrincipal()!= null && securityContext.getUserPrincipal().getName()!= null) {
                username = securityContext.getUserPrincipal().getName();
                Optional<EventState> eventState = eventOperations.getEventStatus(username, play.get().id);
                eventState.ifPresent(outgoingEventIdDTOMobile::setEventState);
            }
            int active=1;
        if(referrer != null) {
            if(referrer.contains("deins"))
                active = 0;
            if(referrer.contains("events"))
                active = 1;
            if(referrer.contains("performances"))
                active = 2;
        }
        TemplateInstance instance = details.data("event", outgoingEventIdDTOMobile, "referrer",referrer, "active", active);
        String html = instance.render();
        return Response.ok().entity(html).build();
        }
        //TODO: Errorhandling
        return null;
    }
}
