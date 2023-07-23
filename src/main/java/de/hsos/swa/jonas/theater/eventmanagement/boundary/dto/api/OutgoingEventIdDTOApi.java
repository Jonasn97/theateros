package de.hsos.swa.jonas.theater.eventmanagement.boundary.dto.api;

import de.hsos.swa.jonas.theater.eventmanagement.entity.Event;

/**
 * DTO for outgoing events in JSON format
 */
public class OutgoingEventIdDTOApi {
    public String title;
    public String kind;
    public String location;
    public String thumbnailPath;
    public String stid;
    public String description;
    public String duration;
    public String team;
    public String press;

    public OutgoingEventIdDTOApi(String title, String kind, String location, String thumbnailPath, String stid, String description, String duration, String team, String press) {
        this.title = title;
        this.kind = kind;
        this.location = location;
        this.thumbnailPath = thumbnailPath;
        this.stid = stid;
        this.description = description;
        this.duration = duration;
        this.team = team;
        this.press = press;
    }
    public OutgoingEventIdDTOApi() {

    }
    public static class Converter{
        public static OutgoingEventIdDTOApi toDTO(Event event){
            return new OutgoingEventIdDTOApi(event.getTitle(), event.getKind(), event.getLocation(), event.getThumbnailPath(), event.getStid(), event.getDescription(), event.getDuration(), event.getTeam(), event.getPress());
        }
    }

}
