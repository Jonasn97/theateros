package de.hsos.swa.jonas.theater.crawler.gateway;

import de.hsos.swa.jonas.theater.crawler.entity.CrawlerCatalog;
import de.hsos.swa.jonas.theater.shared.Performance;
import de.hsos.swa.jonas.theater.shared.Play;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import org.jsoup.nodes.Document;
import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.Objects;

@ApplicationScoped
public class WebsiteRepository implements CrawlerCatalog,PanacheRepositoryBase<Play, Long> {
    Document doc = null;
    private static final String WEBSITE_URL = "https://www.theater-osnabrueck.de/kalender/";
    private static final String PLAY_ELEMENTS_SELECTOR = "div.container.mod-teaser--kalender";
    private static final String INFO_LINK_SELECTOR = "a[href^=/veranstaltung]";
    private static final String OVERLINE_SELECTOR = "h4";
    private static final String TIME_REGEX = ".*Beginn: ([0-9:]+) Uhr.*";
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");

    @Override
    @Transactional
    public int updateDatabase(CalendarElementDTO calendarElementDTO) {
        int updatedElements = 0;
        Play play = Play.find("infolink", calendarElementDTO.infolink).firstResult();
        if (play == null) {
            play = new Play(calendarElementDTO.infolink, calendarElementDTO.overline, calendarElementDTO.title, calendarElementDTO.kind, calendarElementDTO.location);
            play.persist();
            updatedElements++;
        } else if (!Objects.equals(play.overline, calendarElementDTO.overline) || !Objects.equals(play.title, calendarElementDTO.title) || !Objects.equals(play.kind, calendarElementDTO.kind) || !Objects.equals(play.location, calendarElementDTO.location)){
            play.overline = calendarElementDTO.overline;
            play.title = calendarElementDTO.title;
            play.kind = calendarElementDTO.kind;
            play.location = calendarElementDTO.location;
            play.persist();
            updatedElements++;
        }
        Performance existingPerformance = play.performances.stream()
                .filter(p -> Objects.equals(p.date, calendarElementDTO.date) && Objects.equals(p.time, calendarElementDTO.time))
                .findFirst()
                .orElse(null);
        if (existingPerformance != null && (existingPerformance.isCancelled != calendarElementDTO.isCancelled || !Objects.equals(existingPerformance.performanceType, calendarElementDTO.performanceType))) {
            existingPerformance.bookingLink = calendarElementDTO.bookingLink;
            existingPerformance.isCancelled = calendarElementDTO.isCancelled;
            existingPerformance.performanceType = calendarElementDTO.performanceType;
            existingPerformance.persist();
            updatedElements++;
        } else {
            Performance performance = new Performance(calendarElementDTO.time, calendarElementDTO.date, calendarElementDTO.bookingLink, calendarElementDTO.isCancelled, calendarElementDTO.performanceType);
            play.performances.add(performance);
            play.persist();
            play.performances.forEach(p -> PanacheEntityBase.persist(p));
            updatedElements++;
        }
        return updatedElements;
    }
}
