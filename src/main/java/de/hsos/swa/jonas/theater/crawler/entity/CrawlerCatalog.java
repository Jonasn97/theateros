package de.hsos.swa.jonas.theater.crawler.entity;

import de.hsos.swa.jonas.theater.crawler.gateway.CalendarElementDTO;

public interface CrawlerCatalog {
    int updateDatabase(CalendarElementDTO calendarElementDTO);
}
