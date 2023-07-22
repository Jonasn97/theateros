package de.hsos.swa.jonas.theater.userdata.control;

import de.hsos.swa.jonas.theater.shared.EventState;
import de.hsos.swa.jonas.theater.userdata.boundary.dto.UserParametersDTO;
import de.hsos.swa.jonas.theater.userdata.entity.UserDataCatalog;
import de.hsos.swa.jonas.theater.userdata.entity.UserEvent;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
@ApplicationScoped
public class UserDataService implements UserDataOperations {
    @Inject
    UserDataCatalog userDataCatalog;

    @Override
    public Map<Long, EventState> getEventStatesByEventIdsForUser(String username, Set<Long> eventIds) {
        return null;
    }

    @Override
    public Collection<UserEvent> getUserEventsForUser(UserParametersDTO userParametersDTO) {
        return userDataCatalog.getUserEventsForUser(userParametersDTO);
    }

    @Override
    public long getUserEventsForUserCount(UserParametersDTO userParametersDTO) {
        return userDataCatalog.getUserEventsForUserCount(userParametersDTO);
    }
}
