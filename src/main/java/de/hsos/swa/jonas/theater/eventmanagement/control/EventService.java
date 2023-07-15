package de.hsos.swa.jonas.theater.eventmanagement.control;

import de.hsos.swa.jonas.theater.eventmanagement.boundary.dto.QueryParametersDTO;
import de.hsos.swa.jonas.theater.shared.Event;
import de.hsos.swa.jonas.theater.eventmanagement.entity.EventCatalog;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Collection;
import java.util.Optional;

@ApplicationScoped
public class EventService implements EventOperations {
    @Inject
    EventCatalog eventCatalog;

    @Override
    public Collection<Event> getEvents(QueryParametersDTO queryParametersDTO) {
        return eventCatalog.getEvents(queryParametersDTO);
    }

    @Override
    public long getEventsCount(QueryParametersDTO queryParametersDTO) {
        return eventCatalog.getEventsCount(queryParametersDTO);
    }

    @Override
    public Optional<Event> getEventsById(long playId) {
        return eventCatalog.getEventsById(playId);
    }
}
