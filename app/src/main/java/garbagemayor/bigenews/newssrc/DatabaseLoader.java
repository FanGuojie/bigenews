package garbagemayor.bigenews.newssrc;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import garbagemayor.bigenews.newssrc.NewsItem;
import garbagemayor.bigenews.newssrc.PageItem;
import garbagemayor.bigenews.pageprovider.NewsList;

public class DatabaseLoader{
    final String TAG = "DatabaseLoader";

    Context mContext;

//    private Map<String, PageItem> page;
//    private Map<String, NewsItem> news;
    public List<PageItem> history;
    public List<PageItem> history_list;
    public NewsItem item;
    private static boolean online;
    private final String fileName = "list.json";


    public DatabaseLoader(Context context) {
        this.mContext = context;
        history = new ArrayList<>();
//        page = new HashMap<>();
//        news = new HashMap<>();
        String[] filelist = mContext.fileList();
        for (String file : filelist) {
            if (file.contains("2016")) {
//                Toast.makeText(mContext, file, Toast.LENGTH_SHORT).show();
                history.add(new PageItem(queryNews(file)));
            }
        }
//        String DBName = context.getApplicationInfo().dataDir + "/databases/test.db";//android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "test.db";
//        File f_=new File(DBName);
//            if(!f_.getParentFile().exists()){
//                Log.d("DBService", "文件夹不存在，新建一个");
//                f_.getParentFile().mkdirs();
//            }
//        db = SQLiteDatabase.openOrCreateDatabase(DBName, null);
//        db.execSQL("CREATE TABLE IF NOT EXISTS History (id INTEGER PRIMARY KEY AUTOINCREMENT, type TEXT, content TEXT)");
//        Cursor cursor = db.query ("History",null,null,null,null,null,null);
//        if(cursor.moveToFirst()) {
//            do {
//                int id = cursor.getInt(cursor.getColumnIndex("id"));
//                String type = cursor.getString(cursor.getColumnIndex("type"));
//                String content = cursor.getString(cursor.getColumnIndex("content"));
//                if (type == "page") {
//                    Gson gson = new GsonBuilder().create();
//                    PageItem p = gson.fromJson(content, PageItem.class);
//                    page.put(p.getId(), p);
//                    history.add(p);
//                } else {
//                    Gson gson = new GsonBuilder().create();
//                    NewsItem n = gson.fromJson(content, NewsItem.class);
//                    news.put(n.getId(), n);
//                }
//            }while (cursor.moveToNext());
//        }

    }


//
//    public void storeNews(PageItem pageItem) {
//        page.put(pageItem.getId(), pageItem);
//        Gson gson = new Gson();
//        String str = gson.toJson(pageItem);
//        ContentValues contentValues = new ContentValues();
//        contentValues.put("type", "page");
//        contentValues.put("content", str);
//        db.insert("History", null, contentValues);
//    }
//
//    public void storeNewsDetail(NewsItem newsItem) {
//        if (news.containsKey(newsItem.getId()))
//            return;
//        news.put(newsItem.getId(), newsItem);
//        Gson gson = new Gson();
//        String str = gson.toJson(newsItem);
//        ContentValues contentValues = new ContentValues();
//        contentValues.put("type", "news");
//        contentValues.put("content", str);
//        db.insert("History", null, contentValues);
//    }
//
//    public PageItem queryPage(String id) {
//        if (page.containsKey(id))
//            return page.get(id);
//        else
//            return null;
//    }

