package de.hsos.swa.jonas.theater.shared;

import de.hsos.swa.jonas.theater.eventmanagement.boundary.resources.api.EventResourceApi;
import de.hsos.swa.jonas.theater.shared.dto.jsonapi.LinksDTO;

import javax.ws.rs.core.UriInfo;

public class LinkBuilder {
    public static LinksDTO createSelfLink(UriInfo uriInfo, Class<?> klasse, String id) {
        LinksDTO linksDTO = new LinksDTO();
        linksDTO.self = uriInfo.getBaseUriBuilder()
                .path(klasse)
                .build(id)
                .toString();
        return linksDTO;
    }
}
