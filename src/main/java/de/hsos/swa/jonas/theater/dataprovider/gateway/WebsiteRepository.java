package de.hsos.swa.jonas.theater.dataprovider.gateway;

import de.hsos.swa.jonas.theater.dataprovider.entity.CrawlerCatalog;
import de.hsos.swa.jonas.theater.shared.Event;
import de.hsos.swa.jonas.theater.shared.Performance;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.logging.Log;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.Objects;

@ApplicationScoped
public class WebsiteRepository implements CrawlerCatalog,PanacheRepositoryBase<Event, Long> {

    @Override
    @Transactional
    public int updateDatabase(CalendarElementDTO calendarElementDTO) {
        int updatedElements = 0;
        Event event = Event.find("stid", calendarElementDTO.stid).firstResult();
        if (event == null) {
            event = new Event(calendarElementDTO.stid, calendarElementDTO.infolink, calendarElementDTO.overline, calendarElementDTO.title, calendarElementDTO.kind, calendarElementDTO.location);
            event.persist();
            updatedElements++;
        } else if (!Objects.equals(event.overline, calendarElementDTO.overline) || !Objects.equals(event.title, calendarElementDTO.title) || !Objects.equals(event.kind, calendarElementDTO.kind) || !Objects.equals(event.location, calendarElementDTO.location)){
            event.overline = calendarElementDTO.overline;
            event.title = calendarElementDTO.title;
            event.kind = calendarElementDTO.kind;
            event.location = calendarElementDTO.location;
            event.persist();
            updatedElements++;
        }
        Performance existingPerformance = event.performances.stream()
                .filter(p -> Objects.equals(p.auid, calendarElementDTO.auid))
                .findFirst()
                .orElse(null);
        if (existingPerformance != null && (existingPerformance.isCancelled != calendarElementDTO.isCancelled || !Objects.equals(existingPerformance.performanceType, calendarElementDTO.performanceType))) {
            existingPerformance.bookingLink = calendarElementDTO.bookingLink;
            existingPerformance.isCancelled = calendarElementDTO.isCancelled;
            existingPerformance.performanceType = calendarElementDTO.performanceType;
            existingPerformance.persist();
        } else {
            Performance performance = new Performance(event, calendarElementDTO.auid, calendarElementDTO.datetime, calendarElementDTO.hasTime, calendarElementDTO.bookingLink, calendarElementDTO.isCancelled, calendarElementDTO.performanceType);
            event.performances.add(performance);
            event.persist();
            event.performances.forEach(p -> PanacheEntityBase.persist(p));
        }
        updatedElements++;
        return updatedElements;
    }

    @Override
    @Transactional
    public int updateDatabase(EventElementDTO eventElementDTO) {
        Event event = Event.find("stid", eventElementDTO.stid).firstResult();
        if (event == null) {
            Log.error("Event doesn't exist!");
            throw new RuntimeException("Event doesn't exist!");
        }
        if(event.description== null &&eventElementDTO.description!= null) {
            event.description = eventElementDTO.description;
        }
        if(event.duration== null &&eventElementDTO.duration!= null)
            event.duration = eventElementDTO.duration;
        if(event.bannerPath== null &&eventElementDTO.bannerPath!= null)
            event.bannerPath = eventElementDTO.bannerPath;
        if(eventElementDTO.imagePaths!= null)
            event.imagePaths.addAll(eventElementDTO.imagePaths);
        if(eventElementDTO.videoUris!= null)
            event.videoUris.addAll(eventElementDTO.videoUris);
        if(eventElementDTO.spotifyUris!= null)
            event.spotifyUris.addAll(eventElementDTO.spotifyUris);
        if(eventElementDTO.vimeoUris!= null)
            event.vimeoUris.addAll(eventElementDTO.vimeoUris);
        if(eventElementDTO.soundcloudUris!= null)
            event.soundcloudUris.addAll(eventElementDTO.soundcloudUris);
        if(event.team== null &&eventElementDTO.cast!= null)
            event.team = eventElementDTO.cast;
        if(event.press== null &&eventElementDTO.press!= null)
            event.press = eventElementDTO.press;
        if(event.thumbnailPath== null && event.imagePaths!= null && !event.imagePaths.isEmpty())
            event.thumbnailPath = event.imagePaths.iterator().next();
        event.persist();
        return 1;
    }
}
