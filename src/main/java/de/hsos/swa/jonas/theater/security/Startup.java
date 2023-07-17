package de.hsos.swa.jonas.theater.security;

import de.hsos.swa.jonas.theater.security.entity.User;
import io.quarkus.runtime.StartupEvent;

import javax.enterprise.event.Observes;
import javax.inject.Singleton;
import javax.transaction.Transactional;

@Singleton
public class Startup {
    @Transactional
    public void init(@Observes StartupEvent event){
        User.deleteAll();
        User.add("user", "user", "user");
        User.add("admin", "admin", "admin");
    }
}
