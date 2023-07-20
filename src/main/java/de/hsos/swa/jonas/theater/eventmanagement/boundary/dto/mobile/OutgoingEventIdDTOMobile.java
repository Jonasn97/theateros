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
            dto.title = event.title;
            dto.kind = event.kind;
            dto.location = event.location;
            dto.thumbnailPath = event.bannerPath;
            dto.duration = event.duration;
            dto.description = event.description;
            dto.performances = new ArrayList<>();
            List<Performance> sortedPerformances = event.performances.stream()
                    .sorted((p1, p2) -> p1.datetime.compareTo(p2.datetime)).toList();
            dto.performances = sortedPerformances.stream()
                    .map(OutgoingPerformanceDTOMobile.Converter::toDTO)
                    .collect(Collectors.toList());
            dto.cast = event.team;
            dto.imagePaths = event.imagePaths;
            dto.videoUris = event.videoUris;
            dto.spotifyUris = event.spotifyUris;
            dto.vimeoUris = event.vimeoUris;
            dto.soundcloudUris = event.soundcloudUris;
            return dto;
        }
    }


}
