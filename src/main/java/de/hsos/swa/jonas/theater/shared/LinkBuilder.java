package de.hsos.swa.jonas.theater.shared;

import de.hsos.swa.jonas.theater.eventmanagement.boundary.dto.QueryParametersDTO;
import de.hsos.swa.jonas.theater.eventmanagement.boundary.resources.api.EventResourceApi;
import de.hsos.swa.jonas.theater.shared.dto.jsonapi.LinksDTO;
import de.hsos.swa.jonas.theater.shared.dto.jsonapi.RelationshipDTO;
import io.quarkus.logging.Log;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

@ApplicationScoped
public class LinkBuilder {
    private final static long FIRSTPAGE = 0;
    public LinksDTO createSelfLink(Class<?> resourceClass, UriInfo uriInfo, String id) {
        LinksDTO linksDTO = new LinksDTO();
        linksDTO.self = uriInfo.getBaseUriBuilder()
                .path(resourceClass)
                .path(id)
                .build()
                .toString();
        return linksDTO;
    }
    public LinksDTO createPaginationLinks(Class<?> resourceClass, UriInfo uriInfo, long pageNumber, long pageSize, long maxSize) {
        LinksDTO linksDTO = new LinksDTO();
        UriBuilder uriBuilder = uriInfo.getBaseUriBuilder()
                .path(resourceClass);
        linksDTO.first = uriBuilder
                .queryParam("page[number]", FIRSTPAGE)
                .queryParam("page[size]", pageSize)
                .build()
                .toString();
        if(pageNumber>FIRSTPAGE)
            linksDTO.prev = uriBuilder
                    .replaceQueryParam("page[number]", "{pageNumber}")
                    .build(pageNumber-1)
                    .toString();
        else
            linksDTO.prev = "";
        if((pageNumber+1) * pageSize < maxSize)
            linksDTO.next = uriBuilder
                    .replaceQueryParam("page[number]", "{pageNumber}")
                    .build(pageNumber+1)
                    .toString();
        else
            linksDTO.next = "";
        linksDTO.last = uriBuilder
                .replaceQueryParam("page[number]", (maxSize/pageSize))
                .build()
                .toString();
        return linksDTO;
    }
    private LinksDTO createRelationshipLink(Class<?> resourceClass, UriInfo uriInfo, String id, String relationship) {
        LinksDTO linksDTO = new LinksDTO();
        linksDTO.related = uriInfo.getBaseUriBuilder()
                .path(resourceClass)
                .path(id)
                .path(relationship)
                .build()
                .toString();
        return linksDTO;
    }
    public RelationshipDTO<Object> addRelationship(Class<?> resourceClass, UriInfo uriInfo, long id, String relationship) {
        LinksDTO linksDTO = createRelationshipLink(resourceClass, uriInfo, String.valueOf(id),relationship);
        RelationshipDTO<Object> relationshipDTO = new RelationshipDTO<>();
        relationshipDTO.links = linksDTO;
        return relationshipDTO;
    }
}