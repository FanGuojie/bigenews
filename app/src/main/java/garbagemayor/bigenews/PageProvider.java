package garbagemayor.bigenews;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.io.IOException;
import java.lang.reflect.Type;
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


    String API_BASE_URL = "http://166.111.68.66:2042/news/action/query/";
    private static NewsList newsList = new NewsList();
    private static NewsDetail newsDetail = new NewsDetail();
    NewsAPI api;
    NewsList getNewsList() {
        return newsList;
    }
    NewsDetail getNewsDetail() {
        return newsDetail;
    }

    void loadNewsList(String keyword, int category, int no, int size, final MyCallBack callback) {
        Call<NewsList> call = api.search(keyword, category, no, size);
        asynLoad(call ,callback);
    }

    void loadNewsList(int category, int no, int size, final MyCallBack callback) {
        Call<NewsList> call = api.category(category, no, size);
        asynLoad(call, callback);
    }

    void loadNewsList(int no, int size, final MyCallBack callback) {
        Call<NewsList> call = api.latest(no, size);
        asynLoad(call, callback);
    }

    void loadNewsDetail(String id, final MyCallBack callback) {
        Call<NewsDetail> call = api.detail(id);
        call.enqueue(new Callback<NewsDetail>() {
            @Override
            public void onResponse(Call<NewsDetail> call, Response<NewsDetail> response) {
                newsDetail = response.body();
                callback.callbackCall();
            }

            @Override
            public void onFailure(Call<NewsDetail> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    PageProvider() {
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

    void asynLoad(Call call, final MyCallBack callback) {
        // asynchronous
        call.enqueue(new Callback<NewsList>() {
            @Override
            public void onResponse(Call<NewsList> call, Response<NewsList> response) {
                newsList = response.body();
                callback.callbackCall();
            }

            @Override
            public void onFailure(Call<NewsList> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
