package de.hsos.swa.jonas.theater.userdata.control;

import de.hsos.swa.jonas.theater.userdata.entity.EventState;
import de.hsos.swa.jonas.theater.userdata.entity.UserDataCatalog;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Map;
import java.util.Set;
@ApplicationScoped
public class UserDataService implements UserDataOperations {
    @Inject
    UserDataCatalog userDataCatalog;

    @Override
    public Map<Long, EventState> getEventState(String username, Set<Long> eventIds) {
        return userDataCatalog.getEventState(username, eventIds);
    }
}
