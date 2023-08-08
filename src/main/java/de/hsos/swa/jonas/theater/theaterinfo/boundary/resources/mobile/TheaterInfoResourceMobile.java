package de.hsos.swa.jonas.theater.theaterinfo.boundary.resources.mobile;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/mobile/more")
public class TheaterInfoResourceMobile {

    @Inject
    Template more;

    @GET
    public Response getMorePage() {
        int active = 3;
        TemplateInstance instance = more.data("active", active);
        String html = instance.render();
        return Response.ok().entity(html).build();
    }

}
