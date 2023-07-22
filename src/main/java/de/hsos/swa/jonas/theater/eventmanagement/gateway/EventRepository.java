package de.hsos.swa.jonas.theater.eventmanagement.gateway;

import de.hsos.swa.jonas.theater.eventmanagement.boundary.dto.QueryParametersDTO;
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

@ApplicationScoped
public class EventRepository implements EventCatalog, PanacheRepository<Performance> {

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

    @Override
    @Transactional(Transactional.TxType.MANDATORY)
    public Optional<Event> getEventById(long playId) {
        return Event.findByIdOptional(playId);
    }

    @Override
    @Transactional(Transactional.TxType.MANDATORY)
    public Collection<Performance> getPerformancesByEventId(long id) {
        Optional<Event> event = Event.findByIdOptional(id);
        if (event.isPresent()) {
            return event.get().getPerformances();
        }
        return Collections.emptyList();
    }
}
