package de.hsos.swa.jonas.theater.eventmanagement.gateway;

import de.hsos.swa.jonas.theater.eventmanagement.entity.AddEventsCatalog;
import de.hsos.swa.jonas.theater.shared.dto.internal.CalendarElementDTO;
import de.hsos.swa.jonas.theater.shared.dto.internal.EventElementDTO;
import de.hsos.swa.jonas.theater.eventmanagement.entity.Event;
import de.hsos.swa.jonas.theater.eventmanagement.entity.Performance;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.logging.Log;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.Objects;

@ApplicationScoped
public class AddEventsRepository implements AddEventsCatalog,PanacheRepositoryBase<Event, Long> {

    @Override
    @Transactional
    public int updateDatabase(CalendarElementDTO calendarElementDTO) {
        int updatedElements = 0;
        Event event = Event.find("stid", calendarElementDTO.stid).firstResult();
        if (event == null) {
            event = new Event(calendarElementDTO.stid, calendarElementDTO.infolink, calendarElementDTO.overline, calendarElementDTO.title, calendarElementDTO.kind, calendarElementDTO.location);
            event.persist();
            updatedElements++;
        } else if (!Objects.equals(event.getOverline(), calendarElementDTO.overline) || !Objects.equals(event.getTitle(), calendarElementDTO.title) || !Objects.equals(event.getKind(), calendarElementDTO.kind) || !Objects.equals(event.getLocation(), calendarElementDTO.location)){
            event.setOverline(calendarElementDTO.overline);
            event.setTitle(calendarElementDTO.title);
            event.setKind(calendarElementDTO.kind);
            event.setLocation(calendarElementDTO.location);
            event.persist();
            updatedElements++;
        }
        Performance existingPerformance = event.getPerformances().stream()
                .filter(p -> Objects.equals(p.getAuid(), calendarElementDTO.auid))
                .findFirst()
                .orElse(null);
        if (existingPerformance != null && (existingPerformance.isCancelled() != calendarElementDTO.isCancelled || !Objects.equals(existingPerformance.getPerformanceType(), calendarElementDTO.performanceType))) {
            existingPerformance.setBookingLink(calendarElementDTO.bookingLink);
            existingPerformance.setCancelled(calendarElementDTO.isCancelled);
            existingPerformance.setPerformanceType(calendarElementDTO.performanceType);
            existingPerformance.persist();
        } else {
            Performance performance = new Performance(event, calendarElementDTO.auid, calendarElementDTO.datetime, calendarElementDTO.hasTime, calendarElementDTO.bookingLink, calendarElementDTO.isCancelled, calendarElementDTO.performanceType);
            event.getPerformances().add(performance);
            event.persist();
            event.getPerformances().forEach(p -> PanacheEntityBase.persist(p));
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
        if(event.getDescription()== null &&eventElementDTO.description!= null) {
            event.setDescription(eventElementDTO.description);
        }
        if(event.getDuration()== null &&eventElementDTO.duration!= null)
            event.setDuration(eventElementDTO.duration);
        if(event.getBannerPath()== null &&eventElementDTO.bannerPath!= null)
            event.setBannerPath(eventElementDTO.bannerPath);
        if(eventElementDTO.imagePaths!= null)
            event.getImagePaths().addAll(eventElementDTO.imagePaths);
        if(eventElementDTO.videoUris!= null)
            event.getVideoUris().addAll(eventElementDTO.videoUris);
        if(eventElementDTO.spotifyUris!= null)
            event.getSpotifyUris().addAll(eventElementDTO.spotifyUris);
        if(eventElementDTO.vimeoUris!= null)
            event.getVimeoUris().addAll(eventElementDTO.vimeoUris);
        if(eventElementDTO.soundcloudUris!= null)
            event.getSoundcloudUris().addAll(eventElementDTO.soundcloudUris);
        if(event.getTeam()== null &&eventElementDTO.cast!= null)
            event.setTeam(eventElementDTO.cast);
        if(event.getPress()== null &&eventElementDTO.press!= null)
            event.setPress(eventElementDTO.press);
        if(event.getThumbnailPath()== null && event.getImagePaths()!= null && !event.getImagePaths().isEmpty())
            event.setThumbnailPath(event.getImagePaths().iterator().next());
        event.persist();
        return 1;
    }
}
