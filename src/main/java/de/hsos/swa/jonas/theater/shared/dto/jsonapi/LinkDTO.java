package de.hsos.swa.jonas.theater.shared.dto.jsonapi;

public class LinkDTO {
    public String href;
    public String title;

    public LinkDTO(String href, String title) {
        this.href = href;
        this.title = title;
    }
    public LinkDTO() {
    }
}
