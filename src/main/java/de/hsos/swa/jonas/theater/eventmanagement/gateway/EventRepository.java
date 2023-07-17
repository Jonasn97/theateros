package de.hsos.swa.jonas.theater.eventmanagement.gateway;

import de.hsos.swa.jonas.theater.eventmanagement.boundary.dto.QueryParametersDTO;
import de.hsos.swa.jonas.theater.shared.Event;
import de.hsos.swa.jonas.theater.eventmanagement.entity.EventCatalog;
import de.hsos.swa.jonas.theater.shared.Performance;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.Query;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class EventRepository implements EventCatalog, PanacheRepository<Performance> {

    @Override
    public Collection<Event> getEvents(QueryParametersDTO queryParametersDTO) {
        List<Event> playlist = Event.listAll();
        try(Stream<Event> plays = playlist.stream()) {
            return plays
                    .filter(play -> queryParametersDTO.nameFilter == null || play.title.toLowerCase().contains(queryParametersDTO.nameFilter.toLowerCase()))
                    //.filter(play -> statusFilter == null|| statusFilter.contains(play.status))
                    .filter(play -> queryParametersDTO.playTypeFilter == null || queryParametersDTO.playTypeFilter.isEmpty()|| queryParametersDTO.playTypeFilter.contains(play.kind))
                    //.filter(play -> performanceTypeFilter == null || performanceTypeFilter.isEmpty() || performanceTypeFilter.contains(play.performances.contains(performanceTypeFilter)))
                    .filter(play -> {
                        if (queryParametersDTO.startDateTimeFilter == null || queryParametersDTO.endDateTimeFilter == null) {
                            return true;
                        }
                        return play.performances.stream()
                                .anyMatch(performance ->
                                        (performance.datetime != null &&
                                                performance.datetime.compareTo(queryParametersDTO.startDateTimeFilter)>=0 && performance.datetime.compareTo(queryParametersDTO.endDateTimeFilter)<=0
                                ));
                    })
                    .skip(queryParametersDTO.pageNumber * queryParametersDTO.pageSize)
                    .limit(queryParametersDTO.pageSize)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public long getEventsCount(QueryParametersDTO queryParametersDTO) {
        List<Event> playlist = Event.listAll();
        try(Stream<Event> plays = playlist.stream()) {
            return plays
                    .filter(play -> queryParametersDTO.nameFilter == null || play.title.toLowerCase().contains(queryParametersDTO.nameFilter.toLowerCase()))
                    //.filter(play -> statusFilter == null|| statusFilter.contains(play.status))
                    .filter(play -> queryParametersDTO.playTypeFilter == null || queryParametersDTO.playTypeFilter.isEmpty()|| queryParametersDTO.playTypeFilter.contains(play.kind))
                    //.filter(play -> performanceTypeFilter == null || performanceTypeFilter.isEmpty() || performanceTypeFilter.contains(play.performances.contains(performanceTypeFilter)))
                    .filter(play -> {
                        if (queryParametersDTO.startDateTimeFilter == null || queryParametersDTO.endDateTimeFilter == null) {
                            return true;
                        }

                        return play.performances.stream()
                                .anyMatch(performance ->
                                        (performance.datetime != null &&
                                                performance.datetime.compareTo(queryParametersDTO.startDateTimeFilter)>=0)
                                );
                    })
                    .count();
        }
    }

    @Override
    public Optional<Event> getEventsById(long playId) {
        return Event.findByIdOptional(playId);
    }

    @Override
    public Collection<Performance> getPerformances(QueryParametersDTO queryParametersDTO) {
        List<Performance> performances = Performance.listAll();
        return performances.stream()
                //.filter(performance -> performance.datetime == null || performance.datetime.isAfter(queryParametersDTO.startDateTimeFilter) && performance.datetime.isBefore(queryParametersDTO.endDateTimeFilter))
                .filter(performance -> queryParametersDTO.nameFilter == null || performance.event.title.toLowerCase().contains(queryParametersDTO.nameFilter.toLowerCase()))
                .filter(performance -> queryParametersDTO.playTypeFilter == null || queryParametersDTO.playTypeFilter.isEmpty()|| queryParametersDTO.playTypeFilter.contains(performance.event.kind))
                //.filter(play -> performanceTypeFilter == null || performanceTypeFilter.isEmpty() || performanceTypeFilter.contains(play.performances.contains(performanceTypeFilter)))
                .skip(queryParametersDTO.pageNumber * queryParametersDTO.pageSize)
                .limit(queryParametersDTO.pageSize)
                .collect(Collectors.toList());
    }

    private boolean isPerformanceWithinDateRange(Event event, LocalDateTime startDateTimeFilter, LocalDateTime endDateTimeFilter) {
        return true;
    }
}
