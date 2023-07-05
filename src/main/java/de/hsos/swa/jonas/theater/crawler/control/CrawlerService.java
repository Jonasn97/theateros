package de.hsos.swa.jonas.theater.crawler.control;

import de.hsos.swa.jonas.theater.crawler.entity.CrawlerCatalog;
import de.hsos.swa.jonas.theater.crawler.gateway.CalendarElementDTO;
import de.hsos.swa.jonas.theater.crawler.gateway.EventElementDTO;
import io.quarkus.logging.Log;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Date;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ApplicationScoped
public class CrawlerService implements CrawlerOperations {
    private static final String PLAY_ELEMENTS_SELECTOR = "div.container.mod-teaser--kalender";
    private static final String INFO_LINK_SELECTOR = "a[href^=/veranstaltung]";
    private static final String OVERLINE_SELECTOR = "h4";
    private static final String TIME_REGEX = ".*Beginn: ([0-9:]+) Uhr.*";
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
    private static String LANGUAGE_MODEL_PATH = "src/main/resources/opennlp-de-sentence_1-0_1-9-3.bin";

    @Inject
    CrawlerCatalog crawlerCatalog;
    @Override
    public Set<String> updateCalendar(Document calendarDocument) {
        Elements playElements = calendarDocument.select(PLAY_ELEMENTS_SELECTOR);

        Log.info("Anzahl Theaterstücke: " + playElements.size());
        int updatedElements = 0;
        Set<String> updatedInfolinks = new HashSet<>();
        for (Element playElement : playElements) {
            CalendarElementDTO calendarElementDTO = getDataFromPlayElement(playElement);
            updatedInfolinks.add(calendarElementDTO.infolink);
            updatedElements+= crawlerCatalog.updateDatabase(calendarElementDTO);
        }
    return updatedInfolinks;
    }

    @Override
    public int updateEvent(Document eventDocument) {
        EventElementDTO eventElementDTO = getDataFromEventElement(eventDocument);


        return 0;
    }

    private EventElementDTO getDataFromEventElement(Document eventDocument) {
        Elements divElements = eventDocument.select("div.container.mod.mod-content");
        EventElementDTO eventElementDTO = new EventElementDTO();
        Elements possibleDescriptionElements = new Elements();
        for (Element divElement : divElements) {
            if(divElement.classNames().size()==3)
                possibleDescriptionElements.add(divElement);
        }
        if (possibleDescriptionElements.size()==1) {
            //Log.info("Description: " + description);
            eventElementDTO.description = possibleDescriptionElements.get(0).text();
        }
        else if(possibleDescriptionElements.size()>1) {
            eventElementDTO.description= possibleDescriptionElements.get(1).text();

            //duration
            Element durationElement = possibleDescriptionElements.get(0).selectFirst("p:contains(Dauer)");
            String duration = durationElement.text().split("\n")[durationElement.text().split("\n").length-1];
            duration = duration.replace("Dauer: ", "");
            duration = duration.replace("Dauer ", "");
            eventElementDTO.duration = duration;

            //additional dates
            if(possibleDescriptionElements.get(1).selectFirst("h4:contains(Termine)")!=null) {
                Element termineElement = possibleDescriptionElements.get(1).selectFirst("h4:contains(Termine)").nextElementSibling().nextElementSibling();
                String termine = termineElement.text();
                }
            }
        else {
            Log.info("Keine Beschreibung gefunden");
        }
        Element bannerElement = eventDocument.selectFirst("div.mod.mod-teaser--top");
        //Get url from div: <div class="mod mod-teaser mod-teaser--top" style="background-image: url(/media/1400px_der_weg_zurueck_0411.jpg)"></div>
        String bannerUrl = bannerElement.attr("style");
        bannerUrl = bannerUrl.substring(bannerUrl.indexOf('(')+1, bannerUrl.indexOf(')'));
        eventElementDTO.bannerPath = saveImage(bannerUrl);
        Elements carouselElement = eventDocument.select("div.mod.mod-carousel");
        Elements imageElements;
        if(!carouselElement.isEmpty()) {
            imageElements = carouselElement.select("img");
            eventElementDTO.imagePaths = saveImages(imageElements);
        }
        Elements videoDivs = eventDocument.select(".mod-video");
        ArrayList<String> videoLinks = new ArrayList<>();
        for (Element videoDiv : videoDivs) {
            videoLinks.add(videoDiv.select(".content-video").attr("data-id"));
        }
        eventElementDTO.videoUris = videoLinks;

        Elements spotifyLinks = eventDocument.select("a[href*=spotify]");
        for (Element spotifyLink : spotifyLinks) {
            String spotifyUrl = spotifyLink.attr("href");
            //Log.info("Spotify Link: " + spotifyUrl);
        }

        Elements presseStimmenElements = eventDocument.select("h4:contains(Pressestimmen) + p");
        eventElementDTO.press = presseStimmenElements.text();

        Elements besetzungElements = eventDocument.select("h4:contains(Besetzung) + p");
        eventElementDTO.cast = besetzungElements.text();

        ArrayList<String> soundCloudWidgetLinks = new ArrayList<>();

        // Alle iframe-Elemente mit der Klasse "soundcloud-widget" auswählen
        Elements iframeElements = eventDocument.select("iframe.soundcloud-widget");

        // Durch die ausgewählten iframe-Elemente iterieren und die Links extrahieren
        for (Element iframe : iframeElements) {
            String src = iframe.attr("src");
            soundCloudWidgetLinks.add(src);
        }
        eventElementDTO.soundcloudUris = soundCloudWidgetLinks;
        return eventElementDTO;
    }

