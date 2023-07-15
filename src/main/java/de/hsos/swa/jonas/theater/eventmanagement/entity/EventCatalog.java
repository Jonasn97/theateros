package de.hsos.swa.jonas.theater.eventmanagement.entity;

import de.hsos.swa.jonas.theater.eventmanagement.boundary.dto.QueryParametersDTO;
import de.hsos.swa.jonas.theater.shared.Event;

import java.util.Collection;
import java.util.Optional;

public interface EventCatalog {

    Collection<Event> getEvents(QueryParametersDTO queryParametersDTO);

    long getEventsCount(QueryParametersDTO queryParametersDTO);

    Optional<Event> getEventsById(long playId);
}
