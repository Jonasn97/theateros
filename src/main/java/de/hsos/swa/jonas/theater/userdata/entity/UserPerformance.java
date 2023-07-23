package de.hsos.swa.jonas.theater.userdata.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Entity
public class UserPerformance extends PanacheEntity {
    private long performanceId;

    @Enumerated(EnumType.STRING)
    private PerformanceState performanceState;

    public long getPerformanceId() {
        return performanceId;
    }

    public void setPerformanceId(long performanceId) {
        this.performanceId = performanceId;
    }

    public PerformanceState getPerformanceState() {
        return performanceState;
    }

    public void setPerformanceState(PerformanceState performanceState) {
        this.performanceState = performanceState;
    }


}
