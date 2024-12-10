package com.example.snakify;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GoogleSearchAPI {
    @GET("customsearch/v1")
    Call<SearchResponse> searchSnakes(
            @Query("key") String apiKey,
            @Query("cx") String cx,
            @Query("q") String query
    );
}
