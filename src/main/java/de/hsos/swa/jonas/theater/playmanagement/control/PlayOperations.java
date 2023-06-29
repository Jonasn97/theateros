package de.hsos.swa.jonas.theater.playmanagement.control;

import de.hsos.swa.jonas.theater.playmanagement.boundary.dto.QueryParametersDTO;
import de.hsos.swa.jonas.theater.shared.Play;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public interface PlayOperations {
    Collection<Play> getPlays(String nameFilter, ArrayList<String> statusFilter, ArrayList<String> playTypeFilter, ArrayList<String> performanceTypeFilter, LocalDateTime startDateTimeFilter, LocalDateTime endDateTimeFilter, String include, long pageNumber, long pageSize);

    Collection<Play> getPlays(QueryParametersDTO queryParametersDTO);

    long getPlaysCount(QueryParametersDTO queryParametersDTO);

    Optional<Play> getPlayById(long playId);
}
