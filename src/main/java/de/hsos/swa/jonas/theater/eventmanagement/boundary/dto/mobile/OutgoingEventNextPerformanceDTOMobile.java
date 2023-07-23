package de.hsos.swa.jonas.theater.eventmanagement.boundary.dto.mobile;

import de.hsos.swa.jonas.theater.eventmanagement.entity.Performance;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * DTO containing the next performance of an event
 * Used for mobile
 */
public class OutgoingEventNextPerformanceDTOMobile {
    public String performanceType = null;
    public String startDate = null;
    public String startTime = null;
    public boolean hasTime = false;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    public OutgoingEventNextPerformanceDTOMobile(String performanceType, LocalDateTime dateTime, boolean hasTime) {
        this.performanceType = performanceType;
        this.startDate = dateTime.format(DATE_FORMAT);
        this.hasTime = hasTime;
        if(hasTime)
            this.startTime = dateTime.format(TIME_FORMAT);
    }

    public OutgoingEventNextPerformanceDTOMobile() {

    }

    public static class Converter {
        public static OutgoingEventNextPerformanceDTOMobile toDTO(Performance nextPerformance) {
            return new OutgoingEventNextPerformanceDTOMobile(nextPerformance.getPerformanceType(), nextPerformance.getDatetime(), nextPerformance.isHasTime());
        }
    }
}
