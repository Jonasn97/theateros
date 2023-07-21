package de.hsos.swa.jonas.theater.userdata.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Entity
public class UserPerformance extends PanacheEntity {
    public long performanceId;
    @Enumerated(EnumType.STRING)
    public PerformanceState performanceState;
}
