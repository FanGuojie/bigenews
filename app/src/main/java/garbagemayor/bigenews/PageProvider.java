package garbagemayor.bigenews;

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

public class PageProvider implements Callback<News> {
    String API_BASE_URL = "http://166.111.68.66:2042/news/action/query/";
    List<String> newsID = new ArrayList<String >();
    NewsAPI api;

    public void start() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        GsonBuilder gs = new GsonBuilder();
        gs.registerTypeAdapter(News.class, new JsonDeserializer<News>() {
            @Override
            public News deserialize(JsonElement arg0, Type arg1,
                                    JsonDeserializationContext arg2) throws JsonParseException {
                return new Gson().fromJson(arg0, News.class);
            }
        });
        Gson gson = gs.create();

        Retrofit.Builder builder = new Retrofit.Builder().baseUrl(API_BASE_URL).addConverterFactory(
                GsonConverterFactory.create(gson));

        Retrofit retrofit = builder.client(httpClient.build()).build();

        api = retrofit.create(NewsAPI.class);
//        Call<News> call = api.latest(1, 1);
        Call<News> call = api.category(2, 1, 1);

        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<News> call, Response<News> response) {
        List<News.ListBean> i =  response.body().getList();
        for (News.ListBean ii : i) {
            Call<NewsContent> call2 = api.detail(ii.getNews_ID());
            call2.enqueue(new Callback<NewsContent>() {
                @Override
                public void onResponse(Call<NewsContent> call2, Response<NewsContent> r) {
                    // The network call was a success and we got a response
                    // TODO: use the repository list and display it
                    System.out.print(r.body().getNews_Title());
                    System.out.print(r.body().getNews_Content());
                }

                @Override
                public void onFailure(Call<NewsContent> call2, Throwable t) {
                    // the network call was a failure
                    // TODO: handle error
                    t.printStackTrace();
                }
            });
        }
    }

    @Override
    public void onFailure(Call<News> call, Throwable t) {
        t.printStackTrace();
    }
}
