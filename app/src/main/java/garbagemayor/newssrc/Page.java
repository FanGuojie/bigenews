package my_news;

import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;
import java.net.URL;
import java.net.URLConnection;

import net.sf.json.*;
import com.google.gson.*;



class Content {
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

    public String print() {
        return "newsClassTag; " + newsClassTag + "\n" +
                "Source: " + news_Source + "\n" +
                "Title: " + news_Title + "\n" +
                "Time: " + news_Time+ "\n" +
                "ID: " + news_ID + "\n";
    }
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
}

class news {
    private Content[] list;
    private String pageNo;
    private String pageSize;
    private String totalPages;
    private String totalRecords;

    public String print() {
        String res = "";
        for (Content c : list)
            res += c.print();
        return res + "pageNo:" + pageNo;
    }
    public String getPageNo() {
        return pageNo;
    }
    public String getTotalPages() {
        return totalPages;
    }
    public String getTotalRecords() {
        return totalRecords;
    }
    public Content[] getContent() {
        return list;
    }

}

class Detail {
    private String news_Time;           //<!-- 时间 -->
    private String news_URL;            //<!-- 新闻的URL链接 -->
    private String news_Author;         //<!--新闻的作者-->
    private String news_Content;        //<!-- 新闻正文 -->
    private String news_Journal;        //<!--记者列表-->

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

public class Page {
    private news n;
    public Content[] cont;
    public Page(int No, int Size) {
        this(String.format("http://166.111.68.66:2042/news/action/query/latest?pageNo=%d&pageSize=%d", No, Size));
    }
    public Page(int category, int No, int Size){
        this(String.format("http://166.111.68.66:2042/news/action/query/latest?category=%d&pageNo=%d&pageSize=%d", category, No, Size));
    }
    public Page(String keyword, int category, int No, int Size){
        this(String.format("http://166.111.68.66:2042/news/action/query/search?keyword=%s&category=%d&pageNo=%d&pageSize=%d", keyword, category, No, Size));
    }
    public Page(String url) {
        String result = "";
        BufferedReader in = null;
        try {
            URL realUrl = new URL(url);
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
        //System.out.println(result);
        Gson gson = new GsonBuilder().create();
        news n1 = gson.fromJson(result, news.class);
        n=n1;
        cont = n.getContent();
    }
    public Detail getdetails(int id) {
        Detail d=new Detail();
        if (id >= cont.length)
            return d;
        Content[] c = n.getContent();
        String result = "";
        BufferedReader in = null;
        String urlNameString = String.format("http://166.111.68.66:2042/news/action/query/detail?newsId="+c[id].getId());
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
        d = gson.fromJson(result, Detail.class);
        //System.out.println(d.getContent());
        return d;

    }
    public String print() {
        return n.print();
    }
    public int getLength() {
        return cont.length;
    }
    public String getId(int id) {
        return cont[id].getId();
    }
    public String getClassTag(int id) {
        return cont[id].getClassTag();
    }
    public String getSource(int id) {
        return cont[id].getSource();
    }
    public String getAuthor(int id) {
        return cont[id].getAuthor();
    }
    public String getTitle(int id) {
        return cont[id].getTitle();
    }
    public String getTime(int id) {
        return cont[id].getTime();
    }
    public String getIntro(int id) {
        return cont[id].getIntro();
    }
}

