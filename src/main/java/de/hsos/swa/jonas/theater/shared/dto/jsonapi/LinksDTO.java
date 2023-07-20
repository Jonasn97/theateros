package de.hsos.swa.jonas.theater.shared.dto.jsonapi;

public class LinksDTO {
    public String related;
    public String self;
    public String first;
    public String prev;
    public String next;
    public String last;

    public LinksDTO() {
    }
    public LinksDTO(String related) {
        this.related = related;
    }
}
