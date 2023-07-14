package de.hsos.swa.jonas.theater.crawler.control;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.enterprise.context.ApplicationScoped;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;
import java.net.URL;

@ApplicationScoped
public class WebsiteDownloader {
    private static final String OUTPUTFOLDER = "src/main/resources/crawledPages";
    private static final String WEBSITE_URL = "https://www.theater-osnabrueck.de/spielplan-detail/";
    private static int counter = 1;

    public void downloadAllWebsites(Map<String, String> websites) {
        for (Map.Entry<String, String> entry : websites.entrySet()) {
            String stid = entry.getKey();
            String link = entry.getValue();
            try {
                downloadWebsite(link, stid);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public String getPath(String websiteUrl) {
        try {
            URL url = new URL(websiteUrl);
            String path= url.getPath();
            String query = url.getQuery();
            if (query != null && !query.isEmpty()) {
                path += "?" + query;
            }
            String fileName = path+".html";
            return OUTPUTFOLDER + "/" + fileName;
        } catch (IOException e) {
            e.printStackTrace();
        return null;
        }
    }
    public void downloadWebsite(String websiteURL, String filepath) throws IOException {
            URL url = new URL(websiteURL);
            String path = getPath(websiteURL);

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
}
