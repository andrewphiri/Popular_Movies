package com.example.popularmoviesretrofit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;


import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.popularmoviesretrofit.database.MoviesViewModel;
import com.example.popularmoviesretrofit.utils.AppExecutors;
import com.example.popularmoviesretrofit.utils.MoviesApiService;
import com.example.popularmoviesretrofit.utils.NetworkUtils;
import com.example.popularmoviesretrofit.utils.TrailersResponse;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailsActivity extends AppCompatActivity {

    public static final String FRAGMENT_DISPLAYED = "FRAGMENT_DISPLAYED";
    ImageView mImageView;
    TextView titleTextView;
    TextView overviewTextView;
    TextView releaseDateTextView;
    TextView popularityTextView;
    TextView userRatingTextView;
    ImageView markFav;

    public final String PREFS_FILES = "com.example.popularmoviesapp.PREFS";

    private String image;
    private String title;
    private String overview;
    private String releaseDate;
    private String popularity;
    private String userRating;
    private String id;
    boolean isMarkedAsFavorite;
    int marked;
    MoviesViewModel moviesViewModel;
    static SharedPreferences preferences;
    private Movies favMovie;
    List<Trailers> trailerKeys = new ArrayList<>();
    boolean isFragmentDisplayed = false;

    LinearLayout trailer1;
    LinearLayout trailer2;

    TextView trailer1TextView;
    TextView trailer2TextView;
    TextView borderTextView;

    FrameLayout frameLayout;

    private static Retrofit retrofit = null;

    /**
     * mark a movie as favorite or not
     * marked has value of 1
     * not marked has value of 0
     */
    public void mark() {

        if (marked == 0) {
            isMarkedAsFavorite = false;
            marked = 1;
            markFav.setImageResource(R.drawable.mark_as_favorite);
            // isMarkedAsFavorite = true;
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    favMovie = new Movies(title, id, overview, image, releaseDate, userRating, popularity, marked);
                    moviesViewModel.insert(favMovie);
                }
            });
        } else {
            isMarkedAsFavorite = true;
            marked = 0;
            markFav.setImageResource(R.drawable.baseline_favorite);
            //isMarkedAsFavorite = false;
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    favMovie = new Movies(title, id, overview, image, releaseDate, userRating, popularity, marked);
                    moviesViewModel.delete(favMovie);
                }
            });
        }
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
     * Load video keys for trailers for specific movie
     * add them to a list
     * check if this list is empty or not to show trailer views
     */
    public void loadVideoKeys() {
        buildAndGetApiData();

        MoviesApiService moviesApiService = retrofit.create(MoviesApiService.class);
        Call<TrailersResponse> call = moviesApiService.getTrailers(id, NetworkUtils.API_KEY);

        call.enqueue(new Callback<TrailersResponse>() {
            @Override
            public void onResponse(Call<TrailersResponse> call, Response<TrailersResponse> response) {
                trailerKeys = response.body().getTrailers();

                if (trailerKeys != null) {
                    if (trailerKeys.size() == 0) {
                        trailer1.setVisibility(View.VISIBLE);
                        trailer1TextView.setText(R.string.trailer_not_available);
                        trailer1.setEnabled(false);
                        trailer2.setVisibility(View.GONE);
                        borderTextView.setVisibility(View.GONE);

                    } else if (trailerKeys.size() == 1) {
                        trailer1.setVisibility(View.VISIBLE);
                        trailer1.setEnabled(true);
                        trailer2.setVisibility(View.GONE);
                        borderTextView.setVisibility(View.GONE);

                    } else {
                        trailer1.setVisibility(View.VISIBLE);
                        trailer2.setVisibility(View.VISIBLE);
                        trailer1.setEnabled(true);
                        trailer2.setEnabled(true);
                    }
                }
            }

            @Override
            public void onFailure(Call<TrailersResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.failure_response),
                        Toast.LENGTH_LONG).show();
            }
        });

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);


        //initialize views
        mImageView = findViewById(R.id.poster_iv);
        titleTextView = findViewById(R.id.tv_title);
        overviewTextView = findViewById(R.id.tv_overview);
        releaseDateTextView = findViewById(R.id.tv_date_released);
        popularityTextView = findViewById(R.id.tv_popularity);
        userRatingTextView = findViewById(R.id.tv_rating);
        markFav = findViewById(R.id.markFav);
        trailer1 = findViewById(R.id.trailer1);
        trailer2 = findViewById(R.id.trailer2);
        trailer1TextView = findViewById(R.id.textViewTrailer1);
        trailer2TextView = findViewById(R.id.textViewTrailer2);
        borderTextView = findViewById(R.id.borderline);

        frameLayout = findViewById(R.id.fragment_container);

        //frameLayout.setVisibility(View.GONE);

        moviesViewModel = new ViewModelProvider(this).get(MoviesViewModel.class);

        preferences = getSharedPreferences(PREFS_FILES, MODE_PRIVATE);

        if (savedInstanceState != null) {
            Picasso.get().load(savedInstanceState.getString(MainActivity.IMAGE)).into(mImageView);
            titleTextView.setText(savedInstanceState.getString(MainActivity.TITLE));
            overviewTextView.setText(savedInstanceState.getString(MainActivity.OVERVIEW));
            releaseDateTextView.setText(savedInstanceState.getString(MainActivity.RELEASE_DATE));
            popularityTextView.setText(savedInstanceState.getString(MainActivity.POPULARITY));
            userRatingTextView.setText(savedInstanceState.getString(MainActivity.USER_RATING));
            id = savedInstanceState.getString(MainActivity.ID);
            isFragmentDisplayed = savedInstanceState.getBoolean(FRAGMENT_DISPLAYED);
        }

        //check if fragment is showing
        if (isFragmentDisplayed) {
            displayFragment();
        } else {
            closeFragment();
        }

        //get intent values of selected movie from MainActivity
        Intent movieSelected = getIntent();

        image = movieSelected.getStringExtra(MainActivity.IMAGE);
        title = movieSelected.getStringExtra(MainActivity.TITLE);
        overview = movieSelected.getStringExtra(MainActivity.OVERVIEW);
        releaseDate = movieSelected.getStringExtra(MainActivity.RELEASE_DATE);
        popularity = movieSelected.getStringExtra(MainActivity.POPULARITY);
        userRating = movieSelected.getStringExtra(MainActivity.USER_RATING);
        id = movieSelected.getStringExtra(MainActivity.ID);
        marked = preferences.getInt(id, 0);

        favMovie = new Movies(title, id, overview, image, releaseDate, userRating, popularity, marked);


        //check if movie is selected as favourite
        if (marked == 1) {
            markFav.setImageResource(R.drawable.mark_as_favorite);
        } else {
            markFav.setImageResource(R.drawable.baseline_favorite);
        }

        setTitle(title);

        //populate views
        Picasso.get().load(image).into(mImageView, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {
                supportStartPostponedEnterTransition();
            }

            @Override
            public void onError(Exception e) {
                supportStartPostponedEnterTransition();
            }
        });

        titleTextView.setText(title);
        overviewTextView.setText(overview);
        releaseDateTextView.setText(releaseDate);
        popularityTextView.setText(popularity);
        userRatingTextView.setText(userRating);

        loadVideoKeys();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(MainActivity.IMAGE, image);
        outState.putString(MainActivity.TITLE, title);
        outState.putString(MainActivity.OVERVIEW, overview);
        outState.putString(MainActivity.RELEASE_DATE, releaseDate);
        outState.putString(MainActivity.POPULARITY, popularity);
        outState.putString(MainActivity.USER_RATING, userRating);
        outState.putBoolean(FRAGMENT_DISPLAYED, isFragmentDisplayed);
        outState.putString(MainActivity.ID, id);
    }


    public void markAsFavorite(View view) {
        mark();
    }

    /**
     * Get current values of movie and add them to shared prefs to save state
     */
    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor sharedPreferences = preferences.edit();
        sharedPreferences.putInt(id, marked);
        sharedPreferences.apply();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home) {
         onBackPressed();
         return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        closeFragment();
        super.onBackPressed();
    }

    public void trailerOne(View view){
        playTrailer(trailerKeys.get(0).getKey());
    }

    public void trailerTwo(View view){
        playTrailer(trailerKeys.get(1).getKey());
    }

    /**
     * Open Youtube app or browser to watch movie trailer
     * @param keys
     */
    private void playTrailer(String keys) {
        Uri appUri = Uri.parse(NetworkUtils.VND_YOUTUBE + keys);
        Uri webUri = Uri.parse(NetworkUtils.TRAILER_BASE_URL + keys);
        Intent appIntent = new Intent(Intent.ACTION_VIEW, appUri);
        Intent webIntent = new Intent(Intent.ACTION_VIEW, webUri);

        try {
            startActivity(appIntent);
        } catch (ActivityNotFoundException ex){
            startActivity(webIntent);
        }
    }

    /**
     * Read movie reviews by diplaying reviews fragment
     * @param view
     */
    public void readViews(View view) {
        displayFragment();
    }

    /**
     * Attach reviews fragment
     */
    public void displayFragment() {
        frameLayout.setVisibility(View.VISIBLE);
        ReviewsFragment fragment = ReviewsFragment.newInstance(id);

        FragmentManager fragmentManager = getSupportFragmentManager();

        if (fragmentManager != null) {
            fragmentManager.popBackStackImmediate();
        }
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().addToBackStack(null);

        fragmentTransaction.add(R.id.fragment_container, fragment)
                .commit();

        isFragmentDisplayed = true;
    }

    /**
     * remove fragment on back pressed
     */
    public void closeFragment() {
        FragmentManager manager = getSupportFragmentManager();

        ReviewsFragment reviewsFragment = (ReviewsFragment) manager
                .findFragmentById(R.id.fragment_container);

        if (reviewsFragment != null) {
            FragmentTransaction fragmentTransaction = manager.beginTransaction();
                    fragmentTransaction.remove(reviewsFragment).commit();
                    isFragmentDisplayed = false;
        }
       frameLayout.setVisibility(View.GONE);
    }
}