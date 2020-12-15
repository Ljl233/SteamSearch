package com.ljl.steamsearch.crawler;

import com.google.gson.Gson;
import com.ljl.steamsearch.parse.RetrievePage;
import com.ljl.steamsearch.model.GamesResponse;
import com.ljl.steamsearch.util.FileUtils;

import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GamesUrlsCrawler {
    public static final String POPULAR_GAMES_URL =
            "https://store.steampowered.com/search/?filter=popularnew&sort_by=Released_DESC&os=win";
    public int totalCount = 0;
    private HashSet<String> gamesUrls = new HashSet<>();

    public HashSet<String> getGamesUrls() {
        return gamesUrls;
    }

    public static void main(String[] args) {
//        RetrievePage.downloadPage("https://store.steampowered.com/search/results/?query&start=550&count=50&dynamic_data=&sort_by=Released_DESC&os=win&snr=1_7_7_230_7&infinite=1",
//                content -> {
//                    String html = getResultHtml(content);
//                });

/*
        String responseHtml = FileUtils.readFile("E:/IdeaProject/response.html");
        getHrefAndClean(responseHtml);
*/
        GamesUrlsCrawler crawler = new GamesUrlsCrawler();
        crawler.crawlGames();
        FileUtils.writeFile(crawler.getGamesUrls(), "gamesUrls.txt", true);

//        FileUtils.readFileByLines("gamesUrls.txt");
    }

    public String getResultHtml(String response) {
        Gson gson = new Gson();
        GamesResponse gamesResponse = gson.fromJson(response, GamesResponse.class);
        if (totalCount == 0) {
            totalCount = gamesResponse.getTotal_count();
        }
        return gamesResponse.getResults_html();
    }

    //href=\"https:\/\/store.steampowered.com\/app\/1406640\/Fantasy_Grounds__Aquatic_Menace_3\/?snr=1_7_7_230_150_7\"
    //remove href=“”
    public void getHrefAndClean(String html) {
        if (html.isEmpty())
            html = "href=\\\"https:\\/\\/store.steampowered.com\\/app\\/1406640\\/Fantasy_Grounds__Aquatic_Menace_3\\/?snr=1_7_7_230_150_7\\\"";
        String s = "href=\".*?\"";
        Pattern pattern = Pattern.compile(s);
        Matcher matcher = pattern.matcher(html);

        while (matcher.find()) {
            String href = matcher.group();
            String url = href.substring(6, href.length() - 1);
            if (url.contains("https://store.steampowered.com/app/")) {
                System.out.println(url);
                gamesUrls.add(url);
            }
        }
    }

    public void crawlGames() {
        int start = 0;
        do {
            String request = "https://store.steampowered.com/search/results/?query&start="
                    + start
                    + "&count=50&dynamic_data=&sort_by=Released_DESC&os=win&snr=1_7_7_230_7&infinite=1";
            RetrievePage.downloadPage(request, content -> {
                String html = getResultHtml(content);
                System.out.println("------------------------------");
                getHrefAndClean(html);
            });
            start += 50;
        } while (start < totalCount);
    }
}
