package de.hsos.swa.jonas.theater.eventmanagement.boundary.dto;

import de.hsos.swa.jonas.theater.shared.Event;
import de.hsos.swa.jonas.theater.shared.Performance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

public class OutgoingDetailEventDTO {
    public long id;
    public String title;
    public String kind;
    public String location;
    public String bannerPath;
    public String duration;
    public String description;
    public Collection<PerformanceDTO> performances;
    public String cast;
    public Set<String> imagePaths;
    public Set<String> videoUris;
    public Set<String> spotifyUris;
    public Set<String> vimeoUris;
    public Set<String> soundcloudUris;

    public static class Converter {
        public static OutgoingDetailEventDTO toDTO(Event event) {
            OutgoingDetailEventDTO dto = new OutgoingDetailEventDTO();
            dto.id = event.id;
            dto.title = event.title;
            dto.kind = event.kind;
            dto.location = event.location;
            dto.bannerPath = event.bannerPath;
            dto.duration = event.duration;
            dto.description = event.description;
            dto.performances = new ArrayList<>();
            for (Performance performance : event.performances) {
                dto.performances.add(PerformanceDTO.Converter.toDTO(performance));
            }
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
