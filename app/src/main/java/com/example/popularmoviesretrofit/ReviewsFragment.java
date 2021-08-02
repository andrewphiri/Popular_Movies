package com.example.popularmoviesretrofit;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.popularmoviesretrofit.adapters.ReviewsAdapter;
import com.example.popularmoviesretrofit.utils.MoviesApiService;
import com.example.popularmoviesretrofit.utils.NetworkUtils;
import com.example.popularmoviesretrofit.utils.ReviewsResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReviewsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReviewsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String MOVIE_ID = "param1";
    //private static final String MOVIE_ID = "com.example.popularmoviesapp.MOVIE_ID";

    ReviewsAdapter adapter;
    RecyclerView reviewsRecyclerView;
    LinearLayoutManager linearLayout;

    // TODO: Rename and change types of parameters
    private String ID;
    private String reviews = "reviews";
    List<Reviews> movieReviews = new ArrayList<>();
    TextView textView;
    private static Retrofit retrofit = null;

    public ReviewsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment ReviewsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReviewsFragment newInstance(String param1) {
        ReviewsFragment fragment = new ReviewsFragment();
        Bundle args = new Bundle();
        args.putString(MOVIE_ID, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ID = getArguments().getString(MOVIE_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reviews, container, false);

        int configuration = getActivity().getResources().getConfiguration().orientation;

        if (configuration == Configuration.ORIENTATION_LANDSCAPE) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        reviewsRecyclerView = view.findViewById(R.id.reviewsRecyclerView);
        textView = view.findViewById(R.id.textViewRevs);

        linearLayout = new LinearLayoutManager(getActivity());
        adapter = new ReviewsAdapter(getActivity());

        reviewsRecyclerView.setLayoutManager(linearLayout);
        reviewsRecyclerView.setAdapter(adapter);

        loadReviews();
    }

    /**
     * Build URL using Retrofit Builder
     * And add a converter to process JSON data received
     */
    public void buildAndGetApiData() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(NetworkUtils.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
    }

    /**
     * load movie reviews by getting movie ID from clicked listener
     */
    public void loadReviews() {
        buildAndGetApiData();
        MoviesApiService moviesApiService = retrofit.create(MoviesApiService.class);
        Call<ReviewsResponse> call = moviesApiService.getReviews(ID, NetworkUtils.API_KEY);

        call.enqueue(new Callback<ReviewsResponse>() {
            @Override
            public void onResponse(Call<ReviewsResponse> call, Response<ReviewsResponse> response) {
                movieReviews = response.body().getReviews();
                if (movieReviews != null ) {
                    adapter.setReviews(movieReviews);

                } else {
                    textView.setVisibility(View.VISIBLE);
                    textView.setText(getString(R.string.no_reviews_message));
                    reviewsRecyclerView.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onFailure(Call<ReviewsResponse> call, Throwable t) {
                Toast.makeText(getActivity(), getActivity().getString(R.string.failure_response),
                        Toast.LENGTH_LONG).show();
            }
        });

    }
}