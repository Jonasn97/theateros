package de.hsos.swa.jonas.theater.eventmanagement.control;

import de.hsos.swa.jonas.theater.eventmanagement.boundary.dto.QueryParametersDTO;
import de.hsos.swa.jonas.theater.eventmanagement.entity.Event;
import de.hsos.swa.jonas.theater.eventmanagement.entity.Performance;
import de.hsos.swa.jonas.theater.shared.EventState;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface EventOperations {
    Collection<Event> getEvents(QueryParametersDTO queryParametersDTO);

    long getEventsCount(QueryParametersDTO queryParametersDTO);

    Optional<Event> getEventsById(long playId);
    Map<Long, EventState> getEventStatus(String username, Set<Long> eventIds);
    Optional<EventState> getEventStatus(String username, Long eventId);

    Collection<Performance> getPerformances(QueryParametersDTO queryParametersDTO);

    Optional<Performance> getPerformance(Long id);
}
