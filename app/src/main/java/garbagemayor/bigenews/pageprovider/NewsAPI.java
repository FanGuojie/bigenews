package garbagemayor.bigenews.pageprovider;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NewsAPI{

    @GET("latest?")
    Call<NewsList> latest(@Query("pageNo") int no, @Query("pageSize") int size);

    @GET("search?")
    Call<NewsList> search(
            @Query("keyword") String keyword,
            @Query("category") int category,
            @Query("pageNo") int no,
            @Query("pageSize") int size
    );

    @GET("latest?")
    Call<NewsList> category(@Query("category") int id, @Query("pageNo") int no, @Query("pageSize") int size);

    @GET("detail?")
    Call<NewsDetail> detail(@Query("newsId") String id);

    int RECOMMEND = -2;
    int FAVORITE = -1;
    int LATEST = 0;
    int TECHNOLOGY = 1;
    int EDUCATION = 2;
    int MILITARY = 3;
    int CHINA = 4;
    int SOCIAL = 5;
    int CULTURE = 6;
    int AUTOMOBILE = 7;
    int INTERNATIONAL = 8;
    int SPORTS = 9;
    int FINANCIAL = 10;
    int HEALTH = 11;
    int ENTERTAINMENT = 12;

}
