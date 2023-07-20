package de.hsos.swa.jonas.theater.eventmanagement.entity;

import de.hsos.swa.jonas.theater.shared.dto.internal.CalendarElementDTO;
import de.hsos.swa.jonas.theater.shared.dto.internal.EventElementDTO;

public interface AddEventsCatalog {
    int updateDatabase(CalendarElementDTO calendarElementDTO);
    int updateDatabase(EventElementDTO eventElementDTO);
}
