package garbagemayor.bigenews;

import java.util.Observable;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NewsAPI {

    @GET("latest?")
    Call<NewsList> latest(@Query("pageNo") int no, @Query("pageSize") int size);

    @GET("search?")
    Call<NewsList> search(
            @Query("keyword") String keyword,
            @Query("category") String category,
            @Query("pageNo") int no,
            @Query("pageSize") int size
    );

    @GET("latest?")
    Call<NewsList> category(@Query("category") int id, @Query("pageNo") int no, @Query("pageSize") int size);

    @GET("detail?")
    Call<NewsDetail> detail(@Query("newsId") String id);
}
