package de.hsos.swa.jonas.theater.eventmanagement.boundary.dto;

import de.hsos.swa.jonas.theater.shared.Event;

public class OutgoingEventDTO {
    public long id;
    public String title;
    public String kind;
    public String thumbnailPath;
    public OutgoingNextPerformanceDTO nextPerformance = null;

    public OutgoingEventDTO(long id, String title, String kind, String thumbnailPath) {
        this.id = id;
        this.title = title;
        this.kind = kind;
        this.thumbnailPath = thumbnailPath;
    }

    public OutgoingEventDTO() {

    }

    public static class Converter {
        public static OutgoingEventDTO toDTO(Event event) {
            return new OutgoingEventDTO(event.id, event.title, event.kind, event.thumbnailPath);
        }
    }

}
