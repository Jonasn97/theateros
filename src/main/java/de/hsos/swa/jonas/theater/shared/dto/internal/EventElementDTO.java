package de.hsos.swa.jonas.theater.shared.dto.internal;

import java.sql.Date;
import java.sql.Time;
import java.util.Set;

/**
 * DTO for EventElement
 * Contains all necessary information for a EventElement of the theater-osnabrueck.de website
 */
public class EventElementDTO {
    public String stid;
    public String description;
    public String duration;
    public Date date;
    public Time time;
    public String bannerPath;
    public Set<String> imagePaths;
    public Set<String> videoUris;
    public Set<String> spotifyUris;
    public Set<String> vimeoUris;
    public Set<String> soundcloudUris;
    public String cast;
    public String press;

}
