package de.hsos.swa.jonas.theater.eventmanagement.boundary.dto;

import de.hsos.swa.jonas.theater.shared.Performance;

import java.time.LocalDateTime;

public class PerformanceEventDTO {
    public long id;
    public String title;
    public String kind;
    public String thumbnailPath;
    public String month;
    public String day;
    public String time;
    public boolean isCancelled;
    public String performanceType;

    public PerformanceEventDTO(long id, String title, String kind, String thumbnailPath, LocalDateTime datetime, boolean hasTime, boolean isCancelled, String performanceType) {
        this.id = id;
        this.title = title;
        this.kind = kind;
        this.thumbnailPath = thumbnailPath;
        this.day = datetime.format(java.time.format.DateTimeFormatter.ofPattern("dd"));
        this.month = datetime.format(java.time.format.DateTimeFormatter.ofPattern("MMMM"));
        if(hasTime) {
            this.time = datetime.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
        }
        this.isCancelled = isCancelled;
        this.performanceType = performanceType;
    }
    public static class Converter {
        public static PerformanceEventDTO toDTO(Performance performance) {
            return new PerformanceEventDTO(performance.id, performance.event.title, performance.event.kind, performance.event.thumbnailPath, performance.datetime, performance.hasTime, performance.isCancelled, performance.performanceType);
        }
    }
}
