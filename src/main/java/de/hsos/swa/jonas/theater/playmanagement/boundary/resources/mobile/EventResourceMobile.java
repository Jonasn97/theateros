package de.hsos.swa.jonas.theater.playmanagement.boundary.resources.mobile;

import de.hsos.swa.jonas.theater.playmanagement.boundary.dto.InitialPlayDTO;
import de.hsos.swa.jonas.theater.playmanagement.boundary.dto.QueryParametersDTO;
import de.hsos.swa.jonas.theater.playmanagement.control.PlayOperations;
import de.hsos.swa.jonas.theater.shared.Play;
import io.quarkus.logging.Log;
import io.quarkus.qute.Location;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.qute.Template;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Path("web/events")
public class EventResourceMobile {
    private final static String FIRSTPAGE_STRING = "0";
    @Inject
    PlayOperations playOperations;
    @Inject
    Template base;

    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.TEXT_HTML)
    @GET
    public Response listEvents(@QueryParam("filter[name]") String nameFilter,
                               @QueryParam("filter[status]") ArrayList<String> statusFilter,
                               @QueryParam("filter[playType]") ArrayList<String> playTypeFilter,
                               @QueryParam("filter[performanceType]") ArrayList<String> performanceTypeFilter,
                               @QueryParam("filter[startDateTime]") String startDateTimeFilter,
                               @QueryParam("filter[endDateTime]") String endDateTimeFilter,
                               @QueryParam("include") String include,
                               @DefaultValue(FIRSTPAGE_STRING)@QueryParam("page[number]") Long pageNumber,
                               @DefaultValue("10")@QueryParam("page[size]") Long pageSize) {
        QueryParametersDTO queryParametersDTO = new QueryParametersDTO(nameFilter, statusFilter, playTypeFilter, performanceTypeFilter, startDateTimeFilter, endDateTimeFilter, include, pageNumber, pageSize);
        Collection<Play> plays = playOperations.getPlays(queryParametersDTO);
        List<InitialPlayDTO> playDTOS = plays.stream().map(InitialPlayDTO.Converter::toDTO).collect(Collectors.toList());
        Log.info(plays.size());
        Log.info(playDTOS.size());
        TemplateInstance templateInstance =base.data("plays", playDTOS);
    String html = templateInstance.render();
    return Response.ok().entity(html).build();
    }
}
