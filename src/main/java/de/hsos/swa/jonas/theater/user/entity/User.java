package de.hsos.swa.jonas.theater.user.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.Set;

@Entity(name = "AppUser")
public class User extends PanacheEntity {
    public String username;
    @ElementCollection
    public Set<Long> commentId;
    @OneToMany
    public Set<UserEvent> event;
    @OneToMany
    public Set<UserPerformance> performance;
}
