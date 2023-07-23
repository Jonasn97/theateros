package de.hsos.swa.jonas.theater.eventmanagement.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Entity for Performances
 * Contains all necessary information for a Performance of the theater-osnabrueck.de website
 */
@Entity
public class Performance extends PanacheEntity {

        @ManyToOne
        private Event event;

        @UpdateTimestamp
        private Timestamp lastUpdateTimestamp;
        @CreationTimestamp
        private Timestamp createdTimestamp;
        @Convert(converter = LocalDateTimeConverter.class)
        private LocalDateTime datetime;
        private String auid;
        private String bookingLink;
        private boolean hasTime;

        private boolean isCancelled;

        private String performanceType; // Letzte Vorstellung, Wiederaufnahme, offene Probe, Premiere, Zusatzvorstellung

        //freie Plätze

        public Performance() {
        }


        public Performance(Event event, String auid, LocalDateTime datetime, boolean hasTime, String bookingLink, boolean isCancelled, String performanceTypeString) {
                this.event = event;
                this.auid = auid;
                this.datetime = datetime;
                this.hasTime = hasTime;
                this.bookingLink = bookingLink;
                this.isCancelled = isCancelled;
                this.performanceType = performanceTypeString;
        }
        @Converter
        public static class LocalDateTimeConverter implements AttributeConverter<LocalDateTime, Timestamp> {
                @Override
                public Timestamp convertToDatabaseColumn(LocalDateTime localDateTime) {
                        if (localDateTime != null) {
                                return Timestamp.valueOf(localDateTime);
                        }
                        return null;
                }

                @Override
                public LocalDateTime convertToEntityAttribute(Timestamp timestamp) {
                        if (timestamp != null) {
                                return timestamp.toLocalDateTime();
                        }
                        return null;
                }
        }

        public Event getEvent() {
                return event;
        }

        public void setEvent(Event event) {
                this.event = event;
        }

        public Timestamp getLastUpdateTimestamp() {
                return lastUpdateTimestamp;
        }

        public void setLastUpdateTimestamp(Timestamp lastUpdateTimestamp) {
                this.lastUpdateTimestamp = lastUpdateTimestamp;
        }

        public Timestamp getCreatedTimestamp() {
                return createdTimestamp;
        }

        public void setCreatedTimestamp(Timestamp createdTimestamp) {
                this.createdTimestamp = createdTimestamp;
        }

        public LocalDateTime getDatetime() {
                return datetime;
        }

        public void setDatetime(LocalDateTime datetime) {
                this.datetime = datetime;
        }

        public String getAuid() {
                return auid;
        }

        public void setAuid(String auid) {
                this.auid = auid;
        }

        public String getBookingLink() {
                return bookingLink;
        }

        public void setBookingLink(String bookingLink) {
                this.bookingLink = bookingLink;
        }

        public boolean isHasTime() {
                return hasTime;
        }

        public void setHasTime(boolean hasTime) {
                this.hasTime = hasTime;
        }

        public boolean isCancelled() {
                return isCancelled;
        }

        public void setCancelled(boolean cancelled) {
                isCancelled = cancelled;
        }

        public String getPerformanceType() {
                return performanceType;
        }

        public void setPerformanceType(String performanceType) {
                this.performanceType = performanceType;
        }
}
