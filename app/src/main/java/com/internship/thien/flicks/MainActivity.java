package com.internship.thien.flicks;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.internship.thien.flicks.data.model.MovieList;
import com.internship.thien.flicks.data.model.Result;
import com.internship.thien.flicks.data.remote.APIService;
import com.internship.thien.flicks.data.remote.ApiUtils;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {

    private final String API_KEY = "4c397c33e7b2ed29b87718ab19999748";
    private final String TAG_LIST_MOVIES = "now_playing";
    private final String TAG_TRAILERS = "trailer";
    private APIService mService;
    private MoviesAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private Integer id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        callApi();
    }

    public void callApi() {
        mService = ApiUtils.getAPIService();
        mRecyclerView = findViewById(R.id.rv_movie);
        mAdapter = new MoviesAdapter(this, new ArrayList<Result>(0), new MoviesAdapter.PostItemListener() {

            @Override
            public void onPostClick(long id) {
                Toast.makeText(MainActivity.this, "Post id is" + id, Toast.LENGTH_SHORT).show();
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        mRecyclerView.addItemDecoration(itemDecoration);

        loadResults();
        loadTrailers(id);
    }


    public void loadResults() {
        Call<MovieList> call = mService.getMovies(TAG_LIST_MOVIES, API_KEY);
        call.enqueue(new Callback<MovieList>() {
            @Override
            public void onResponse(Call<MovieList> call, Response<MovieList> response) {

                if(response.isSuccessful()) {
                    mAdapter.updateResults(response.body().getResults());
                    Log.d("MainActivity", "results loaded from API");

                }else {
                    int statusCode  = response.code();
                    // handle request errors depending on status code
                    Log.d("Response:", String.valueOf(statusCode));
                }
            }

            @Override
            public void onFailure(Call<MovieList> call, Throwable t) {
                //showErrorMessage();
                Log.d("MainActivity", "error loading from API");

            }
        });
    }

    private void loadTrailers(Integer id) {

    }

}
