package newapp.com.newsapp.M;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetroInterface {

    @GET("/v2/top-headlines")
    Call<NewsPost> getNewsList(@Query("country") String country, @Query("category") String category, @Query("apiKey") String apiKey);
}
