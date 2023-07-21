package de.hsos.swa.jonas.theater.eventmanagement.gateway;

import de.hsos.swa.jonas.theater.eventmanagement.boundary.dto.QueryParametersDTO;
import de.hsos.swa.jonas.theater.eventmanagement.entity.Performance;
import de.hsos.swa.jonas.theater.eventmanagement.entity.PerformanceCatalog;

import javax.enterprise.context.ApplicationScoped;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class PerformanceRepository implements PerformanceCatalog {
    @Override
    public Collection<Performance> getPerformances(QueryParametersDTO queryParametersDTO) {
        List<Performance> performances = Performance.listAll();
        return performances.stream()
                .filter(performance -> queryParametersDTO.startDateTimeFilter == null && queryParametersDTO.endDateTimeFilter== null|| queryParametersDTO.startDateTimeFilter.isBefore(performance.getDatetime())&&queryParametersDTO.endDateTimeFilter.isAfter(performance.getDatetime()))
                .filter(performance -> queryParametersDTO.nameFilter == null || performance.getEvent().getTitle().toLowerCase().contains(queryParametersDTO.nameFilter.toLowerCase()))
                .filter(performance -> queryParametersDTO.kindFilter == null || queryParametersDTO.kindFilter.isEmpty()|| queryParametersDTO.kindFilter.contains(performance.getEvent().getKind()))
                .filter(performance -> queryParametersDTO.performanceTypeFilter == null || queryParametersDTO.performanceTypeFilter.isEmpty() || queryParametersDTO.performanceTypeFilter.contains(performance.getPerformanceType()))
                //Sort by dates from now on
                .sorted(Comparator.comparing(performance -> performance.getDatetime()))
                .skip(queryParametersDTO.pageNumber * queryParametersDTO.pageSize)
                .limit(queryParametersDTO.pageSize)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Performance> getPerformance(Long id) {
        return Performance.findByIdOptional(id);
    }
}
