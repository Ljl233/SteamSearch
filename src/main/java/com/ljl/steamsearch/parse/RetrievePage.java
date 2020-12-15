package com.ljl.steamsearch.parse;

import com.ljl.steamsearch.parse.OnPageListener;
import com.ljl.steamsearch.util.FileUtils;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.util.concurrent.TimeUnit;


//获取页面
public class RetrievePage {

    public static final String STEAM_HOME_URL = "https://store.steampowered.com/";
//    public static String downloadPage(String path) {
//        URL pageURL;
//        BufferedReader reader;
//        String line;
//        StringBuilder pageBuffer = new StringBuilder();
//
//        try {
//            pageURL = new URL(path);
//            reader = new BufferedReader(new InputStreamReader(pageURL.openStream()));
//            while ((line = reader.readLine()) != null) {
//                pageBuffer.append(line);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return pageBuffer.toString();
//    }

    //https://www.cnblogs.com/z-belief/p/11171822.html
    public static void downloadPage(String path, OnPageListener listener) {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        CloseableHttpResponse response = null;
        HttpEntity entity = null;
        try {
            HttpGet httpGet = new HttpGet(path);
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectionRequestTimeout(2, TimeUnit.SECONDS)
                    .build();
            httpGet.setConfig(requestConfig);
            response = httpClient.execute(httpGet);

            entity = response.getEntity();
            System.out.println("响应状态为：" + response.getCode());
            if (entity != null && listener != null) {
                listener.dealPage(EntityUtils.toString(entity, "utf-8"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void downloadToLocal(String url, String path) {
        if (path == null) {
            path = "E:\\IdeaProject\\page.html";
        }
        String finalPath = path;
        downloadPage(url, html -> {
            FileUtils.writeFile(html, finalPath, false);
        });
    }

    public static void main(String[] args) {
        downloadPage("https://store.steampowered.com/search/results/?query&start=550&count=50&dynamic_data=&sort_by=Released_DESC&os=win&snr=1_7_7_230_7&infinite=1",
                content -> {
                    System.out.println(content);
                });

    }
}

