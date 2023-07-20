package de.hsos.swa.jonas.theater.eventmanagement.boundary.dto;

import de.hsos.swa.jonas.theater.eventmanagement.entity.Performance;

import java.time.LocalDateTime;

public class PerformanceDTO {
    public long id;
    public String month;
    public String day;
    public String time;

    public String bookingLink;

    public boolean isCancelled;

    public String performanceType; // Letzte Vorstellung, Wiederaufnahme, offene Probe, Premiere, Zusatzvorstellung

    public PerformanceDTO() {
    }

    public PerformanceDTO(long id, LocalDateTime datetime, boolean hasTime, String bookingLink, boolean isCancelled, String performanceTypeString) {
        this.id = id;
        this.day = datetime.format(java.time.format.DateTimeFormatter.ofPattern("dd"));
        this.month = datetime.format(java.time.format.DateTimeFormatter.ofPattern("MMMM"));
        if(hasTime) {
            this.time = datetime.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
        }
        this.bookingLink = bookingLink;
        this.isCancelled = isCancelled;
        this.performanceType = performanceTypeString;
    }
    public static class Converter {
        public static PerformanceDTO toDTO(Performance performance) {
            return new PerformanceDTO(performance.id, performance.datetime, performance.hasTime, performance.bookingLink, performance.isCancelled, performance.performanceType);
        }
    }
}
