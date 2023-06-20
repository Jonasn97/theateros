package de.hsos.swa.jonas.theater.crawler.gateway;

import java.sql.Date;
import java.sql.Time;

public class PerformanceDTO {
    public Date date;
    public Time time;
    public String bookingLink;

    public boolean isCancelled;

    public String performanceType; // Letzte Vorstellung, Wiederaufnahme, offene Probe, Premiere, Zusatzvorstellung

    public PerformanceDTO() {
    }

    public PerformanceDTO(Time time, Date date, String bookingLink, boolean isCancelled, String performanceTypeString) {
        this.time = time;
        this.date = date;
        this.bookingLink = bookingLink;
        this.isCancelled = isCancelled;
        this.performanceType = performanceTypeString;
    }
}
