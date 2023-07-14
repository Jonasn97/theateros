package de.hsos.swa.jonas.theater.crawler.gateway;

import java.sql.Date;
import java.sql.Time;
import java.util.List;
import java.util.Set;

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
