package de.hsos.swa.jonas.theater.dataprovider.control;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.enterprise.context.ApplicationScoped;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.net.URL;

@ApplicationScoped
public class WebsiteDownloader {
    private static final String OUTPUTFOLDER = "src/main/resources/crawledPages/";
    private static final String EVENTPATH = "spielplan-detail/";
    private static final String EVENT_URL = "https://www.theater-osnabrueck.de/spielplan-detail";
    private static final String CALENDAR_URL = "https://www.theater-osnabrueck.de/kalender";
    private static final String CALENDAR_PATH = "calendar.html";

    public void downloadAllWebsites(Set<String> stids) {
        for (String stid : stids) {
            String path = OUTPUTFOLDER + EVENTPATH + stid +".html";
            String websiteUrl = EVENT_URL + "?stid=" + stid;
            try {
                downloadWebsite(websiteUrl, path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void downloadWebsite(String websiteURL, String path) throws IOException {
            URL url = new URL(websiteURL);

                try (BufferedInputStream in = new BufferedInputStream(url.openStream())) {
                    // Read the website content into a string
                    String websiteContent = readWebsiteContent(in);

                    // Modify the content by removing unwanted parts
                    //websiteContent = removeUnwantedParts(websiteContent);

                    // Write the modified content to the file
                    try (FileOutputStream fileOutputStream = new FileOutputStream(path)) {
                        fileOutputStream.write(websiteContent.getBytes(StandardCharsets.UTF_8));
                    }
            }
        }
    public void downloadCalendar() {
        try {
            downloadWebsite(CALENDAR_URL, OUTPUTFOLDER + CALENDAR_PATH);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private String readWebsiteContent(BufferedInputStream in) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = in.read(buffer, 0, 1024)) != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead);
        }
        return byteArrayOutputStream.toString(StandardCharsets.UTF_8);
    }
    private String removeUnwantedParts(String websiteContent) {
        Document document = Jsoup.parse(websiteContent);

        Element headerElement = document.selectFirst("header");
        if (headerElement != null) {
            headerElement.remove();
        }

        // Remove all elements after the element with class "mod-teaser--4er"
        Element fourthElement = document.selectFirst("div.mod-teaser--4er");
        if (fourthElement != null) {
            fourthElement.remove();
        }
        Element footerElement = document.selectFirst("footer");
        if (footerElement != null) {
            footerElement.remove();
        }

        // Remove <script> tags and their contents
        Elements scriptElements = document.select("script");
        scriptElements.remove();

        // Remove <link> tags and their contents
        Elements linkElements = document.select("link");
        linkElements.remove();
        // Get the modified HTML content

        return document.html();
        }
        public File readFile(String filePath) {
        return new File(filePath);
        }

    public String getPath(String stid) {
        return OUTPUTFOLDER + EVENTPATH + stid +".html";

    }

    public String getUrlFromStid(String stid) {
        return EVENT_URL + "?stid=" + stid;
    }

    public String getCalendarPath() {
        return OUTPUTFOLDER + CALENDAR_PATH;
    }
}
