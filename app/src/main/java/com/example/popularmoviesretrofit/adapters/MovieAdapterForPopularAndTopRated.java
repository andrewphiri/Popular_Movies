package com.example.popularmoviesretrofit.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.popularmoviesretrofit.MainActivity;
import com.example.popularmoviesretrofit.Movies;
import com.example.popularmoviesretrofit.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieAdapterForPopularAndTopRated extends RecyclerView.Adapter<MovieAdapterForPopularAndTopRated.MovieViewHolder> {

    List<Movies> mMoviesData;
    OnItemClickListener clickListener;
    private Context context;

    public interface OnItemClickListener{
        void onItemClick(List<Movies> mData, int position, Bundle bundle);
    }

    public MovieAdapterForPopularAndTopRated(OnItemClickListener listener) {
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public MovieAdapterForPopularAndTopRated.MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.movie_item_list, parent, false);

        MovieViewHolder viewHolder = new MovieViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MovieAdapterForPopularAndTopRated.MovieViewHolder holder, int position) {
        Movies popularMovies = mMoviesData.get(position);
        holder.bind(popularMovies);
    }

    @Override
    public int getItemCount() {
        if (mMoviesData == null) {
            return 0;
        } else {
            return mMoviesData.size();
        }
    }

    public void setMovies(List<Movies> data) {
        mMoviesData = data;
        notifyDataSetChanged();
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView movieImageView;
        ProgressBar loadingBar;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            movieImageView = itemView.findViewById(R.id.movie_image_view);
            //movieTitle = itemView.findViewById(R.id.movie_title);
            loadingBar = itemView.findViewById(R.id.loading_bar);
            itemView.setOnClickListener(this);
        }

        /**
         * Bind data to the recyclerView
         * progress bar while loading image
         * error image in the event of failure
         * @param movies
         */
        public void bind(Movies movies) {
            loadingBar.setVisibility(View.VISIBLE);
            Picasso.get()
                    .load(movies.getImage())
                    .into(movieImageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            movieImageView.setTransitionName(context.getResources().
                                    getString(R.string.transition_image));

                            loadingBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Exception e) {
                            movieImageView.setBackgroundResource(R.drawable.no_image_available);
                            loadingBar.setVisibility(View.INVISIBLE);
                        }
                    });
        }

        /**
         * Link on click method to custom interface click method
         * so that click listener can be implemented elsewhere
         * @param view
         */
        @Override
        public void onClick(View view) {
            int positionClicked = getAdapterPosition();
            List<Movies> movies = mMoviesData;
            Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    (MainActivity) context, movieImageView, movieImageView.getTransitionName()).toBundle();
            clickListener.onItemClick(movies, positionClicked, bundle);
        }
    }
}
