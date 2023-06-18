package de.hsos.swa.jonas.theater.crawler.gateway;

import de.hsos.swa.jonas.theater.crawler.entity.Performance;
import de.hsos.swa.jonas.theater.crawler.entity.Play;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
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
        //Connect to Website. If Error throw RuntimeException, TODO Log Error
        try {
            doc = Jsoup.connect(WEBSITE_URL).timeout(3000).get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //TODO Check if Document changed from previous call. If it changed, update database, else log info, return
        //TODO Check Document structure for changes. If changed, log alert, throw Exception
        //Extract all performances
        Elements playElements = doc.select(PLAY_ELEMENTS_SELECTOR);
        //Info Log performance count
        System.out.println("Anzahl Theaterstücke: " + playElements.size());
        //loop performances
        for (Element playElement : playElements) {

            //Extract further information from infolink: duration, playtype, youtubeLinks?, pictureList?, spotifyPreview?, pressReview?, pressQuote?, cast?, supporting?, frame?

            //Extract basic playinfos: overline, title, infolink, sparte, location
            Element overlineElement = playElement.selectFirst(OVERLINE_SELECTOR);
            String overline = (overlineElement!=null)?overlineElement.text(): null;
            String title = playElement.attr("data-sp-stueck");

            Element infoLinkElement = playElement.selectFirst(INFO_LINK_SELECTOR);
            String infoLink = infoLinkElement != null ? infoLinkElement.attr("abs:href") : "";
            infoLink = infoLink.substring(0, infoLink.lastIndexOf("/"));

            String sparte = playElement.attr("data-sp-sparte");
            String location = playElement.attr("data-sp-ort");

            //Extract performance infos: date, time, bookingLink, isCancelled, performanceType
            String dateString = playElement.attr("data-sp-day");
            Element infoElement = playElement.selectFirst(".info");
            String timeString = (infoElement!=null)?infoElement.text().replaceAll(TIME_REGEX, "$1"): null;
            Element bookinglinkElement = playElement.selectFirst("a.btn-primary");
            String bookingLink = bookinglinkElement!=null? bookinglinkElement.attr("abs:href"): null;
            boolean isCancelled = title.contains("Abgesagt");
            if(isCancelled)
                bookingLink = null;
            Element performanceType = playElement.select("span").last();
            String performanceTypeString = performanceType!=null? performanceType.text(): null; //TODO make performanceTypeString to enum

            Time time = parseTime(timeString);
            Date date = parseDate(dateString);

            Play play = Play.find("infolink", infoLink).firstResult();
            if (play == null) {
                play = new Play();
                play.infolink = infoLink;
                play.overline = overline;
                play.title = title;
                play.kind = sparte;
                play.location = location;
                play.persist();
            } else if (!Objects.equals(play.overline, overline) || !Objects.equals(play.title, title) || !Objects.equals(play.kind, sparte) || !Objects.equals(play.location, location)){
                play.overline = overline;
                play.title = title;
                play.kind = sparte;
                play.location = location;
            }
            Performance existingPerformance = play.performances.stream()
                    .filter(p -> Objects.equals(p.date, date) && Objects.equals(p.time, time))
                    .findFirst()
                    .orElse(null);
            if (existingPerformance != null && (existingPerformance.isCancelled != isCancelled || !Objects.equals(existingPerformance.performanceType, performanceTypeString))) {
                existingPerformance.bookingLink = bookingLink;
                existingPerformance.isCancelled = isCancelled;
                existingPerformance.performanceType = performanceTypeString;
                existingPerformance.persist();
            } else {
                Performance performance = new Performance();
                performance.time = time;
                performance.date = date;
                performance.bookingLink = bookingLink;
                performance.isCancelled = isCancelled;
                performance.performanceType = performanceTypeString;
                play.performances.add(performance);
                play.persist();
                play.performances.forEach(p -> PanacheEntityBase.persist(p));
            }
        }
    }

    private Time parseTime(String timeString) {

        if(timeString == null) return null;
        if(timeString.matches("[0-9:]+")){
            try {
                Time time = null;
                return new Time(TIME_FORMAT.parse(timeString).getTime());
            } catch (ParseException e) {
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
            throw new RuntimeException(e);
        }
    }
}
