package de.hsos.swa.jonas.theater.crawler.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collection;

@Entity
public class Play extends PanacheEntity {
    public String infolink;
    public String overline;
    public String title;
    public String description;
    public String kind;
    public String location;
    public String duration;

    public String playtype; //Gastspiel, Sonderveranstaltung, Konzert, Sonstiges

    @OneToMany
    public Collection<Performance> performances = new ArrayList<>();
}
    //Youtubelinks
    //PictureList
    //Spotify Vorschau
    //Pressestimmen
    //Pressezitat
    //Besetzung
    //Fördernde
    //Rahmen
