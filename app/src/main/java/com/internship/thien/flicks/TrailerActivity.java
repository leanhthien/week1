package com.internship.thien.flicks;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.internship.thien.flicks.data.model.TrailerList;
import com.internship.thien.flicks.data.remote.APIService;
import com.internship.thien.flicks.data.remote.ApiUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrailerActivity extends AppCompatActivity {

    private final String API_KEY = "4c397c33e7b2ed29b87718ab19999748";
    /**
     * The Tag trailers.
     */
    final String TAG_TRAILERS = "trailers";
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private APIService mService;
    private MoviesAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trailer);

        id = getIntent().getStringExtra("MOVIE_ID");
        loadTrailers(id);
    }

    private void loadTrailers(String id) {
        mService = ApiUtils.getAPIService();
        Call<TrailerList> call = mService.getTrailers(id, TAG_TRAILERS, API_KEY);
        call.enqueue(new Callback<TrailerList>() {
            @Override
            public void onResponse(@NonNull Call<TrailerList> call, @NonNull Response<TrailerList> response) {

                if (response.isSuccessful()) {
                    Log.d("Status: ", "Links are loaded from API");

                } else {
                    int statusCode = response.code();
                    // handle request errors depending on status code
                    Log.d("Response: ", String.valueOf(statusCode));
                }
            }

            @Override
            public void onFailure(@Nullable Call<TrailerList> call, @Nullable Throwable t) {
                //showErrorMessage();
                Log.d("MainActivity", "error loading from API");

            }
        });
    }
}
