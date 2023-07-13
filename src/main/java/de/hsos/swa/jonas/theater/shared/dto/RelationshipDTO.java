package de.hsos.swa.jonas.theater.shared.dto;
//TODO Needs To Contain Specific Relationshipobject, e.g. CommentRelationship
public class RelationshipDTO <T>{
    public LinksDTO links;
    public ResourceObjectDTO<T> data;
}
