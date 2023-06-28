package de.hsos.swa.jonas.theater.playmanagement.gateway;

import de.hsos.swa.jonas.theater.playmanagement.boundary.dto.QueryParametersDTO;
import de.hsos.swa.jonas.theater.shared.Play;
import de.hsos.swa.jonas.theater.playmanagement.entity.PlayCatalog;
import io.quarkus.logging.Log;

import javax.enterprise.context.ApplicationScoped;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.sql.Timestamp;

@ApplicationScoped
public class PlayRepository implements PlayCatalog {
    @Override
    public Collection<Play> getPlays(String nameFilter, ArrayList<String> statusFilter, ArrayList<String> playTypeFilter, ArrayList<String> performanceTypeFilter, LocalDateTime startDateTimeFilter, LocalDateTime endDateTimeFilter, String include, long pageNumber, long pageSize) {

        return null;//TODO Cascade delete function

    }

    @Override
    public Collection<Play> getPlays(QueryParametersDTO queryParametersDTO) {
        List<Play> playlist = Play.listAll();
        try(Stream<Play> plays = playlist.stream()) {
            return plays
                    .filter(play -> queryParametersDTO.nameFilter == null || play.title.toLowerCase().contains(queryParametersDTO.nameFilter.toLowerCase()))
                    //.filter(play -> statusFilter == null|| statusFilter.contains(play.status))
                    .filter(play -> queryParametersDTO.playTypeFilter == null || queryParametersDTO.playTypeFilter.isEmpty()|| queryParametersDTO.playTypeFilter.contains(play.kind))
                    //.filter(play -> performanceTypeFilter == null || performanceTypeFilter.isEmpty() || performanceTypeFilter.contains(play.performances.contains(performanceTypeFilter)))
                    .filter(play -> {
                        if (queryParametersDTO.startDateTimeFilter == null || queryParametersDTO.endDateTimeFilter == null) {
                            return true;
                        }
                        Date start = Date.valueOf(queryParametersDTO.startDateTimeFilter.toLocalDate());
                        Log.info("start: " + start.toString());
                        Date end = Date.valueOf(queryParametersDTO.endDateTimeFilter.toLocalDate());
                        Log.info("end: " + end.toString());

                        return play.performances.stream()
                                .anyMatch(performance ->
                                        performance.date != null &&
                                                performance.date.getTime()>= start.getTime() &&
                                                performance.date.getTime() <= end.getTime()
                                );
                    })
                    .skip(queryParametersDTO.pageNumber * queryParametersDTO.pageSize)
                    .limit(queryParametersDTO.pageSize)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public long getPlaysCount(QueryParametersDTO queryParametersDTO) {
        List<Play> playlist = Play.listAll();
        try(Stream<Play> plays = playlist.stream()) {
            return plays
                    .filter(play -> queryParametersDTO.nameFilter == null || play.title.toLowerCase().contains(queryParametersDTO.nameFilter.toLowerCase()))
                    //.filter(play -> statusFilter == null|| statusFilter.contains(play.status))
                    .filter(play -> queryParametersDTO.playTypeFilter == null || queryParametersDTO.playTypeFilter.isEmpty()|| queryParametersDTO.playTypeFilter.contains(play.kind))
                    //.filter(play -> performanceTypeFilter == null || performanceTypeFilter.isEmpty() || performanceTypeFilter.contains(play.performances.contains(performanceTypeFilter)))
                    .filter(play -> {
                        if (queryParametersDTO.startDateTimeFilter == null || queryParametersDTO.endDateTimeFilter == null) {
                            return true;
                        }
                        Date start = Date.valueOf(queryParametersDTO.startDateTimeFilter.toLocalDate());
                        Log.info("start: " + start.toString());
                        Date end = Date.valueOf(queryParametersDTO.endDateTimeFilter.toLocalDate());
                        Log.info("end: " + end.toString());

                        return play.performances.stream()
                                .anyMatch(performance ->
                                        performance.date != null &&
                                                performance.date.getTime()>= start.getTime() &&
                                                performance.date.getTime() <= end.getTime()
                                );
                    })
                    .count();
        }
    }

    private boolean isPerformanceWithinDateRange(Play play, LocalDateTime startDateTimeFilter, LocalDateTime endDateTimeFilter) {
        return true;
    }
}