    public NewsItem queryNews(final String id) {
        online = false;
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    if (Inet4Address.getByName("166.111.68.66").isReachable(200)) {
                        DatabaseLoader.online = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (DatabaseLoader.online) {
            Thread t = new Thread() {
                @Override
                public void run() {
                    try {
                            item = PagePlus.getNewsItem(id);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            t.start();
            try {
                t.join();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
               writeJsonStream(mContext.openFileOutput(item.getId(), Context.MODE_PRIVATE), item);
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        } else {
            try {
                item = readJsonStreamDetail(mContext.openFileInput(id));
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        }
        return item;
    }

    public List<PageItem> queryPage(final int i, final int sizeOfPage) {
        online = false;
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    if (Inet4Address.getByName("166.111.68.66").isReachable(200)) {
                        DatabaseLoader.online = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (DatabaseLoader.online) {
            Thread t = new Thread() {
                @Override
                public void run() {
                    try {
                        PagePlus p = new PagePlus(i, sizeOfPage);
                        history_list = Arrays.asList(p.cont);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            t.start();
            try {
                t.join();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                writeJsonStream(mContext.openFileOutput(fileName, Context.MODE_PRIVATE), history_list);
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        } else {
            try {
                history_list = readJsonStream(mContext.openFileInput(fileName));
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        }
        return history_list;
    }

    public List<PageItem> queryPage(final int nowCategoryId, final int i, final int sizeOfPage) {
        online = false;
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    if (Inet4Address.getByName("166.111.68.66").isReachable(200)) {
                        DatabaseLoader.online = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (DatabaseLoader.online) {
            Thread t = new Thread() {
                @Override
                public void run() {
                    try {
                        PagePlus p = new PagePlus(nowCategoryId, i, sizeOfPage);
                        history_list = Arrays.asList(p.cont);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            t.start();
            try {
                t.join();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                writeJsonStream(mContext.openFileOutput(fileName, Context.MODE_PRIVATE), history_list);
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        } else {
            try {
                history_list = readJsonStream(mContext.openFileInput(fileName));
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        }
        return history_list;
    }

    public List<PageItem> queryPage(final String nowSearchText, final int i, final int sizeOfPage) {
        online = false;
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    if (Inet4Address.getByName("166.111.68.66").isReachable(200)) {
                        DatabaseLoader.online = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (DatabaseLoader.online) {
            Thread t = new Thread() {
                @Override
                public void run() {
                    try {
                        PagePlus p = new PagePlus(nowSearchText, i, sizeOfPage);
                        history_list = Arrays.asList(p.cont);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            t.start();
            try {
                t.join();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                writeJsonStream(mContext.openFileOutput(fileName, Context.MODE_PRIVATE), history_list);
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        } else {
            try {
                history_list = readJsonStream(mContext.openFileInput(fileName));
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        }
        return history_list;
    }

    public List<PageItem> queryPage(final String nowSearchText, final int nowCategoryId, final int i, final int sizeOfPage) {
        online = false;
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    if (Inet4Address.getByName("166.111.68.66").isReachable(200)) {
                        DatabaseLoader.online = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (DatabaseLoader.online) {
            Thread t = new Thread() {
                @Override
                public void run() {
                    try {
                        PagePlus p = new PagePlus(nowSearchText, nowCategoryId, i, sizeOfPage);
                        history_list = Arrays.asList(p.cont);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            t.start();
            try {
                t.join();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                writeJsonStream(mContext.openFileOutput(fileName, Context.MODE_PRIVATE), history_list);
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        } else {
            try {
                history_list = readJsonStream(mContext.openFileInput(fileName));
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        }
        return history_list;
    }

    private List<PageItem> readJsonStream(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        Gson gson = new Gson();
        List<PageItem> messages = new ArrayList<>();
        reader.beginArray();
        while (reader.hasNext()) {
            PageItem message = gson.fromJson(reader, PageItem.class);
            messages.add(message);
        }
        reader.endArray();
        reader.close();
        return messages;
    }

    private void writeJsonStream(OutputStream out, List<PageItem> messages) throws IOException {
        JsonWriter writer = new JsonWriter(new OutputStreamWriter(out, "UTF-8"));
        Gson gson = new Gson();
        writer.setIndent("  ");
        writer.beginArray();
        for (PageItem message : messages) {
            gson.toJson(message, PageItem.class, writer);
        }
        writer.endArray();
        writer.close();
    }
    private NewsItem readJsonStreamDetail(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        Gson gson = new Gson();
        NewsItem messages = gson.fromJson(reader, NewsItem.class);
        return messages;
    }

    private void writeJsonStream(OutputStream out, NewsItem messages) throws IOException {
        JsonWriter writer = new JsonWriter(new OutputStreamWriter(out, "UTF-8"));
        Gson gson = new Gson();
        gson.toJson(messages, NewsItem.class, writer);
        writer.close();
    }
}
