package de.hsos.swa.jonas.theater.userdata.entity;

import de.hsos.swa.jonas.theater.shared.EventState;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 * Entity for UserEvent
 */
@Entity
public class UserEvent extends PanacheEntity {
    private long eventId;
    private boolean isFavorite;
    @Enumerated(EnumType.STRING)
    private EventState eventState;

    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public EventState getEventState() {
        return eventState;
    }

    public void setEventState(EventState eventState) {
        this.eventState = eventState;
    }
}
