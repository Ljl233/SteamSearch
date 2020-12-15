package com.ljl.steamsearch.parse;

import com.google.gson.Gson;
import com.ljl.steamsearch.model.CommentResponse;
import com.ljl.steamsearch.util.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Comment;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PageParser {
    static final String CHARSET_STRING = "charset";
    public static String COMMENT_BASE_URL = "https://store.steampowered.com/appreviews/";

    public static String getCharset(String content) {
        if (content == null) return null;

        int index;
        String ans = null;
        index = content.indexOf(CHARSET_STRING);
        if (index == -1) return null;

        content = content.substring(index + CHARSET_STRING.length()).trim();
        if (content.startsWith("=")) {
            content = content.substring(1).trim();
            if (content.startsWith("\"") && content.endsWith("\"") && (1 < content.length())) {
                content = content.substring(1, content.length() - 1);
            } else if (content.startsWith("'") && content.endsWith("'") && (1 < content.length())) {
                content = content.substring(1, content.length() - 1);
            } else {
                int end = content.indexOf("\"");
                content = content.substring(0, end);
            }
            ans = content;
        }

        return ans;
    }

    //get url from html
    //href="https://href="https://store.steampowered.com/app/306130/The_Elder_Scrolls_Online/?snr=1_4_660__629"306130/The_Elder_Scrolls_Online/?snr=1_4_660__629"
    public static HashSet<String> findHref(String content) {
        HashSet<String> urls = new HashSet<>();
        if (content.isEmpty())
            content = "<link href=\"https://store.st.dl.pinyuncloud.com/public/shared/css/shared_global.css?v=b6223625&amp;l=english&amp;_cdn=china_pinyuncloud\" rel=\"stylesheet\" type=\"text/css\" >";
        String s = "href=\".*?\"";
        Pattern pattern = Pattern.compile(s);
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            System.out.println(matcher.group());
            urls.add(matcher.group());
        }
        System.out.println("hrefs.size = " + urls.size());
        return urls;
    }

    public static HashSet<String> getGamesUrlFromHref(String content) {
        HashSet<String> hrefs = findHref(content);
        HashSet<String> gameUrls = new HashSet<>();
        hrefs.forEach(href -> {
            String url = href.substring(6, href.length() - 1);
            if (url.contains("https://store.steampowered.com/app/")) {
                gameUrls.add(url);
            }
        });
        return gameUrls;
    }

    public static ArrayList<String> getComments(String path) {
        ArrayList<String> comments = new ArrayList<>();
        Element element = getCommentElement(path);
        if (element == null) return comments;
        Elements elements = element.getElementsByClass("content");
        System.out.println(elements.size());
        elements.forEach(e -> {
            comments.add(e.text());
        });
        return comments;
    }

    public static Element getCommentElement(String path) {
        File file = new File("E:\\IdeaProject\\page.html");

        try {
            Document document = Jsoup.parse(file, "utf-8");
            return document.getElementById("Reviews_summary");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

//    public static Comment getCommentHtmlFromHref(String href){
//
//    }

    /**
     * @param href "https://store.steampowered.com/app/1453220/Be_a_Pirate/?snr=1_7_7_230_150_28";
     * @return https://store.steampowered.com/appreviews/1453220?cursor=*&day_range=30&start_date=-1&end_date=-1&date_range_type=all&filter=summary&language=schinese&l=schinese&review_type=all&purchase_type=all&playtime_filter_min=0&playtime_filter_max=0&filter_offtopic_activity=1&summary_num_positive_reviews=0&summary_num_reviews=0
     */
    public static String getGameCommentRequestFromHref(String href) {
        String request = COMMENT_BASE_URL;
        return request + getGameId(href)
                + "?cursor=*&day_range=30&start_date=-1&end_date=-1&date_range_type=all&filter=summary&language=schinese&l=schinese&review_type=all&purchase_type=all&playtime_filter_min=0&playtime_filter_max=0&filter_offtopic_activity=1&summary_num_positive_reviews=0&summary_num_reviews=0";
    }

    public static String getGameId(String href) {
        String s = "https://store.steampowered.com/app/.*?/";
        Pattern pattern = Pattern.compile(s);
        Matcher matcher = pattern.matcher(href);
        String gameId = null;
        if (matcher.find()) {
            gameId = matcher.group().substring(35, matcher.group().length() - 1);
            System.out.println(gameId);
        }
        return gameId;
    }

    public static void main(String[] args) {
        String href = "https://store.steampowered.com/app/265610/Epic_Battle_Fantasy_4/";
        String requestUrl = getGameCommentRequestFromHref(href);

        String gameName;
        String publishDate;

        Document document = null;
        try {
            document = Jsoup.connect(href).get();
            gameName = document.getElementsByClass("apphub_AppName").get(0).text();
            System.out.println("gameName:" + gameName);

            //todo:设置中国
            publishDate = document.getElementsByClass("date").get(0).text();
            System.out.println("publishDate:" + publishDate);
        } catch (IOException e) {
            e.printStackTrace();
        }


        final String[] commentsHtml = new String[1];
        RetrievePage.downloadPage(requestUrl,
                content -> {
                    Gson gson = new Gson();
                    CommentResponse commentResponse = gson.fromJson(content, CommentResponse.class);
                    commentsHtml[0] = commentResponse.getHtml();
                    parseCommentsHtml(commentsHtml[0]);
                });
    }

    public static void parseCommentsHtml(String html) {
        int approveCount;
        String postDate;
        int index;
        String content;
        String publisher;
        int publisherCommentCount;
        int publisherGamesCount;

        Document document = Jsoup.parse(html);
        Elements reviewBox = document.getElementsByClass("review_box   ");
        Elements reviewsBoxPartial = document.getElementsByClass("review_box    partial");
        Element element = reviewBox.get(0);
        System.out.println("html---------------------");
        System.out.println(element.html());

        postDate = element.getElementsByClass("postedDate").get(0).text();
        postDate = postDate.substring(4);
        System.out.println("postDate:" + postDate);

        String voteInfo = element.getElementsByClass("vote_info").get(0).text();
        voteInfo = voteInfo.substring(2, voteInfo.length() - 11);
        approveCount = Integer.parseInt(voteInfo);
        System.out.println("approveCount:" + approveCount);

        content = element.getElementsByClass("content").get(0).text();
        System.out.println("content:" + content);

        publisher = element.getElementsByClass("persona_name").get(0)
                .getElementsByTag("a").get(0).text();
        System.out.println("publisher:" + publisher);

        String numOwnedGames = element.getElementsByClass("num_owned_games").get(0)
                .getElementsByTag("a").get(0).text();
        publisherGamesCount = Integer.parseInt(numOwnedGames.substring(6, numOwnedGames.length() - 4));
        System.out.println("publisherGamesCount:" + publisherGamesCount);

        String numReviews = element.getElementsByClass("num_reviews").get(0)
                .getElementsByTag("a").get(0).text();
        publisherCommentCount = Integer.parseInt(numReviews.substring(0, numReviews.length() - 4));
        System.out.println("publisherCommentCount:" + publisherCommentCount);

    }
}
