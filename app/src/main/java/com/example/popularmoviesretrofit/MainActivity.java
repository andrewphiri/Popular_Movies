package com.example.popularmoviesretrofit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavAction;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.popularmoviesretrofit.database.MoviesViewModel;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String IMAGE = "com.example.popularmoviesapp.IMAGE";
    public static final String TITLE = "com.example.popularmoviesapp.TITLE";
    public static final String ID = "com.example.popularmoviesapp.Id";
    public static final String RELEASE_DATE = "com.example.popularmoviesapp.RELEASE_DATE";
    public static final String POPULARITY = "com.example.popularmoviesapp.POPULARITY";
    public static final String USER_RATING = "com.example.popularmoviesapp.USER_RATING";
    public static final String OVERVIEW = "com.example.popularmoviesapp.OVERVIEW";
    public static final String MOVIE_LIST_STATE = "com.example.popularmoviesapp.MOVIE_LIST_STATE";
    public static final String FRAGMENT_LIST_STATE = "com.example.popularmoviesapp.FRAGMENT_LIST_STATE";
    public static String MARKED_FAV;

    public static final String MOST_POPULAR = "popular";
    public static final String TOP_RATED = "top_rated";
    private String sharedPrefFile = "com.example.popularmoviesapp";

    public static List<Movies> movies;

    // 1 = popular movies
    // 2 = top rated movies
    // 3 = favourite movies
    static int listCurrentlyShowing = 1;
    SharedPreferences sharedPreferences;

    MoviesViewModel mViewModel;

    NavController navController;
    NavHostFragment navHostFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();

        NavigationUI.setupActionBarWithNavController(this, navController);


        if (savedInstanceState != null) {
            listCurrentlyShowing = savedInstanceState.getInt(MOVIE_LIST_STATE, 1);
        }

        mViewModel = new ViewModelProvider(this).get(MoviesViewModel.class);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int menuItemSelected = item.getItemId();

         switch (menuItemSelected) {
             //when refresh is pressed, refresh the right movie list currently showing
             case R.id.action_refresh:
                 if(listCurrentlyShowing == 1) {
                     @NonNull NavDirections directions =
                             NavGraphDirections.actionGlobalMostPopularMoviesFragment();
                     navController.navigate(directions);
                 } else if (listCurrentlyShowing == 2){
                     NavDirections navDirections =
                             NavGraphDirections.actionGlobalTopRatedMoviesFragment();
                     navController.navigate(navDirections);
                 } else if (listCurrentlyShowing == 3){
                     NavDirections navDirections1 =
                             NavGraphDirections.actionGlobalFavoritesMoviesFragment();
                     navController.navigate(navDirections1);
                 }
                 break;
            case R.id.mostPopularMoviesFragment:
                 @NonNull NavDirections directions =
                         NavGraphDirections.actionGlobalMostPopularMoviesFragment();
                 navController.navigate(directions);
                 listCurrentlyShowing = 1;
                 break;

            case R.id.topRatedMoviesFragment:
                 NavDirections navDirections =
                         NavGraphDirections.actionGlobalTopRatedMoviesFragment();
                 navController.navigate(navDirections);
                 listCurrentlyShowing = 2;
                 break;

            case R.id.favoritesMoviesFragment:
                 NavDirections navDirections1 =
                         NavGraphDirections.actionGlobalFavoritesMoviesFragment();
                 navController.navigate(navDirections1);
                 listCurrentlyShowing = 3;
                 break;
             default:

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(MOVIE_LIST_STATE, listCurrentlyShowing);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//            closeFragment();
        FragmentManager manager = navHostFragment.getChildFragmentManager();
        Fragment fragment = manager.getPrimaryNavigationFragment();
        int fragmentId = navController.getCurrentDestination().getId();

        //if Most popular fragment is attached, clear the back stack
        if (fragmentId == R.id.mostPopularMoviesFragment) {
            listCurrentlyShowing = 1;
            Log.i("CURRENT FRAGMENT", String.valueOf(fragmentId));
            manager.popBackStackImmediate();
        } else if (fragmentId == R.id.topRatedMoviesFragment) {
            listCurrentlyShowing = 2;
            Log.i("CURRENT FRAGMENT", String.valueOf(fragmentId));
        } else if (fragmentId == R.id.favoritesMoviesFragment){
            listCurrentlyShowing = 3;
            Log.i("CURRENT FRAGMENT", String.valueOf(fragmentId));
        }
    }


    @Override
    public boolean onSupportNavigateUp() {
        FragmentManager manager = navHostFragment.getChildFragmentManager();

        int fragmentId = navController.getCurrentDestination().getId();

        //if Most popular fragment is attached, dismiss back button
        if (fragmentId == R.id.mostPopularMoviesFragment) {
            listCurrentlyShowing = 1;
            Log.i("CURRENT FRAGMENT", String.valueOf(fragmentId));
        } else if (fragmentId == R.id.topRatedMoviesFragment) {
            listCurrentlyShowing = 2;
            Log.i("CURRENT FRAGMENT", String.valueOf(fragmentId));
        } else if (fragmentId == R.id.favoritesMoviesFragment){
            listCurrentlyShowing = 3;
            Log.i("CURRENT FRAGMENT", String.valueOf(fragmentId));
        }
        return navController.navigateUp() || super.onSupportNavigateUp();
    }

}