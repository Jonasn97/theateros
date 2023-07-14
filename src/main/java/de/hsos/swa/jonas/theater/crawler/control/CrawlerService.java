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
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ApplicationScoped
public class CrawlerService implements CrawlerOperations {
    private static final String CALENDER_ELEMENTS_SELECTOR = "div.container.mod-teaser--kalender";
    private static final String INFO_LINK_SELECTOR = "a[href^=/veranstaltung]";
    private static final String OVERLINE_SELECTOR = "h4";
    private static final String TIME_REGEX = ".*Beginn: ([0-9:]+) Uhr.*";
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final String HOSTURL = "https://www.theater-osnabrueck.de/";

    @Inject
    CrawlerCatalog crawlerCatalog;
    @Override
    public Set<String> updateCalendar(Document calendarDocument) {
        Elements calenderElements = calendarDocument.select(CALENDER_ELEMENTS_SELECTOR);
        int updatedElements = 0;
        Set<String> updatedStids = new HashSet<>();
        for (Element calenderElement : calenderElements) {
            CalendarElementDTO calendarElementDTO = getDataFromCalenderEntryElement(calenderElement);
            updatedStids.add(calendarElementDTO.stid);
            updatedElements+= crawlerCatalog.updateDatabase(calendarElementDTO);
        }
    return updatedStids;
    }

    @Override
    public int updateEvent(String stid, Document eventDocument) {
        EventElementDTO eventElementDTO = getDataFromEventElement(eventDocument);
        eventElementDTO.stid = stid;
        int updatedElements=0;
        updatedElements+= crawlerCatalog.updateDatabase(eventElementDTO);

        return updatedElements;
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
            eventElementDTO.description = possibleDescriptionElements.get(0).text();
        }
        else if(possibleDescriptionElements.size()>1) {
            eventElementDTO.description= possibleDescriptionElements.get(1).text();

            //duration
            Element durationElement = possibleDescriptionElements.get(0).selectFirst("p:contains(Dauer)");

            String duration;
            if (durationElement != null) {
                duration = durationElement.text();
                Pattern pattern = Pattern.compile("Dauer:?\\s*(.*)");
                Matcher matcher = pattern.matcher(duration);
                if(matcher.find()) {
                    duration = matcher.group(1).trim();
                }
                eventElementDTO.duration = duration;
            }


            //additional dates
            Element termineBaseElement;
            Element termineElement;
            if(possibleDescriptionElements.get(1).selectFirst("h4:contains(Termine)")!=null) {
                termineBaseElement = possibleDescriptionElements.get(1).selectFirst("h4:contains(Termine)");
                if(termineBaseElement.nextElementSibling()!=null) {
                    if (termineBaseElement.nextElementSibling().nextElementSibling() != null) {
                        termineElement = termineBaseElement.nextElementSibling().nextElementSibling();
                        if (termineElement != null) {
                            String termine = termineElement.text();
                        }
                    }
                }
            }
        }
        else {
            Log.info("Keine Beschreibung gefunden");
        }
        Element bannerElement = eventDocument.selectFirst("div.mod.mod-teaser--top");
        //Get url from div: <div class="mod mod-teaser mod-teaser--top" style="background-image: url(/media/1400px_der_weg_zurueck_0411.jpg)"></div>
        String bannerUrl;
        if (bannerElement != null) {
            bannerUrl = bannerElement.attr("style");
            bannerUrl = bannerUrl.substring(bannerUrl.indexOf('(')+1, bannerUrl.indexOf(')'));
            eventElementDTO.bannerPath = saveImage(bannerUrl);
        }
        Elements carouselElement = eventDocument.select("div.mod.mod-carousel");
        Elements imageElements;
        if(!carouselElement.isEmpty()) {
            imageElements = carouselElement.select("img");
            eventElementDTO.imagePaths = saveImages(imageElements);
        }
        Elements videoDivs = eventDocument.select(".mod-video");
        Set<String> videoLinks = new HashSet<>();
        for (Element videoDiv : videoDivs) {
            videoLinks.add(videoDiv.select(".content-video").attr("data-id"));
        }
        eventElementDTO.videoUris = videoLinks;

        Elements spotifyLinks = eventDocument.select("a[href*=spotify]");
        Set <String> spotifyUri = new HashSet<>();
        for (Element spotifyLink : spotifyLinks) {
            String spotifyUrl = spotifyLink.attr("href");
            spotifyUri.add(spotifyUrl);
        }
        eventElementDTO.spotifyUris = spotifyUri;

        Elements presseStimmenElements = eventDocument.select("h4:contains(Pressestimmen) + p");
        eventElementDTO.press = presseStimmenElements.text();

