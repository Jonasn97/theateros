package de.hsos.swa.jonas.theater.userdata.boundary.dto;

import de.hsos.swa.jonas.theater.shared.EventState;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

/**
 * DTO for incoming UserEvent from @see UserEventResource
 */
public class IncomingUpdateUserEventDTO {
    public boolean isFavorite;
    @Valid
    public EventState eventState;
}
