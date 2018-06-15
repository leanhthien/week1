package com.internship.thien.flicks;

import android.content.Context;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
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

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;


/**
 * The type Movies adapter.
 */
public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder> {

    private List<Result> mResults;
    private Context mContext;
    private MovieItemListener mResultListener;

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

        int orientation = mContext.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (result.getVoteAverage() > 5)
                link_poster = createPosterLink(result.getBackdropPath());
            else
                link_poster = createPosterLink(result.getPosterPath());

        }
        else
            link_poster = createPosterLink(result.getBackdropPath());

        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(mContext);
        circularProgressDrawable.setStrokeWidth(4f);
        circularProgressDrawable.setCenterRadius(40f);
        circularProgressDrawable.setColorSchemeColors(R.color.white);
        circularProgressDrawable.start();

        holder.title.setText(result.getTitle());
        holder.overview.setText(result.getOverview());

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
        if (mResults.get(position).getVoteAverage() <= 5)
            return 0;
        else
            return 1;

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
        final String POSTER_SIZE = "w185";

        if (path == null) return null;
        return BASE_IMAGES_URL + POSTER_SIZE + path;
    }

    /**
     * Clear.
     */
    // Clean all elements of the recycler
    public void clear() {
        mResults.clear();
        notifyDataSetChanged();
    }

    /**
     * Add all.
     *
     * @param list the list
     */
    // Add a list of items -- change to type used
    public void addAll(List<Result> list) {
        mResults.addAll(list);
        notifyDataSetChanged();
    }



    /**
     * The interface Movie item listener.
     */
    public interface MovieItemListener {
        /**
         * On movie click.
         *
         * @param id the id
         */
        void onMovieClick(Integer id);
    }

    /**
     * The type View holder.
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView title;
        private TextView overview;
        private ImageView thumbnail;

        /**
         * The M result listener.
         */
        MovieItemListener mResultListener;

        private ViewHolder(View itemView, MovieItemListener MovieItemListener) {
            super(itemView);
            title = itemView.findViewById(R.id.title_movie);
            overview = itemView.findViewById(R.id.overview_movie);
            thumbnail = itemView.findViewById(R.id.thumbnail_movie);

            this.mResultListener = MovieItemListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Result result = getItem(getAdapterPosition());
            this.mResultListener.onMovieClick(result.getId());

            notifyDataSetChanged();
        }
    }

}