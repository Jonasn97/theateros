package de.hsos.swa.jonas.theater.userdata.entity;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface UserDataCatalog {
    Map<Long, EventState> getEventState(String username, Set<Long> eventIds);
    Optional<EventState> getEventState(String username, long eventId);
}
