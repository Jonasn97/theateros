package de.hsos.swa.jonas.theater.eventmanagement.boundary.dto;

import de.hsos.swa.jonas.theater.shared.Event;

public class InitialPlayDTO {
    public String stid;
    public String infolink;
    public String overline;
    public String title;
    public String kind;
    public String location;
    public InitialPlayDTO(String stid, String infoLink, String overline, String title, String sparte, String location) {
        this.stid = stid;
        this.infolink = infoLink;
        this.overline = overline;
        this.title = title;
        this.kind = sparte;
        this.location = location;
    }
    public InitialPlayDTO() {

    }
    public static class Converter{
        public static InitialPlayDTO toDTO(Event event){
            return new InitialPlayDTO(event.stid, event.infolink, event.overline, event.title, event.kind, event.location);
        }
        public static Event fromDTO(InitialPlayDTO play){
            return new Event(play.stid, play.infolink, play.overline, play.title, play.kind, play.location);
        }
    }

}
