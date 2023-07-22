package de.hsos.swa.jonas.theater.userdata.control;

import de.hsos.swa.jonas.theater.shared.EventState;
import de.hsos.swa.jonas.theater.userdata.boundary.dto.UserParametersDTO;
import de.hsos.swa.jonas.theater.userdata.entity.UserEvent;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface UserDataOperations {
    Map<Long, EventState> getEventStatesByEventIdsForUser(String username, Set<Long> eventIds);
    Collection<UserEvent> getUserEventsForUser(UserParametersDTO userParametersDTO);


    long getUserEventsForUserCount(UserParametersDTO userParametersDTO);
}
