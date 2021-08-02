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
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.popularmoviesretrofit.MainActivity;
import com.example.popularmoviesretrofit.Movies;
import com.example.popularmoviesretrofit.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieAdapter extends PagedListAdapter<Movies, MovieAdapter.MovieViewHolder> {

    List<Movies> mMoviesData;
    ItemClickListener clickListener;
    private Context context;

    public interface ItemClickListener{
        void onItemClick(Movies mData, Bundle bundle);
    }

    public MovieAdapter(ItemClickListener listener) {
        super(DIFF_CALLBACK);
        this.clickListener = listener;
    }


    private static DiffUtil.ItemCallback<Movies> DIFF_CALLBACK = new DiffUtil.ItemCallback<Movies>() {
        @Override
        public boolean areItemsTheSame(@NonNull Movies oldItem, @NonNull Movies newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Movies oldItem, @NonNull Movies newItem) {
            return oldItem.getId().equals(newItem.getId());
        }
    };

    @NonNull
    @Override
    public MovieAdapter.MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.movie_item_list, parent, false);

        MovieViewHolder viewHolder = new MovieViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MovieAdapter.MovieViewHolder holder, int position) {
        Movies popularMovies = getItem(position);

        if (popularMovies != null) {
            holder.bind(popularMovies);
        }

    }

    @Override
    public int getItemCount() {
       return super.getItemCount();
    }

    public void setMovies(List<Movies> data) {
        mMoviesData = data;
        notifyDataSetChanged();
    }


    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView movieImageView;
        ProgressBar loadingBar;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            movieImageView = itemView.findViewById(R.id.movie_image_view);
            //movieTitle = itemView.findViewById(R.id.movie_title);
            loadingBar = itemView.findViewById(R.id.loading_bar);
            itemView.setOnClickListener(this);
        }


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
         //   movieTitle.setText(movies.getTitle());
        }

        @Override
        public void onClick(View view) {
            Movies movies = getItem(getAdapterPosition());
            Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    (MainActivity) context, movieImageView, movieImageView.getTransitionName()).toBundle();
            clickListener.onItemClick(movies, bundle);
        }
    }
}
