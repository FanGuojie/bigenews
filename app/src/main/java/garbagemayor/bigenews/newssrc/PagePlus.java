package garbagemayor.bigenews.newssrc;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLConnection;

import garbagemayor.bigenews.MainActivity;

public class PagePlus {
    public static String TAG = "PagePlusTag";
    private News n;
    public PageItem[] cont;
    public PagePlus(int No, int Size) {
        this(String.format("http://166.111.68.66:2042/news/action/query/latest?pageNo=%d&pageSize=%d", No, Size));
    }
    public PagePlus(int category, int No, int Size){
        this(String.format("http://166.111.68.66:2042/news/action/query/latest?category=%d&pageNo=%d&pageSize=%d", category, No, Size));
    }
    public PagePlus(String keyword, int category, int No, int Size){
        this(String.format("http://166.111.68.66:2042/news/action/query/search?keyword=%s&category=%d&pageNo=%d&pageSize=%d", keyword, category, No, Size));
    }
    public PagePlus(String url) {
        Log.d(TAG, "PagePlus start");
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
            Log.d(TAG, e.toString());
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
        Log.d(TAG, "result = " + result);


        //System.out.println(result);
        Gson gson = new GsonBuilder().create();
        n = gson.fromJson(result, News.class);
        cont = n.getPageItem();


    }
    public NewsItem getNewsItem(int id) {
        NewsItem d=new NewsItem();
        if (id >= cont.length)
            return d;
        PageItem[] c = n.getPageItem();
        String result = "";
        BufferedReader in = null;
        String urlNameString = String.format("http://166.111.68.66:2042/news/action/query/NewsItem?newsId="+c[id].getId());
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
