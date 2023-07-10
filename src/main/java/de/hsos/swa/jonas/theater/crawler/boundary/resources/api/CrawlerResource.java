package de.hsos.swa.jonas.theater.crawler.boundary.resources.api;

import de.hsos.swa.jonas.theater.crawler.control.CrawlerOperations;
import de.hsos.swa.jonas.theater.crawler.control.WebsiteDownloader;
import de.hsos.swa.jonas.theater.shared.dto.ErrorDTO;
import de.hsos.swa.jonas.theater.shared.dto.ResourceObjectDTO;
import de.hsos.swa.jonas.theater.shared.dto.ResponseWrapperDTO;
import io.quarkus.logging.Log;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/crawler")
public class CrawlerResource {
    Document calendarDocument = null;
    private static final String WEBSITE_URL = "https://www.theater-osnabrueck.de/kalender/";
    @Inject
    CrawlerOperations crawlerOperations;
    @Inject
    WebsiteDownloader websiteDownloader;

    @POST
    public Response postPlay() {
        //Step 1 - Update Entries from WEBSITE_URL
        //Step 2 - Update Plays from infolinks
        Set<String> updatedLinks;
        ResponseWrapperDTO<Object> responseWrapperDTO = new ResponseWrapperDTO<>();
        try {
            updatedLinks = updateCalendar();
            if(updatedLinks==null){
                Log.info("No changes on " + WEBSITE_URL);
                responseWrapperDTO.errors = new ArrayList<>();
                responseWrapperDTO.errors.add(new ErrorDTO("204","CRAWL:0","No changes", "No changes on " + WEBSITE_URL));
                return Response.status(Response.Status.NO_CONTENT).entity(responseWrapperDTO).build();
            }
            websiteDownloader.downloadAllWebsites(updatedLinks);
            updateEvents(updatedLinks);

            Log.info("Updated " + updatedLinks.size() + " events on " + WEBSITE_URL);
            ResourceObjectDTO<String> updateDTO = new ResourceObjectDTO<>();
            updateDTO.id = "0";
            updateDTO.type = "Crawler";
            updateDTO.attributes = "Updated " + updatedLinks.size() + " events on " + WEBSITE_URL;
            responseWrapperDTO.data = updateDTO;
            return Response.ok(responseWrapperDTO).build();
        } catch (IOException e) {
            Log.error("Error while connecting to " + WEBSITE_URL + "\n" + e.getMessage());
            responseWrapperDTO.errors = new ArrayList<>();
            responseWrapperDTO.errors.add(new ErrorDTO("502","CRAWL:1","Error while connecting to " + WEBSITE_URL, e.getMessage()));
            return Response.status(Response.Status.BAD_GATEWAY).entity(responseWrapperDTO).build();
        }
        //TODO Check Document structure for changes. If changed, log alert, throw Exception and send 500
    }
    Set<String> updateCalendar() throws IOException {
        try {
            Document newCalendarDocument = Jsoup.connect(WEBSITE_URL).timeout(3000).get();
            if(calendarDocument !=null && calendarDocument.equals(newCalendarDocument)){
                return null; //TODO maybe throw NoChangesException?
            }
            calendarDocument = newCalendarDocument;
            return crawlerOperations.updateCalendar(calendarDocument);
        } catch (IOException e) {
            Log.error("Error while connecting to " + WEBSITE_URL + "\n" + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    Set<String> updateEvents(Set<String> updatedLinks) {
        int updatedEvents = 0;
        for (String updatedLink : updatedLinks)
        {
            try {
                Document eventDocument = Jsoup.connect(updatedLink).timeout(3000).get();
                updatedEvents += crawlerOperations.updateEvent(updatedLink, eventDocument);
            } catch (IOException e) {
                Log.error("Error while connecting to " + updatedLink + "\n" + e.getMessage());
                e.printStackTrace();
            }
        }
        return null;
    }
    public void updateEvent(String filepath) {
        try {
            Document eventDocument=Jsoup.parse(websiteDownloader.readFile(filepath),"UTF-8");
            crawlerOperations.updateEvent("https://www.theater-osnabrueck.de/spielplan-detail/?stid=230",eventDocument);
        } catch (IOException e) {
            Log.error("Error while reading File from " + filepath + "\n" + e.getMessage());
            e.printStackTrace();
        }
    }
    @GET
    public Response testSpecificEvents() {
        updateEvent("src/main/resources/crawledPages/file25.html");
        return Response.ok().build();
    }

}
