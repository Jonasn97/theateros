package de.hsos.swa.jonas.theater.crawler.gateway;

public class InitialPlayDTO {
    public String infolink;
    public String overline;
    public String title;
    public String kind;
    public String location;
    public InitialPlayDTO(String infoLink, String overline, String title, String sparte, String location) {

        this.infolink = infoLink;
        this.overline = overline;
        this.title = title;
        this.kind = sparte;
        this.location = location;
    }
    public InitialPlayDTO() {

    }
}
