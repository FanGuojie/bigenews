package garbagemayor.bigenews.newssrc;

public class NewsItem {
    private String news_Time;           //<!-- 时间 -->
    private String news_URL;            //<!-- 新闻的URL链接 -->
    private String news_Author;         //<!--新闻的作者-->
    private String news_Content;        //<!-- 新闻正文 -->
    private String news_Journal;        //<!--记者列表-->
    private String news_Title;

    public String getTitle() {
        return news_Title;
    }
    public String getContent() {
        return news_Content;
    }
    public String getAuthor() {
        return news_Author;
    }
    public String getTime() {
        return news_Time;
    }
    public String getJournal() {
        return news_Journal;
    }


}
