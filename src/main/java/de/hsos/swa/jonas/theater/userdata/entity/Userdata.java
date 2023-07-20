package de.hsos.swa.jonas.theater.userdata.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.transaction.Transactional;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Set;

@Entity(name = "userdata")
public class Userdata extends PanacheEntity {
    @Size(min=3, max=25)
    @Pattern(regexp = "^[a-zA-z ]*$")
    public String username;
    @ElementCollection
    public Set<Long> commentIds;
    @OneToMany
    public Set<UserEvent> userEvents;
    @OneToMany
    public Set<UserPerformance> userPerformances;

    @Transactional(Transactional.TxType.MANDATORY)
    public static boolean add(String username){
        Userdata.find("username", username).firstResultOptional().ifPresentOrElse(
                user -> {
                    throw new RuntimeException("User already exists");
                },
                () -> {
                    Userdata user = new Userdata();
                    user.username = username;
                    user.persist();
                }
        );
        return false;
    }
}
