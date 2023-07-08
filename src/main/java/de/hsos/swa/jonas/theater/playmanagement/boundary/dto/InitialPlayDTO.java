package de.hsos.swa.jonas.theater.playmanagement.boundary.dto;

import de.hsos.swa.jonas.theater.shared.Play;

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
        public static InitialPlayDTO toDTO(Play play){
            return new InitialPlayDTO(play.stid, play.infolink, play.overline, play.title, play.kind, play.location);
        }
        public static Play fromDTO(InitialPlayDTO play){
            return new Play(play.stid, play.infolink, play.overline, play.title, play.kind, play.location);
        }
    }

}
