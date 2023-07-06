package de.hsos.swa.jonas.theater.crawler.entity;

import de.hsos.swa.jonas.theater.crawler.gateway.CalendarElementDTO;
import de.hsos.swa.jonas.theater.crawler.gateway.EventElementDTO;

public interface CrawlerCatalog {
    int updateDatabase(CalendarElementDTO calendarElementDTO);
    int updateDatabase(EventElementDTO eventElementDTO);
}
