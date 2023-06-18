package de.hsos.swa.jonas.theater.crawler.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.Entity;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

@Entity
public class Performance extends PanacheEntity {

        public Timestamp timestamp;
        public Date date;
        public Time time;
        public String bookingLink;

        public boolean isCancelled;

        public String performanceType; // Letzte Vorstellung, Wiederaufnahme, offene Probe, Premiere, Zusatzvorstellung

        //freie Plätze

        public Performance() {
        }


}
