package de.hsos.swa.jonas.theater.eventmanagement.boundary.resources.api;

import de.hsos.swa.jonas.theater.eventmanagement.boundary.dto.api.OutgoingPerformanceDTOApi;
import de.hsos.swa.jonas.theater.eventmanagement.control.EventOperations;
import de.hsos.swa.jonas.theater.eventmanagement.entity.Performance;
import de.hsos.swa.jonas.theater.shared.dto.jsonapi.ErrorDTO;
import de.hsos.swa.jonas.theater.shared.dto.jsonapi.LinksDTO;
import de.hsos.swa.jonas.theater.shared.dto.jsonapi.ResourceObjectDTO;
import de.hsos.swa.jonas.theater.shared.dto.jsonapi.ResponseWrapperDTO;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.Collection;

@Path("/api/events")
public class EventIdPerformanceResourceApi {
    @Inject
    EventOperations eventOperations;
    @Context
    UriInfo uriInfo;
    @GET
    @Path("/{id}/performances")
    public Response getPerformancesByEventId(@PathParam("id") long id){
        Collection<Performance> performances = eventOperations.getPerformancesByEventId(id);
        ResponseWrapperDTO<Object> responseWrapperDTO = new ResponseWrapperDTO<>();
        if(performances.isEmpty()){
            responseWrapperDTO.errors = new ArrayList<>();
            responseWrapperDTO.errors.add(new ErrorDTO("404", "PERFORMANCES:1","No performances for id found", "Couldn't find any performances with the given ids"));
            return Response.status(Response.Status.NOT_FOUND).entity(responseWrapperDTO).build();
        }
        responseWrapperDTO.data = performances.stream()
                .map(performance -> {
                    String performanceId = String.valueOf(performance.id);
                    String type = "performance";
                    LinksDTO linksDTO = null;//TODO LinkBuilder.createSelfLink(uriInfo, PerformanceResourceApi.class, performanceId);
                    OutgoingPerformanceDTOApi outgoingPerformanceDTOApi = OutgoingPerformanceDTOApi.Converter.toDTO(performance);
                    return new ResourceObjectDTO<>(performanceId, type, outgoingPerformanceDTOApi, null, linksDTO);
                }).toList();

        return Response.ok().entity(responseWrapperDTO).build();
    }
}
