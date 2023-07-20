package de.hsos.swa.jonas.theater.eventmanagement.boundary.dto.api;

import de.hsos.swa.jonas.theater.eventmanagement.entity.Event;

public class OutgoingEventDTOApi {
    public String title;
    public String kind;
    public String location;
    public String thumbnailPath;

    public OutgoingEventDTOApi(String title, String kind, String location, String thumbnailPath) {
        this.title = title;
        this.kind = kind;
        this.location = location;
        this.thumbnailPath = thumbnailPath;
    }
    public OutgoingEventDTOApi() {

    }
    public static class Converter{
        public static OutgoingEventDTOApi toDTO(Event event){
            return new OutgoingEventDTOApi(event.title, event.kind, event.location, event.thumbnailPath);
        }
    }

}
