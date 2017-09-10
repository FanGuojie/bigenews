package garbagemayor.bigenews;

import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PageProvider {
    String API_BASE_URL = "http://166.111.68.66:2042/news/action/query/";
    NewsAPI api;
    private static List<NewsList.ListBean> newsList = new ArrayList<>();
    private static NewsDetail newsDetail = new NewsDetail();

    public List<NewsList.ListBean> getNewsList(int category, int count) {
        loadNewsList(1, count);
        return newsList;
    }

    public NewsDetail getNewsDetail(String id) {
        loadNewsDetail(id);
        return newsDetail;
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

    void loadNewsList(int no, int size) {
        Call<NewsList> call = api.latest(no, size);
        call.enqueue(new Callback<NewsList>() {
            @Override
            public void onResponse(Call<NewsList> call, Response<NewsList> response) {
                newsList = response.body().getList();
            }

            @Override
            public void onFailure(Call<NewsList> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }


    void loadNewsDetail(String id) {
        Call<NewsDetail> call = api.detail(id);
        // load details for certain news id
        call.enqueue(new Callback<NewsDetail>() {
            @Override
            public void onResponse(Call<NewsDetail> call, Response<NewsDetail> response) {
                // The network call was a success and we got a response
                newsDetail = response.body();
            }

            @Override
            public void onFailure(Call<NewsDetail> call, Throwable t) {
                // the network call was a failure
                t.printStackTrace();
            }
        });
    }
}
