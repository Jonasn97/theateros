package de.hsos.swa.jonas.theater.shared;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
public class Play extends PanacheEntity {
    public String infolink;

    public String stid;
    public String overline;
    public String title;
    public String description;
    public String kind;
    public String location;
    public String duration;

    public String bannerPath;
    @ElementCollection
    public List<String> imagePaths;
    @ElementCollection
    public List<String> videoUris;
    @ElementCollection
    public List<String> spotifyUris;
    @ElementCollection
    public List<String> vimeoUris;
    @ElementCollection
    public List<String> soundcloudUris;

    public String team;
    public String press;

    @UpdateTimestamp
    public Timestamp lastUpdateTimestamp;
    @CreationTimestamp
    public Timestamp createdTimestamp;

    public String playtype; //Gastspiel, Sonderveranstaltung, Konzert, Sonstiges

    @OneToMany
    public Collection<Performance> performances = new ArrayList<>();

    public Play(String infoLink, String overline, String title, String sparte, String location) {

        this.infolink = infoLink;
        this.overline = overline;
        this.title = title;
        this.kind = sparte;
        this.location = location;
    }


    public Play() {

    }
}
    //Youtubelinks
    //PictureList
    //Spotify Vorschau
    //Pressestimmen
    //Pressezitat
    //Besetzung
    //Fördernde
    //Rahmen
