package de.hsos.swa.jonas.theater.dataprovider.boundary.resources.api;

import de.hsos.swa.jonas.theater.dataprovider.control.CrawlerOperations;
import de.hsos.swa.jonas.theater.dataprovider.control.WebsiteDownloader;
import de.hsos.swa.jonas.theater.shared.dto.jsonapi.ErrorDTO;
import de.hsos.swa.jonas.theater.shared.dto.jsonapi.ResourceObjectDTO;
import de.hsos.swa.jonas.theater.shared.dto.jsonapi.ResponseWrapperDTO;
import io.quarkus.logging.Log;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

/**
 * Resource for crawling the websites from the local storage
 */
@Path("/crawler/local")
public class LocalCrawlerResource {
    Document calendarDocument = null;
    @Inject
    CrawlerOperations crawlerOperations;
    @Inject
    WebsiteDownloader websiteDownloader;

    /**
     * Crawls the stored websites in /src/main/resources/crawledPages and updates the database
     * @return Response with status 200 and a message of how many events were updated
     *
     */
    @GET
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    @Fallback(fallbackMethod = "crawlEventsLocallyFallback")
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.75, delay = 10000)
    @Operation(summary = "Crawl Events from website", description = "Crawl Events from Website and update Database")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "A message of how many events were updated is returned"),
            @APIResponse(responseCode = "400", description = "Bad Request"),
            @APIResponse(responseCode = "204", description = "No changes found on website"),
            @APIResponse(responseCode = "500", description = "Internal Server Error"),
            @APIResponse(responseCode = "502", description = "Error while connecting to website")
    })
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

    /**
     * @param updatedStids Set of updated stids
     *                     Parses the downloaded eventDocuments and updates the database
     */
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

    /**
     * @return Set of updated stids
     * @throws IOException if the calendarDocument could not be read
     */
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

    /**
     * @return Response with status 500 and a message that something went wrong while processing the request
     *         if the fallback method is called
     */
    @Path("/fallback")
    public Response crawlEventsLocallyFallback() {
        ResponseWrapperDTO<ErrorDTO> responseWrapperDTO = new ResponseWrapperDTO<>();
        responseWrapperDTO.errors = new ArrayList<>();
        responseWrapperDTO.errors.add(new ErrorDTO("500", "CRAWL:2","Internal Server Error", "Something went wrong while processing your request"));
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseWrapperDTO).build();
    }
}
