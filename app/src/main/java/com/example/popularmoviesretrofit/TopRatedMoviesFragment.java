package com.example.popularmoviesretrofit;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.popularmoviesretrofit.R;
import com.example.popularmoviesretrofit.adapters.MovieAdapterForPopularAndTopRated;
import com.example.popularmoviesretrofit.database.MoviesViewModel;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TopRatedMoviesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TopRatedMoviesFragment extends Fragment implements MovieAdapterForPopularAndTopRated.OnItemClickListener {

    public static RecyclerView recyclerView_top_rated;
    public static MovieAdapterForPopularAndTopRated adapter_top_rated;
    GridLayoutManager layoutManager_top_rated;
    public static TextView errorDisplay_top_rated;
    ProgressBar loadingIndicator_top_rated;

    MoviesViewModel mViewModel;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TopRatedMoviesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment TopRatedMoviesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TopRatedMoviesFragment newInstance() {
        TopRatedMoviesFragment fragment = new TopRatedMoviesFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_top_rated_movies, container, false);

        mViewModel = new ViewModelProvider(this).get(MoviesViewModel.class);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView_top_rated = view.findViewById(R.id.movie_top_rated_recycler_view);
        errorDisplay_top_rated = view.findViewById(R.id.tv_error_top_rated);
        loadingIndicator_top_rated = view.findViewById(R.id.progress_bar_top_rated);

        int spanCount = getResources().getInteger(R.integer.columns);

        String title = getResources().getString(R.string.top_rated_title);
        getActivity().setTitle(title);

        adapter_top_rated = new MovieAdapterForPopularAndTopRated(this);
        layoutManager_top_rated = new GridLayoutManager(getActivity(), spanCount);

        recyclerView_top_rated.setLayoutManager(layoutManager_top_rated);
        recyclerView_top_rated.setAdapter(adapter_top_rated);
        loadTopRatedMoviesData();
    }

    /**
     * Method for loading data for loading top rated movies
     * show error message if data is unavailable
     * show progress bar when data is loading
     */
    public void loadTopRatedMoviesData() {

        if (errorDisplay_top_rated.getVisibility() == View.VISIBLE) {
            errorDisplay_top_rated.setVisibility(View.INVISIBLE);
        }
        mViewModel.init();
        mViewModel.getTopRatedMoviesList().observe(getActivity(), new Observer<List<Movies>>() {
            @Override
            public void onChanged(List<Movies> movies) {
                adapter_top_rated.setMovies(movies);
                adapter_top_rated.notifyDataSetChanged();
            }
        });

        mViewModel.fetchingData().observe(getActivity(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    loadingIndicator_top_rated.setVisibility(View.VISIBLE);
                } else {
                    loadingIndicator_top_rated.setVisibility(View.INVISIBLE);
                }
            }
        });

        mViewModel.isTopRatedMovieDataAvailable().observe(getActivity(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    showMovieData();
                } else {
                    showErrorMessage();
                }
            }
        });
    }

    /**
     * Show error message when there is a network failure
     */
    public void showErrorMessage() {
        recyclerView_top_rated.setVisibility(View.INVISIBLE);
        errorDisplay_top_rated.setVisibility(View.VISIBLE);

    }

    /**
     * display data when available
     */
    public void showMovieData() {
        recyclerView_top_rated.setVisibility(View.VISIBLE);
        errorDisplay_top_rated.setVisibility(View.INVISIBLE);
    }


    /**
     *
     * @param mData
     * @param position
     * @param bundle
     * interface method from adapter to respond to clicks and launch details activity
     * with the necessary information
     * Bundle used to animate the transition
     */
    @Override
    public void onItemClick(List<Movies> mData, int position, Bundle bundle) {
        Intent intent = new Intent(getActivity(), DetailsActivity.class);
        intent.putExtra(MainActivity.IMAGE, mData.get(position).getImage());
        intent.putExtra(MainActivity.TITLE, mData.get(position).getTitle());
        intent.putExtra(MainActivity.ID, mData.get(position).getId());
        intent.putExtra(MainActivity.OVERVIEW, mData.get(position).getOverview());
        intent.putExtra(MainActivity.USER_RATING, mData.get(position).getVote_average() + "/10");
        intent.putExtra(MainActivity.RELEASE_DATE, mData.get(position).getRelease_date());
        intent.putExtra(MainActivity.POPULARITY, mData.get(position).getPopularity());
        intent.putExtra(MainActivity.MARKED_FAV, mData.get(position).getMarkedAsFavorite());

        startActivity(intent, bundle);
    }

}