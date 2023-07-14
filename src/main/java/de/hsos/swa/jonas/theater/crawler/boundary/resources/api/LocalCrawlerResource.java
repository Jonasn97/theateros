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
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

@Path("/local/crawler")
public class LocalCrawlerResource {
    Document calendarDocument = null;
    @Inject
    CrawlerOperations crawlerOperations;
    @Inject
    WebsiteDownloader websiteDownloader;

    private static final String CALENDER_PATH = "src/main/resources/crawledPages/calendar.html";
    @GET
    public Response crawlPlaysLocally() {
        //Step 1 - Update Entries from WEBSITE_URL
        //Step 2 - Update Plays from infolinks
        Set<String> updatedLinks;
        ResponseWrapperDTO<Object> responseWrapperDTO = new ResponseWrapperDTO<>();
        try {
            updatedLinks = updateCalendarLocally(CALENDER_PATH);
            if(updatedLinks==null){
                responseWrapperDTO.errors = new ArrayList<>();
                responseWrapperDTO.errors.add(new ErrorDTO("204","CRAWL:0","No changes", "No changes on " + CALENDER_PATH));
                return Response.status(Response.Status.NO_CONTENT).entity(responseWrapperDTO).build();
            }
            updateEventsLocally(updatedLinks);

            Log.info("Updated " + updatedLinks.size() + " events on " + CALENDER_PATH);
            ResourceObjectDTO<String> updateDTO = new ResourceObjectDTO<>();
            updateDTO.id = "0";
            updateDTO.type = "Crawler";
            updateDTO.attributes = "Updated " + updatedLinks.size() + " events on " + CALENDER_PATH;
            responseWrapperDTO.data = updateDTO;
            return Response.ok(responseWrapperDTO).build();
        } catch (IOException e) {
            Log.error("Error while connecting to " + CALENDER_PATH + "\n" + e.getMessage());
            responseWrapperDTO.errors = new ArrayList<>();
            responseWrapperDTO.errors.add(new ErrorDTO("502","CRAWL:1","Error while connecting to " + CALENDER_PATH, e.getMessage()));
            return Response.status(Response.Status.BAD_GATEWAY).entity(responseWrapperDTO).build();
        }
        //TODO Check Document structure for changes. If changed, log alert, throw Exception and send 500
    }
    void updateEventsLocally(Set<String> updatedLinks) {
        int updatedEvents = 0;
        for (String updatedLink : updatedLinks)
        {
            String filepath = websiteDownloader.getPath(updatedLink);
            try {
            Document eventDocument = Jsoup.parse(websiteDownloader.readFile(filepath),"UTF-8");
            updatedEvents += crawlerOperations.updateEvent(updatedLink, eventDocument);

            } catch (IOException e) {
                Log.error("Error while connecting to filepath" + "\n" + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    Set<String> updateCalendarLocally(String calendarPath) throws IOException {
        try {
            Document newCalendarDocument = Jsoup.parse(websiteDownloader.readFile(calendarPath),"UTF-8");
            if(calendarDocument !=null && calendarDocument.equals(newCalendarDocument)){
                return null; //TODO maybe throw NoChangesException?
            }
            calendarDocument = newCalendarDocument;
            return crawlerOperations.updateCalendar(calendarDocument);
        } catch (IOException e) {
            Log.error("Error while reading " + CALENDER_PATH + "\n" + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}