        Elements besetzungElements = eventDocument.select("h4:contains(Besetzung) + p");
        eventElementDTO.cast = besetzungElements.text();

        Set<String> soundCloudWidgetLinks = new HashSet<>();
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

    private String saveImage(String imageUrl) { //TODO should be in Boundary
        String imageName = imageUrl.substring(imageUrl.lastIndexOf('/') + 1);
        imageName = imageName.replaceAll("[<>:\"/\\\\|?*]", "_");
        //store image in META-INF/resources/media
        Path imagePath = Paths.get("src/main/resources/META-INF/resources/media/" + imageName);

        if(Files.exists(imagePath))
            return "/media/" + imageName;
        if(!imageUrl.startsWith("https://"))
            imageUrl = HOSTURL + imageUrl;
        try (InputStream in = new BufferedInputStream(new URL(imageUrl).openStream())) {
            Files.copy(in, imagePath, StandardCopyOption.REPLACE_EXISTING);
            return "/media/" + imageName;

        } catch (IOException e) {
            Log.error("Fehler beim Speichern des Bilds: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private Set<String> saveImages(Elements imageElements) {
        Set<String> imagePaths = new HashSet<>();
        String imagePath;
        for (Element imageElement : imageElements) {
            String imageUrl = imageElement.attr("src");
            imagePath = saveImage(imageUrl);
            if(imagePath!=null)
                imagePaths.add(imagePath);
        }
        return imagePaths;
    }

    private CalendarElementDTO getDataFromCalenderEntryElement(Element calendarEntryElement) {
        //Extract basic playinfos: overline, title, infolink, sparte, location
        CalendarElementDTO calendarElementDTO = new CalendarElementDTO();
        Element overlineElement = calendarEntryElement.selectFirst(OVERLINE_SELECTOR);
        calendarElementDTO.overline = (overlineElement!=null)?overlineElement.text(): null;
        calendarElementDTO.title = calendarEntryElement.attr("data-sp-stueck");

        Element infoLinkElement = calendarEntryElement.selectFirst(INFO_LINK_SELECTOR);
        String infolink = infoLinkElement != null ? infoLinkElement.attr("href") : "";

        String[] infolinkParts = infolink.split("/");
        String stid = infolinkParts[infolinkParts.length - 2];
        String auid = infolinkParts[infolinkParts.length - 1];
        infolink = "https://www.theater-osnabrueck.de/spielplan-detail/" + "?stid=" + stid;

        //TODO auid und stid extrahieren und in die Datenbank schreiben
        calendarElementDTO.infolink = infolink;
        calendarElementDTO.stid = stid;
        calendarElementDTO.auid = auid;

        calendarElementDTO.kind = calendarEntryElement.attr("data-sp-sparte");

        calendarElementDTO.location = calendarEntryElement.attr("data-sp-ort");

        //Extract performance infos: date, time, bookingLink, isCancelled, performanceType
        String dateString = calendarEntryElement.attr("data-sp-day");
        Element infoElement = calendarEntryElement.selectFirst(".info");
        String timeString = (infoElement!=null)?infoElement.text().replaceAll(TIME_REGEX, "$1"): null;
        Element bookinglinkElement = calendarEntryElement.selectFirst("a.btn-primary");
        calendarElementDTO.bookingLink = bookinglinkElement!=null? bookinglinkElement.attr("abs:href"): null;
        boolean isCancelled = calendarElementDTO.title.contains("Abgesagt");
        if(isCancelled)
            calendarElementDTO.bookingLink = null;
        Element performanceType = calendarEntryElement.select("span").last();
        calendarElementDTO.performanceType = performanceType!=null? performanceType.text(): null; //TODO make performanceTypeString to enum


        LocalTime time = parseTime(timeString);

        LocalDate date = parseDate(dateString);
        if (time != null) {
            calendarElementDTO.datetime = LocalDateTime.of(date,time);
            calendarElementDTO.hasTime = true;
        } else {
            calendarElementDTO.datetime = LocalDateTime.from(date);
            calendarElementDTO.hasTime = false;
        }
        return calendarElementDTO;
    }

    private LocalTime parseTime(String timeString) {

        if(timeString == null) return null;
        if(timeString.matches("[0-9:]+")){
            try {
                return LocalTime.parse(timeString, TIME_FORMAT);
            } catch (DateTimeParseException e) {
                Log.error("Error parsing time: " + timeString);
                e.printStackTrace();
            }
        }
        return null;
    }

    private LocalDate parseDate(String dateString) {
        if(dateString == null) return null;
        try {
            return LocalDate.parse(dateString, DATE_FORMAT);
        } catch (DateTimeParseException e) {
            Log.error("Error parsing date: " + dateString);
            e.printStackTrace();
        }
        return null;
    }
}
