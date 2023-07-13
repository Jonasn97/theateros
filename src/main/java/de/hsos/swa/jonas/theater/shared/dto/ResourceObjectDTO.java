package de.hsos.swa.jonas.theater.shared.dto;

public class ResourceObjectDTO<T> {
    public String id;
    public String type;
    public T attributes;
    public RelationshipDTO<T> relationships;
    public LinksDTO links;

    public ResourceObjectDTO(String id,String type, T attributes, RelationshipDTO<T> relationships, LinksDTO links) {
        this.id = id;
        this.type = type;
        this.attributes = attributes;
        this.relationships = relationships;
        this.links = links;
    }

    public ResourceObjectDTO() {
    }
}
