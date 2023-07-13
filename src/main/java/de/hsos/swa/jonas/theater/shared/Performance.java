package de.hsos.swa.jonas.theater.shared;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
public class Performance extends PanacheEntity {
        @UpdateTimestamp
        public Timestamp lastUpdateTimestamp;
        @CreationTimestamp
        public Timestamp createdTimestamp;
        @Convert(converter = LocalDateTimeConverter.class)
        public LocalDateTime datetime;
        public String auid;
        public String bookingLink;
        public boolean hasTime;

        public boolean isCancelled;

        public String performanceType; // Letzte Vorstellung, Wiederaufnahme, offene Probe, Premiere, Zusatzvorstellung

        //freie Plätze

        public Performance() {
        }


        public Performance(String auid, LocalDateTime datetime, boolean hasTime, String bookingLink, boolean isCancelled, String performanceTypeString) {
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
}
