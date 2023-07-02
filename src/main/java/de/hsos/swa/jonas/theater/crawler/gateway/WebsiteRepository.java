package de.hsos.swa.jonas.theater.crawler.gateway;

import de.hsos.swa.jonas.theater.crawler.entity.CrawlerCatalog;
import de.hsos.swa.jonas.theater.shared.Performance;
import de.hsos.swa.jonas.theater.shared.Play;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.Objects;

@ApplicationScoped
public class WebsiteRepository implements CrawlerCatalog,PanacheRepositoryBase<Play, Long> {

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
        } else {
            Performance performance = new Performance(calendarElementDTO.time, calendarElementDTO.date, calendarElementDTO.bookingLink, calendarElementDTO.isCancelled, calendarElementDTO.performanceType);
            play.performances.add(performance);
            play.persist();
            play.performances.forEach(p -> PanacheEntityBase.persist(p));
        }
        updatedElements++;
        return updatedElements;
    }
}
