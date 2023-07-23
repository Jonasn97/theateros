package de.hsos.swa.jonas.theater.eventmanagement.control;

import de.hsos.swa.jonas.theater.eventmanagement.boundary.dto.QueryParametersDTO;
import de.hsos.swa.jonas.theater.eventmanagement.boundary.dto.api.IncomingEventIdDTOApi;
import de.hsos.swa.jonas.theater.eventmanagement.entity.Event;
import de.hsos.swa.jonas.theater.eventmanagement.entity.EventCatalog;
import de.hsos.swa.jonas.theater.eventmanagement.entity.Performance;
import de.hsos.swa.jonas.theater.shared.EventState;
import de.hsos.swa.jonas.theater.userdata.entity.UserDataCatalog;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.*;

@ApplicationScoped
public class EventService implements EventOperations {
    @Inject
    EventCatalog eventCatalog;
    @Inject
    UserDataCatalog userDataCatalog;

    @Override
    public Collection<Event> getEvents(QueryParametersDTO queryParametersDTO) {
        return eventCatalog.getEvents(queryParametersDTO);
    }

    @Override
    public long getEventsCount(QueryParametersDTO queryParametersDTO) {
        return eventCatalog.getEventsCount(queryParametersDTO);
    }

    @Override
    public Optional<Event> getEventById(long playId) {
        return eventCatalog.getEventById(playId);
    }

    @Override
    public Map<Long, EventState> getEventStatus(String username, Set<Long> eventIds) {
        return userDataCatalog.getEventState(username, eventIds);
    }

    @Override
    public Optional<EventState> getEventStatus(String username, long eventId) {
        return userDataCatalog.getEventState(username, eventId);
    }

    @Override
    public Collection<Performance> getPerformancesByEventId(long id) {
        return eventCatalog.getPerformancesByEventId(id);
    }

    @Override
    public Optional<Performance> getNextPerformance(Event event) {
        LocalDateTime currentTime = LocalDateTime.now();
        return event.getPerformances().stream().filter(performance -> !performance.isCancelled())
                .filter(performance -> performance.getDatetime() != null)
                .filter(performance -> performance.getDatetime().isAfter(currentTime))
                .min(Comparator.comparing(Performance::getDatetime));
    }

    @Override
    public boolean isFavorite(String username, long id) {
        return userDataCatalog.isFavorite(username, id);
    }

    @Override
    public boolean deleteEventById(long eventId) {
        return eventCatalog.deleteEventById(eventId);
    }

    @Override
    public Optional<Event> updateEventById(long eventId, IncomingEventIdDTOApi incomingEventIdDTOApi) {
        return eventCatalog.updateEventById(eventId, incomingEventIdDTOApi);
    }
}
