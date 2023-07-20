package de.hsos.swa.jonas.theater.eventmanagement.entity;

import de.hsos.swa.jonas.theater.eventmanagement.boundary.dto.QueryParametersDTO;

import java.util.Collection;
import java.util.Optional;

public interface PerformanceCatalog {
    Collection<Performance> getPerformances(QueryParametersDTO queryParametersDTO);

    Optional<Performance> getPerformance(Long id);
}
