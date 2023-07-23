package de.hsos.swa.jonas.theater.shared.dto.internal;

import java.time.LocalDateTime;

/**
 * DTO for CalendarElement
 * Contains all necessary information for a CalendarElement of the theater-osnabrueck.de website
 */
public class CalendarElementDTO {
    public String infolink;
    public String stid;
    public String auid;
    public String overline;
    public String title;
    public String kind;
    public String location;

    public LocalDateTime datetime;
    public boolean hasTime;
    public String bookingLink;

    public boolean isCancelled;

    public String performanceType; // Letzte Vorstellung, Wiederaufnahme, offene Probe, Premiere, Zusatzvorstellung

    public CalendarElementDTO(String infoLink, String overline, String title, String sparte, String location, LocalDateTime datetime, boolean hasTime, String bookingLink, boolean isCancelled, String performanceTypeString) {
        this.infolink = infoLink;
        this.overline = overline;
        this.title = title;
        this.kind = sparte;
        this.location = location;
        this.datetime = datetime;
        this.hasTime = hasTime;
        this.bookingLink = bookingLink;
        this.isCancelled = isCancelled;
        this.performanceType = performanceTypeString;
    }

    public CalendarElementDTO() {

    }
}
