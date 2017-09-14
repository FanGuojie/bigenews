package garbagemayor.bigenews.pageprovider;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.iflytek.sunflower.task.e;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.net.ConnectException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.ContentValues.TAG;

public class PageProvider {

    public interface MyCallBack {
        void callbackCall();
    }

    String API_BASE_URL = "http://166.111.68.66:2042/news/action/query/";
    private transient Context mContext;
    private static NewsList newsList = new NewsList();
    private static NewsDetail newsDetail = new NewsDetail();
    private NewsAPI api;
    private static boolean online;

    public NewsList getNewsList() {
        return newsList;
    }

    public NewsDetail getNewsDetail() {
        return newsDetail;
    }

    public void loadNewsList(String keyword, int category, int no, int size, final MyCallBack callback) {
        Call<NewsList> call = api.search(keyword, category, no, size);
        asynLoad(call, callback);
    }

    public void loadNewsList(int category, int no, int size, final MyCallBack callback) {
        Call<NewsList> call = api.category(category, no, size);
        asynLoad(call, callback);
    }

    public void loadNewsList(int no, int size, final MyCallBack callback) {
        Call<NewsList> call = api.latest(no, size);
        asynLoad(call, callback);
    }

    public void loadNewsDetail(String id, final MyCallBack callback) {
        try {
            newsDetail = readJsonStreamDetail(mContext.openFileInput(id + ".json"));
            callback.callbackCall();
        } catch (FileNotFoundException e) {
            Call<NewsDetail> call = api.detail(id);
            call.enqueue(new Callback<NewsDetail>() {
                @Override
                public void onResponse(Call<NewsDetail> call, Response<NewsDetail> response) {
                    newsDetail = response.body();
                    try {
//                        System.out.print(newsList.getList().get(0).getNews_ID());
                        writeJsonStream(mContext.openFileOutput(newsDetail.getNews_ID() + ".json", Context.MODE_PRIVATE), newsDetail);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    callback.callbackCall();
                }

                @Override
                public void onFailure(Call<NewsDetail> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void asynLoad(final Call call, final MyCallBack callback) {
        final String fileName = "list.json";
        PageProvider.online = false;
        Log.d(TAG, "asynLoad: load");

        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    if (Inet4Address.getByName("166.111.68.66").isReachable(200)) {
                        PageProvider.online = true;
                        Log.d(TAG, "run: online");;
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

        if (!PageProvider.online) {
            Log.d(TAG, "asynLoad: local");
//            System.out.print("from file");
//            System.out.print(newsList.getList().get(0).getNews_ID());
            try {
                newsList.setList(readJsonStream(mContext.openFileInput(fileName)));
                callback.callbackCall();
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        } else {
            Log.d(TAG, "asynLoad: network");
//                System.out.print("from network");
            call.enqueue(new Callback<NewsList>() {
                @Override
                public void onResponse(Call<NewsList> call, Response<NewsList> response) {
                    newsList = response.body();
                    try {
//                        System.out.print(newsList.getList().get(0).getNews_ID());
                        writeJsonStream(mContext.openFileOutput(fileName, Context.MODE_PRIVATE), newsList.getList());
                        callback.callbackCall();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<NewsList> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }
    }

    public PageProvider(Context context) {
        mContext = context;
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        GsonBuilder gs = new GsonBuilder();
        gs.registerTypeAdapter(NewsList.class, new JsonDeserializer<NewsList>() {
            @Override
            public NewsList deserialize(JsonElement arg0, Type arg1,
                                        JsonDeserializationContext arg2) throws JsonParseException {
                return new Gson().fromJson(arg0, NewsList.class);
            }
        });
        Gson gson = gs.create();

        Retrofit.Builder builder = new Retrofit.Builder().baseUrl(API_BASE_URL).addConverterFactory(
                GsonConverterFactory.create(gson));

        Retrofit retrofit = builder.client(httpClient.build()).build();

        api = retrofit.create(NewsAPI.class);
    }

    private List<NewsList.ListBean> readJsonStream(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        Gson gson = new Gson();
        List<NewsList.ListBean> messages = new ArrayList<NewsList.ListBean>();
        reader.beginArray();
        while (reader.hasNext()) {
            NewsList.ListBean message = gson.fromJson(reader, NewsList.ListBean.class);
            messages.add(message);
        }
        reader.endArray();
        reader.close();
        return messages;
    }

    private void writeJsonStream(OutputStream out, List<NewsList.ListBean> messages) throws IOException {
        JsonWriter writer = new JsonWriter(new OutputStreamWriter(out, "UTF-8"));
        Gson gson = new Gson();
        writer.setIndent("  ");
        writer.beginArray();
        for (NewsList.ListBean message : messages) {
            gson.toJson(message, NewsList.ListBean.class, writer);
        }
        writer.endArray();
        writer.close();
    }

    private NewsDetail readJsonStreamDetail(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        Gson gson = new Gson();
        NewsDetail messages = gson.fromJson(reader, NewsDetail.class);
        return messages;
    }

    private void writeJsonStream(OutputStream out, NewsDetail messages) throws IOException {
        JsonWriter writer = new JsonWriter(new OutputStreamWriter(out, "UTF-8"));
        Gson gson = new Gson();
        gson.toJson(messages, NewsDetail.class, writer);
        writer.close();
    }

}
