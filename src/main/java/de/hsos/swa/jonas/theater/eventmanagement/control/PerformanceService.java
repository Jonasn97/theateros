package de.hsos.swa.jonas.theater.eventmanagement.control;

import de.hsos.swa.jonas.theater.eventmanagement.boundary.dto.QueryParametersDTO;
import de.hsos.swa.jonas.theater.eventmanagement.entity.Performance;
import de.hsos.swa.jonas.theater.eventmanagement.entity.PerformanceCatalog;
import de.hsos.swa.jonas.theater.shared.PerformanceState;
import de.hsos.swa.jonas.theater.userdata.entity.UserDataCatalog;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@ApplicationScoped
public class PerformanceService implements PerformanceOperations {
    @Inject
    UserDataCatalog userDataCatalog;

    @Inject
    PerformanceCatalog performanceCatalog;

    @Override
    public Collection<Performance> getPerformances(QueryParametersDTO queryParametersDTO) {
        return performanceCatalog.getPerformances(queryParametersDTO);
    }

    @Override
    public Optional<Performance> getPerformance(Long id) {
        return performanceCatalog.getPerformance(id);
    }

    @Override
    public Map<Long, PerformanceState> getPerformanceStatus(String username, Set<Long> performanceIds) {
        return userDataCatalog.getPerformanceState(username, performanceIds);
    }

    @Override
    public Optional<PerformanceState> getPerformanceStatus(String username, Long performanceId) {
        return userDataCatalog.getPerformanceState(username, performanceId);
    }

    @Override
    public long getPerformancesCount(QueryParametersDTO queryParametersDTO) {
        return performanceCatalog.getPerformancesCount(queryParametersDTO);
    }

}
