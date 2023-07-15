package de.hsos.swa.jonas.theater.eventmanagement.gateway;

import de.hsos.swa.jonas.theater.eventmanagement.boundary.dto.QueryParametersDTO;
import de.hsos.swa.jonas.theater.shared.Event;
import de.hsos.swa.jonas.theater.eventmanagement.entity.EventCatalog;
import javax.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class EventRepository implements EventCatalog {

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
                                                performance.datetime.compareTo(queryParametersDTO.startDateTimeFilter)>=0)
                                );
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

    private boolean isPerformanceWithinDateRange(Event event, LocalDateTime startDateTimeFilter, LocalDateTime endDateTimeFilter) {
        return true;
    }
}
