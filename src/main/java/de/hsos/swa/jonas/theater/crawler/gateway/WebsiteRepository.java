package de.hsos.swa.jonas.theater.crawler.gateway;

import de.hsos.swa.jonas.theater.crawler.entity.CrawlerCatalog;
import de.hsos.swa.jonas.theater.shared.Performance;
import de.hsos.swa.jonas.theater.shared.Play;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.logging.Log;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.HashSet;
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

    @Override
    @Transactional
    public int updateDatabase(EventElementDTO eventElementDTO) {
        Play play = Play.find("infolink", eventElementDTO.infolink).firstResult();
        if (play == null) {
            Log.error("Play doesn't exist!");
            throw new RuntimeException("Play doesn't exist!");
        }
        if(play.description== null &&eventElementDTO.description!= null) {
            play.description = eventElementDTO.description;
        }
        if(play.duration== null &&eventElementDTO.duration!= null)
            play.duration = eventElementDTO.duration;
        if(play.bannerPath== null &&eventElementDTO.bannerPath!= null)
            play.bannerPath = eventElementDTO.bannerPath;
        if(eventElementDTO.imagePaths!=null && !new HashSet<>(play.imagePaths).containsAll(eventElementDTO.imagePaths)) {

            eventElementDTO.imagePaths.stream()
                    .filter(imagePath -> !play.imagePaths.contains(imagePath))
                    .forEach(imagePath -> play.imagePaths.add(imagePath));
        }
        if(eventElementDTO.videoUris!=null && !new HashSet<>(play.videoUris).containsAll(eventElementDTO.videoUris)) {
            eventElementDTO.videoUris.stream()
                    .filter(videoUris -> !play.videoUris.contains(videoUris))
                    .forEach(videoUris -> play.videoUris.add(videoUris));
        }
        if(eventElementDTO.spotifyUris!=null && !new HashSet<>(play.spotifyUris).containsAll(eventElementDTO.spotifyUris)) {
            eventElementDTO.spotifyUris.stream()
                    .filter(spotifyUris -> !play.spotifyUris.contains(spotifyUris))
                    .forEach(spotifyUris -> play.spotifyUris.add(spotifyUris));
        }
        if(eventElementDTO.vimeoUris!=null && !new HashSet<>(play.vimeoUris).containsAll(eventElementDTO.vimeoUris)) {
            eventElementDTO.vimeoUris.stream()
                    .filter(vimeoUris -> !play.vimeoUris.contains(vimeoUris))
                    .forEach(vimeoUris -> play.vimeoUris.add(vimeoUris));
        }
        if(eventElementDTO.soundcloudUris!=null && !new HashSet<>(play.soundcloudUris).containsAll(eventElementDTO.soundcloudUris)) {
            eventElementDTO.soundcloudUris.stream()
                    .filter(soundcloudUris -> !play.soundcloudUris.contains(soundcloudUris))
                    .forEach(soundcloudUris -> play.soundcloudUris.add(soundcloudUris));
        }
        if(play.team== null &&eventElementDTO.cast!= null)
            play.team = eventElementDTO.cast;
        if(play.press== null &&eventElementDTO.press!= null)
            play.press = eventElementDTO.press;
        play.persist();
        return 1;
    }
}
