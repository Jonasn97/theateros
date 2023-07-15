package de.hsos.swa.jonas.theater.shared;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

@Entity
public class Event extends PanacheEntity {
    public String infolink;

    public String stid;
    public String overline;
    public String title;
    @Column(length = 10000)
    public String description;
    public String thumbnailPath;
    public String kind;
    public String location;
    public String duration;

    public String bannerPath;
    @ElementCollection
    public Set<String> imagePaths;
    @ElementCollection
    public Set<String> videoUris;
    @ElementCollection
    public Set<String> spotifyUris;
    @ElementCollection
    public Set<String> vimeoUris;
    @ElementCollection
    public Set<String> soundcloudUris;
    @Column(length = 10000)
    public String team;
    @Column(length = 10000)
    public String press;

    @UpdateTimestamp
    public Timestamp lastUpdateTimestamp;
    @CreationTimestamp
    public Timestamp createdTimestamp;

    public String playtype; //Gastspiel, Sonderveranstaltung, Konzert, Sonstiges

    @OneToMany
    public Collection<Performance> performances = new ArrayList<>();

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
}
    //Pressezitat
    //Fördernde
    //Rahmen
