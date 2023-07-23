package de.hsos.swa.jonas.theater.eventmanagement.boundary.dto.mobile;

import de.hsos.swa.jonas.theater.eventmanagement.entity.Event;
import de.hsos.swa.jonas.theater.shared.EventState;

/**
 * DTO with needed data for events list which may contain the next performance of an event
 */
public class OutgoingEventDTOMobile {
    public long id;
    public String title;
    public String kind;
    public String thumbnailPath;
    public EventState eventState;
    public OutgoingEventNextPerformanceDTOMobile nextPerformance = null;

    public OutgoingEventDTOMobile(long id, String title, String kind, String thumbnailPath) {
        this.id = id;
        this.title = title;
        this.kind = kind;
        this.thumbnailPath = thumbnailPath;
    }

    public OutgoingEventDTOMobile() {

    }

    public static class Converter {
        public static OutgoingEventDTOMobile toDTO(Event event) {
            return new OutgoingEventDTOMobile(event.id, event.getTitle(), event.getKind(), event.getThumbnailPath());
        }
    }

}
