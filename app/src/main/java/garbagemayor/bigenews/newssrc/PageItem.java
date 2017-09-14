package garbagemayor.bigenews.newssrc;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PageItem {
    public static String TAG = "PageItemTag";

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
    public PageItem(NewsItem item) {
        this.news_Title = item.getTitle();
        this.news_Time = item.getTime();
    }

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
        news_Source = prefixTrim(news_Source);
        return news_Source;
    }
    public String getAuthor() {
        news_Author = prefixTrim(news_Author);
        return news_Author;
    }
    public String getTitle() {
        news_Title = prefixTrim(news_Title);
        return news_Title;
    }
    public String getTime() {
        try {
            int year = Integer.parseInt(news_Time.substring(0, 4));
            int month = Integer.parseInt(news_Time.substring(4, 6));
            int day = Integer.parseInt(news_Time.substring(6, 8));
            return year + "年" + month + "月" + day + "日";
        } catch (Exception e) {
            Log.d(TAG, "news_Time = " + news_Time);
            return "未知时间" + news_Time;
        }
    }
    public String getIntro() {
        news_Intro = prefixTrim(news_Intro);
        return news_Intro;
    }

    public List<Bitmap> getImageList() {
        List<Bitmap> bmpList = new ArrayList<Bitmap>();
        String[] bmpUrlStrList = news_Pictures.split(";| |\n|\t|\r");
        for (String bmpUrlStr: bmpUrlStrList) {
            Bitmap bitmap = getBitmapFromUrl(bmpUrlStr);
            if (bitmap != null) {
                Log.d(TAG, "加载图片成功，URL=" + bmpUrlStr);
                bmpList.add(bitmap);
            }
        }
        return bmpList;
    }

    public List<String> getImageUrlList() {
        List<String> urlList = new ArrayList<>();
        String[] urlArray = news_Pictures.split(";| |\n|\t|\r");
        for(String url: urlArray) {
            urlList.add(url);
        }
        return urlList;
    }

    private static String prefixTrim(String str) {
        str = str.replaceAll("　","  ");
        str = str.replaceAll("\t","  ");
        for (int i = 0; i < str.length(); i++) {
            if(str.charAt(i) != ' ') {
                str =  str.substring(i, str.length());
                break;
            }
        }
        return str;
    }

    private static Bitmap getBitmapFromUrl(String urlStr) {
        Bitmap bitmap = null;
        try {
            URL imgUrl = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) imgUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
