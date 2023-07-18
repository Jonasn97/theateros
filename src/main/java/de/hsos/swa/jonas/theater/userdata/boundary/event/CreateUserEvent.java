package de.hsos.swa.jonas.theater.userdata.boundary.event;

import de.hsos.swa.jonas.theater.userdata.entity.Userdata;
import io.quarkus.logging.Log;

import javax.enterprise.event.Observes;
import javax.transaction.Transactional;

@Transactional(Transactional.TxType.REQUIRES_NEW)
public class CreateUserEvent {

    public void createUser(@Observes String username){
        Userdata.add(username);
    }
}
