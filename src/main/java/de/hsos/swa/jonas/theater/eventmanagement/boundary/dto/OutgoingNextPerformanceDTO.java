package de.hsos.swa.jonas.theater.eventmanagement.boundary.dto;

import de.hsos.swa.jonas.theater.shared.Performance;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class OutgoingNextPerformanceDTO {
    public String performanceType = null;
    public String startDate = null;
    public String startTime = null;
    public boolean hasTime = false;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    public OutgoingNextPerformanceDTO(String performanceType, LocalDateTime dateTime, boolean hasTime) {
        this.performanceType = performanceType;
        this.startDate = dateTime.format(DATE_FORMAT);
        this.hasTime = hasTime;
        if(hasTime)
            this.startTime = dateTime.format(TIME_FORMAT);
    }

    public OutgoingNextPerformanceDTO() {

    }

    public static class Converter {
        public static OutgoingNextPerformanceDTO toDTO(Performance nextPerformance) {
            return new OutgoingNextPerformanceDTO(nextPerformance.performanceType, nextPerformance.datetime, nextPerformance.hasTime);
        }
    }
}
