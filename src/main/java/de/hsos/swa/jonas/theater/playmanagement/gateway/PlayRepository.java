package de.hsos.swa.jonas.theater.playmanagement.gateway;

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
        if(nameFilter == null) Log.info("nameFilter is null");
        if(statusFilter == null) Log.info("statusFilter is null");
        if(playTypeFilter == null) Log.info("playTypeFilter is null");
        if(performanceTypeFilter == null) Log.info("performanceTypeFilter is null");
        if(startDateTimeFilter == null) Log.info("startDateTimeFilter is null");
        if(endDateTimeFilter == null) Log.info("endDateTimeFilter is null");
        if(include == null) Log.info("include is null");
        if(pageNumber == 0) Log.info("pageNumber is 0");
        if(pageSize == 0) Log.info("pageSize is 0");


        List<Play> playlist = Play.listAll();
        try(Stream<Play> plays = playlist.stream()) {
            return plays
                    .filter(play -> nameFilter == null || play.title.toLowerCase().contains(nameFilter.toLowerCase()))
                    //.filter(play -> statusFilter == null|| statusFilter.contains(play.status))
                    .filter(play -> playTypeFilter == null || playTypeFilter.isEmpty()|| playTypeFilter.contains(play.kind))
                    //.filter(play -> performanceTypeFilter == null || performanceTypeFilter.isEmpty() || performanceTypeFilter.contains(play.performances.contains(performanceTypeFilter)))
                    .filter(play -> {
                        if (startDateTimeFilter == null || endDateTimeFilter == null) {
                            return true;
                        }
                        Date start = Date.valueOf(startDateTimeFilter.toLocalDate());
                        Log.info("start: " + start.toString());
                        Date end = Date.valueOf(endDateTimeFilter.toLocalDate());
                        Log.info("end: " + end.toString());

                        return play.performances.stream()
                                .anyMatch(performance ->
                                        performance.date != null &&
                                                performance.date.getTime()>= start.getTime() &&
                                                performance.date.getTime() <= end.getTime()
                                );
                    })
                    .skip(pageNumber * pageSize)
                    .limit(pageSize)
                    .collect(Collectors.toList());
        }

    }

    private boolean isPerformanceWithinDateRange(Play play, LocalDateTime startDateTimeFilter, LocalDateTime endDateTimeFilter) {
        return true;
    }
}
