package de.hsos.swa.jonas.theater.dataprovider.boundary.resources.api;

import de.hsos.swa.jonas.theater.dataprovider.control.CrawlerOperations;
import de.hsos.swa.jonas.theater.dataprovider.control.WebsiteDownloader;
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
    private static final String CALENDAR_URL = "https://www.theater-osnabrueck.de/kalender";
    private static final String EVENT_URL = "https://www.theater-osnabrueck.de/spielplan-detail";
    @Inject
    CrawlerOperations crawlerOperations;
    @Inject
    WebsiteDownloader websiteDownloader;

    @GET
    public Response crawlEventsFromWebsite() {
        //Step 1 - Update Entries from WEBSITE_URL
        //Step 2 - Update Plays from infolinks
        Set<String> updatedStids;
        ResponseWrapperDTO<Object> responseWrapperDTO = new ResponseWrapperDTO<>();
        try {
            updatedStids = updateCalendarFromWebsite();
            if(updatedStids==null){
                Log.info("No changes on " + CALENDAR_URL);
                responseWrapperDTO.errors = new ArrayList<>();
                responseWrapperDTO.errors.add(new ErrorDTO("204","CRAWL:0","No changes", "No changes on " + CALENDAR_URL));
                return Response.status(Response.Status.NO_CONTENT).entity(responseWrapperDTO).build();
            }
            websiteDownloader.downloadAllWebsites(updatedStids);
            updateEventsFromWebsite(updatedStids);

            Log.info("Updated " + updatedStids.size() + " events on " + CALENDAR_URL);
            ResourceObjectDTO<String> updateDTO = new ResourceObjectDTO<>();
            updateDTO.id = "0";
            updateDTO.type = "Crawler";
            updateDTO.attributes = "Updated " + updatedStids.size() + " events on " + CALENDAR_URL;
            responseWrapperDTO.data = updateDTO;
            return Response.ok(responseWrapperDTO).build();
        } catch (IOException e) {
            Log.error("Error while connecting to " + CALENDAR_URL + "\n" + e.getMessage());
            responseWrapperDTO.errors = new ArrayList<>();
            responseWrapperDTO.errors.add(new ErrorDTO("502","CRAWL:1","Error while connecting to " + CALENDAR_URL, e.getMessage()));
            return Response.status(Response.Status.BAD_GATEWAY).entity(responseWrapperDTO).build();
        }
        //TODO Check Document structure for changes. If changed, log alert, throw Exception and send 500
    }
    Set<String> updateCalendarFromWebsite() throws IOException {
        try {
            Document newCalendarDocument = Jsoup.connect(CALENDAR_URL).timeout(3000).get();
            if(calendarDocument !=null && calendarDocument.equals(newCalendarDocument)){
                return null; //TODO maybe throw NoChangesException?
            }
            calendarDocument = newCalendarDocument;
            websiteDownloader.downloadCalendar();
            return crawlerOperations.updateCalendar(calendarDocument);
        } catch (IOException e) {
            Log.error("Error while connecting to " + CALENDAR_URL + "\n" + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    Set<String> updateEventsFromWebsite(Set<String> updatedStids) {
        int updatedEvents = 0;
        String updatedLink;
        for (String stid : updatedStids)
        {
            try {
                updatedLink = websiteDownloader.getUrlFromStid(stid);

                Document eventDocument = Jsoup.connect(updatedLink).timeout(3000).get();
                updatedEvents += crawlerOperations.updateEvent(stid, eventDocument);
            } catch (IOException e) {
                Log.error("Error while connecting to Website with " + stid + "\n" + e.getMessage());
                e.printStackTrace();
            }
        }
        return null;
    }
}
