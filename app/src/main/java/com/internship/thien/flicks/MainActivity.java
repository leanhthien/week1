package com.internship.thien.flicks;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.internship.thien.flicks.data.model.MovieList;
import com.internship.thien.flicks.data.model.Result;
import com.internship.thien.flicks.data.remote.APIService;
import com.internship.thien.flicks.data.remote.ApiUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * The type Main activity.
 */
public class MainActivity extends AppCompatActivity {

    final String TAG_LIST_MOVIES = "now_playing";
    final String THEMOVIEDB_API_KEY = "4c397c33e7b2ed29b87718ab19999748";
    APIService mService;
    private MoviesAdapter mAdapter;
    private List<Result> mMovieList;

    @BindView(R.id.rv_movie)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindColor(android.R.color.holo_blue_bright)
    int blue_bright;
    @BindColor(android.R.color.holo_green_light)
    int green_light;
    @BindColor(android.R.color.holo_orange_light)
    int orange_light;
    @BindColor(android.R.color.holo_red_light)
    int red_light;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mSwipeRefreshLayout.setColorSchemeColors(red_light, orange_light, green_light, blue_bright);

        callAPI();
    }

    /**
     * Call api boolean.
     *
     * @return the boolean
     */
    public boolean callAPI() {

        loadResults();
        setUpList();
        return true;
    }

    /**
     * Load results.
     */
    public void loadResults() {

        mService = ApiUtils.getAPIService();
        Call<MovieList> call = mService.getMovies(TAG_LIST_MOVIES, THEMOVIEDB_API_KEY);
        call.enqueue(new Callback<MovieList>() {
            @Override
            public void onResponse(@NonNull Call<MovieList> call, @NonNull Response<MovieList> response) {

                if (response.isSuccessful()) {
                    Log.d("Status: ", "Results loaded from API successfully");
                    mMovieList = response.body().getResults();
                    mAdapter.updateResults(mMovieList);

                } else {
                    int statusCode = response.code();
                    // handle request errors depending on status code
                    Log.e("Response: ", String.valueOf(statusCode));
                }
            }

            @Override
            public void onFailure(@Nullable Call<MovieList> call, @Nullable Throwable t) {
                Toast.makeText(MainActivity.this, "TheMovie API does not response!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Sets up list.
     */
    public void setUpList() {

        //Action on every tap on a single Recycler View
        mAdapter = new MoviesAdapter(this, new ArrayList<Result>(0), new MoviesAdapter.MovieItemListener() {

            @Override
            public void onMovieClick(Result result) {
                Intent intent = new Intent(getBaseContext(), TrailerActivity.class);
                intent.putExtra("MOVIE", result);
                startActivity(intent);
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);

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

        if (mMovieList == null) {
            callAPI();
        }
        else {
            Log.d("Position","Screen has been rotate");
            callAPI();
        }

    }

}
