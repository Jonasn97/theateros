package de.hsos.swa.jonas.theater.eventmanagement.gateway;

import de.hsos.swa.jonas.theater.eventmanagement.boundary.dto.QueryParametersDTO;
import de.hsos.swa.jonas.theater.eventmanagement.boundary.dto.api.IncomingEventIdDTOApi;
import de.hsos.swa.jonas.theater.eventmanagement.entity.Event;
import de.hsos.swa.jonas.theater.eventmanagement.entity.EventCatalog;
import de.hsos.swa.jonas.theater.eventmanagement.entity.Performance;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Repository for Events
 */
@ApplicationScoped
public class EventRepository implements EventCatalog, PanacheRepository<Performance> {

    /**
     * @param queryParametersDTO with filters and pagination
     * @return filtered and paginated events
     * filters by title, kind, startDateTime, endDateTime
     * startDateTime and endDateTime are filtered by performances and returns all events that have at least one performance in the given time frame
     */
    @Override
    @Transactional(Transactional.TxType.MANDATORY)
    public Collection<Event> getEvents(QueryParametersDTO queryParametersDTO) {
        List<Event> playlist = Event.listAll();
        try(Stream<Event> plays = playlist.stream()) {
            return plays
                    .filter(play -> queryParametersDTO.nameFilter == null || play.getTitle().toLowerCase().contains(queryParametersDTO.nameFilter.toLowerCase()))
                    .filter(play -> queryParametersDTO.kindFilter == null || queryParametersDTO.kindFilter.isEmpty()|| queryParametersDTO.kindFilter.contains(play.getKind()))
                    .filter(play -> {
                        if (queryParametersDTO.startDateTimeFilter == null || queryParametersDTO.endDateTimeFilter == null) {
                            return true;
                        }
                        return play.getPerformances().stream()
                                .anyMatch(performance ->
                                        (performance.getDatetime() != null &&
                                                performance.getDatetime().compareTo(queryParametersDTO.startDateTimeFilter)>=0 && performance.getDatetime().compareTo(queryParametersDTO.endDateTimeFilter)<=0
                                ));
                    })
                    .skip(queryParametersDTO.pageNumber * queryParametersDTO.pageSize)
                    .limit(queryParametersDTO.pageSize)
                    .collect(Collectors.toList());
        }
    }

    /**
     * @param queryParametersDTO with filters and pagination
     * @return count of filtered events for pagination
     */
    @Override
    @Transactional(Transactional.TxType.MANDATORY)
    public long getEventsCount(QueryParametersDTO queryParametersDTO) {
        List<Event> playlist = Event.listAll();
        try(Stream<Event> plays = playlist.stream()) {
            return plays
                    .filter(play -> queryParametersDTO.nameFilter == null || play.getTitle().toLowerCase().contains(queryParametersDTO.nameFilter.toLowerCase()))
                    .filter(play -> queryParametersDTO.kindFilter == null || queryParametersDTO.kindFilter.isEmpty()|| queryParametersDTO.kindFilter.contains(play.getKind()))
                    .filter(play -> {
                        if (queryParametersDTO.startDateTimeFilter == null || queryParametersDTO.endDateTimeFilter == null) {
                            return true;
                        }

                        return play.getPerformances().stream()
                                .anyMatch(performance ->
                                        (performance.getDatetime() != null &&
                                                performance.getDatetime().compareTo(queryParametersDTO.startDateTimeFilter)>=0)
                                );
                    })
                    .count();
        }
    }

    /**
     * @param playId id of event
     * @return event with given id
     */
    @Override
    @Transactional(Transactional.TxType.MANDATORY)
    public Optional<Event> getEventById(long playId) {
        return Event.findByIdOptional(playId);
    }

    /**
     * @param id id of event
     * @return performances of event with given id
     */
    @Override
    @Transactional(Transactional.TxType.MANDATORY)
    public Collection<Performance> getPerformancesByEventId(long id) {
        Optional<Event> event = Event.findByIdOptional(id);
        if (event.isPresent()) {
            return event.get().getPerformances();
        }
        return Collections.emptyList();
    }

    /** Deletes event with given id
     * @param eventId id of event
     * @return true if event was deleted
     */
    @Override
    public boolean deleteEventById(long eventId) {
        return Event.deleteById(eventId);
    }

    /** Updates event with given id
     * @param eventId id of event
     * @param incomingEventIdDTOApi updated event
     * @return updated event
     */
    @Override
    public Optional<Event> updateEventById(long eventId, IncomingEventIdDTOApi incomingEventIdDTOApi) {
        Optional<Event> event = Event.findByIdOptional(eventId);
        if (event.isPresent()) {
            Event event1 = event.get();
            event1.setTitle(incomingEventIdDTOApi.title);
            event1.setKind(incomingEventIdDTOApi.kind);
            event1.setDescription(incomingEventIdDTOApi.description);
            event1.setDuration(incomingEventIdDTOApi.duration);
            event1.setBannerPath(incomingEventIdDTOApi.bannerPath);
            event1.setThumbnailPath(incomingEventIdDTOApi.thumbnailPath);
            event1.setTeam(incomingEventIdDTOApi.team);
            event1.setPress(incomingEventIdDTOApi.press);
            event1.setPlaytype(incomingEventIdDTOApi.playtype);
            event1.persist();
            return Optional.of(event1);
        }
        return Optional.empty();
    }
}
