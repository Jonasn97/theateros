package de.hsos.swa.jonas.theater.eventmanagement.control;

import de.hsos.swa.jonas.theater.eventmanagement.boundary.dto.QueryParametersDTO;
import de.hsos.swa.jonas.theater.eventmanagement.entity.Performance;
import de.hsos.swa.jonas.theater.shared.EventState;
import de.hsos.swa.jonas.theater.userdata.entity.PerformanceState;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface PerformanceOperations {
    Collection<Performance> getPerformances(QueryParametersDTO queryParametersDTO);

    Optional<Performance> getPerformance(Long id);
    Map<Long, PerformanceState> getPerformanceStatus(String username, Set<Long> performanceIds);
    Optional<PerformanceState> getPerformanceStatus(String username, Long performanceId);

    long getPerformancesCount(QueryParametersDTO queryParametersDTO);
}
