package de.hsos.swa.jonas.theater.playmanagement.boundary.resources.api;

import de.hsos.swa.jonas.theater.playmanagement.control.PlayOperations;
import de.hsos.swa.jonas.theater.shared.dto.LinksDTO;
import de.hsos.swa.jonas.theater.shared.Play;
import de.hsos.swa.jonas.theater.playmanagement.boundary.dto.InitialPlayDTO;
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
@Path("/play")
public class PlayIdResourceApi {

    @Inject
    PlayOperations playOperations;

    @Context
    UriInfo uriInfo;

    @Path("/{playId}")
    @GET
    public Response getPlayById(@PathParam("playId") long playId,
                                @QueryParam("include") String include){
        Optional<Play> play = playOperations.getPlayById(playId);
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
        return null;
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
}
