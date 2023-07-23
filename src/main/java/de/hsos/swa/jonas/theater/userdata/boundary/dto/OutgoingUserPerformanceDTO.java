package de.hsos.swa.jonas.theater.userdata.boundary.dto;

import de.hsos.swa.jonas.theater.userdata.entity.UserPerformance;

/**
 * DTO for outgoing userPerformance
 */
public class OutgoingUserPerformanceDTO {
    public String performanceId;
    public String performanceState;

    public OutgoingUserPerformanceDTO(String performanceId, String performanceState) {
        this.performanceId = performanceId;
        this.performanceState = performanceState;
    }

    public OutgoingUserPerformanceDTO() {
    }

    public static class Converter {
        public static OutgoingUserPerformanceDTO toDTO(UserPerformance userPerformance) {
            return new OutgoingUserPerformanceDTO(String.valueOf(userPerformance.getPerformanceId()), String.valueOf(userPerformance.getPerformanceState()));
        }
    }
}
