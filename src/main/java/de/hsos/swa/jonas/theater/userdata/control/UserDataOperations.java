package de.hsos.swa.jonas.theater.userdata.control;

import de.hsos.swa.jonas.theater.shared.EventState;

import java.util.Map;
import java.util.Set;

public interface UserDataOperations {
    Map<Long, EventState> getEventState(String username, Set<Long> eventIds);

}
