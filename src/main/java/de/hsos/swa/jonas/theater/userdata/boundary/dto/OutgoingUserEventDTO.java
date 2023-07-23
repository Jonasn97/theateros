package de.hsos.swa.jonas.theater.userdata.boundary.dto;

import de.hsos.swa.jonas.theater.userdata.entity.UserEvent;

/**
 * DTO for outgoing UserEvent
 */
public class OutgoingUserEventDTO {
    public String eventId;
    public String isFavorite;
    public String eventState;

    public OutgoingUserEventDTO(String eventId, String isFavorite, String eventState) {
        this.eventId = eventId;
        this.isFavorite = isFavorite;
        this.eventState = eventState;
    }

    public OutgoingUserEventDTO() {
    }

    public static class Converter {
        public static OutgoingUserEventDTO toDTO(UserEvent userEvent) {
            return new OutgoingUserEventDTO(String.valueOf(userEvent.getEventId()), String.valueOf(userEvent.isFavorite()), String.valueOf(userEvent.getEventState()));
        }
    }
}
