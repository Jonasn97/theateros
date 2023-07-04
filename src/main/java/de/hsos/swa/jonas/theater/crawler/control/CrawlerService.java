package de.hsos.swa.jonas.theater.crawler.control;

import de.hsos.swa.jonas.theater.crawler.entity.CrawlerCatalog;
import de.hsos.swa.jonas.theater.crawler.gateway.CalendarElementDTO;
import io.quarkus.logging.Log;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.sql.Date;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Map;
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
        Elements divElements = eventDocument.select("div.container.mod.mod-content");
        String description = detectDescription(divElements);

        String beschreibungstext = eventDocument.select(".mod-content").eq(1).text();
        if(beschreibungstext!= null && !beschreibungstext.isEmpty()) {
            //Log.info("Beschreibungstext: " + beschreibungstext);
        }

        Elements videoDivs = eventDocument.select(".mod-video");
        for (Element videoDiv : videoDivs) {
            String videoLink = videoDiv.select(".content-video").attr("data-id");
            //Log.info("Video Link: " + videoLink);
        }

        Elements spotifyLinks = eventDocument.select("a[href*=spotify]");
        for (Element spotifyLink : spotifyLinks) {
            String spotifyUrl = spotifyLink.attr("href");
            //Log.info("Spotify Link: " + spotifyUrl);
        }

        Elements presseStimmenElements = eventDocument.select("h4:contains(Pressestimmen) + p");
        for (Element presseStimmenElement : presseStimmenElements) {
            String presseStimme = presseStimmenElement.text();
            String herausgeber = presseStimmenElement.nextElementSibling().text();
            //Log.info("Pressestimme: " + presseStimme);
            //Log.info("Herausgeber: " + herausgeber);
        }

        Elements besetzungElements = eventDocument.select("h4:contains(Besetzung) + p");
        for (Element besetzungElement : besetzungElements) {
            String besetzung = besetzungElement.text();
            //Log.info("Besetzung: " + besetzung);
        }
        return 0;
    }

    private String detectDescription(Elements divElements) {
        for (Element divElement : divElements) {
            if(divElement.classNames().size()==3)
                Log.info(divElement);

        }
        return null;
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
