package de.hsos.swa.jonas.theater.crawler.gateway;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

public class EventElementDTO {
    public String infolink;
    public String description;
    public String duration;
    public Date date;
    public Time time;
    public String bannerPath;
    public List<String> imagePaths;
    public List<String> videoUris;
    public List<String> spotifyUris;
    public List<String> vimeoUris;
    public List<String> soundcloudUris;
    public String cast;
    public String press;

}
