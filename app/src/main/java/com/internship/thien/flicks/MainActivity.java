package com.internship.thien.flicks;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.internship.thien.flicks.data.model.MovieList;
import com.internship.thien.flicks.data.model.Result;
import com.internship.thien.flicks.data.remote.APIService;
import com.internship.thien.flicks.data.remote.ApiUtils;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * The type Main activity.
 */
public class MainActivity extends AppCompatActivity {

    /**
     * The Tag list movies.
     */
    final String TAG_LIST_MOVIES = "now_playing";
    /**
     * The Tag trailers.
     */
    final String TAG_TRAILERS = "trailer";
    private final String API_KEY = "4c397c33e7b2ed29b87718ab19999748";
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private APIService mService;
    private MoviesAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private Integer id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSwipeRefreshLayout = findViewById(R.id.swipe_refresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.pink, R.color.indigo, R.color.lime);


        callAPI();
    }

    /**
     * Call api boolean.
     *
     * @return the boolean
     */
    public boolean callAPI() {

        mService = ApiUtils.getAPIService();

        loadResults();
        setUpList();
        return true;
    }

    /**
     * Load results.
     */
    public void loadResults() {
        Call<MovieList> call = mService.getMovies(TAG_LIST_MOVIES, API_KEY);
        call.enqueue(new Callback<MovieList>() {
            @Override
            public void onResponse(@NonNull Call<MovieList> call, @NonNull Response<MovieList> response) {

                if (response.isSuccessful()) {

                    mAdapter.updateResults(response.body().getResults());
                    Log.d("Status: ", "Results loaded from API successfully");
                } else {
                    int statusCode = response.code();
                    // handle request errors depending on status code
                    Log.e("Response: ", String.valueOf(statusCode));
                }
            }

            @Override
            public void onFailure(@Nullable Call<MovieList> call, @Nullable Throwable t) {
                //showErrorMessage();
                Log.e("Status:", "Error while loading from API");
            }
        });
    }

    /**
     * Sets up list.
     */
    public void setUpList() {
        mRecyclerView = findViewById(R.id.rv_movie);

        //Action on every tap on a single Recycler View
        mAdapter = new MoviesAdapter(this, new ArrayList<Result>(0), new MoviesAdapter.MovieItemListener() {

            @Override
            public void onMovieClick(Integer id) {
                Intent intent = new Intent(getBaseContext(), TrailerActivity.class);
                intent.putExtra("MOVIE_ID", String.valueOf(id));
                startActivity(intent);
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
        //RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        //mRecyclerView.addItemDecoration(itemDecoration);

        //Action for swipe-to-refresh-layout feature
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (callAPI())
                    mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setUpList();

    }

}
