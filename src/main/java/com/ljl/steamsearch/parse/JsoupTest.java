package com.ljl.steamsearch.parse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;

public class JsoupTest {
    public static void main(String[] args) {
        File file = new File("E:\\IdeaProject\\page.html");

        try {
            Document document = Jsoup.parse(file, "utf-8");
            Element element = document.getElementById("Reviews_summary");
            Elements elements = element.getElementsByClass("content");
            System.out.println(elements.size());
            elements.forEach(e -> {
//            System.out.println("key:" + element.attr("name") + "  value:" + element.attr("value"));
                System.out.println(e.text());
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
