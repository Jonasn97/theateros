package de.hsos.swa.jonas.theater.eventmanagement.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

@Entity
public class Event extends PanacheEntity {
    private String infolink;

    private String stid;
    private String overline;
    private String title;
    @Column(length = 10000)
    private String description;
    private String thumbnailPath;
    private String kind;
    private String location;
    private String duration;

    private String bannerPath;

    @ElementCollection
    private Set<String> imagePaths;
    @ElementCollection
    private Set<String> videoUris;
    @ElementCollection
    private Set<String> spotifyUris;
    @ElementCollection
    private Set<String> vimeoUris;
    @ElementCollection
    private Set<String> soundcloudUris;
    @Column(length = 10000)
    private String team;
    @Column(length = 10000)
    private String press;

    @UpdateTimestamp
    private Timestamp lastUpdateTimestamp;
    @CreationTimestamp
    private Timestamp createdTimestamp;

    private String playtype; //Gastspiel, Sonderveranstaltung, Konzert, Sonstiges

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private Collection<Performance> performances = new ArrayList<>();

    public Event(String stid, String infoLink, String overline, String title, String sparte, String location) {
        this.stid = stid;
        this.infolink = infoLink;
        this.overline = overline;
        this.title = title;
        this.kind = sparte;
        this.location = location;
    }


    public Event() {

    }
    public String getInfolink() {
        return infolink;
    }

    public void setInfolink(String infolink) {
        this.infolink = infolink;
    }

    public String getStid() {
        return stid;
    }

    public void setStid(String stid) {
        this.stid = stid;
    }

    public String getOverline() {
        return overline;
    }

    public void setOverline(String overline) {
        this.overline = overline;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getBannerPath() {
        return bannerPath;
    }

    public void setBannerPath(String bannerPath) {
        this.bannerPath = bannerPath;
    }

    public Set<String> getImagePaths() {
        return imagePaths;
    }

    public void setImagePaths(Set<String> imagePaths) {
        this.imagePaths = imagePaths;
    }

    public Set<String> getVideoUris() {
        return videoUris;
    }

    public void setVideoUris(Set<String> videoUris) {
        this.videoUris = videoUris;
    }

    public Set<String> getSpotifyUris() {
        return spotifyUris;
    }

    public void setSpotifyUris(Set<String> spotifyUris) {
        this.spotifyUris = spotifyUris;
    }

    public Set<String> getVimeoUris() {
        return vimeoUris;
    }

    public void setVimeoUris(Set<String> vimeoUris) {
        this.vimeoUris = vimeoUris;
    }

    public Set<String> getSoundcloudUris() {
        return soundcloudUris;
    }

    public void setSoundcloudUris(Set<String> soundcloudUris) {
        this.soundcloudUris = soundcloudUris;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getPress() {
        return press;
    }

    public void setPress(String press) {
        this.press = press;
    }

    public Timestamp getLastUpdateTimestamp() {
        return lastUpdateTimestamp;
    }

    public void setLastUpdateTimestamp(Timestamp lastUpdateTimestamp) {
        this.lastUpdateTimestamp = lastUpdateTimestamp;
    }

    public Timestamp getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Timestamp createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public String getPlaytype() {
        return playtype;
    }

    public void setPlaytype(String playtype) {
        this.playtype = playtype;
    }

    public Collection<Performance> getPerformances() {
        return performances;
    }

    public void setPerformances(Collection<Performance> performances) {
        this.performances = performances;
    }
}
    //Pressezitat
    //Fördernde
    //Rahmen
