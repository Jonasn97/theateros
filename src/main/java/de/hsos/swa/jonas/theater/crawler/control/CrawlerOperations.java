package de.hsos.swa.jonas.theater.crawler.control;

import org.jsoup.nodes.Document;

import java.util.Set;

public interface CrawlerOperations {
    Set<String> updateCalendar(Document calendarDocument);
    int updateEvent(String updatedLink, Document eventDocument);
}
