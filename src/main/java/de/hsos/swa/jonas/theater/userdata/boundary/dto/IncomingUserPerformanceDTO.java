package de.hsos.swa.jonas.theater.userdata.boundary.dto;

import de.hsos.swa.jonas.theater.shared.PerformanceState;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

/**
 * DTO for changing the state of a performance for user via api
 */
public class IncomingUserPerformanceDTO {
    @Positive public long performanceId;
    @Valid public PerformanceState performanceState;

    public IncomingUserPerformanceDTO(long performanceId, PerformanceState performanceState) {
        this.performanceId = performanceId;
        this.performanceState = performanceState;
    }

    public IncomingUserPerformanceDTO() {
    }

    public static class Converter{
        public static IncomingUserPerformanceDTO convert(long performanceId, PerformanceState performanceState){
            return new IncomingUserPerformanceDTO(performanceId, performanceState);
        }
    }
}