    private String saveImage(String bannerUrl) {
        String imageName = bannerUrl.substring(bannerUrl.lastIndexOf('/') + 1);

        try (InputStream in = new BufferedInputStream(new URL(bannerUrl).openStream())) {
            Path imagePath = Path.of(imageName);
            Files.copy(in, imagePath, StandardCopyOption.REPLACE_EXISTING);
            Log.info("Bild gespeichert: " + imagePath);
            return imagePath.toString();

        } catch (IOException e) {
            Log.error("Fehler beim Speichern des Bildes: " + e.getMessage());
        }
        return null;
    }

    private List<String> saveImages(Elements imageElements) {
        ArrayList<String> imagePaths = new ArrayList<>();
        for (Element imageElement : imageElements) {
            String imageUrl = imageElement.absUrl("src");
            String imageName = imageUrl.substring(imageUrl.lastIndexOf('/') + 1);

            try (InputStream in = new BufferedInputStream(new URL(imageUrl).openStream())) {
                Path imagePath = Path.of(imageName);
                Files.copy(in, imagePath, StandardCopyOption.REPLACE_EXISTING);
                imagePaths.add(imagePath.toString());
                Log.info("Bild gespeichert: " + imagePath);

            } catch (IOException e) {
                Log.error("Fehler beim Speichern des Bildes: " + e.getMessage());
            }
        }
        return imagePaths;
    }

    private CalendarElementDTO getDataFromPlayElement(Element playElement) {
        //Extract basic playinfos: overline, title, infolink, sparte, location
        CalendarElementDTO calendarElementDTO = new CalendarElementDTO();
        Element overlineElement = playElement.selectFirst(OVERLINE_SELECTOR);
        calendarElementDTO.overline = (overlineElement!=null)?overlineElement.text(): null;

        calendarElementDTO.title = playElement.attr("data-sp-stueck");

        Element infoLinkElement = playElement.selectFirst(INFO_LINK_SELECTOR);
        String infolink = infoLinkElement != null ? infoLinkElement.attr("abs:href") : "";

        String[] infolinkParts = infolink.split("/");
        String stid = infolinkParts[infolinkParts.length - 2];
        String auid = infolinkParts[infolinkParts.length - 1];
        infolink = "https://www.theater-osnabrueck.de/spielplan-detail/" + "?stid=" + stid;

        //TODO auid und stid extrahieren und in die Datenbank schreiben
        calendarElementDTO.infolink = infolink;
        calendarElementDTO.stid = stid;
        calendarElementDTO.auid = auid;

        calendarElementDTO.kind = playElement.attr("data-sp-sparte");

        calendarElementDTO.location = playElement.attr("data-sp-ort");

        //Extract performance infos: date, time, bookingLink, isCancelled, performanceType
        String dateString = playElement.attr("data-sp-day");
        Element infoElement = playElement.selectFirst(".info");
        String timeString = (infoElement!=null)?infoElement.text().replaceAll(TIME_REGEX, "$1"): null;
        Element bookinglinkElement = playElement.selectFirst("a.btn-primary");
        calendarElementDTO.bookingLink = bookinglinkElement!=null? bookinglinkElement.attr("abs:href"): null;
        boolean isCancelled = calendarElementDTO.title.contains("Abgesagt");
        if(isCancelled)
            calendarElementDTO.bookingLink = null;
        Element performanceType = playElement.select("span").last();
        calendarElementDTO.performanceType = performanceType!=null? performanceType.text(): null; //TODO make performanceTypeString to enum

        calendarElementDTO.time = parseTime(timeString);
        calendarElementDTO.date = parseDate(dateString);
        return calendarElementDTO;
    }

    private Time parseTime(String timeString) {

        if(timeString == null) return null;
        if(timeString.matches("[0-9:]+")){
            try {
                return new Time(TIME_FORMAT.parse(timeString).getTime());
            } catch (ParseException e) {
                Log.error("Error parsing time: " + timeString);
                e.printStackTrace();
            }
        }
        return null;
    }

    private Date parseDate(String dateString) {
        if(dateString == null) return null;
        try {
            return new Date(DATE_FORMAT.parse(dateString).getTime());
        } catch (ParseException e) {
            Log.error("Error parsing date: " + dateString);
            e.printStackTrace();
        }
        return null;
    }
}
