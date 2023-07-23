package de.hsos.swa.jonas.theater.userdata.boundary.dto.api;

import de.hsos.swa.jonas.theater.userdata.entity.UserPerformance;

public class OutgoingUserPerformanceDTOApi {
    public String performanceId;
    public String performanceState;

    public OutgoingUserPerformanceDTOApi(String performanceId, String performanceState) {
        this.performanceId = performanceId;
        this.performanceState = performanceState;
    }

    public OutgoingUserPerformanceDTOApi() {
    }

    public static class Converter {
        public static OutgoingUserPerformanceDTOApi toDTO(UserPerformance userPerformance) {
            return new OutgoingUserPerformanceDTOApi(String.valueOf(userPerformance.getPerformanceId()), String.valueOf(userPerformance.getPerformanceState()));
        }
    }
}
