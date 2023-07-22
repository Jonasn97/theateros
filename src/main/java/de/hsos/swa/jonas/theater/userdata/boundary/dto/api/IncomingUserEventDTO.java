package de.hsos.swa.jonas.theater.userdata.boundary.dto.api;

import de.hsos.swa.jonas.theater.shared.EventState;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;

public class IncomingUserEventDTO {
    @Positive()
    public long eventId;
    public boolean isFavorite;
    @Valid
    public EventState eventState;
}
