package garbagemayor.bigenews.newssrc;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class PageItem {
    private String newsClassTag;            //<!--新闻所属的分类-->
    private String news_ID;                  //<!-- 新闻id-->
    private String news_Source;             //<!-- 新闻来源 -->
    private String news_Title;              //<!--标题 -->
    private String news_Time;               //<!--时间 -->
    private String news_URL;                //<!--新闻的URL链接 -->
    private String news_Author;            //<!--新闻的作者-->
    private String lang_Type;              //<!--语言类型 -->
    private String news_Pictures;          //<!--新闻的图片路径-->
    private String news_Video;
    private String news_Intro;             //<!-- 简介 -->


    public String getId() {
        return news_ID;
    }
    public String getClassTag() {
        return newsClassTag;
    }
    public String getSource() {
        return news_Source;
    }
    public String getAuthor() {
        return news_Author;
    }
    public String getTitle() {
        return news_Title;
    }
    public String getTime() {
        return news_Time;
    }
    public String getIntro() {
        return news_Intro;
    }

    public String print() {
        return "newsClassTag; " + newsClassTag + "\n" +
                "Source: " + news_Source + "\n" +
                "Title: " + news_Title + "\n" +
                "Time: " + news_Time+ "\n" +
                "ID: " + news_ID + "\n";
    }

    public NewsItem getNewsItem() {
        NewsItem d=new NewsItem();
        String result = "";
        BufferedReader in = null;
        String urlNameString = String.format("http://166.111.68.66:2042/news/action/query/NewsItem?newsId=" + news_ID);
        try {
            URL realUrl = new URL(urlNameString);
            URLConnection connection = realUrl.openConnection();
            connection.connect();
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("无网络连接" + e);
            e.printStackTrace();
        }
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        Gson gson = new GsonBuilder().create();
        d = gson.fromJson(result, NewsItem.class);
        //System.out.println(d.getPageItem());
        return d;
    }
}
