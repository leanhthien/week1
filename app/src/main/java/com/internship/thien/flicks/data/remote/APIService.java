package com.internship.thien.flicks.data.remote;

import com.internship.thien.flicks.data.model.MovieList;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIService {

    @GET("/3/movie/{tag}?")
    Call<MovieList> getMovies(@Path("tag") String tag,
                              @Query("api_key") String APIKey);

    @GET("/3/movie/{id}/{tag}?")
    Call<List<MovieList>> getTrailers(@Path("id") int id,
                                      @Path("tag") int tag,
                                      @Query("api_key") String APIKey);
}

