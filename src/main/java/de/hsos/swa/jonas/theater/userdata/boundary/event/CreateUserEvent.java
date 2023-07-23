package de.hsos.swa.jonas.theater.userdata.boundary.event;

import de.hsos.swa.jonas.theater.userdata.entity.Userdata;
import io.quarkus.logging.Log;

import javax.enterprise.event.Observes;
import javax.enterprise.event.ObservesAsync;
import javax.transaction.Transactional;
import java.util.concurrent.CompletableFuture;

/**
 * Event for creating a user in userdata observed from @see RegisterResourceMobile
 */
@Transactional(Transactional.TxType.REQUIRES_NEW)
public class CreateUserEvent {

    public CompletableFuture<String> createUser(@Observes String username){
        try {
            Userdata.add(username);
            return CompletableFuture.completedFuture(username + " added!");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
