package de.hsos.swa.jonas.theater.userdata.boundary.dto.api;

import de.hsos.swa.jonas.theater.userdata.entity.UserEvent;

public class OutgoingUserEventDTOApi {
    public String eventId;
    public String isFavorite;
    public String eventState;

    public OutgoingUserEventDTOApi(String eventId, String isFavorite, String eventState) {
        this.eventId = eventId;
        this.isFavorite = isFavorite;
        this.eventState = eventState;
    }

    public OutgoingUserEventDTOApi() {
    }

    public static class Converter {
        public static OutgoingUserEventDTOApi toDTO(UserEvent userEvent) {
            return new OutgoingUserEventDTOApi(String.valueOf(userEvent.eventId), String.valueOf(userEvent.isFavorite), String.valueOf(userEvent.eventState));
        }
    }
}
