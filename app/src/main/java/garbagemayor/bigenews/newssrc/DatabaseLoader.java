package garbagemayor.bigenews.newssrc;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

    public List<PageItem> history;
    public List<PageItem> favorite;
    public List<PageItem> history_list;
    public NewsItem item;
    private static boolean online;
    private final String fileName = "list.json";

    private int network_delay = 5000;


    public DatabaseLoader(Context context) {
        this.mContext = context;
        history = new ArrayList<>();
        favorite = new ArrayList<>();
    }

    public void updateHistory() {
        String[] filelist = mContext.fileList();
        history.clear();
        for (String file : filelist) {
            if (file.contains("history")) {
                history.add(new PageItem(queryNews(file.substring(7))));
            }
        }
    }
    public void updateFavorite() {
        String[] filelist = mContext.fileList();
        favorite.clear();
        for (String file : filelist) {
            if (file.contains("favorite")) {
                favorite.add(new PageItem(queryNews(file.substring(8))));
            }
        }
    }


    public static boolean isNetworkAvailable(final Context context) {
        final boolean[] flag = {false};
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                if (cm == null) {
                } else {
                    //如果仅仅是用来判断网络连接
                    //则可以使用 cm.getActiveNetworkInfo().isAvailable();
                    NetworkInfo[] info = cm.getAllNetworkInfo();
                    if (info != null) {
                        for (int i = 0; i < info.length; i++) {
                            if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                                flag[0] = true;
                                return;
                            }
                        }
                    }
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag[0];
    }

    public NewsItem queryNews(final String id) {
        if (isNetworkAvailable(mContext)) {
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
                writeJsonStream(mContext.openFileOutput("history" + item.getId(), Context.MODE_PRIVATE), item);
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        } else {
            try {
                item = readJsonStreamDetail(mContext.openFileInput("history" + id));
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        }
        return item;
    }

    public List<PageItem> queryPage(final int i, final int sizeOfPage) {
        if (isNetworkAvailable(mContext)) {
            Log.d(TAG, "online");
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
            Log.d(TAG, "offline");
            try {
                history_list = readJsonStream(mContext.openFileInput(fileName));
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        }
        return history_list;
    }

    public List<PageItem> queryPage(final int nowCategoryId, final int i, final int sizeOfPage) {
        if (isNetworkAvailable(mContext)) {
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
        if (isNetworkAvailable(mContext)) {
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
        if (isNetworkAvailable(mContext)) {
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

    public static void writeJsonStream(OutputStream out, List<PageItem> messages) throws IOException {
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

    public static void writeJsonStream(OutputStream out, NewsItem messages) throws IOException {
        JsonWriter writer = new JsonWriter(new OutputStreamWriter(out, "UTF-8"));
        Gson gson = new Gson();
        gson.toJson(messages, NewsItem.class, writer);
        writer.close();
    }
}
