package de.hsos.swa.jonas.theater.crawler.boundary.resources.api;

import de.hsos.swa.jonas.theater.crawler.control.CrawlerOperations;
import de.hsos.swa.jonas.theater.shared.dto.ErrorDTO;
import de.hsos.swa.jonas.theater.shared.dto.ResourceObjectDTO;
import de.hsos.swa.jonas.theater.shared.dto.ResponseWrapperDTO;
import io.quarkus.logging.Log;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/crawler")
public class CrawlerResource {
    Document doc = null;
    private static final String WEBSITE_URL = "https://www.theater-osnabrueck.de/kalender/";
    @Inject
    CrawlerOperations crawlerOperations;

    @POST
    public Response postPlay() {
        int updates;
        ResponseWrapperDTO<Object> responseWrapperDTO = new ResponseWrapperDTO<>();
        try {
            Document update = Jsoup.connect(WEBSITE_URL).timeout(3000).get();

            if(doc!=null && doc.equals(update)){
                Log.info("No changes on " + WEBSITE_URL);
                responseWrapperDTO.errors = new ArrayList<>();
                responseWrapperDTO.errors.add(new ErrorDTO("204","CRAWL:0","No changes", "No changes on " + WEBSITE_URL));
                return Response.status(Response.Status.NO_CONTENT).entity(responseWrapperDTO).build();
            } else {
                updates = crawlerOperations.updateCalendar(update);
                doc = update;
                Log.info("Updated " + updates + " entries on " + WEBSITE_URL);
                ResourceObjectDTO<String> updateDTO = new ResourceObjectDTO<>();
                updateDTO.id = "0";
                updateDTO.type = "Crawler";
                updateDTO.attributes = "Updated " + updates + " entries in Database";
                responseWrapperDTO.data = updateDTO;
                return Response.ok(responseWrapperDTO).build();}
        } catch (IOException e) {
            Log.error("Error while connecting to " + WEBSITE_URL + " " + e.getMessage());
            responseWrapperDTO.errors = new ArrayList<>();
            responseWrapperDTO.errors.add(new ErrorDTO("502","CRAWL:1","Error while connecting to " + WEBSITE_URL, e.getMessage()));
            return Response.status(Response.Status.BAD_GATEWAY).entity(responseWrapperDTO).build();
        }
        //TODO Check Document structure for changes. If changed, log alert, throw Exception and send 500
    }
}
