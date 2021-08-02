package com.example.popularmoviesretrofit;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.popularmoviesretrofit.R;
import com.example.popularmoviesretrofit.adapters.MovieAdapterForPopularAndTopRated;
import com.example.popularmoviesretrofit.database.MoviesViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MostPopularMoviesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MostPopularMoviesFragment extends Fragment implements MovieAdapterForPopularAndTopRated.OnItemClickListener {

    public static RecyclerView recyclerView_popular;
    public static MovieAdapterForPopularAndTopRated adapter_popular;
    GridLayoutManager layoutManager_popular;
    public static TextView errorDisplay_popular;
    ProgressBar loadingIndicator_popular;

    MoviesViewModel mViewModel;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MostPopularMoviesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
     * @return A new instance of fragment MostPopularMoviesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MostPopularMoviesFragment newInstance() {
        MostPopularMoviesFragment fragment = new MostPopularMoviesFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        //fragment.setArguments(args);
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
        View view = inflater.inflate(R.layout.fragment_most_popular_movies, container, false);

        mViewModel = new ViewModelProvider(this).get(MoviesViewModel.class);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView_popular = view.findViewById(R.id.movie_popular_recycler_view);
        errorDisplay_popular = view.findViewById(R.id.tv_error_popular);
        loadingIndicator_popular = view.findViewById(R.id.progress_bar_popular);

        int spanCount = getResources().getInteger(R.integer.columns);

        String title = getResources().getString(R.string.most_popular_title);
        getActivity().setTitle(title);

        adapter_popular = new MovieAdapterForPopularAndTopRated(this);
        layoutManager_popular = new GridLayoutManager(getActivity(), spanCount);

        recyclerView_popular.setLayoutManager(layoutManager_popular);
        recyclerView_popular.setAdapter(adapter_popular);
        recyclerView_popular.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull @NotNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.d("STATE_RECYCLERVIEW", String.valueOf(newState));
            }

            @Override
            public void onScrolled(@NonNull @NotNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.d("RECYCLERVIEW", String.valueOf(dy));
            }
        });
        loadMostPopularMoviesData();
    }

    /**
    * Method for loading data for loading most popular movies
     * show error message if data is unavailable
     * show progress bar when data is loading
     */
    public void loadMostPopularMoviesData() {

        if (errorDisplay_popular.getVisibility() == View.VISIBLE) {
            errorDisplay_popular.setVisibility(View.INVISIBLE);
        }
        mViewModel.init();
        mViewModel.getPopularMoviesList().observe(getActivity(), new Observer<List<Movies>>() {
            @Override
            public void onChanged(List<Movies> movies) {
                adapter_popular.setMovies(movies);
                adapter_popular.notifyDataSetChanged();

            }
        });

        mViewModel.fetchingData().observe(getActivity(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    loadingIndicator_popular.setVisibility(View.VISIBLE);
                } else {
                    loadingIndicator_popular.setVisibility(View.INVISIBLE);

                }
            }
        });

        mViewModel.isMostPopularMovieDataAvailable().observe(getActivity(), new Observer<Boolean>() {
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
        recyclerView_popular.setVisibility(View.INVISIBLE);
        errorDisplay_popular.setVisibility(View.VISIBLE);

    }

    /**
     * display data when available
     */
    public void showMovieData() {
        recyclerView_popular.setVisibility(View.VISIBLE);
        errorDisplay_popular.setVisibility(View.INVISIBLE);
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