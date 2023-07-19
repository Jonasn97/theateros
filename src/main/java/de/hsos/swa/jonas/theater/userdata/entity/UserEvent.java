package de.hsos.swa.jonas.theater.userdata.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Entity
public class UserEvent extends PanacheEntity {
    public long eventId;
    public boolean isFavorite;
    @Enumerated(EnumType.STRING)
    public EventState eventState;

}
