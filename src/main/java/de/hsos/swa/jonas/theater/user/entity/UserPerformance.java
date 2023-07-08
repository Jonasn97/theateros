package de.hsos.swa.jonas.theater.user.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.Entity;

@Entity
public class UserPerformance extends PanacheEntity {
    public long eventId;
    public PerformanceState state;
}
