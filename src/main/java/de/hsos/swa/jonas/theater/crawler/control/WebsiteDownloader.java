package de.hsos.swa.jonas.theater.crawler.control;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.net.URL;

public class WebsiteDownloader {
    private static String OUTPUTFOLDER = "src/main/resources";
    private static int counter = 1;

    public static void downloadWebsites(Set<String> websiteUrls) {
        for (String websiteUrl : websiteUrls) {
                try {
                    downloadWebsite(websiteUrl, OUTPUTFOLDER);
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }
    public static void downloadWebsite(String websiteURL, String outputFolder) throws IOException {
        URL url = new URL(websiteURL);
        String fileName = "file" + counter++ + ".html";
                // ...

                try (BufferedInputStream in = new BufferedInputStream(url.openStream())) {
                    // Read the website content into a string
                    String websiteContent = readWebsiteContent(in);

                    // Modify the content by removing unwanted parts
                    String modifiedContent = removeUnwantedParts(websiteContent);

                    // Write the modified content to the file
                    try (FileOutputStream fileOutputStream = new FileOutputStream(outputFolder + "/" + fileName)) {
                        fileOutputStream.write(modifiedContent.getBytes(StandardCharsets.UTF_8));
                    }
            }
        }
    private static String readWebsiteContent(BufferedInputStream in) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = in.read(buffer, 0, 1024)) != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead);
        }
        return byteArrayOutputStream.toString(StandardCharsets.UTF_8);
    }
    private static String removeUnwantedParts(String websiteContent) {
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
        String modifiedContent = document.html();

        return modifiedContent;
        }
}
