package de.hsos.swa.jonas.theater.shared.dto.jsonapi;

/**Tries to accomodate the JSON-API specification.
 * But still needs To Contain a specific Relationshipobject, e.g. CommentRelationship
 * @param <T>
 */
public class RelationshipDTO <T>{
    public LinksDTO links;
    public ResourceObjectDTO<T> data;
}
