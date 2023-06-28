package de.hsos.swa.jonas.theater.playmanagement.entity;

import de.hsos.swa.jonas.theater.shared.Play;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

public interface PlayCatalog {

    Collection<Play> getPlays(String nameFilter, ArrayList<String> statusFilter, ArrayList<String> playTypeFilter, ArrayList<String> performanceTypeFilter, LocalDateTime startDateTimeFilter, LocalDateTime endDateTimeFilter, String include, long pageNumber, long pageSize);
}
