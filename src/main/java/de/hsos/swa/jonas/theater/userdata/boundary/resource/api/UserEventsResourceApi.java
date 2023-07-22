package de.hsos.swa.jonas.theater.userdata.boundary.resource.api;

import de.hsos.swa.jonas.theater.eventmanagement.boundary.resources.api.EventResourceApi;
import de.hsos.swa.jonas.theater.shared.LinkBuilder;
import de.hsos.swa.jonas.theater.shared.dto.jsonapi.*;
import de.hsos.swa.jonas.theater.userdata.boundary.dto.UserParametersDTO;
import de.hsos.swa.jonas.theater.userdata.boundary.dto.api.OutgoingUserEventDTOApi;
import de.hsos.swa.jonas.theater.userdata.control.UserDataOperations;
import de.hsos.swa.jonas.theater.userdata.entity.UserEvent;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.constraints.Max;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.Collection;

@Path("api/user/eventstates")
@Transactional(Transactional.TxType.REQUIRES_NEW)
@RolesAllowed("user")
public class UserEventsResourceApi {

    @Inject
    UserDataOperations userDataOperations;
    @Inject
    LinkBuilder linkBuilder;

    @GET
    public Response getEventsForUser(@PositiveOrZero @DefaultValue("0")@QueryParam("page[number]") Long pageNumber,
                                     @Max(50) @Positive @DefaultValue("10")@QueryParam("page[size]") Long pageSize,
                                             @Context SecurityContext securityContext,
                                             @Context UriInfo uriInfo){
        ResponseWrapperDTO<Object> responseWrapperDTO = new ResponseWrapperDTO<>();
        if(securityContext==null || securityContext.getUserPrincipal() == null) {
            responseWrapperDTO.errors = new ArrayList<>();
            responseWrapperDTO.errors.add(new ErrorDTO("401", "USER_ES:1","Not Authorized", "You are not authorized to access this resource"));
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        String username = securityContext.getUserPrincipal().getName();
        UserParametersDTO userParametersDTO = new UserParametersDTO(pageNumber, pageSize,username);
        Collection<UserEvent> userEvents = userDataOperations.getUserEventsForUser(userParametersDTO);
        if(userEvents.isEmpty()) {
            responseWrapperDTO.errors = new ArrayList<>();
            responseWrapperDTO.errors.add(new ErrorDTO("404", "USER_ES:2","No userevents found", "Couldn't find any events for this user"));
            return Response.status(Response.Status.NOT_FOUND).entity(responseWrapperDTO).build();
        }
        responseWrapperDTO.data = userEvents.stream()
                .map( userEvent -> {
                    String id = String.valueOf(userEvent.id);
                    String type = "userevent";

                    LinksDTO linksDTO = linkBuilder.createSelfLink(EventResourceApi.class, uriInfo, id);
                    OutgoingUserEventDTOApi outgoingEventDTOApi = OutgoingUserEventDTOApi.Converter.toDTO(userEvent);
                    RelationshipDTO<Object> relationshipDTO = linkBuilder.addRelationship(EventResourceApi.class, uriInfo, userEvent.id, "userevent");
                    return new ResourceObjectDTO<>(id, type, outgoingEventDTOApi, relationshipDTO, linksDTO);
                })
                .toList();
        long maxSize = userDataOperations.getUserEventsForUserCount(userParametersDTO);
        responseWrapperDTO.links = linkBuilder.createPaginationLinks(UserEventsResourceApi.class, uriInfo, pageNumber, pageSize, maxSize);
        return Response.ok().entity(responseWrapperDTO).build();
    }
}
