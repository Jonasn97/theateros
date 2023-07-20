package de.hsos.swa.jonas.theater.eventmanagement.boundary.dto.mobile;

import de.hsos.swa.jonas.theater.eventmanagement.entity.Event;
import de.hsos.swa.jonas.theater.eventmanagement.entity.Performance;
import de.hsos.swa.jonas.theater.shared.EventState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class OutgoingEventIdDTOMobile {
    public long id;
    public String title;
    public String kind;
    public String location;
    public EventState eventState;
    public String thumbnailPath;
    public String duration;
    public String description;
    public Collection<OutgoingPerformanceDTOMobile> performances;
    public String cast;
    public Set<String> imagePaths;
    public Set<String> videoUris;
    public Set<String> spotifyUris;
    public Set<String> vimeoUris;
    public Set<String> soundcloudUris;

    public void setEventState(EventState eventState) {
        this.eventState = eventState;
    }

    public static class Converter {
        public static OutgoingEventIdDTOMobile toDTO(Event event) {
            OutgoingEventIdDTOMobile dto = new OutgoingEventIdDTOMobile();
            dto.id = event.id;
            dto.title = event.getTitle();
            dto.kind = event.getKind();
            dto.location = event.getLocation();
            dto.thumbnailPath = event.getBannerPath();
            dto.duration = event.getDuration();
            dto.description = event.getDescription();
            dto.performances = new ArrayList<>();
            List<Performance> sortedPerformances = event.getPerformances().stream()
                    .sorted((p1, p2) -> p1.getDatetime().compareTo(p2.getDatetime())).toList();
            dto.performances = sortedPerformances.stream()
                    .map(OutgoingPerformanceDTOMobile.Converter::toDTO)
                    .collect(Collectors.toList());
            dto.cast = event.getTeam();
            dto.imagePaths = event.getImagePaths();
            dto.videoUris = event.getVideoUris();
            dto.spotifyUris = event.getSpotifyUris();
            dto.vimeoUris = event.getVimeoUris();
            dto.soundcloudUris = event.getSoundcloudUris();
            return dto;
        }
    }


}
