package de.hsos.swa.jonas.theater.userdata.boundary.dto.api;

import de.hsos.swa.jonas.theater.shared.EventState;
import de.hsos.swa.jonas.theater.userdata.entity.PerformanceState;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

public class IncomingUserPerformanceDTO {
    @Positive public long performanceId;
    @Valid public PerformanceState performanceState;

    public IncomingUserPerformanceDTO(long performanceId, PerformanceState performanceState) {
        this.performanceId = performanceId;
        this.performanceState = performanceState;
    }

    public IncomingUserPerformanceDTO() {
    }
}
