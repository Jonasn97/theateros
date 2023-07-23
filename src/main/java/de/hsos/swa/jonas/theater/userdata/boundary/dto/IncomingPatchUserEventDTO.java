package de.hsos.swa.jonas.theater.userdata.boundary.dto;

import de.hsos.swa.jonas.theater.shared.EventState;

/**
 * DTO for incoming PATCH-Requests for UserEvent from @see UserEventResourceMobile
 */
public class IncomingPatchUserEventDTO {
    public Boolean isFavorite;
    public EventState eventState;
}
