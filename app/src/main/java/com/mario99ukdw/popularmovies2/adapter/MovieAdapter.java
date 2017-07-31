package com.mario99ukdw.popularmovies2.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mario99ukdw.popularmovies2.R;
import com.mario99ukdw.popularmovies2.data.Movie;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by mario99ukdw on 30.07.2017.
 */

public class MovieAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();

    Context mContext = null;
    ArrayList<Movie> mMovies = new ArrayList<>();

    public MovieAdapter(Context context, ArrayList<Movie> movies) {
        mContext = context;
        mMovies = movies;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_list_item, parent, false);
        viewHolder = new MovieItemHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        Movie movie = getItem(position);
        Picasso.with(mContext).load(movie.getPosterPath()).into(((MovieItemHolder) holder).mThumbnailImageView);
    }
    @Override
    public int getItemCount() {
        return mMovies.size();
    }

    public Movie getItem(int position) {
        return mMovies.get(position);
    }

    public static class MovieItemHolder extends RecyclerView.ViewHolder {
        ImageView mThumbnailImageView;

        public MovieItemHolder(View itemView) {
            super(itemView);

            mThumbnailImageView = (ImageView) itemView.findViewById(R.id.iv_thumbnail);
        }

    }
}
