package de.hsos.swa.jonas.theater.shared;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Entity;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

@Entity
public class Performance extends PanacheEntity {
        @UpdateTimestamp
        public Timestamp lastUpdateTimestamp;
        @CreationTimestamp
        public Timestamp createdTimestamp;
        public Date date;
        public Time time;
        public String bookingLink;

        public boolean isCancelled;

        public String performanceType; // Letzte Vorstellung, Wiederaufnahme, offene Probe, Premiere, Zusatzvorstellung

        //freie Plätze

        public Performance() {
        }


        public Performance(Time time, Date date, String bookingLink, boolean isCancelled, String performanceTypeString) {
                this.time = time;
                this.date = date;
                this.bookingLink = bookingLink;
                this.isCancelled = isCancelled;
                this.performanceType = performanceTypeString;
        }
}
