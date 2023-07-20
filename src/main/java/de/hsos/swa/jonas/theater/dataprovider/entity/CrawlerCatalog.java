package de.hsos.swa.jonas.theater.dataprovider.entity;

import de.hsos.swa.jonas.theater.dataprovider.gateway.CalendarElementDTO;
import de.hsos.swa.jonas.theater.dataprovider.gateway.EventElementDTO;

public interface CrawlerCatalog {
    int updateDatabase(CalendarElementDTO calendarElementDTO);
    int updateDatabase(EventElementDTO eventElementDTO);
}
