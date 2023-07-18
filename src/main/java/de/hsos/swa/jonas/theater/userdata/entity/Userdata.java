package de.hsos.swa.jonas.theater.userdata.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.transaction.Transactional;
import java.util.Set;

@Entity(name = "userdata")
public class Userdata extends PanacheEntity {
    public String username;
    @ElementCollection
    public Set<Long> commentId;
    @OneToMany
    public Set<UserEvent> event;
    @OneToMany
    public Set<UserPerformance> performance;

    @Transactional(Transactional.TxType.MANDATORY)
    public static void add(String username){
        Userdata userdata = new Userdata();
        userdata.username = username;
        userdata.persist();
    }
}
