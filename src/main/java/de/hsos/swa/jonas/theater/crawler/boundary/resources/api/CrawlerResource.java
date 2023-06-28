package de.hsos.swa.jonas.theater.crawler.boundary.resources.api;

import de.hsos.swa.jonas.theater.crawler.gateway.WebsiteRepository;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/crawler")
public class CrawlerResource {

    @Inject
    WebsiteRepository websiteRepository;

    @POST
    public Response postPlay() {
        websiteRepository.parseWebsites();
        return Response.ok().build();
    }
}
