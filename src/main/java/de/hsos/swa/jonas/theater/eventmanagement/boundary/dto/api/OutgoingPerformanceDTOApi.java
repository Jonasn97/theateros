package de.hsos.swa.jonas.theater.eventmanagement.boundary.dto.api;

import de.hsos.swa.jonas.theater.eventmanagement.entity.Performance;

import java.time.LocalDateTime;

/**
 * DTO for outgoing performances in JSON format
 */
public class OutgoingPerformanceDTOApi {
    public String bookingLink;
    public String auid;
    public String datetime;
    public boolean hasTime;
    public boolean isCancelled;
    public String performanceType;

    public OutgoingPerformanceDTOApi(String bookingLink, String auid, LocalDateTime datetime, boolean hasTime, boolean isCancelled, String performanceType) {
        this.bookingLink = bookingLink;
        this.auid = auid;
        this.datetime = datetime.toString();
        this.hasTime = hasTime;
        this.isCancelled = isCancelled;
        this.performanceType = performanceType;
    }
    public static class Converter {
        public static OutgoingPerformanceDTOApi toDTO(Performance performance) {
            return new OutgoingPerformanceDTOApi(performance.getBookingLink(), performance.getAuid(), performance.getDatetime(), performance.isHasTime(), performance.isCancelled(), performance.getPerformanceType());
        }
    }
}
