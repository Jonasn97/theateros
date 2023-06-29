package de.hsos.swa.jonas.theater.playmanagement.control;

import de.hsos.swa.jonas.theater.playmanagement.boundary.dto.QueryParametersDTO;
import de.hsos.swa.jonas.theater.shared.Play;
import de.hsos.swa.jonas.theater.playmanagement.entity.PlayCatalog;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@ApplicationScoped
public class PlayService implements PlayOperations{
    @Inject
    PlayCatalog playCatalog;
    @Override
    public Collection<Play> getPlays(String nameFilter, ArrayList<String> statusFilter, ArrayList<String> playTypeFilter, ArrayList<String> performanceTypeFilter, LocalDateTime startDateTimeFilter, LocalDateTime endDateTimeFilter, String include, long pageNumber, long pageSize) {
        return playCatalog.getPlays(nameFilter, statusFilter, playTypeFilter, performanceTypeFilter, startDateTimeFilter, endDateTimeFilter, include, pageNumber, pageSize);
    }

    @Override
    public Collection<Play> getPlays(QueryParametersDTO queryParametersDTO) {
        return playCatalog.getPlays(queryParametersDTO);
    }

    @Override
    public long getPlaysCount(QueryParametersDTO queryParametersDTO) {
        return playCatalog.getPlaysCount(queryParametersDTO);
    }

    @Override
    public Optional<Play> getPlayById(long playId) {
        return playCatalog.getPlayById(playId);
    }
}
