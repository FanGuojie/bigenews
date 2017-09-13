package garbagemayor.bigenews;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import garbagemayor.bigenews.newssrc.NewsItem;
import garbagemayor.bigenews.newssrc.PageItem;

public class DatabaseLoader{
    final String TAG = "DatabaseLoader";

    Context context;

    private SQLiteDatabase db;

    private Map<String, PageItem> page;
    private Map<String, NewsItem> news;
    private List<PageItem> history;


    public DatabaseLoader(Context context) {
        this.context = context;
        history = new ArrayList<>();
        page = new HashMap<>();
        news = new HashMap<>();
        String DBName = context.getApplicationInfo().dataDir + "/databases/test.db";//android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "test.db";
        File f_=new File(DBName);
            if(!f_.getParentFile().exists()){
                Log.d("DBService", "文件夹不存在，新建一个");
                f_.getParentFile().mkdirs();
            }
        db = SQLiteDatabase.openOrCreateDatabase(DBName, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS History (id INTEGER PRIMARY KEY AUTOINCREMENT, type TEXT, content TEXT)");
        Cursor cursor = db.query ("History",null,null,null,null,null,null);
        if(cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String type = cursor.getString(cursor.getColumnIndex("type"));
                String content = cursor.getString(cursor.getColumnIndex("content"));
                if (type == "page") {
                    Gson gson = new GsonBuilder().create();
                    PageItem p = gson.fromJson(content, PageItem.class);
                    page.put(p.getId(), p);
                    history.add(p);
                } else {
                    Gson gson = new GsonBuilder().create();
                    NewsItem n = gson.fromJson(content, NewsItem.class);
                    news.put(n.getId(), n);
                }
            }while (cursor.moveToNext());
        }   
    } 



    public void storeNews(PageItem pageItem) {
        if (page.containsKey(pageItem.getId()))
            return;
        page.put(pageItem.getId(), pageItem);
        Gson gson = new Gson();
        String str = gson.toJson(pageItem);
        ContentValues contentValues = new ContentValues();
        contentValues.put("type", "page");
        contentValues.put("content", str);
        db.insert("History", null, contentValues);
    }

    public void storeNewsDetail(NewsItem newsItem) {
        if (news.containsKey(newsItem.getId()))
            return;
        news.put(newsItem.getId(), newsItem);
        Gson gson = new Gson();
        String str = gson.toJson(newsItem);
        ContentValues contentValues = new ContentValues();
        contentValues.put("type", "news");
        contentValues.put("content", str);
        db.insert("History", null, contentValues);
    }

    public PageItem queryPage(String id) {
        if (page.containsKey(id))
            return page.get(id);
        else
            return null;
    }

    public NewsItem queryNews(String id) {
        if (news.containsKey(id))
            return news.get(id);
        else
            return null;
    }

    public void addHistory(PageItem pageItem) {
        Log.d(TAG, "adding");
        history.add(pageItem);
        Log.d(TAG, "finish");
        storeNews(pageItem);
    }




}