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
    @GET
    public Response crawlPlaysLocally() {
        //Step 1 - Update Entries from WEBSITE_URL
        //Step 2 - Update Plays from infolinks
        Set<String> updatedStids;
        ResponseWrapperDTO<Object> responseWrapperDTO = new ResponseWrapperDTO<>();
        try {
            updatedStids = updateCalendarLocally();
            if(updatedStids==null){
                responseWrapperDTO.errors = new ArrayList<>();
                responseWrapperDTO.errors.add(new ErrorDTO("204","CRAWL:0","No changes", "No changes on calendarDocument"));
                return Response.status(Response.Status.NO_CONTENT).entity(responseWrapperDTO).build();
            }
            updateEventsLocally(updatedStids);

            Log.info("Updated " + updatedStids.size() + " events");
            ResourceObjectDTO<String> updateDTO = new ResourceObjectDTO<>();
            updateDTO.id = "0";
            updateDTO.type = "Crawler";
            updateDTO.attributes = "Updated " + updatedStids.size() + " events on calendarDocument";
            responseWrapperDTO.data = updateDTO;
            return Response.ok(responseWrapperDTO).build();
        } catch (IOException e) {
            Log.error("Error while connecting to calendarDocument" + "\n" + e.getMessage());
            responseWrapperDTO.errors = new ArrayList<>();
            responseWrapperDTO.errors.add(new ErrorDTO("502","CRAWL:1","Error while connecting to calendarDocument\n", e.getMessage()));
            return Response.status(Response.Status.BAD_GATEWAY).entity(responseWrapperDTO).build();
        }
        //TODO Check Document structure for changes. If changed, log alert, throw Exception and send 500
    }
    void updateEventsLocally(Set<String> updatedStids) {
        int updatedEvents = 0;
        for (String updatedStid : updatedStids)
        {
            String filepath = websiteDownloader.getPath(updatedStid);
            try {
            Document eventDocument = Jsoup.parse(websiteDownloader.readFile(filepath),"UTF-8");
            updatedEvents += crawlerOperations.updateEvent(updatedStid, eventDocument);

            } catch (IOException e) {
                Log.error("Error while connecting to filepath" + "\n" + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    Set<String> updateCalendarLocally() throws IOException {
        try {
            String calendarPath = websiteDownloader.getCalendarPath();
            Document newCalendarDocument = Jsoup.parse(websiteDownloader.readFile(calendarPath),"UTF-8");
            if(calendarDocument !=null && calendarDocument.equals(newCalendarDocument)){
                return null; //TODO maybe throw NoChangesException?
            }
            calendarDocument = newCalendarDocument;
            return crawlerOperations.updateCalendar(calendarDocument);
        } catch (IOException e) {
            Log.error("Error while reading calendarDocument" + "\n" + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}
