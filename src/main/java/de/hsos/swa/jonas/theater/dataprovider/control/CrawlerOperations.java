package de.hsos.swa.jonas.theater.dataprovider.control;

import org.jsoup.nodes.Document;

import java.util.Set;

/**
 * Interface for the CrawlerOperations
 * @see CrawlerService
 */
public interface CrawlerOperations {
    /** Analyzes the calendarDocument and updates the database with the new events
     * @param calendarDocument the calendarDocument to update the database with
     * @return a set of stids that were updated
     */
    Set<String> updateCalendar(Document calendarDocument);

    /**
     * @param updatedLink the link to the updated event
     * @param eventDocument the eventDocument to update the database with
     * @return 1 if the event was updated, 0 if not
     */
    int updateEvent(String updatedLink, Document eventDocument);
}
