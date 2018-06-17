package com.internship.thien.flicks;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.internship.thien.flicks.data.model.Result;
import com.internship.thien.flicks.data.model.TrailerList;
import com.internship.thien.flicks.data.model.Youtube;
import com.internship.thien.flicks.data.remote.APIService;
import com.internship.thien.flicks.data.remote.ApiUtils;

import java.util.List;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class TrailerActivity extends AppCompatActivity {

    final String TAG_TRAILERS = "trailers";
    final String THEMOVIEDB_API_KEY = "4c397c33e7b2ed29b87718ab19999748";
    final String GOOGLE_API_KEY = "AIzaSyDlp0O5c39zqBoSme4jZleCLgW_PSokG_I";
    APIService mService;
    Bundle data;
    Result result;
    private List<Youtube> mTrailerList;

    @BindView(R.id.thumbnail_movie)
    ImageView thumbnail;
    @BindView (R.id.title_movie)
    TextView title;
    @BindView(R.id.overview_movie)
    TextView overview;
    @BindView(R.id.rating_bar)
    RatingBar rate;
    @BindColor(R.color.white)
    int white;

    //ImageView thumbnail;
    //TextView title;
    //TextView overview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trailer);


        title = findViewById(R.id.title_movie);
        overview = findViewById(R.id.overview_movie);
        thumbnail = findViewById(R.id.thumbnail_movie);
        ButterKnife.bind(this);

        data = getIntent().getExtras();
        result =  data.getParcelable("MOVIE");

        loadTrailers(String.valueOf(result.getId()));
        setLayout(result);

    }

    private void loadTrailers(String id) {

        mService = ApiUtils.getAPIService();
        Call<TrailerList> call = mService.getTrailers(id, TAG_TRAILERS, THEMOVIEDB_API_KEY);
        call.enqueue(new Callback<TrailerList>() {
            @Override
            public void onResponse(@NonNull Call<TrailerList> call, @NonNull Response<TrailerList> response) {

                if (response.isSuccessful()) {
                    mTrailerList = response.body().getYoutube();
                    prepareYoutube(mTrailerList);
                    Log.d("List: ", "Links are loaded from API");

                } else {
                    int statusCode = response.code();
                    // handle request errors depending on status code
                    Log.d("Response: ", String.valueOf(statusCode));
                }
            }

            @Override
            public void onFailure(@Nullable Call<TrailerList> call, @Nullable Throwable t) {
                Toast.makeText(TrailerActivity.this, "TheMovie API does not response!", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void prepareYoutube(final List<Youtube> listYoutube) {

        YouTubePlayerFragment youtubeFragment = (YouTubePlayerFragment)
                getFragmentManager().findFragmentById(R.id.youtubeFragment);
        youtubeFragment.initialize(GOOGLE_API_KEY,
                new YouTubePlayer.OnInitializedListener() {
                    @Override
                    public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                        YouTubePlayer youTubePlayer, boolean b) {
                        // do any work here to cue video, play video, etc.
                        Log.d("Link",findTrailer(listYoutube));
                        if (result.getPopularity() > 50)
                            youTubePlayer.loadVideo(findTrailer(listYoutube));
                        else
                            youTubePlayer.cueVideo(findTrailer(listYoutube));
                    }
                    @Override
                    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                        YouTubeInitializationResult youTubeInitializationResult) {
                        Toast.makeText(TrailerActivity.this, "Youtube Failed!", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    public String findTrailer(List<Youtube> listYoutube) {
        String link;
        Integer pos = 0;
        if (listYoutube != null) {
            for (int i=0; i<listYoutube.size(); i++) {
                link = listYoutube.get(i).getName();
                if (link.contains("Official") && link.contains("Final") && link.contains("Trailer"))
                    return listYoutube.get(i).getSource();
                else if (link.contains("Official") && link.contains("Trailer"))
                    pos = i;
                else if (link.contains("Trailer"))
                    pos = i;

            }
            return listYoutube.get(pos).getSource();
        }

        return "5xVh-7ywKpE";
    }

    private void setLayout(Result mResult) {

        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(this);
        circularProgressDrawable.setStrokeWidth(4f);
        circularProgressDrawable.setCenterRadius(40f);
        circularProgressDrawable.setColorSchemeColors(white);
        circularProgressDrawable.start();



        title.setText(mResult.getTitle());
        overview.setText(mResult.getOverview());
        rate.setRating(result.getVoteAverage());

        Glide.with(this).load(createPosterLink(result.getPosterPath()))
                .apply(new RequestOptions()
                        .placeholder(circularProgressDrawable)
                        .fitCenter())
                .apply(bitmapTransform(new RoundedCornersTransformation(8, 0, RoundedCornersTransformation.CornerType.ALL)))
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(thumbnail);

    }

    private String createPosterLink(String path) {

        final String BASE_IMAGES_URL = "http://image.tmdb.org/t/p/";
        final String POSTER_SIZE = "w500";

        if (path == null) return null;
        return BASE_IMAGES_URL + POSTER_SIZE + path;
    }

}
