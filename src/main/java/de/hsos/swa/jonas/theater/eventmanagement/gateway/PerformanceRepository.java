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
                //.filter(performance -> performance.datetime == null || performance.datetime.isAfter(queryParametersDTO.startDateTimeFilter) && performance.datetime.isBefore(queryParametersDTO.endDateTimeFilter))
                .filter(performance -> queryParametersDTO.nameFilter == null || performance.event.getTitle().toLowerCase().contains(queryParametersDTO.nameFilter.toLowerCase()))
                .filter(performance -> queryParametersDTO.playTypeFilter == null || queryParametersDTO.playTypeFilter.isEmpty()|| queryParametersDTO.playTypeFilter.contains(performance.event.getKind()))
                //.filter(play -> performanceTypeFilter == null || performanceTypeFilter.isEmpty() || performanceTypeFilter.contains(play.performances.contains(performanceTypeFilter)))
                //Sort by dates from now on
                .sorted(Comparator.comparing(performance -> performance.datetime))
                .skip(queryParametersDTO.pageNumber * queryParametersDTO.pageSize)
                .limit(queryParametersDTO.pageSize)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Performance> getPerformance(Long id) {
        return Performance.findByIdOptional(id);
    }
}
