package org.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

public class Scraper {
    public Document getDocument(){
        String url = "https://arenariga.com/#events";
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();

            //con.setRequestProperty("User-Agent", "Mozilla/5.0");

            int responseCode = con.getResponseCode();
            System.out.println("Response code: " + responseCode);

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            String html = response.toString();
            return Jsoup.parse(html);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public String scrapeEvents(){
        Document doc = getDocument();

        Advent[] adventList = {};
        Elements targets = doc.select("article").select(".event_href");
        for (Element target : targets) {
            Advent advent = new Advent(target.select(".entry-title").text());
            advent.date = target.select(".date").text().replaceAll(" • "+target.select(".cats").text(), ""); // Second argument formats the .date class string (replaces garbage with "" > nothing)
            advent.link = target.select(".event_href").attr("href");
            //System.out.printf("TITLE: %s | DATE: %s | IMG: %s\n", advent.title, advent.date, advent.image);
            adventList = Arrays.copyOf(adventList, adventList.length+1);
            adventList[adventList.length-1] = advent;
        }
        StringBuilder adventListString = new StringBuilder();
        for (Advent advent : adventList){
            adventListString.append("\n• %s <strong>|</strong> <i>%s</i> <strong>|</strong> %s".formatted(advent.title, advent.date, advent.link));
        }
        return adventListString.toString();
    }
}
