package com.internship.thien.flicks;

import android.content.Context;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.internship.thien.flicks.data.model.Result;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;


/**
 * The type Movies adapter.
 */
public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder> {

    private List<Result> mResults;
    private Context mContext;
    private MovieItemListener mResultListener;
    int LENGTH = 150;

    /**
     * Instantiates a new Movies adapter.
     *
     * @param context      the context
     * @param movies       the movies
     * @param itemListener the item listener
     */
    public MoviesAdapter(Context context, List<Result> movies, MovieItemListener itemListener) {
        mResults = movies;
        mContext = context;
        mResultListener = itemListener;
    }

    /**
     * Instantiates a new Movies adapter.
     *
     * @param ctx the ctx
     */
    public MoviesAdapter(Context ctx) {
        this.mContext = ctx;
    }

    @Override
    @NonNull
    public MoviesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View postView;
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        int orientation = mContext.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (viewType == 0)
                postView = inflater.inflate(R.layout.item_movie, parent, false);
            else
                postView = inflater.inflate(R.layout.item_movie_popular, parent, false);

        }
        else
            postView = inflater.inflate(R.layout.item_movie, parent, false);

        return new ViewHolder(postView, this.mResultListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MoviesAdapter.ViewHolder holder, int position) {

        final String link_poster;

        Result result = mResults.get(position);
        String overview = result.getOverview();

        if (overview.length() > LENGTH)
        overview = overview.substring(0,LENGTH) + "...";

        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(mContext);
        circularProgressDrawable.setStrokeWidth(4f);
        circularProgressDrawable.setCenterRadius(40f);
        circularProgressDrawable.setColorSchemeColors(android.R.color.holo_orange_light);
        circularProgressDrawable.start();

        int orientation = mContext.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {

            if (result.getPopularity() < 50.0) {
                holder.title.setText(result.getTitle());
                holder.overview.setText(overview);
                link_poster = createPosterLink(result.getPosterPath());
            }
            else {
                holder.title.setText(result.getTitle());
                link_poster = createPosterLink(result.getBackdropPath());
            }

        }
        else if (orientation == Configuration.ORIENTATION_LANDSCAPE){
            holder.title.setText(result.getTitle());
            holder.overview.setText(overview);
            link_poster = createPosterLink(result.getBackdropPath());
        }
        else
            link_poster = createPosterLink(result.getPosterPath());


        Glide.with(mContext).load(link_poster)
                .apply(new RequestOptions()
                        .placeholder(circularProgressDrawable)
                        .fitCenter())
                .apply(bitmapTransform(new RoundedCornersTransformation(8, 0, RoundedCornersTransformation.CornerType.ALL)))
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.thumbnail);

    }

    @Override
    public int getItemCount() {
        return mResults.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mResults.get(position).getPopularity() < 50.0)
            return 0;
        else
            return 1;

    }
    /**
     * Sets the click listener
     * @param listener listener for adapter
     */
    public void setListener(MovieItemListener listener) {
        this.mResultListener = listener;
    }

    /**
     * Update results.
     *
     * @param results the results
     */
    public void updateResults(List<Result> results) {
        mResults = results;
        notifyDataSetChanged();
    }

    private Result getItem(int adapterPosition) {
        return mResults.get(adapterPosition);
    }

    /**
     * Create a full path that link to the poster's storage
     * @param path A string that is a part of poster's link
     * @return A string that contain the full path
     */
    private String createPosterLink(String path) {

        final String BASE_IMAGES_URL = "http://image.tmdb.org/t/p/";
        final String POSTER_SIZE = "w500";

        if (path == null) return null;
        return BASE_IMAGES_URL + POSTER_SIZE + path;
    }

    /**
     * The interface Movie item listener.
     */
    public interface MovieItemListener {
        /**
         * On movie click.
         *
         * @param result the movie
         */
        void onMovieClick(Result result);
    }

    /**
     * The type View holder.
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.title_movie)
        @Nullable TextView title;
        @BindView(R.id.overview_movie)
        @Nullable TextView overview;
        @BindView(R.id.thumbnail_movie)
        ImageView thumbnail;

        /**
         * The M result listener.
         */
        MovieItemListener mResultListener;

        private ViewHolder(View itemView, MovieItemListener MovieItemListener) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            this.mResultListener = MovieItemListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            this.mResultListener.onMovieClick(getItem(getAdapterPosition()));
            notifyDataSetChanged();
        }
    }

}