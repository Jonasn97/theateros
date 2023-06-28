package de.hsos.swa.jonas.theater.crawler.gateway;

import de.hsos.swa.jonas.theater.shared.Performance;
import de.hsos.swa.jonas.theater.shared.Play;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.logging.Log;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.io.IOException;
import java.sql.Time;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Objects;

@ApplicationScoped
public class WebsiteRepository implements PanacheRepositoryBase<Play, Long> {
    Document doc = null;
    private static final String WEBSITE_URL = "https://www.theater-osnabrueck.de/kalender/";
    private static final String PLAY_ELEMENTS_SELECTOR = "div.container.mod-teaser--kalender";
    private static final String INFO_LINK_SELECTOR = "a[href^=/veranstaltung]";
    private static final String OVERLINE_SELECTOR = "h4";
    private static final String TIME_REGEX = ".*Beginn: ([0-9:]+) Uhr.*";
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
    @Transactional
    public void parseWebsites() {
        int countPerformanceUpdates = 0;
        int countPlayUpdates = 0;
        try {
            Document update = Jsoup.connect(WEBSITE_URL).timeout(3000).get();

            if(doc!=null && doc.equals(update)){
                Log.info("No changes on " + WEBSITE_URL);
                return;
            } else
                doc = update;
        } catch (IOException e) {
            Log.error("Error while connecting to " + WEBSITE_URL);
            throw new RuntimeException(e);
        }
        //TODO Check Document structure for changes. If changed, log alert, throw Exception
        //Extract all performances
        Elements playElements = doc.select(PLAY_ELEMENTS_SELECTOR);

        Log.info("Anzahl Theaterstücke: " + playElements.size());

        for (Element playElement : playElements) {
            CalendarElementDTO calendarElementDTO = getDataFromPlayElement(playElement);
            //Extract further information from infolink: duration, playtype, youtubeLinks?, pictureList?, spotifyPreview?, pressReview?, pressQuote?, cast?, supporting?, frame?

            Play play = Play.find("infolink", calendarElementDTO.infolink).firstResult();
            if (play == null) {
                play = new Play(calendarElementDTO.infolink, calendarElementDTO.overline, calendarElementDTO.title, calendarElementDTO.kind, calendarElementDTO.location);
                play.persist();
            } else if (!Objects.equals(play.overline, calendarElementDTO.overline) || !Objects.equals(play.title, calendarElementDTO.title) || !Objects.equals(play.kind, calendarElementDTO.kind) || !Objects.equals(play.location, calendarElementDTO.location)){
                play.overline = calendarElementDTO.overline;
                play.title = calendarElementDTO.title;
                play.kind = calendarElementDTO.kind;
                play.location = calendarElementDTO.location;
            }
            Performance existingPerformance = play.performances.stream()
                    .filter(p -> Objects.equals(p.date, calendarElementDTO.date) && Objects.equals(p.time, calendarElementDTO.time))
                    .findFirst()
                    .orElse(null);
            if (existingPerformance != null && (existingPerformance.isCancelled != calendarElementDTO.isCancelled || !Objects.equals(existingPerformance.performanceType, calendarElementDTO.performanceType))) {
                existingPerformance.bookingLink = calendarElementDTO.bookingLink;
                existingPerformance.isCancelled = calendarElementDTO.isCancelled;
                existingPerformance.performanceType = calendarElementDTO.performanceType;
                existingPerformance.persist();
            } else {
                Performance performance = new Performance(calendarElementDTO.time, calendarElementDTO.date, calendarElementDTO.bookingLink, calendarElementDTO.isCancelled, calendarElementDTO.performanceType);
                play.performances.add(performance);
                play.persist();
                play.performances.forEach(p -> PanacheEntityBase.persist(p));
            }
        }
    }

    private CalendarElementDTO getDataFromPlayElement(Element playElement) {
        //Extract basic playinfos: overline, title, infolink, sparte, location
        CalendarElementDTO calendarElementDTO = new CalendarElementDTO();
        Element overlineElement = playElement.selectFirst(OVERLINE_SELECTOR);
        calendarElementDTO.overline = (overlineElement!=null)?overlineElement.text(): null;

        calendarElementDTO.title = playElement.attr("data-sp-stueck");

        Element infoLinkElement = playElement.selectFirst(INFO_LINK_SELECTOR);
        String infolink = infoLinkElement != null ? infoLinkElement.attr("abs:href") : "";
        calendarElementDTO.infolink = infolink.substring(0, infolink.lastIndexOf("/"));

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
