package garbagemayor.bigenews.newssrc;

import android.util.Log;

public class NewsItem {
    private String news_ID;
    private String news_Time;           //<!-- 时间 -->
    private String news_URL;            //<!-- 新闻的URL链接 -->
    private String news_Author;         //<!--新闻的作者-->
    private String news_Content;        //<!-- 新闻正文 -->
    private String news_Journal;        //<!--记者列表-->
    private String news_Title;
    private String news_Pictures;

    public String getId() {
        return news_ID == null ? "" : news_ID;
    }
    public String getURL() {
        return news_URL == null ? "" : news_URL;
    }
    public String getTitle() {
        return news_Title == null ? "" : news_Title;
    }
    public String getContent() {
        return news_Content == null ? "" : news_Content;
    }
    public String getAuthor() {
        return news_Author == null ? "" : news_Author;
    }
    public String getTime() {
        try {
            if(news_Time.contains("年")) {
                return news_Time;
            }
            int year = Integer.parseInt(news_Time.substring(0, 4));
            int month = Integer.parseInt(news_Time.substring(4, 6));
            int day = Integer.parseInt(news_Time.substring(6, 8));
            return year + "年" + month + "月" + day + "日";
        } catch (Exception e) {
            return "未知时间" + news_Time;
        }
    }
    public String getJournal() {
        return news_Journal == null ? "" : news_Journal;
    }
    public String getPictures() {
        return news_Pictures == null ? "" : news_Pictures;
    }


}
