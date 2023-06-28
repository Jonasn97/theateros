package de.hsos.swa.jonas.theater.shared;

public class ResourceObjectDTO<T> {
    public String id;
    public String type;
    public T attributes;
    public LinkDTO links;

    //relationships

    public ResourceObjectDTO(String id,String type, T attributes, LinkDTO links) {
        this.id = id;
        this.type = type;
        this.attributes = attributes;
        this.links = links;
    }

    public ResourceObjectDTO() {
    }
}
