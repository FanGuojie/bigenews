package garbagemayor.bigenews;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NewsAPI {

    @GET("latest?")
    Call<News> latest(@Query("pageNo") int no, @Query("pageSize") int size);

    @GET("search?")
    Call<News> search(
            @Query("keyword") String keyword,
            @Query("category") String category,
            @Query("pageNo") int no,
            @Query("pageSize") int size
    );

    @GET("latest?")
    Call<News> category(@Query("category") int id, @Query("pageNo") int no, @Query("pageSize") int size);

    @GET("detail?")
    Call<NewsContent> detail(@Query("newsId") String id);
}
