package com.internship.thien.flicks;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.internship.thien.flicks.data.model.Result;

import java.util.List;


public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder> {
    private List<Result> mResults;
    private Context mContext;
    private PostItemListener mResultListener;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView titleTv;
        PostItemListener mResultListener;

        public ViewHolder(View itemView, PostItemListener postItemListener) {
            super(itemView);
            titleTv = (TextView) itemView.findViewById(android.R.id.text1);

            this.mResultListener = postItemListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Result result = getItem(getAdapterPosition());
            this.mResultListener.onPostClick(result.getId());

            notifyDataSetChanged();
        }
    }

    public MoviesAdapter(Context context, List<Result> posts, PostItemListener itemListener) {
        mResults = posts;
        mContext = context;
        mResultListener = itemListener;
    }

    @Override
    public MoviesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View postView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);

        ViewHolder viewHolder = new ViewHolder(postView, this.mResultListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MoviesAdapter.ViewHolder holder, int position) {

        Result result = mResults.get(position);
        TextView textView = holder.titleTv;
        textView.setText(result.getTitle());
    }

    @Override
    public int getItemCount() {
        return mResults.size();
    }

    public void updateResults(List<Result> results) {
        mResults = results;
        notifyDataSetChanged();
    }

    private Result getItem(int adapterPosition) {
        return mResults.get(adapterPosition);
    }

    public interface PostItemListener {
        void onPostClick(long id);
    }

}