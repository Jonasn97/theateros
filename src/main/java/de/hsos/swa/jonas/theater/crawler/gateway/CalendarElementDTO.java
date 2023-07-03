package de.hsos.swa.jonas.theater.crawler.gateway;

import java.sql.Date;
import java.sql.Time;

public class CalendarElementDTO {
    public String infolink;
    public String stid;
    public String auid;
    public String overline;
    public String title;
    public String kind;
    public String location;

    public Date date;
    public Time time;
    public String bookingLink;

    public boolean isCancelled;

    public String performanceType; // Letzte Vorstellung, Wiederaufnahme, offene Probe, Premiere, Zusatzvorstellung

    public CalendarElementDTO(String infoLink, String overline, String title, String sparte, String location, Date date, Time time, String bookingLink, boolean isCancelled, String performanceTypeString) {
        this.infolink = infoLink;
        this.overline = overline;
        this.title = title;
        this.kind = sparte;
        this.location = location;
        this.date = date;
        this.time = time;
        this.bookingLink = bookingLink;
        this.isCancelled = isCancelled;
        this.performanceType = performanceTypeString;
    }

    public CalendarElementDTO() {

    }
}
