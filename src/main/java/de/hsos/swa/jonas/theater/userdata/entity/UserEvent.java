package de.hsos.swa.jonas.theater.userdata.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.Entity;

@Entity
public class UserEvent extends PanacheEntity {
    public long eventId;
    public boolean isFavorite;
    public EventState state;

}
