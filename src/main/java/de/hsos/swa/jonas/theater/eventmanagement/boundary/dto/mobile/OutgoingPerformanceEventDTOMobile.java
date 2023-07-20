package de.hsos.swa.jonas.theater.eventmanagement.boundary.dto.mobile;

import de.hsos.swa.jonas.theater.eventmanagement.entity.Performance;
import de.hsos.swa.jonas.theater.userdata.entity.PerformanceState;

import java.time.LocalDateTime;

public class OutgoingPerformanceEventDTOMobile {
    public long performanceId;
    public long eventId;
    public String title;
    public String kind;
    public String thumbnailPath;
    public String month;
    public String day;
    public String time;
    public PerformanceState performanceState;
    public boolean isCancelled;
    public String performanceType;

    public OutgoingPerformanceEventDTOMobile(long performanceId, long eventId, String title, String kind, String thumbnailPath, LocalDateTime datetime, boolean hasTime, boolean isCancelled, String performanceType) {
        this.performanceId = performanceId;
        this.eventId = eventId;
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
        public static OutgoingPerformanceEventDTOMobile toDTO(Performance performance) {
            return new OutgoingPerformanceEventDTOMobile(performance.id, performance.getEvent().id, performance.getEvent().getTitle(), performance.getEvent().getKind(), performance.getEvent().getThumbnailPath(), performance.getDatetime(), performance.isHasTime(), performance.isCancelled(), performance.getPerformanceType());
        }
    }

    public void setPerformanceState(PerformanceState performanceState) {
        this.performanceState = performanceState;
    }
}
